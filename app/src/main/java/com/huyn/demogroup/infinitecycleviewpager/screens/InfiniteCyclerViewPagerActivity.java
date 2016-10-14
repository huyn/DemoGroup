package com.huyn.demogroup.infinitecycleviewpager.screens;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.huyn.demogroup.R;
import com.huyn.demogroup.infinitecycleviewpager.adapters.MainPagerAdapter;

public class InfiniteCyclerViewPagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infinitecycleviewpager);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_main);
        viewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(2);

        final NavigationTabStrip navigationTabStrip = (NavigationTabStrip) findViewById(R.id.nts);
        navigationTabStrip.setTitles("HOW WE WORK", "WE WORK WITH");
        navigationTabStrip.setViewPager(viewPager);
    }
}
