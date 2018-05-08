package com.example.syedmuhammadawais.mapapp;

import android.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

public class Functions_Activity extends AppCompatActivity implements LocationListener, OnCompleteListener<Location> {

    RelativeLayout findlocation, mylocation, get_direction, sharebutton, sharelocation, ratting, moreApps;
    Location loc = null;
    LocationManager locationManager;
    double lon = 0.0, lat = 0.0;
    LocationListener locationListener;
    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    Location location; // location
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    // declare this
    private StartAppAd startAppAd = new StartAppAd(this);
    String StartAppID = "204012655"; // change here StartApp ID
    InterstitialAd mInterstitialAd;//declare these
    private AdView mAdView;//declare these
    AdRequest adRequestFull;//declare these
    String AdmobAppID = "ca-app-pub-3940256099942544/1033173712";
    ViewPager pager;
    TabLayout tab;
    private MainActivityPagerAdapter pagerAdapter;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager manager;
    private Task<Location> mLastLocation;
    private String TAG = "";
    private Location currentLocation;
    private int showAdd = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StartAppSDK.init(this, StartAppID,false);
//        StartAppAd.disableSplash();
//
//
//
        setContentView(R.layout.activity_functions_);


        manager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        pager = (ViewPager) findViewById(R.id.pager);
        this.tab = (TabLayout) findViewById(R.id.tabs);
        pagerAdapter = new MainActivityPagerAdapter(getSupportFragmentManager(), this);
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(this.tab));
        this.tab.setupWithViewPager(pager);
        mInterstitialAd = new InterstitialAd(Functions_Activity.this);
        mInterstitialAd.setAdUnitId((AdmobAppID));
        adRequestFull = new AdRequest.Builder()
                .build();
        AdRequest adRequest= new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
//        findlocation = (RelativeLayout) findViewById(R.id.findlocation_button);
//        mylocation = (RelativeLayout) findViewById(R.id.mylocationbutton);
//        get_direction = (RelativeLayout) findViewById(R.id.directionbutton);
//        sharebutton = (RelativeLayout) findViewById(R.id.sharebutton);
//        sharelocation = (RelativeLayout) findViewById(R.id.share_location);
//        moreApps = (RelativeLayout) findViewById(R.id.more_apps);
//        ratting = (RelativeLayout) findViewById(R.id.rate_us);
//        findlocation.setOnClickListener(this);
//        mylocation.setOnClickListener(this);
//        get_direction.setOnClickListener(this);
//        sharebutton.setOnClickListener(this);
//        sharelocation.setOnClickListener(this);
//        moreApps.setOnClickListener(this);
//        ratting.setOnClickListener(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);






    }
    public void navigation(final View v){

       // showAdd++;
        Log.e("add",showAdd+"");


//        if(showAdd%2==0){
//            this.showAdd();
//        }
//        else{

        if(!this.showAdd())
            nearByintent(v);
        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdClosed() {
                nearByintent(v);
                AdRequest adRequest= new AdRequest.Builder().build();
                mInterstitialAd.loadAd(adRequest);
                super.onAdClosed();
            }
        });



    }

    public void nearByintent(View v){
        Intent mapIntent;
        if(this.getlocationOfUser()!=null){
            mapIntent = new Intent(Intent.ACTION_VIEW,  Uri.parse("geo:"+this.getlocationOfUser().getLatitude()+","+this.getlocationOfUser().getLongitude()+"?q="+ v.getContentDescription().toString()));

        }
        else
            mapIntent = new Intent(Intent.ACTION_VIEW,  Uri.parse("geo:0.0,0.0?q="+v.getContentDescription().toString()));

        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean Is_Location_On = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!Is_Location_On) {
            Toast.makeText(getApplicationContext(), "Please Enable Your Location", Toast.LENGTH_LONG).show();
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 12);

        } else {
            getLocation(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12) {
            boolean Is_Location_On = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!Is_Location_On) {
                Toast.makeText(getApplicationContext(), "You Can Not Use That Function Without Location Enabled Try Again ", Toast.LENGTH_LONG).show();
                finish();
            } else {
                get_my_location();
            }
        }
    }


//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.findlocation_button:
//                if(appInstalledOrNot("com.google.android.apps.maps")){
//                    Log.e("Mapapp","install");
//                    Intent mapIntent = new Intent(Intent.ACTION_VIEW);
//                    mapIntent.setPackage("com.google.android.apps.maps");
//                    startActivity(mapIntent);
//                }
//                else{
//                    Intent nextActivity1 = new Intent(this, MainActivity.class);
//                    startActivity(nextActivity1);
//                }
//
//
//
//                break;
//
//            case R.id.mylocationbutton:
//                if(appInstalledOrNot("com.google.android.apps.maps")){
//                    Log.e("Mapapp","install");
//                    Intent mapIntent = new Intent(Intent.ACTION_VIEW);
//                    mapIntent.setPackage("com.google.android.apps.maps");
//                    startActivity(mapIntent);
//                }
//                else {
//
//                    Intent nextActivity2 = new Intent(this, Current_Location.class);
//                    startActivity(nextActivity2);
//                }
//
//                break;
//
//            case R.id.directionbutton:
//                if(appInstalledOrNot("com.google.android.apps.maps")){
//                    Log.e("Mapapp","install");
//
//                    Intent mapIntent =  new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:"+lat+","+lon));
//                    mapIntent.setPackage("com.google.android.apps.maps");
//                    startActivity(mapIntent);
//                }
//                else {
//
//
//                    if (lat != 0.0 && lon != 0.0) {
//
//                        Intent nextActivity3 = new Intent(this, Make_Route.class);
//                        nextActivity3.putExtra("LAT", String.valueOf(lat));
//                        nextActivity3.putExtra("LONG", String.valueOf(lon));
//                        startActivity(nextActivity3);
//
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Please Wait While We Are Getting Your Location", Toast.LENGTH_LONG).show();
//                    }
//                }
//                break;
//
//            case R.id.sharebutton:
//
//                try {
//                    Intent nextActivity4 =  new Intent(Intent.ACTION_SEND);
//                    nextActivity4.setType("text/plain");
//                    nextActivity4.putExtra(Intent.EXTRA_SUBJECT, "MapApp");
//                    String sAux = "\nLet me recommend you this application which is best application for finding locations, Please view and download the app from the link below\n\n";
//                    sAux = sAux + "https://play.google.com/store/apps/details?id=Orion.Soft \n\n";
//                    nextActivity4.putExtra(Intent.EXTRA_TEXT, sAux);
//                    startActivity(Intent.createChooser(nextActivity4, "choose one"));
//
//
//                } catch (Exception e) {
//                    //e.toString();
//                }
//
//                break;
//
//            case R.id.share_location:
//                if (mInterstitialAd.isLoaded()) {
//                    mInterstitialAd.show();
//                }
//                else
//                    startAppAd.showAd(this);
//                if (lat != 0.0 && lon != 0.0) {
//
//                    String uri = "http://maps.google.com/maps?saddr=" + lat + "," + lon;
//                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//                    sharingIntent.setType("text/plain");
//                    String ShareSub = "Here is my location";
//                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
//                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
//                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
//                } else {
//                    Toast.makeText(getApplicationContext(), "Please Wait While We Are Getting Your Location", Toast.LENGTH_LONG).show();
//                }
//                break;
//            case R.id.more_apps:
//                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
//                try {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//                } catch (android.content.ActivityNotFoundException anfe) {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//                }
//                break;
//            case R.id.rate_us:
//                final String appPackageName2 = getPackageName(); // getPackageName() from Context or Activity object
//                try {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName2)));
//                } catch (android.content.ActivityNotFoundException anfe) {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName2)));
//                }
//                break;
//
//        }
//    }

    private void get_my_location() {
        Log.e("T:", "get_my_location");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= 16) {
                Log.e("T:", "Requesting Location");
                ActivityCompat.requestPermissions(Functions_Activity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }

        } else {
            Get_User_Location();
            Log.e("T:", "inside get_my_location get_user_location called");
        }


    }


    public void getLocation(OnCompleteListener<Location> listener) {
        if (this.checkLocationAppPermission()) {
            if (this.isGPSEnable()) {

                mLastLocation = fusedLocationProviderClient.getLastLocation();
                mLastLocation.addOnCompleteListener(listener);

            } else
                displayLocationSettingsRequest();
        } else this.requestLocationAppPermission();

    }

    private void displayLocationSettingsRequest() {
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
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
    }

    public boolean checkLocationAppPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }
    public boolean isGPSEnable() {
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    public void requestLocationAppPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                99);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 99: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                       // M.l("permission granted");

                    }

                } else {
                    //M.t(this, "Permission Require");
                    this.requestLocationAppPermission();
                }
                return;
            }
        }
    }
    public Location getlocationOfUser(){
        return currentLocation;
    }

    public void Get_User_Location() {

        Log.e("T:", "In Get Location");

      /*  locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,this);
        Log.e("T:", "In Get Location");*/

        // getting GPS status
        try{
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            // no network provider is enabled
        } else {
            this.canGetLocation = true;
            // First get location from Network Provider
            if (isNetworkEnabled) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                }
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        0,
                        0, this);

                Log.d("Network", "Network");
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    if (location != null) {
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                    }
                }
            }

            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    Log.d("GPS Enabled", "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (location != null) {
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }
                }
            }
        }

    } catch(
    Exception e)

    {
        e.printStackTrace();
    }
}


    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(Functions_Activity.this);
// Add the buttons
        builder.setTitle("Wait");
        builder.setMessage("Do You Want To Quit?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
// Set other dialog properties
// Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
        //super.onBackPressed();

    }
    public double getLat(){
        return this.lat;
    }
    public double getLong(){
        return this.lon;
    }

    @Override
    public void onLocationChanged(Location location) {

        lon=location.getLongitude();
        lat=location.getLatitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onComplete(@NonNull Task<Location> task) {
        if (task.isSuccessful()) {
             currentLocation=task.getResult();

        }

    }

//    private boolean appInstalledOrNot(String uri) {
//        PackageManager pm = getPackageManager();
//        try {
//            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
//            return true;
//        } catch (PackageManager.NameNotFoundException e) {
//        }
//
//        return false;
//    }

    class MainActivityPagerAdapter extends FragmentStatePagerAdapter {
        String []tittles={"Navigation","NearBy"};
        Functions_Activity context;
        Fragment fragment;

        public MainActivityPagerAdapter(FragmentManager fm, Functions_Activity activity) {

            super(fm);
            this.context=activity;
            /*tittles=context.getResources().getStringArray(R.array.tittles_name);*/
        }

        @Override
        public Fragment getItem(int position) {


            switch (position) {

                case 0: {
                    fragment = new NavigationFragment();


                    break;
                }

                case 1: {



                    fragment = new NearByFragment();


                    break;
                }


                default: {

                    fragment= new NavigationFragment();
                }
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return this.tittles[position];
        }
    }
    public boolean showAdd(){
        if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    return true;
                }
                return false;


    }
}
