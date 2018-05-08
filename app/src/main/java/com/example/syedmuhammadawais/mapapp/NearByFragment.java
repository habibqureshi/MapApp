package com.example.syedmuhammadawais.mapapp;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class NearByFragment extends Fragment {
    EditText input;
    Button Go;
    View layout;
    Functions_Activity activity;
    int  showAdd=0;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Functions_Activity)
            activity= (Functions_Activity) context;
    }

    public NearByFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_near_by, container, false);
        return  layout;
    }



}
