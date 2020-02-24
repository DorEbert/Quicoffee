package com.example.quicoffee;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.quicoffee.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private User user;
    private int usernameTextboxID;
    private int passwordTextboxID;
    private int newAccountTextVieID;
    private int mainActivityWitdh;
    private int mainActivityHeight;
    private LinearLayout linearLayout;
    private FirebaseAuth auth;
    private FireBaseUtill fireBaseUtill = new FireBaseUtill();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        InititalVariablesOfLocalActivity();
        BuildActivityUI();

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
                //findShops();
                return true;
            case R.id.favoirtCoffee:
             //   favoirtCoffee();
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


    private void BuildActivityUI(){
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams((int)(mainActivityWitdh *0.9),mainActivityHeight/20);
        lparams.gravity = Gravity.CENTER;
        //Email label and textBox
        usernameTextboxID = addPairOfTextViewAndEditText(Global_Variable.EMAIL,lparams);
        //Password label and textBox
        passwordTextboxID = addPairOfTextViewAndEditText(Global_Variable.PASSWORD,lparams);
        addLoginButton();
        addNewAccount();
    }

    private void addNewAccount(){
        TextView textView = CreateTextView(Global_Variable.NEW_ACCOUNT);
        textView.setPadding(50,10,50,10);
        textView.setTextSize(14);
        SpannableString content = new SpannableString(Global_Variable.NEW_ACCOUNT);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);
        linearLayout.addView(textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this , "cool!", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(LoginActivity.this,
                        signIn.class);
                startActivity(myIntent);
            }
        });
    }

    private int addPairOfTextViewAndEditText(String labelText,LinearLayout.LayoutParams lparams){
        TextView textView = CreateTextView(labelText);
        textView.setPadding(50,10,50,10);
        EditText editText = CreateEditText(lparams);
        editText.setId(Global_Variable.GetID());
        linearLayout.addView(textView);
        linearLayout.addView(editText);
        return editText.getId();
    }
    private void InititalVariablesOfLocalActivity(){
        mainActivityWitdh = getResources().getDisplayMetrics().widthPixels;
        mainActivityHeight = getResources().getDisplayMetrics().heightPixels;
        linearLayout = findViewById(R.id.linear_layout);
        auth = FirebaseAuth.getInstance();
    }
    private void addLoginButton() {
        //Set Button Settings
        Button loginButton = new Button(this);
        loginButton.setOnClickListener(this);
        loginButton.setText(Global_Variable.LOGIN);
        LinearLayout.LayoutParams loginButtonLayoutParams =
                new LinearLayout.LayoutParams((int)(mainActivityWitdh *0.5),mainActivityHeight/20);
        loginButtonLayoutParams.gravity = Gravity.CENTER;
        loginButtonLayoutParams.setMargins(0
                ,mainActivityHeight/20
                ,0
                ,mainActivityHeight/40);
        loginButton.setLayoutParams(loginButtonLayoutParams);
        loginButton.setBackgroundResource(R.color.colorCoffee);
        loginButton.setTextColor(getApplication().getResources().getColor(R.color.textViewColor));
        linearLayout.addView(loginButton);
    }
    private TextView CreateTextView(String labelText){
        //Set Label Setting
        TextView textView = new TextView(this);
        textView.setText(labelText);
        textView.setTextColor(getApplication().getResources().getColor(R.color.colorBlack));
        textView.setTextSize(mainActivityWitdh/40);
        return textView;
    }
    private EditText CreateEditText(LinearLayout.LayoutParams lparams) {
        //Set EditText Setting
        EditText editText = new EditText(this);
        editText.setMaxLines(1);
        editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        editText.setBackgroundColor(getApplication().getResources().getColor(R.color.textViewColor));
        editText.setLayoutParams(lparams);
        return editText;
    }
    @Override
    public void onClick(View v) {
        //todo change to which activity
        //final User user2 = new User("Dor","Ebert","dor123@gmail.com","123","");
        //fireBaseUtill.addUser(user2);
        //getUser("-M0OkJc1uoX2y227WG6S");
        fireBaseUtill.getRefrencesUsers().
                orderByChild(Global_Variable.ID.toLowerCase()).equalTo("-M0dEouLY5J-QKinwhe0")
                .addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot usersSnapShot: dataSnapshot.getChildren()){
                            try {
                                String firstName = usersSnapShot.child(Global_Variable.COLUMN_FIRSTNAME).getValue().toString();
                                String lastName = usersSnapShot.child(Global_Variable.COLUMN_LASTNAME).getValue().toString();
                                String email = usersSnapShot.child(Global_Variable.COLUMN_EMAIL.toLowerCase()).getValue().toString();
                                String password = usersSnapShot.child(Global_Variable.COLUMN_PASSWORD.toLowerCase()).getValue().toString();
                                String shop = usersSnapShot.child(Global_Variable.COLUMN_SHOPS.toLowerCase()).getValue().toString();
                                user = new User(firstName,lastName,email,password,shop);
                                break;
                            }
                            catch(Exception e){
                                e.getMessage();
                            }
                        }
                        user.setID("-M0dEouLY5J-QKinwhe0");
                        Global_Variable.user = user;
                        Intent intent = new Intent(LoginActivity.this,  ShopActivity.class);
                        startActivity(intent);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
        //finish();
        /*String email = ((EditText)findViewById(usernameTextboxID)).getText().toString();
        final String password = ((EditText)findViewById(passwordTextboxID)).getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), Global_Variable.MISSING_EMAIL_INFORMATION, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), Global_Variable.MISSING_PASSWORD_INFORMATION, Toast.LENGTH_SHORT).show();
            return;
        }

        //authenticate user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        //progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                ((EditText)findViewById(passwordTextboxID)).setError(getString(Global_Variable.MINIMUM_PASSWORD));
                            } else {
                                Toast.makeText(LoginActivity.this, Global_Variable.AUTH_FAILED, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            //todo change to which activity
                            Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });*/
    }
    private void getUser(String userID) {
        fireBaseUtill.getRefrencesUsers().
                orderByChild(Global_Variable.ID).equalTo(userID)
                /*.addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot usersSnapShot: dataSnapshot.getChildren()){
                            try {
                                String firstName = usersSnapShot.child(Global_Variable.COLUMN_FIRSTNAME).getValue().toString();
                                String lastName = usersSnapShot.child(Global_Variable.COLUMN_LASTNAME.toLowerCase()).getValue().toString();
                                String email = usersSnapShot.child(Global_Variable.COLUMN_EMAIL.toLowerCase()).getValue().toString();
                                String password = usersSnapShot.child(Global_Variable.COLUMN_PASSWORD.toLowerCase()).getValue().toString();
                                String shop = usersSnapShot.child(Global_Variable.COLUMN_SHOPS.toLowerCase()).getValue().toString();
                                user = new User(firstName,lastName,email,password,shop);
                                break;
                            }
                            catch(Exception e){
                                e.getMessage();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });*/
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String firstName = dataSnapshot.child(Global_Variable.COLUMN_FIRSTNAME).getValue().toString();
                        String lastName = dataSnapshot.child(Global_Variable.COLUMN_LASTNAME.toLowerCase()).getValue().toString();
                        String email = dataSnapshot.child(Global_Variable.COLUMN_EMAIL.toLowerCase()).getValue().toString();
                        String password = dataSnapshot.child(Global_Variable.COLUMN_PASSWORD.toLowerCase()).getValue().toString();
                        String shop = dataSnapshot.child(Global_Variable.COLUMN_SHOPS.toLowerCase()).getValue().toString();
                        user = new User(firstName,lastName,email,password,shop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
