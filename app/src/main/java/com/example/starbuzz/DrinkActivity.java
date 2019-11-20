package com.example.starbuzz;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class DrinkActivity extends Activity {

    public static final String EXTRA_DRINKID = "drinkId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);

        //получить напиток из данных интента
        int drinkId = (Integer)getIntent().getExtras().get(EXTRA_DRINKID);
        Drink drink  = Drink.drinks[drinkId];

        //заполнение названия напитка
        TextView name = (TextView)findViewById(R.id.name);
        name.setText(drink.getName());

        //заполнение описания напитка
        TextView description = (TextView)findViewById(R.id.description);
        description.setText(drink.getDescription());

        //заполнение изобраения напитка
        ImageView photo = (ImageView)findViewById(R.id.photo);
        photo.setImageResource(drink.getImageResourceId());
        photo.setContentDescription(drink.getName());
    }
}
