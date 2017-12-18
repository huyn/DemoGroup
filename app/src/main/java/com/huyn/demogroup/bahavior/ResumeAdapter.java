package com.huyn.demogroup.bahavior;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by huyaonan on 2017/12/18.
 */

public class ResumeAdapter extends FragmentPagerAdapter {

    public ResumeAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (0 == position) {
            fragment = new ResumeFragment();
        } else if (1 == position) {
            fragment = new ResumeFragment(true);
        } else if (2 == position) {
            fragment = new ResumeFragment();
        } else if (3 == position) {
            fragment = new ResumeFragment(true);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "TAB 1";
            case 1:
                return "TAB 2";
            case 2:
                return "TAB 3";
            case 3:
                return "TAB 4";
        }
        return null;
    }
}