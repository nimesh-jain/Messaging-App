package com.example.saurav.login;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saurav on 20-03-2018.
 */

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    String [] titles =new String []{"Chat","Friends","Requests"};


    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ChatsFragment();

            case 1:
                return new FreindsFragment();

            case 2:
                return new RequestsFragment();

        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
