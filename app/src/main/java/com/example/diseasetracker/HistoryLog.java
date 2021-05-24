package com.example.diseasetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryLog extends AppCompatActivity {
    String ID;
    TableLayout l;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_log);
        drawerLayout = findViewById(R.id.drawer_layout);

        Intent i = getIntent();
        ID = i.getStringExtra("ID");

        l = findViewById(R.id.llHistoryLog);

        Button btnGenerate = findViewById(R.id.btnGenerate);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date1 = getDate1();
                String date2 = getDate2();
                getLogHistory(date1, date2);
            }
        });
    }

    private void getLogHistory(String date1, String date2) {
        PHPRequest req = new PHPRequest("https://lamp.ms.wits.ac.za/home/s2089236/");
        ContentValues cv = new ContentValues();
        cv.put("id", ID);
        cv.put("date1", date1);
        cv.put("date2", date2);
        req.doRequest(this, "getHistoryLog", cv,
                new RequestHandler() {
                    @Override
                    public void processResponse(String requests) {
                        processLogHistory(requests);
                    }
                });
    }

    private void processLogHistory(String json) {
        try{
            JSONArray all = new JSONArray(json);
            final ArrayList<String> addresses = new ArrayList<>();
            if (all.length() == 0){
                HistoryLog.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayout log = new LinearLayout(HistoryLog.this);
                        log.setOrientation(LinearLayout.HORIZONTAL);
                        TextView t1 = new TextView(HistoryLog.this);
                        t1.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                        t1.setGravity(Gravity.START);
                        t1.setText("You have not been anywhere during this time period.");
                        log.addView(t1);
                        l.addView(log);
                    }
                });
            }
            else {
                for (int i = 0; i < all.length(); i++){
                    final JSONObject item = all.getJSONObject(i);
                    final String address = item.getString("LOCATION_ADDRESS");
                    final String city = item.getString("LOCATION_CITY");
                    final String date = item.getString("DATE");
                    final String time = item.getString("TIME");
                    addresses.add(address);
                    final int j = i;
                    HistoryLog.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LinearLayout log = new LinearLayout(HistoryLog.this);
                            log.setOrientation(LinearLayout.HORIZONTAL);
                            TextView tv = new TextView(HistoryLog.this);
                            TextView t1 = new TextView(HistoryLog.this);
                            TextView t2 = new TextView(HistoryLog.this);
                            TextView t3 = new TextView(HistoryLog.this);

                            LinearLayout.LayoutParams lp =
                                    new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                            log.setPadding(12, 12, 12, 12);

                            tv.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1));
                            tv.setGravity(Gravity.START);
                            t1.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            t1.setGravity(Gravity.START);
                            t2.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            t2.setGravity(Gravity.CENTER);
                            t3.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                            t3.setGravity(Gravity.END);

                            tv.setText(address);
                            t1.setText(city);
                            t2.setText(date);
                            t3.setText(time);

                            log.addView(t1);
                            log.addView(t2);
                            log.addView(t3);

                            if (j%2==0) {
                                log.setBackgroundColor(Color.parseColor("#EEEEFF"));
                                tv.setBackgroundColor(Color.parseColor("#EEEEFF"));
                            }

                            l.addView(tv);
                            l.addView(log);
                        }
                    });
                }
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public String getDate1(){
        DatePicker Age = findViewById(R.id.dpDate1);
        int day = Age.getDayOfMonth();
        int month = Age.getMonth()+1;
        int year = Age.getYear();

        String Year = Integer.toString(year);
        String Month = "";
        if (month < 10) {
            Month = "0" + month;
        } else {
            Month = Integer.toString(month);
        }
        String Day = "";
        if (day < 10) {
            Day = "0" + day;
        } else {
            Day = Integer.toString(day);
        }

        return Year + "-" + Month + "-" + Day;
    }

    public String getDate2(){
        DatePicker Age = findViewById(R.id.dpDate2);
        int day = Age.getDayOfMonth();
        int month = Age.getMonth()+1;
        int year = Age.getYear();

        String Year = Integer.toString(year);
        String Month = "";
        if (month < 10) {
            Month = "0" + month;
        } else {
            Month = Integer.toString(month);
        }
        String Day = "";
        if (day < 10) {
            Day = "0" + day;
        } else {
            Day = Integer.toString(day);
        }

        return Year + "-" + Month + "-" + Day;
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
}