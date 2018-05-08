package com.example.syedmuhammadawais.mapapp;

/**
 * Created by Syed Muhammad Awais on 3/13/2017.
 */

import java.util.List;

/**
 * Created by Mai Thanh Hiep on 4/3/2016.
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}