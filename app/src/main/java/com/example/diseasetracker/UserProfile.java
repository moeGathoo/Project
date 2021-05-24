package com.example.diseasetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//TODO:hide button if populated array returned
public class UserProfile extends AppCompatActivity {
    String ID;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        drawerLayout = findViewById(R.id.drawer_layout);

        Intent i = getIntent();
        ID = i.getStringExtra("ID");
        Button b = findViewById(R.id.btnBio);

        PHPRequest req = new PHPRequest("https://lamp.ms.wits.ac.za/home/s2089236/");
        ContentValues cv = new ContentValues();
        cv.put("id", ID);
        req.doRequest(UserProfile.this, "fetchUserBio", cv,
                new RequestHandler() {
                    @Override
                    public void processResponse(final String requests) {
                        processJSON(requests);
                    }
                });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfile.this, BioQuestions.class);
                i.putExtra("ID", ID);
                startActivity(i);
            }
        });

        Button btnYes = findViewById(R.id.btnYes);
        Button btnRecovered = findViewById(R.id.btnRecovered);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(UserProfile.this)
                        .setTitle("COVID-19")
                        .setMessage("You are about to change your profile to testing positive to COVID 19. Once you have recovered, make sure you come back to revert this change.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                PHPRequest req = new PHPRequest("https://lamp.ms.wits.ac.za/home/s2089236/");
                                ContentValues cv = new ContentValues();
                                cv.put("id", ID);
                                req.doRequest(UserProfile.this, "diagnosed", cv,
                                        new RequestHandler() {
                                            @Override
                                            public void processResponse(final String requests) {
                                                ;
                                            }
                                        });
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        btnRecovered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(UserProfile.this)
                        .setTitle("COVID-19")
                        .setMessage("You are about to change your profile to having recovered from COVID-19. Remember to be safe out the and always practice good hygiene.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                PHPRequest req = new PHPRequest("https://lamp.ms.wits.ac.za/home/s2089236/");
                                ContentValues cv = new ContentValues();
                                cv.put("id", ID);
                                req.doRequest(UserProfile.this, "recovered", cv,
                                        new RequestHandler() {
                                            @Override
                                            public void processResponse(final String requests) {
                                                ;
                                            }
                                        });
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    public void ClickMenu(View view){
        MainMenu.openDrawer(drawerLayout);
    }

    public void ClickLogo(View view){
        MainMenu.closeDrawer(drawerLayout);
    }

    public void ClickHome(View view){
        MainMenu.redirectActivity(this, MainMenu.class, ID);
    }

    public void ClickUserProfile(View view){
        recreate();
    }

    public void ClickLocationQuery(View view){
        MainMenu.redirectActivity(this, LocationQuery.class, ID);
    }

    public void ClickLocationHistory(View view){
        MainMenu.redirectActivity(this, LocationHistory.class, ID);
    }

    public void ClickLogout(View view){
        MainMenu.logout(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainMenu.closeDrawer(drawerLayout);
    }

    public void processJSON(String json){
        final TextView txtName = findViewById(R.id.txtName);
        final TextView txtSurname = findViewById(R.id.txtSurname);
        final TextView txtDob = findViewById(R.id.txtDoB);
        final TextView txtAge = findViewById(R.id.txtAge);
        final TextView txtHeight = findViewById(R.id.txtHeight);
        final TextView txtWeight = findViewById(R.id.txtWeight);
        final TextView txtBMI = findViewById(R.id.txtBMI);
        final TextView txtConditions = findViewById(R.id.txtConditions);
        try{
            JSONArray all = new JSONArray(json);
            if (all.length() == 0){
                UserProfile.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(UserProfile.this)
                                .setTitle("Biographical Questionnaire")
                                .setMessage("You Have not completed the biographical questionnaire. Complete the biographical questionnaire to view your complete profile")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = new Intent(UserProfile.this, BioQuestions.class);
                                        i.putExtra("ID", ID);
                                        startActivity(i);
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                });
                return;
            }
            for (int i = 0; i < all.length(); i++){
                final JSONObject item = all.getJSONObject(i);
                final String name = item.getString("USER_FNAME");
                final String surname = item.getString("USER_LNAME");
                final String dob = item.getString("USER_DOB");
                final String age = item.getString("USER_AGE");
                final String height = item.getString("USER_HEIGHT");
                final String weight = item.getString("USER_WEIGHT");
                final String bmi = item.getString("USER_BMI");
                final String conditions = item.getString("USER_CONDITIONS");
                UserProfile.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtName.setText(name);
                        txtSurname.setText(surname);
                        txtDob.setText(dob);
                        txtAge.setText(age);
                        txtHeight.setText(height);
                        txtWeight.setText(weight);
                        txtBMI.setText(bmi);
                        txtConditions.setText(conditions);
                    }
                });
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }
}