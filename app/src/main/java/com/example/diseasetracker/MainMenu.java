package com.example.diseasetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class MainMenu extends AppCompatActivity {
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient fusedLocationProviderClient;
    DrawerLayout drawerLayout;
    String ID;
    TextView txtCity1, txtCity2, txtCity3, txtCity4, txtCity5;
    TextView txtInf1, txtInf2, txtInf3, txtInf4, txtInf5;
    TextView txtRisk1, txtRisk2, txtRisk3, txtRisk4, txtRisk5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        drawerLayout = findViewById(R.id.drawerLayout);
        txtCity1 = findViewById(R.id.txtCity1);
        txtCity2 = findViewById(R.id.txtCity2);
        txtCity3 = findViewById(R.id.txtCity3);
        txtCity4 = findViewById(R.id.txtCity4);
        txtCity5 = findViewById(R.id.txtCity5);
        txtInf1 = findViewById(R.id.txtInf1);
        txtInf2 = findViewById(R.id.txtInf2);
        txtInf3 = findViewById(R.id.txtInf3);
        txtInf4 = findViewById(R.id.txtInf4);
        txtInf5 = findViewById(R.id.txtInf5);
        txtRisk1 = findViewById(R.id.txtRisk1);
        txtRisk2 = findViewById(R.id.txtRisk2);
        txtRisk3 = findViewById(R.id.txtRisk3);
        txtRisk4 = findViewById(R.id.txtRisk4);
        txtRisk5 = findViewById(R.id.txtRisk5);
        TextView[] cities = new TextView[]{txtCity1, txtCity2, txtCity3, txtCity4, txtCity5};
        TextView[] infected = new TextView[]{txtInf1, txtInf2, txtInf3, txtInf4, txtInf5};
        TextView[] risk = new TextView[]{txtRisk1, txtRisk2, txtRisk3, txtRisk4, txtRisk5};
        Intent i = getIntent();
        ID = i.getStringExtra("ID");

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        fusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(this);
        checkPermission();
        getProvinces();
        getCities(cities, infected, risk);
        getCases();
    }

    private void markProvinces(final LatLng latLng, String provName, int infected) {
        final String title = provName + ": " + Integer.toString(infected) + " cases";
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if (location != null){
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            MarkerOptions options = new MarkerOptions()
                                    .position(latLng)
                                    .title(title)
                                    .icon(BitmapDescriptorFactory.defaultMarker(
                                            BitmapDescriptorFactory.HUE_RED
                                    ));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            googleMap.addMarker(options);
                        }
                    });
                }
            }
        });
    }

    public void getProvinces(){
        PHPRequest req = new PHPRequest("https://lamp.ms.wits.ac.za/home/s2089236/");
        ContentValues cv = new ContentValues();
        req.doRequest(this, "saStats", cv,
                new RequestHandler() {
                    @Override
                    public void processResponse(String requests) {
                        processProvinces(requests);
                    }
                });
    }

    public void processProvinces(String json){
        try{
            JSONArray all = new JSONArray(json);
            for (int i = 0; i < all.length(); i++){
                final JSONObject item = all.getJSONObject(i);
                final String province = item.getString("PROVINCE");
                final int infected = item.getInt("INFECTED");
                final double lat = item.getDouble("LAT");
                final double lon = item.getDouble("LON");
                LatLng latLng = new LatLng(lat, lon);
                markProvinces(latLng, province, infected);
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getCities(final TextView[] cities, final TextView[] infected, final TextView[] risk){
        PHPRequest req = new PHPRequest("https://lamp.ms.wits.ac.za/home/s2089236/");
        ContentValues cv = new ContentValues();
        req.doRequest(this, "cityStats", cv,
                new RequestHandler() {
                    @Override
                    public void processResponse(String requests) {
                        processCities(requests, cities, infected, risk);
                    }
                });
    }

    public void processCities(String json, TextView[] cities, TextView[] infected, TextView[] risk){
        try{
            JSONArray all = new JSONArray(json);
            for (int i = 0; i < all.length(); i++){
                JSONObject item = all.getJSONObject(i);
                final String cityName = item.getString("CITY_NAME");
                final double percentage = item.getDouble("PERC");
                final String risklevel = item.getString("CITY_RISK");
                final TextView city = cities[i];
                final TextView cases = infected[i];
                final TextView riskLevel = risk[i];
                MainMenu.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        city.setText(cityName);
                        cases.setText(Double.toString(percentage) + "%");
                        riskLevel.setText(risklevel);
                    }
                });
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getCases(){
        PHPRequest req = new PHPRequest("https://lamp.ms.wits.ac.za/home/s2089236/");
        ContentValues cv = new ContentValues();
        req.doRequest(this, "totalCases", cv,
                new RequestHandler() {
                    @Override
                    public void processResponse(String requests) {
                        processCases(requests);
                    }
                });
    }

    public void processCases(String json){
        try{
            JSONArray all = new JSONArray(json);
            for (int i = 0; i < all.length(); i++){
                JSONObject item = all.getJSONObject(i);
                int cases = item.getInt("TOTAL_CASES");
                TextView txtCases = findViewById(R.id.txtCases);
                txtCases.setText(Integer.toString(cases));
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void checkPermission(){
        if (ActivityCompat.checkSelfPermission(MainMenu.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                getProvinces();
        }
        else{
            ActivityCompat.requestPermissions(MainMenu.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getProvinces();
            }
        }
    }

    public void ClickMenu(View view){
        openDrawer(drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void ClickLogo(View view){
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void ClickHome(View view){
        recreate();
    }

    public void ClickUserProfile(View view){
        redirectActivity(this, UserProfile.class, ID);
    }

    public void ClickLocationQuery(View view){
        redirectActivity(this, LocationQuery.class, ID);
    }

    public void ClickLocationHistory(View view){
        redirectActivity(this, LocationHistory.class, ID);
    }

    public void ClickLogout(View view){
        logout(this);
    }

    public static void logout(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to log out?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finishAffinity();
                System.exit(0);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    public static void redirectActivity(Activity activity, Class aClass, String ID){
        Intent intent = new Intent(activity, aClass);
        intent.putExtra("ID", ID);
        activity.startActivity(intent);
    }
}