package com.example.diseasetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
//TODO: Logging user location if not at home (check if home)
public class LocationHistory extends AppCompatActivity {

    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView textView1, textView2, textView3, textView4, textView5;
    String ID, address;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_history);

        drawerLayout = findViewById(R.id.drawer_layout);
        Intent i = getIntent();
        ID = i.getStringExtra("ID");

        textView1 = findViewById(R.id.text_view1);
        textView2 = findViewById(R.id.text_view2);
        textView3 = findViewById(R.id.text_view3);
        textView4 = findViewById(R.id.text_view4);
        textView5 = findViewById(R.id.text_view5);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.google_map);

        fusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(this);
        checkPermission();

        SearchableSpinner areas = findViewById(R.id.spAreas);
        areas.setTitle("Select Area");
        areas.setPositiveButton("OK");

        Button btnLog = findViewById(R.id.btnLogLocation);
        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String area = getArea();
                logLocation(area, address);
            }
        });

        Button btnHistory = findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LocationHistory.this, HistoryLog.class);
                i.putExtra("ID", ID);
                startActivity(i);
            }
        });
    }

    private void logLocation(String area, String address) {
        PHPRequest req = new PHPRequest("https://lamp.ms.wits.ac.za/home/s2089236/");
        ContentValues cv = new ContentValues();
        cv.put("id", ID);
        cv.put("address", address);
        cv.put("location", area);
        req.doRequest(this, "logLocation", cv,
                new RequestHandler() {
                    @Override
                    public void processResponse(String requests) {
                        LocationHistory.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LocationHistory.this,
                                        "Your location has been logged",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
    }

    private void getLocation() {
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if (location != null){
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            LatLng latLng = new LatLng(location.getLatitude(),
                                    location.getLongitude());
                            MarkerOptions options = new MarkerOptions()
                                    .position(latLng)
                                    .title("You are here now")
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

    public void checkPermission(){
        if (ActivityCompat.checkSelfPermission(LocationHistory.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getLocation();
            getCoordinates();
        }
        else{
            ActivityCompat.requestPermissions(LocationHistory.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
    }

    private void getCoordinates() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //Initialize location
                Location location = task.getResult();
                if (location != null){
                    try {
                        //Initialize geocoder
                        Geocoder geocode = new Geocoder(LocationHistory.this,
                                Locale.getDefault());
                        //Initialize address list
                        List<Address> addresses = geocode.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );
                        //Set latitude on TextView
                        textView1.setText(Html.fromHtml(
                                "<font color='#6200EE'><b>Latitude :</b><br></font>"
                                        + addresses.get(0).getLatitude()
                        ));
                        //Set longitude on TextView
                        textView2.setText(Html.fromHtml(
                                "<font color='#6200EE'><b>Longitude :</b><br></font>"
                                        + addresses.get(0).getLongitude()
                        ));
                        textView3.setText(Html.fromHtml(
                                "<font color='#6200EE'><b>Country Name :</b><br></font>"
                                        + addresses.get(0).getCountryName()
                        ));
                        //Set Locality
                        textView4.setText(Html.fromHtml(
                                "<font color='#6200EE'><b>Locality :</b><br></font>"
                                        + addresses.get(0).getLocality()
                        ));
                        //Set Address
                        textView5.setText(Html.fromHtml(
                                "<font color='#6200EE'><b>Address :</b><br></font>"
                                        + addresses.get(0).getAddressLine(0)
                        ));
                        address = addresses.get(0).getAddressLine(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocation();
                getCoordinates();
            }
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

    public void ClickRisk(View view){
        MainMenu.redirectActivity(this, LocationQuery.class, ID);
    }

    public void ClickLocationQuery(View view){
        MainMenu.redirectActivity(this, LocationQuery.class, ID);
    }

    public void ClickLocationHistory(View view){
        recreate();
    }

    public void ClickLogout(View view){
        MainMenu.logout(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainMenu.closeDrawer(drawerLayout);
    }

    public String getArea(){
        SearchableSpinner areas = findViewById(R.id.spAreas);
        return areas.getSelectedItem().toString();
    }
}