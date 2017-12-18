package com.huyn.demogroup.bahavior;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 2017/12/18.
 */

public class ResumeFragment extends Fragment {

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
