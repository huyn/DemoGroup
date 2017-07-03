package com.huyn.demogroup.bahavior;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huyn.demogroup.R;

public class BehaviorActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_behavior);
//    findViewById(R.id.iv_avatar).setOnClickListener(new View.OnClickListener() {
//      @Override public void onClick(View v) {
//        startActivity(new Intent(BehaviorActivity.this,EasyBehaviorActivity.class));
//      }
//    });

    NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
    nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
      @Override
      public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        System.out.println("++++++++scrollY:" + scrollY + "/oldScrollY:" + oldScrollY);
      }
    });

    CoordinatorLayout root = (CoordinatorLayout) findViewById(R.id.root);

    ViewPager mPager = (ViewPager) findViewById(R.id.viewPager);
    ResumeAdapter mPagerAdapter = new ResumeAdapter(getSupportFragmentManager());
    mPager.setAdapter(mPagerAdapter);
    // ViewPager切换时NestedScrollView滑动到顶部
    mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override
      public void onPageSelected(int position) {
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });
    //newInstance();
  }


//  public void titleMode(View v){
//    findViewById(R.id.iv_avatar).setVisibility(View.INVISIBLE);
//    findViewById(R.id.tv_title).setVisibility(View.VISIBLE);
//  }
//
//  public void avatarMode(View v ){
//
//    findViewById(R.id.iv_avatar).setVisibility(View.VISIBLE);
//    findViewById(R.id.tv_title).setVisibility(View.INVISIBLE);
//  }

  protected void newInstance() {
    setContentView(R.layout.layout_nestedviewpager);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    // 设置返回主页的按钮
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    // TabLayout
    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
    // ViewPager
    ViewPager mPager = (ViewPager) findViewById(R.id.viewPager);
    ResumeAdapter mPagerAdapter = new ResumeAdapter(getSupportFragmentManager());
    mPager.setAdapter(mPagerAdapter);
    tabLayout.setupWithViewPager(mPager);
    // ViewPager切换时NestedScrollView滑动到顶部
    mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override
      public void onPageSelected(int position) {
        ((NestedScrollView) findViewById(R.id.nestedScrollView)).scrollTo(0, 0);
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });
  }

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

  @SuppressLint("ValidFragment")
  public static class ResumeFragment extends Fragment {

    boolean isShort = false;
    public ResumeFragment(boolean isShort) {
      this.isShort = isShort;
    }

    public ResumeFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      return inflater.inflate(isShort ? R.layout.item_blank : R.layout.activity_main, container, false);
    }
  }
}
