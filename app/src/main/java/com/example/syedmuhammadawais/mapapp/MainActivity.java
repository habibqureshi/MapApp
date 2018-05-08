package com.example.syedmuhammadawais.mapapp;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.example.syedmuhammadawais.mapapp.R.id.map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private PlaceAutocompleteFragment input_location;
    Button Search_Button;
    double LAT_POINT, LONG_POINT;
    JSONArray items;
    JSONObject geomtry;
    JSONObject location;
    private GoogleMap mMap = null;
    private StartAppAd startAppAd = new StartAppAd(this);
    String AdmobAppID="ca-app-pub-3940256099942544/1033173712";
    InterstitialAd mInterstitialAd;//declare these
    private AdView mAdView;//declare these
    AdRequest adRequestFull;//declare these

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        mInterstitialAd = new InterstitialAd(MainActivity.this);
        mInterstitialAd.setAdUnitId((AdmobAppID));
        adRequestFull = new AdRequest.Builder()
                .build();
        AdRequest adRequest= new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);

        // copy this before setcontentView in on create mothod

        setContentView(R.layout.activity_main);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
       input_location =(PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.locatiofind);
        input_location.setHint("Enter Location");
        input_location.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                //  mMap.addMarker(new MarkerOptions()
                //        .position(place.getLatLng())
                //      .title("My Location"));
                LatLng Source1 = place.getLatLng();

                // SourceName = (String) place.getName();

                LONG_POINT = Source1.longitude;
                LAT_POINT = Source1.latitude;
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Source1, 18));
                Validation();

            }

            @Override
            public void onError(Status status) {

            }
        });



    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    public  void Validation() {
        if (LONG_POINT == 0.0 && LAT_POINT == 0.0) {
            Toast.makeText(getApplicationContext(),"Something Went Wrong Please Type Location Again",Toast.LENGTH_LONG).show();

            input_location.setText("");
        }
        else
        {
            mMap.clear();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(LAT_POINT,LONG_POINT), 16);
            mMap.animateCamera(cameraUpdate);
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(LAT_POINT,LONG_POINT))
                    .title("Searched Result"));
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        else {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);

        }
        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat,longi), 16);
        //mMap.animateCamera(cameraUpdate);


    }
}
