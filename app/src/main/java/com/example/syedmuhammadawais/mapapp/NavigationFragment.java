package com.example.syedmuhammadawais.mapapp;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationFragment extends Fragment {
    LinearLayout RouteFinder,MyLocation,DrivingRoute,FavPlaces,FrindLocation,ShareLocation,_2DRoute,Recomended;
    View Layout;
    LocationManager locationManager;
    double lon, lat;
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
    Functions_Activity activity;
    int showAdd=0;
    InterstitialAd mInterstitialAd;//declare these
    private AdView mAdView;//declare these
    AdRequest adRequestFull;//declare these

    // test ids
    String AdmobAppID="ca-app-pub-3940256099942544/1033173712";



    public NavigationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Functions_Activity)
            this.activity= (Functions_Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        Layout=inflater.inflate(R.layout.fragment_navigation, container, false);
        findByID();
        init();
        return Layout;
    }

    private void findByID(){
        RouteFinder= (LinearLayout) Layout.findViewById(R.id.route_finder);
        MyLocation= (LinearLayout) Layout.findViewById(R.id.mylocation);
        DrivingRoute= (LinearLayout) Layout.findViewById(R.id.drivingRoute);

        FrindLocation= (LinearLayout) Layout.findViewById(R.id.FriendsLocation);
        ShareLocation= (LinearLayout) Layout.findViewById(R.id.shareLocation);
        _2DRoute= (LinearLayout) Layout.findViewById(R.id._2dRoute);
        Recomended= (LinearLayout) Layout.findViewById(R.id.recomended);


    }
    private void init(){
        RouteFinder.setOnClickListener(onClick);
        MyLocation.setOnClickListener(onClick);
        DrivingRoute.setOnClickListener(onClick);

        FrindLocation.setOnClickListener(onClick);
        ShareLocation.setOnClickListener(onClick);
        _2DRoute.setOnClickListener(onClick);
        Recomended.setOnClickListener(onClick);
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId((AdmobAppID));
        adRequestFull = new AdRequest.Builder()
                .build();
        AdRequest adRequest= new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);


       // RouteFinder.setClickable(true);
        //RouteFinder.setBackgroundResource(android.R.drawable.list_selector_background);
    }
    View.OnClickListener onClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            showAdd++;
//            if(showAdd%2==0){
//               activity.showAdd();
//            }
//            else
            switch (v.getId()) {
                case R.id.route_finder:
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                    else
                        findRoute();

                    mInterstitialAd.setAdListener(new AdListener() {

                        @Override
                        public void onAdClosed() {
                            findRoute();
                            AdRequest adRequest= new AdRequest.Builder().build();
                            mInterstitialAd.loadAd(adRequest);
                            super.onAdClosed();
                        }
                    });
                    break;
                case R.id.mylocation:
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                    else
                        mylocation();

                    mInterstitialAd.setAdListener(new AdListener() {

                        @Override
                        public void onAdClosed() {
                            mylocation();
                            AdRequest adRequest= new AdRequest.Builder().build();
                            mInterstitialAd.loadAd(adRequest);
                            super.onAdClosed();
                        }
                    });

                    break;
                case R.id.drivingRoute:
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                    else
                        showDailog();

                    mInterstitialAd.setAdListener(new AdListener() {

                        @Override
                        public void onAdClosed() {
                            showDailog();
                            AdRequest adRequest= new AdRequest.Builder().build();
                            mInterstitialAd.loadAd(adRequest);
                            super.onAdClosed();
                        }
                    });


                    break;
                case R.id.favPlaces:

                    break;
                case R.id.FriendsLocation:
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                    else
                        friendsLocation();

                    mInterstitialAd.setAdListener(new AdListener() {

                        @Override
                        public void onAdClosed() {
                            friendsLocation();
                            AdRequest adRequest= new AdRequest.Builder().build();
                            mInterstitialAd.loadAd(adRequest);
                            super.onAdClosed();
                        }
                    });


                    break;
                case R.id.shareLocation:
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                    else
                        sharelocation();

                    mInterstitialAd.setAdListener(new AdListener() {

                        @Override
                        public void onAdClosed() {
                            sharelocation();
                            AdRequest adRequest= new AdRequest.Builder().build();
                            mInterstitialAd.loadAd(adRequest);
                            super.onAdClosed();
                        }
                    });

                    break;
                case R.id._2dRoute:
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                    else
                        route();

                    mInterstitialAd.setAdListener(new AdListener() {

                        @Override
                        public void onAdClosed() {
                            route();
                            AdRequest adRequest= new AdRequest.Builder().build();
                            mInterstitialAd.loadAd(adRequest);
                            super.onAdClosed();
                        }
                    });




                    break;
                case R.id.recomended:
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    }
                    else
                        recomended();

                    mInterstitialAd.setAdListener(new AdListener() {

                        @Override
                        public void onAdClosed() {
                            recomended();
                            AdRequest adRequest= new AdRequest.Builder().build();
                            mInterstitialAd.loadAd(adRequest);
                            super.onAdClosed();
                        }
                    });


                    break;
            }

        }
    };

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }
    void showDailog(){
        Dialog d = new Dialog(getActivity());
        d.setContentView(R.layout.dialog_custom);
        Button go = (Button) d.findViewById(R.id.go);
        final EditText value= (EditText) d.findViewById(R.id.place);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(value.getText().toString().length()>0) {
                    value.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    if (appInstalledOrNot("com.google.android.apps.maps")) {
                        Log.e("Mapapp", "install");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + value.getText().toString()));
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    } else {
                        if (activity.getlocationOfUser() != null) {

                            Intent nextActivity3 = new Intent(activity, Make_Route.class);
                            nextActivity3.putExtra("LAT", String.valueOf(activity.getlocationOfUser().getLatitude()));
                            nextActivity3.putExtra("LONG", String.valueOf(activity.getlocationOfUser().getLongitude()));
                            startActivity(nextActivity3);

                        } else {
                            activity.getLocation(activity);
                            Toast.makeText(activity, "Please Wait While We Are Getting Your Location", Toast.LENGTH_LONG).show();

                        }

                    }
                }
                else
                    Toast.makeText(activity,"cant be empty",Toast.LENGTH_LONG).show();




            }
        });
        d.show();
        d.setCanceledOnTouchOutside(false);


    }


public void findRoute(){
    if (appInstalledOrNot("com.google.android.apps.maps")) {
        Log.e("Mapapp", "install");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    } else {
        Intent nextActivity1 = new Intent(getActivity(), MainActivity.class);
        startActivity(nextActivity1);
    }
}

public void mylocation(){
    if(activity.getlocationOfUser()!=null) {


        if (appInstalledOrNot("com.google.android.apps.maps")) {
            Log.e("Mapapp", "install");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"+activity.getlocationOfUser().getLatitude()+ ","+activity.getlocationOfUser().getLongitude()+"?q="+activity.getlocationOfUser().getLatitude()+ ","+activity.getlocationOfUser().getLongitude()+"(You are here)?z=10"));
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        } else {

            Intent nextActivity2 = new Intent(getActivity(), Current_Location.class);
            startActivity(nextActivity2);
        }
    }
    else {
        activity.getLocation(activity);
        Toast.makeText(activity, "Please Wait While We Are Getting Your Location", Toast.LENGTH_LONG).show();


    }
}
public void friendsLocation(){
    try {
        Intent nextActivity4 =  new Intent(Intent.ACTION_SEND);
        nextActivity4.setType("text/plain");
        nextActivity4.putExtra(Intent.EXTRA_SUBJECT, "MapApp");
        String sAux = "\nInstall this app to share your location with me\n\n";
        sAux = sAux + "https://play.google.com/store/apps/details?id=Orion.Soft \n\n";
        nextActivity4.putExtra(Intent.EXTRA_TEXT, sAux);
        startActivity(Intent.createChooser(nextActivity4, "choose one"));


    } catch (Exception e) {
        //e.toString();
    }
}
public void sharelocation(){
    if (activity.getlocationOfUser() != null) {

        String uri = "http://maps.google.com/maps?saddr=" + activity.getlocationOfUser().getLatitude() + "," + activity.getlocationOfUser().getLongitude();
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String ShareSub = "Here is my location";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    } else {
        activity.getLocation(activity);
        Toast.makeText(activity, "Please Wait While We Are Getting Your Location", Toast.LENGTH_LONG).show();
    }
}
public void recomended(){
    try {
        Intent nextActivity4 =  new Intent(Intent.ACTION_SEND);
        nextActivity4.setType("text/plain");
        nextActivity4.putExtra(Intent.EXTRA_SUBJECT, "MapApp");
        String sAux = "\nLet me recommend you this application which is best application for finding locations, Please view and download the app from the link below\n\n";
        sAux = sAux + "https://play.google.com/store/apps/details?id=Orion.Soft \n\n";
        nextActivity4.putExtra(Intent.EXTRA_TEXT, sAux);
        startActivity(Intent.createChooser(nextActivity4, "choose one"));


    } catch (Exception e) {
        //e.toString();
    }
}
public void route(){
        if (activity.getlocationOfUser() != null) {



            Intent nextActivity3 = new Intent(activity, Make_Route.class);
            nextActivity3.putExtra("LAT", String.valueOf(activity.getlocationOfUser().getLatitude()));
            nextActivity3.putExtra("LONG", String.valueOf(activity.getlocationOfUser().getLongitude()));
            startActivity(nextActivity3);


        }
        else{
            Toast.makeText(activity, "Please Wait While We Are Getting Your Location", Toast.LENGTH_LONG).show();
            activity.getLocation(activity);
        }
    }
}
