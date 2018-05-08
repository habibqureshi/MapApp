package com.example.syedmuhammadawais.mapapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;


public class Make_Route extends AppCompatActivity implements OnMapReadyCallback, DirectionFinderListener, View.OnClickListener {

    private GoogleMap mMap = null;
    static double lat, longi, f_lat, f_long;
    private List<Marker> originMarkers = new ArrayList<>( );
    private List<Marker> destinationMarkers = new ArrayList<>( );
    private List<Polyline> polylinePaths = new ArrayList<>( );
    TextView duration, distance;
    private GoogleApiClient client;
    private PlaceAutocompleteFragment Source;
    private PlaceAutocompleteFragment Dest;
    Button  GO_Back;
    JSONArray items;
    JSONObject geomtry;
    JSONObject location;
    double lattitude,longitute;
    LatLng Source1;
    String Error="";
    LatLng Dest1;
    ImageButton b1,b2,b3;
    LocationListener locationListener;
    LocationManager locationManager;
    ProgressDialog progress,progress2=null;
    ImageButton location_button;
    String addressStr = "";
    Geocoder myLocation;
    List<Address> myList = null;
    Address address;
    AlertDialog.Builder builder;
    ProgressDialog progressDialog;
    Runnable myRunnable;
    ExecutorService threadPoolExecutor;
    private StartAppAd startAppAd = new StartAppAd(this);
    String AdmobAppID="ca-app-pub-3940256099942544/1033173712";
    InterstitialAd mInterstitialAd;//declare these
    private AdView mAdView;//declare these
    AdRequest adRequestFull;//declare these

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInterstitialAd = new InterstitialAd(Make_Route.this);
        mInterstitialAd.setAdUnitId((AdmobAppID));
        adRequestFull = new AdRequest.Builder()
                .build();
        AdRequest adRequest= new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);


        setContentView(R.layout.check);

        String s = getIntent().getStringExtra("LAT");
        String s1 = getIntent().getStringExtra("LONG");
        lat=Double.valueOf(s);
        longi=Double.valueOf(s1);
        Source = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.fsource);
        Dest = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.fdestination);
        b1 = (ImageButton) findViewById(R.id.terrin);
        b2 = (ImageButton) findViewById(R.id.stelite);
        b3 = (ImageButton) findViewById(R.id.street);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        Source.setHint("Your Location..");
        Dest.setHint("Enter Destination");
        Source.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Source1 = place.getLatLng();
                longi = Source1.longitude;
                lat = Source1.latitude;
            }

            @Override
            public void onError(Status status) {

            }
        });
        Dest.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Dest1 = place.getLatLng();
                f_long = Dest1.longitude;
                f_lat = Dest1.latitude;
            }
            @Override
            public void onError(Status status) {

            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        boolean Is_Location_On = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Button search_button=(Button) findViewById(R.id.search_button);
        search_button.setOnClickListener(this);


       /* myLocation = new Geocoder(Make_Route.this, Locale.getDefault());
        progressDialog =new ProgressDialog(this);
        progressDialog.setTitle("Wait");
        progressDialog.setMessage("Please Wait We Are Fetching Your Address");
        progressDialog.setCancelable(false);
        threadPoolExecutor = Executors.newSingleThreadExecutor();
        Handler mainHandler = new Handler(this.getMainLooper());

         myRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    progressDialog.show();
                    myList = myLocation.getFromLocation(lat,longi, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (check_network()) {
                    List<Address> addresses = null;

                    try {
                        addresses = myLocation.getFromLocation(
                                lat,
                                longi,
                                // In this sample, get just a single address.
                                1);
                    } catch (IOException ioException) {
                        // Catch network or other I/O problems.
                        Log.e("", "Error", ioException);
                    } catch (IllegalArgumentException illegalArgumentException) {
                        // Catch invalid latitude or longitude values.
                        Error = "ERROR";
                        Log.e("", Error + ". " +
                                "Latitude = " + lat+
                                ", Longitude = " +
                                longi, illegalArgumentException);
                    }

                    // Handle case where no address was found.
                    if (addresses == null || addresses.size()  == 0) {
                        if (Error.isEmpty()) {
                            Error = "No Address Found";
                            Log.e("",Error);
                        }
                        } else {
                        Address address = addresses.get(0);

                        // Fetch the address lines using getAddressLine,
                        // join them, and send them to the thread.
                        for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                            addressStr=addressStr+address.getAddressLine(i)+",";
                        }
                        Log.i("", "Found");
                      }
                    Source.setText(addressStr);
                    progressDialog.dismiss();
                    Future longRunningTaskFuture = threadPoolExecutor.submit(myRunnable);
                    longRunningTaskFuture.cancel(true);

                }
            } // This is your code
        };
        mainHandler.post(myRunnable);*/

    }


  /*  private void get_my_location() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                ActivityCompat.requestPermissions(Make_Route.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 12);

            }
        } else {
            Get_User_Location();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if(requestCode== 12 && grantResults[0]!= PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION}, 12);
            }

        }
        else {
            Get_User_Location();
        }
            // other 'case' statements for other permssions

    }*/

    public  void Validation()
    {
        if (longi != 0.0 && f_long != 0.0) {

                    make_polylines();

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
        } else {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            // moveMapToMyLocation();


        }


    }


    @Override
    protected void onResume() {

        super.onResume();





    }

    @Override
    protected void onDestroy() {
        super.onDestroy( );
       }


    @Override
    public void onDirectionFinderStart() {
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();

            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();

            }
        }
    }

    // After Getting Direction We are drawing polylines on the given route.
    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {

           originMarkers.add( mMap.addMarker( new MarkerOptions()
                    .title( route.startAddress )
                    .position( route.startLocation ) ) );
            destinationMarkers.add( mMap.addMarker( new MarkerOptions()
                    .title( route.endAddress )
                    .position( route.endLocation ) ) );

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic( true ).
                    color(Color.parseColor("#00a7aa") ).
                    width( 10 );

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add( route.points.get( i ) );
            progress.dismiss();
            polylinePaths.add( mMap.addPolyline( polylineOptions ) );
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(new LatLng(lat,longi));
            builder.include(new LatLng(f_lat,f_long));
            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
        }
    }
    public void make_polylines() {
        progress = new ProgressDialog(Make_Route.this);
        progress.setTitle("Making Route");
        progress.setMessage("Fetching Route Please Wait For A While...");
        progress.setCancelable(true);
        progress.show();
        try {
            new DirectionFinder( this, this, lat, longi, f_lat, f_long ).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //mMap.clear();

        return;
    }
    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.terrin:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                Toast.makeText(getApplication(),"Change to Terrain",Toast.LENGTH_LONG).show();
                break;
            case R.id.stelite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                Toast.makeText(getApplication(),"Change to satellite",Toast.LENGTH_LONG).show();
                break;
            case R.id.street:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                Toast.makeText(getApplication(),"Change to Normal",Toast.LENGTH_LONG).show();
                break;
            case R.id.search_button:
                Validation();
                break;
        }
    }

    public boolean check_network() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)this. getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12) {
            boolean Is_Location_On = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!Is_Location_On) {
                Toast.makeText(getApplicationContext(), "You Can Not Use That Function Without Location Enabled Try Again ", Toast.LENGTH_LONG).show();
                finish();
            }
            else {
                get_my_location();
            }

            }
        }*/



}