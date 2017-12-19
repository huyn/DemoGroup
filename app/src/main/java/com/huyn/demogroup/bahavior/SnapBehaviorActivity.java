package com.huyn.demogroup.bahavior;

/**
 * Created by huyaonan on 2017/12/18.
 */

/*
 * Copyright 2016 "Henry Tao <hi@henrytao.me>"
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.huyn.demogroup.R;
import com.huyn.demogroup.bahavior.widget.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SnapBehaviorActivity extends AppCompatActivity {

//    @BindView(android.R.id.list)
//    RecyclerView vRecyclerView;

//    @BindView(R.id.toolbar)
//    Toolbar vToolbar;

    AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gsd_scroll_snap);
        ButterKnife.bind(this);

        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //System.out.println("+++offsetchanged ... " + verticalOffset + "/" + appBarLayout.getTotalScrollRange());
            }
        });

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                appBarLayout.pinHeaderTopBottomOffset(-300);
            }
        }, 1000);*/

//        setSupportActionBar(vToolbar);
//        vToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

//        vRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        vRecyclerView.setAdapter(new DynamicAdapter(getSampleData()));

        ViewPager mPager = (ViewPager) findViewById(R.id.viewPager);

        ViewCompat.offsetTopAndBottom(appBarLayout, 400);
        ViewCompat.offsetTopAndBottom(mPager, 400);

//        ResumeAdapter mPagerAdapter = new ResumeAdapter(getSupportFragmentManager());
//        mPager.setAdapter(mPagerAdapter);
//        // ViewPager切换时NestedScrollView滑动到顶部
//        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        /*mViewPagerAdapter.addFragment("Cat", PagerFragment.newInstance(true));
        mViewPagerAdapter.addFragment("Dog", PagerFragment.newInstance(false));
        mViewPagerAdapter.addFragment("Mouse", PagerFragment.newInstance(true));
        mViewPagerAdapter.addFragment("Bird", PagerFragment.newInstance(false));
        mViewPagerAdapter.addFragment("Chicken", PagerFragment.newInstance(true));
        mViewPagerAdapter.addFragment("Tiger", PagerFragment.newInstance(false));
        mViewPagerAdapter.addFragment("Elephant", PagerFragment.newInstance(true));*/
        mViewPagerAdapter.addFragment("Cat", RecyclerFragment.newInstance(10));
        mViewPagerAdapter.addFragment("Dog", RecyclerFragment.newInstance(20));
        mViewPagerAdapter.addFragment("Mouse", RecyclerFragment.newInstance(5));
        mViewPagerAdapter.addFragment("Bird", RecyclerFragment.newInstance(10));
        mViewPagerAdapter.addFragment("Chicken", RecyclerFragment.newInstance(20));
        mViewPagerAdapter.addFragment("Tiger", RecyclerFragment.newInstance(5));

        mPager.setAdapter(mViewPagerAdapter);
        mPager.setOffscreenPageLimit(mPager.getAdapter().getCount());
    }

    private static final int ITEM_COUNT = 35;

    public static List<String> getSampleData() {
        return getSampleData(ITEM_COUNT);
    }

    public static List<String> getSampleData(int count) {
        List<String> data = new ArrayList<>();
        int i = 0;
        for (int n = count; i < n; i++) {
            data.add(String.format("Line %d", i));
        }
        return data;
    }

}

