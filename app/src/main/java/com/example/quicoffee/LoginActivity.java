package com.example.quicoffee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private int usernameTextboxID;
    private int passwordTextboxID;
    private int mainActivityWitdh;
    private int mainActivityHeight;
    private LinearLayout linearLayout;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InititalVariablesOfLocalActivity();
        BuildActivityUI();

    }
    private void BuildActivityUI(){
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams((int)(mainActivityWitdh *0.9),mainActivityHeight/20);
        lparams.gravity = Gravity.CENTER;
        //Email label and textBox
        TextView emailTextView = CreateTextView(Global_Variable.EMAIL);
        emailTextView.setPadding(50,10,50,10);
        EditText emailEditText = CreateEditText(lparams);
        emailEditText.setId(Global_Variable.GetID());
        usernameTextboxID = emailEditText.getId();
        //Password label and textBox
        TextView passwordTextView = CreateTextView(Global_Variable.PASSWORD);
        passwordTextView.setPadding(50,10,50,10);
        EditText passwordEditText = CreateEditText(lparams);
        passwordEditText.setId(Global_Variable.GetID());
        passwordTextboxID = passwordEditText.getId();
        //Add Views to LinearLayout
        linearLayout.addView(emailTextView);
        linearLayout.addView(emailEditText);
        linearLayout.addView(passwordTextView);
        linearLayout.addView(passwordEditText);
        addLoginButton();
    }
    private void InititalVariablesOfLocalActivity(){
        mainActivityWitdh = getResources().getDisplayMetrics().widthPixels;
        mainActivityHeight = getResources().getDisplayMetrics().heightPixels;
        linearLayout = findViewById(R.id.linear_layout);
        auth = FirebaseAuth.getInstance();
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

    @Override
    public void onClick(View v) {
        String email = ((EditText)findViewById(usernameTextboxID)).getText().toString();
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
                });
    }
}
