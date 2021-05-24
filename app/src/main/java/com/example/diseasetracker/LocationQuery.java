package com.example.diseasetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

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

public class LocationQuery extends AppCompatActivity {

    String ID;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_query);

        drawerLayout = findViewById(R.id.drawer_layout);
        Intent i = getIntent();
        ID = i.getStringExtra("ID");

        Button btnQuery = findViewById(R.id.btnQuery);
        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edtCity = findViewById(R.id.edtCity);
                PHPRequest req = new PHPRequest("https://lamp.ms.wits.ac.za/home/s2089236/");
                ContentValues cv = new ContentValues();
                cv.put("location", edtCity.getText().toString());
                req.doRequest(LocationQuery.this, "percentage", cv,
                        new RequestHandler() {
                            @Override
                            public void processResponse(final String requests) {
                                processJSON(requests);
                            }
                        });
            }
        });
    }

    private void processJSON(String json) {
        final TextView txtCode = findViewById(R.id.txtCode);
        final TextView txtName = findViewById(R.id.txtName);
        final TextView txtProvCode = findViewById(R.id.txtProvCode);
        final TextView txtProvName = findViewById(R.id.txtProvName);
        final TextView txtPopulation = findViewById(R.id.txtPopulation);
        final TextView txtInfected = findViewById(R.id.txtInfected);
        final TextView txtPercentage = findViewById(R.id.txtPercentage);
        final TextView txtRisk = findViewById(R.id.txtRisk);
        try{
            JSONArray all = new JSONArray(json);
            for (int i = 0; i < all.length(); i++){
                JSONObject item = all.getJSONObject(i);
                final String code = item.getString("CITY_CODE");
                final String name = item.getString("CITY_NAME");
                final String provcode = item.getString("CITY_PROVCODE");
                final String province = item.getString("PROVINCE");
                final String population = item.getString("CITY_POPULATION");
                final String infected = item.getString("CITY_INFECTED");
                final String percentage = item.getString("PERCENTAGE(CITY_STATS.CITY_POPULATION, CITY_STATS.CITY_INFECTED)") + "%";
                final String risk = item.getString("CITY_RISK");
                LocationQuery.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtCode.setText(code);
                        txtName.setText(name);
                        txtProvCode.setText(provcode);
                        txtProvName.setText(province);
                        txtPopulation.setText(population);
                        txtInfected.setText(infected);
                        txtPercentage.setText(percentage);
                        txtRisk.setText(risk);
                    }
                });
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
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
        MainMenu.redirectActivity(this, UserProfile.class, ID);
    }

    public void ClickLocationQuery(View view){
        recreate();
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
}