package com.example.starbuzz;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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
                       new String[]{"NAME", "DESCRIPTION", "IMAGE_RESOURCE_ID"},
                       "_id = ?",
                       new String[]{Integer.toString(drinkId)},
                       null, null, null);

               //переход к первой записи в курсоре
               if (cursor.moveToFirst()) {

                   //получение данных напитка из курсора
                   String nameText = cursor.getString(0);
                   String descriptionText = cursor.getString(1);
                   int photoId = cursor.getInt(2);

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
               }
               cursor.close();
               db.close();
           }
           catch (SQLiteException e){
               Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
               toast.show();
           }
    }
}
