package com.example.annotationprocex;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.PrintMe;
import com.example.annotationprocex.staticproc.NullChecking;

@PrintMe
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private MealFactory factory = new MealFactory();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Meal meal = order("Margherita");
        meal.getPrice();
    }

    public Meal order(String mealName) {
        return factory.create(mealName);
    }


    @Override
    public void onClick(View v) {

    }
}
