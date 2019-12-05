package com.example.starbuzz;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.BoringLayout;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;


public class DrinkActivity extends Activity {

    public static final String EXTRA_DRINKID = "drinkId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);

        //получить напиток из данных интента
        final int drinkId = (Integer)getIntent().getExtras().get(EXTRA_DRINKID);

        //создание курсора
        SQLiteOpenHelper starbuzzDatabaseHelper = new StarbuzzDatabaseHelper(this);
           try {
               SQLiteDatabase db = starbuzzDatabaseHelper.getReadableDatabase();
               Cursor cursor = db.query("DRINK",
                       new String[]{"NAME", "DESCRIPTION", "IMAGE_RESOURCE_ID", "FAVORITE"},
                       "_id = ?",
                       new String[]{Integer.toString(drinkId)},
                       null, null, null);

               //переход к первой записи в курсоре
               if (cursor.moveToFirst()) {

                   //получение данных напитка из курсора
                   String nameText = cursor.getString(0);
                   String descriptionText = cursor.getString(1);
                   int photoId = cursor.getInt(2);
                   boolean isFavorite = (cursor.getInt(3) == 1);

                   //заполнение названия напитка
                   TextView name = (TextView) findViewById(R.id.name);
                   name.setText(nameText);

                   //заполнение описания напитка
                   TextView description = (TextView) findViewById(R.id.description);
                   description.setText(descriptionText);

                   //заполнение изображения напитка
                   ImageView photo = (ImageView) findViewById(R.id.photo);
                   photo.setImageResource(photoId);
                   photo.setContentDescription(nameText);

                   //заполнение флажка любимого напитка
                   CheckBox favorite = (CheckBox)findViewById(R.id.favorite);
                   favorite.setChecked(isFavorite);
               }
               cursor.close();
               db.close();
           }
           catch (SQLiteException e){
               Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
               toast.show();
           }
    }

    //обновление базы данных по щелчку на флажке
    public void onFavoriteClicked(View view){
        int drinkId = (Integer)getIntent().getExtras().get(EXTRA_DRINKID);
        new UpdateDrinkTask().execute(drinkId);
    }

    //внутренний класс для обновления напитка
    private class UpdateDrinkTask extends AsyncTask<Integer, Void, Boolean>{
        private ContentValues drinkValues;

        protected void onPreExecute() {
            //получение значения флажка
            CheckBox favorite = (CheckBox) findViewById(R.id.favorite);
            drinkValues = new ContentValues();
            drinkValues.put("FAVORITE", favorite.isChecked());
        }

        //получение ссылки на базу данных и обновление столбца FAVORITE
        protected Boolean doInBackground(Integer... drinks){
            int drinkId = drinks[0];
            SQLiteOpenHelper starbuzzDatabaseHelper = new StarbuzzDatabaseHelper(DrinkActivity.this);
            try{
                SQLiteDatabase db = starbuzzDatabaseHelper.getWritableDatabase();
                db.update("DRINK",
                    drinkValues,
                    "_id = ?",
                    new String[] {Integer.toString(drinkId)});
                db.close();
                return true;
            } catch (SQLiteException e){
                return false;
            }
        }

    protected void onPostExecute(Boolean success){
        if(!success){
            Toast toast = Toast.makeText(DrinkActivity.this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
            }
        }
    }
}
