package com.example.starbuzz;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.List;

public class TopLevelActivity extends Activity {

    private SQLiteDatabase db;
    private Cursor favoritesCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_level);
        setupOptionsListView();
        setupFavoritesListView();
    }

    private void setupOptionsListView(){
        //создание объекта OnItemCLickListener
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View itemView, int position, long id) {
                if(position == 0){
                    Intent intent = new Intent(TopLevelActivity.this, DrinkCategoryActivity.class);
                    startActivity(intent);
                }
            }
        };

        //добавление слушателя для списка команд
        ListView listView = (ListView)findViewById(R.id.list_options);
        listView.setOnItemClickListener(itemClickListener);
    }

    //метод для заполнения списка list_favorites
    private void setupFavoritesListView(){
        //заполение списка list_favorites из курсора
        ListView listFavorites = (ListView)findViewById(R.id.list_favorites);
        try{
            SQLiteOpenHelper starbuzzDatabaseHelper = new StarbuzzDatabaseHelper(this);
            db = starbuzzDatabaseHelper.getReadableDatabase();
            favoritesCursor = db.query("DRINK",
                    new String[] {"_id", "NAME"},
                    "FAVORITE = 1",
                    null, null, null, null);
            CursorAdapter favoriteAdapter = new SimpleCursorAdapter(TopLevelActivity.this,
                    android.R.layout.simple_list_item_1,
                    favoritesCursor,
                    new String[] {"NAME"},
                    new int[] {android.R.id.text1}, 0);
            listFavorites.setAdapter(favoriteAdapter);
        } catch(SQLiteException e){
            Toast toast = Toast.makeText(this, "Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        //переход к DrinkActivity при выборе напитка
        listFavorites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View v, int position, long id) {
                Intent intent = new Intent(TopLevelActivity.this, DrinkActivity.class);
                intent.putExtra(DrinkActivity.EXTRA_DRINKID, (int)id);
                startActivity(intent);
            }
        });
    }

    /*добавление метода onRestart. метод вызывается при возврате пользователя к TopLevelActivity*/
    @Override
    public void onRestart(){
        super.onRestart();
        Cursor newCursor = db.query("DRINK",
                new String[] {"_id", "NAME"},
                "FAVORITE = 1",
                null, null, null, null);
        ListView listFavorites = (ListView)findViewById(R.id.list_favorites);
        CursorAdapter adapter = (CursorAdapter)listFavorites.getAdapter();
        //курсор из list_favorites заменяется новым курсором
        adapter.changeCursor(newCursor);
        /*Значение favoritesCursor заменяется новым курсором, чтобы
        его можно было закрыть в методе onDestroy() активности*/
        favoritesCursor = newCursor;
    }

    //закрытие курсора и базы данных в метоже onDestroy()
    @Override
    public void onDestroy(){
        super.onDestroy();
        favoritesCursor.close();
        db.close();
    }
}
