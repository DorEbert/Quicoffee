package com.example.quicoffee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quicoffee.Models.FavoriteCoffee;
import com.google.firebase.auth.FirebaseUser;

import java.text.Normalizer;

public class FavoriteCoffeeActivity extends AppCompatActivity  {
    public FirebaseUser user;
    private int mainActivityWitdh;
    private int mainActivityHeight;
    private LinearLayout linearLayout;
    private FavoriteCoffee favoriteCoffee;

//TODO: singelton Favorite coffee
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_coffee);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        InititalVariablesOfLocalActivity();
        initAttributesForCoffee(Global_Variable.SIZE_OF_CUP, Global_Variable.SIZE_OF_COFFEE);
        initAttributesForCoffee(Global_Variable.MILK, Global_Variable.TYPES_OF_MILK);
        initAttributesForCoffee(Global_Variable.ESPRESSO, Global_Variable.AMOUNT_OF_ESPRESSO);
        initAttributesForCoffee(Global_Variable.FOAM, Global_Variable.WITH_FOAM);
        addSaveButton();
    }

    private void addSaveButton(){
        Button botton = new Button(this);
        botton.setText(Global_Variable.SAVE_FAVORITE_COFFEE);
        LinearLayout.LayoutParams loginButtonLayoutParams =
                new LinearLayout.LayoutParams((int)(mainActivityWitdh *0.5),mainActivityHeight/20);
        loginButtonLayoutParams.gravity = Gravity.CENTER;
        loginButtonLayoutParams.setMargins(0
                ,mainActivityHeight/20
                ,0
                ,mainActivityHeight/40);
        botton.setLayoutParams(loginButtonLayoutParams);
        botton.setBackgroundResource(R.color.colorCoffee);
        botton.setTextColor(getApplication().getResources().getColor(R.color.textViewColor));
        botton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: save the coffee on the DB
            }
        });
        linearLayout.addView(botton);
    }


    private void initAttributesForCoffee(final String attribute , String[] items){
        linearLayout.addView(CreateTextView(attribute));
        //init spinner:
        final Spinner spinner = new Spinner(this);
        spinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        spinner.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        ArrayAdapter <String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemValue= (String)spinner.getItemAtPosition(position).toString();
               // Toast.makeText(FavoriteCoffeeActivity.this , itemValue, Toast.LENGTH_SHORT).show();
                if (attribute.equals(Global_Variable.SIZE_OF_CUP)){
                    favoriteCoffee.setSizeOfCup(itemValue);
                }
                else if (attribute.equals(Global_Variable.MILK)){
                    favoriteCoffee.setTypeOfMile(itemValue);
                }
                else if (attribute.equals(Global_Variable.ESPRESSO)){
                    favoriteCoffee.setAmountOfEspresso(itemValue);
                }
                else if (attribute.equals(Global_Variable.WITH_FOAM)){
                    favoriteCoffee.setWith_Form(itemValue);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        linearLayout.addView(spinner);
    }


    private TextView CreateTextView(String labelText){
        //Set Label Setting
        TextView textView = new TextView(this);
        textView.setText(labelText+":");
        textView.setTextSize(16);
        textView.setPadding(50,10,50,10);
        textView.setTextColor(getApplication().getResources().getColor(R.color.textViewColor));
        textView.setBackgroundResource(R.color.colorforAttributes);
        textView.setTextSize(mainActivityWitdh/55);
        return textView;
    }



    private void InititalVariablesOfLocalActivity(){
        mainActivityWitdh = getResources().getDisplayMetrics().widthPixels;
        mainActivityHeight = getResources().getDisplayMetrics().heightPixels;
        linearLayout = findViewById(R.id.linear_layout);
        favoriteCoffee = new FavoriteCoffee();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    //TODO: init all the menu oprtions :)
    //findShops, favoirtCoffee, myOrder, setUpAShop, setting,logOut
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.findShops:
                findShops();
                return true;
            case R.id.favoriteCoffee:
                favoriteCoffee();
                return true;
            case R.id.myOrder:
                //     showMyOrders();
                return true;
            case R.id.setUpAShop:
                return true;
            case R.id.setting:
                return true;
            case R.id.logOut:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void findShops(){
        Intent myIntent = new Intent(FavoriteCoffeeActivity.this,
                findShopsActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        startActivity(myIntent);
    }

    public void favoriteCoffee(){
        Intent myIntent = new Intent(FavoriteCoffeeActivity.this,
                FavoriteCoffeeActivity.class);
        myIntent.putExtra(Global_Variable.USER_FOR_MOVE_INTENT,this.user);
        startActivity(myIntent);
    }

}
