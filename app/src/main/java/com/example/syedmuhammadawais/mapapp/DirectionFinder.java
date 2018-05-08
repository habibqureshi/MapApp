package com.example.syedmuhammadawais.mapapp;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class DirectionFinder {

    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyBhnB1X8iTjqIVH0h72JYVzfiYEQdRA48s";
    private DirectionFinderListener listener;
    private double origin_lat,origin_longi;
    private double destination_lat,destination_longi;
    private Context context;

    public DirectionFinder(DirectionFinderListener listener,Context context, double origin_lat,double origin_longi, double dest_lat,double dest_longi) {
        this.listener = listener;
        this.origin_lat = origin_lat;
        this.origin_longi=origin_longi;
        this.destination_lat = dest_lat;
        this.destination_longi=dest_longi;
        this.context=context;

    }

    public void execute() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();
        send_url_to_get_json(createUrl());
    }

    private String createUrl() throws UnsupportedEncodingException {

        return DIRECTION_URL_API + "origin=" + origin_lat+","+origin_longi + "&destination=" + destination_lat+","+destination_longi + "&key=" + GOOGLE_API_KEY;
    }
   private  void send_url_to_get_json(String URL)
   {
       Context context;
       RequestQueue queue = Volley.newRequestQueue( this.context );
       JsonObjectRequest getRequest = new JsonObjectRequest( Request.Method.GET,URL, null,
               new Response.Listener<JSONObject>() {
                   @Override
                   public void onResponse(JSONObject response) {
                       Log.d( "", response.toString() );
                       try {
                          parseJSon( response.toString() );
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                   }
               },
               new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {

                       Log.d( "", error.toString() );


                   }
               }

       );

       queue.add( getRequest );

   }
    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;

        List<Route> routes = new ArrayList<Route>();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");
        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            Route route = new Route();

            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
            JSONObject jsonLeg = jsonLegs.getJSONObject(0);
            JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
            JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");
            route.distance = new Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
            route.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
          //  route.endAddress = jsonLeg.getString("end_address");
           // route.startAddress = jsonLeg.getString("start_address");
            route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
            route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
            route.points = decodePolyLine(overview_polylineJson.getString("points"));

            routes.add(route);
        }

        listener.onDirectionFinderSuccess(routes);
    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }
}