package com.example.diseasetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b = findViewById(R.id.btnSignIn);
        final EditText edtEmail = findViewById(R.id.edtEmail);
        final EditText edtPassword = findViewById(R.id.edtPassword);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sEmail = edtEmail.getText().toString();
                final String sPassword = edtPassword.getText().toString();
                if (!valInput(sEmail, sPassword)){
                    Toast.makeText(MainActivity.this,
                            "Username or password not given",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    doThis(sEmail, sPassword);
                }
            }
        });

        TextView signUp = findViewById(R.id.txtSignUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SignUp.class);
                startActivity(i);
            }
        });
    }

    public boolean valInput(String sPassword, String sEmail){
        return sEmail.length() != 0 && sPassword.length() != 0;
    }

    public void doThis(final String email, final String password){
        PHPRequest req = new PHPRequest("https://lamp.ms.wits.ac.za/home/s2089236/");
        ContentValues cv = new ContentValues();
        cv.put("email", email);
        cv.put("password", password);
        req.doRequest(this, "login", cv,
                new RequestHandler() {
                    @Override
                    public void processResponse(String requests) {
                        processJSON(requests);
                    }
                });
    }

    public void processJSON(String json){
        try{
            JSONArray all = new JSONArray(json);
            for (int i = 0; i < all.length(); i++){
                final JSONObject item = all.getJSONObject(i);
                final String id = item.getString("LOGIN_ID");
                if(all.length() != 0){
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(MainActivity.this, MainMenu.class);
                            i.putExtra("ID", id);
                            startActivity(i);
                        }
                    });
                }
                else{
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                            EditText edt = findViewById(R.id.edtPassword);
                            edt.setText("");
                        }
                    });
                }
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }
}