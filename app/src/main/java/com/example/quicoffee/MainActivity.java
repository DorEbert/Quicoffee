package com.example.quicoffee;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.microedition.khronos.opengles.GL;

public class MainActivity extends AppCompatActivity {
    private int usernameTextboxID;
    private int passwordTextboxID;
    private int mainActivityWitdh;
    private int mainActivityHeight;
    private LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BuildActivityUI();

    }
    private void BuildActivityUI(){
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams((int)(mainActivityWitdh *0.9),mainActivityHeight/20);
        //Email label and textBox
        TextView emailTextView = CreateTextView(Global_Variable.EMAIL);
        EditText emailEditText = CreateEditText(lparams);
        emailEditText.setId(Global_Variable.GetID());
        usernameTextboxID = emailEditText.getId();
        //Password label and textBox
        TextView passwordTextViiew = CreateTextView(Global_Variable.PASSWORD);
        EditText passwordEditText = CreateEditText(lparams);
        passwordEditText.setId(Global_Variable.GetID());
        passwordTextboxID = passwordEditText.getId();
        //Add Views to LinearLayout
        linearLayout.addView(emailTextView);
        linearLayout.addView(emailEditText);
        linearLayout.addView(passwordTextViiew);
        linearLayout.addView(passwordEditText);
        addLoginButton();
    }
    private void InititalVariablesOfLocalActivity(){
        mainActivityWitdh = getResources().getDisplayMetrics().widthPixels;
        mainActivityHeight = getResources().getDisplayMetrics().heightPixels;
        linearLayout = findViewById(R.id.linear_layout);
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
    private void addLoginButton() {
        //Set Button Settings
        Button loginButton = new Button(this);
        //loginButton.setOnClickListener(this);
        loginButton.setText(Global_Variable.LOGIN);
        LinearLayout.LayoutParams loginButtonLayoutParams =
                new LinearLayout.LayoutParams((int)(mainActivityWitdh *0.5),mainActivityHeight/20);
        loginButtonLayoutParams.gravity = Gravity.CENTER;
        loginButtonLayoutParams.setMargins(0
                ,mainActivityHeight/20
                ,0
                ,mainActivityHeight/40);
        loginButton.setLayoutParams(loginButtonLayoutParams);
        loginButton.setBackgroundResource(R.color.loginViewColor);
        loginButton.setTextColor(getApplication().getResources().getColor(R.color.colorBlack));
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
}
