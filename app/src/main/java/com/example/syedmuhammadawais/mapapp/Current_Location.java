package com.example.syedmuhammadawais.mapapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import static com.example.syedmuhammadawais.mapapp.R.id.map;

public class Current_Location extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap = null;
    LocationListener locationListener;
    LocationManager locationManager;
    private StartAppAd startAppAd = new StartAppAd(this);
    InterstitialAd mInterstitialAd;//declare these
    private AdView mAdView;//declare these
    AdRequest adRequestFull;//declare these
    String AdmobAppID="ca-app-pub-3940256099942544/1033173712"; // change here admob ID


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInterstitialAd = new InterstitialAd(Current_Location.this);
        mInterstitialAd.setAdUnitId((AdmobAppID));
        adRequestFull = new AdRequest.Builder()
                .build();
        AdRequest adRequest= new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);


        setContentView(R.layout.activity_current__location);


        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        boolean Is_Location_On = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!Is_Location_On) {
            Toast.makeText(getApplicationContext(), "Please Enable Your Location", Toast.LENGTH_LONG).show();
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 12);

        } else {

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(map);
            mapFragment.getMapAsync(this);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12) {
            boolean Is_Location_On = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!Is_Location_On) {
                Toast.makeText(getApplicationContext(),"You Can Not Use That Function Without Location Enabled Try Again ",Toast.LENGTH_LONG).show();
                finish();


            } else {
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(map);
                mapFragment.getMapAsync(this);
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        else
            startAppAd.showAd(this);
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Current_Location.this,  new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);


        } else {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
           // moveMapToMyLocation();


        }
        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat,longi), 16);
        //mMap.animateCamera(cameraUpdate);

    }

    private void moveMapToMyLocation() {

        LocationManager locMan = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Location loc=null;

        Criteria crit = new Criteria();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                ActivityCompat.requestPermissions(Current_Location.this,  new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }
        }
        else {

            loc = locMan.getLastKnownLocation(locMan.getBestProvider(crit, false));


            CameraPosition camPos = new CameraPosition.Builder()

                    .target(new LatLng(loc.getLatitude(), loc.getLongitude()))

                    .zoom(16)

                    .build();

            CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(camPos);

            mMap.moveCamera(camUpdate);
        }



    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                  moveMapToMyLocation();
                }
                else {
                    ActivityCompat.requestPermissions(Current_Location.this,  new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                }
                return;
            }

            // other 'case' statements for other permssions
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
