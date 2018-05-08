package com.example.syedmuhammadawais.mapapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class Tabs extends AppCompatActivity {
    ViewPager pager;
    TabLayout tab;
    private MainActivityPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);
        pager= (ViewPager) findViewById(R.id.pager);
        this.tab= (TabLayout) findViewById(R.id.tabs);
        pagerAdapter=new MainActivityPagerAdapter(getSupportFragmentManager(),this);
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(this.tab));
        this.tab.setupWithViewPager(pager);

    }


    class MainActivityPagerAdapter extends FragmentStatePagerAdapter {
        String []tittles={"Navigation","NearBy"};
        Tabs context;
        Fragment fragment;

        public MainActivityPagerAdapter(FragmentManager fm, Tabs activity) {

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


}
