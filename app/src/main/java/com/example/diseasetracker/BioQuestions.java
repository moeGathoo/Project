package com.example.diseasetracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BioQuestions extends AppCompatActivity {
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio_questions);
        Intent i = getIntent();
        id = i.getStringExtra("ID");

        final Button btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfo();
                Intent i = new Intent(BioQuestions.this, UserProfile.class);
                startActivity(i);
            }
        });
    }

    public void addBio(String id, String address, double height, double weight, String conditions){
        PHPRequest req = new PHPRequest("https://lamp.ms.wits.ac.za/home/s2089236/");
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("address", address);
        cv.put("height", height);
        cv.put("weight", weight);
        cv.put("conditions", conditions);
        req.doRequest(this, "addBio", cv,
                new RequestHandler() {
                    @Override
                    public void processResponse(String requests) {
                        ;
                    }
                });
    }

    public void getInfo(){
        EditText address1 = findViewById(R.id.edtAddress1);
        EditText address2 = findViewById(R.id.edtAddress2);
        String address = address1.getText().toString() + ", " + address2.getText().toString();

        EditText eHeight = findViewById(R.id.edtHeight);
        double height = Double.parseDouble(eHeight.getText().toString());
        EditText eWeight = findViewById(R.id.edtWeight);
        double weight = Double.parseDouble(eWeight.getText().toString());

        String conditions = getConditions();

        if (address1.length() == 0 || address2.length() == 0 || conditions.length() == 0){
            BioQuestions.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BioQuestions.this,
                            "You have not completed all the required information",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            addBio(id, address, height, weight, conditions);
        }
    }

    public String getConditions() {
        String sConditions = "";

        CheckBox asthma = findViewById(R.id.cbxAsthma);
        CheckBox diabetes = findViewById(R.id.cbxDiabetes);
        CheckBox heart = findViewById(R.id.cbxHeart);
        CheckBox hemoglobin = findViewById(R.id.cbxHemoglobin);
        CheckBox immune = findViewById(R.id.cbxImmune);
        CheckBox kidney = findViewById(R.id.cbxKidney);
        CheckBox liver = findViewById(R.id.cbxLiver);
        CheckBox lungs = findViewById(R.id.cbxLung);
        CheckBox other = findViewById(R.id.cbxOther);

        CheckBox[] conditions = new CheckBox[]{asthma, heart, diabetes
        , hemoglobin, immune, kidney, liver, lungs};

        for (CheckBox condition : conditions){
            if (condition.isChecked()){
                sConditions = sConditions.concat(condition.getText().toString());
                sConditions = sConditions.concat(", ");
            }
        }

        if (other.isChecked()){
            EditText otherConditions = findViewById(R.id.edtConditions);
            String others = otherConditions.getText().toString();
            sConditions = sConditions.concat(others);
        }

        sConditions.trim();
        return sConditions;
    }
}