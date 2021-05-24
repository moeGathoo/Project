package com.example.diseasetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

public class Sign_Up2 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign__up2);

        Button btnNext = findViewById(R.id.btnNext2);
        final Intent j = getIntent();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatePassword()){
                    addInfo(j);
                    addLoginfo(j);
                    Intent i = new Intent(Sign_Up2.this, MainActivity.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(Sign_Up2.this, "Passwords don't match", Toast.LENGTH_LONG).show();
                    EditText Password = findViewById(R.id.edtPassword);
                    EditText Password2 = findViewById(R.id.edtPassword2);
                    Password.setText(null);
                    Password2.setText(null);
                }
            }
        });
    }

    public boolean validatePassword(){
        EditText Password = findViewById(R.id.edtPassword);
        EditText Password2 = findViewById(R.id.edtPassword2);
        String password = Password.getText().toString();
        String password2 = Password2.getText().toString();
        return password.equals(password2);
    }

    public void addInfo(Intent j){
        PHPRequest req = new PHPRequest("https://lamp.ms.wits.ac.za/home/s2089236/");
        ContentValues cv = new ContentValues();
        cv.put("id", j.getStringExtra("ID"));
        cv.put("name", j.getStringExtra("Name"));
        cv.put("surname", j.getStringExtra("Surname"));
        cv.put("gender", j.getStringExtra("Gender"));
        cv.put("age", j.getStringExtra("Age"));
        cv.put("diagnosed", j.getStringExtra("Diagnosed"));
        req.doRequest(this, "signup", cv,
                new RequestHandler() {
                    @Override
                    public void processResponse(String requests) {
                        ;
                    }
                });
    }

    public void addLoginfo(Intent j){
        EditText Password = findViewById(R.id.edtPassword);
        EditText Email = findViewById(R.id.edtEmail);
        String password = Password.getText().toString();
        String email = Email.getText().toString();

        PHPRequest req = new PHPRequest("https://lamp.ms.wits.ac.za/home/s2089236/");
        ContentValues cv = new ContentValues();
        cv.put("id", j.getStringExtra("ID"));
        cv.put("email", email);
        cv.put("password", password);
        req.doRequest(this, "loginfo", cv,
                new RequestHandler() {
                    @Override
                    public void processResponse(String requests) {
                        ;
                    }
                });
    }
}