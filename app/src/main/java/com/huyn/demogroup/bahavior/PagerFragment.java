package com.huyn.demogroup.bahavior;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 2017/12/18.
 */

public class PagerFragment extends Fragment {

    private boolean longTxt = true;

    public void setLong(boolean longTxt) {
        this.longTxt = longTxt;
    }

    public static Fragment newInstance(boolean longTxt) {
        PagerFragment fragment = new PagerFragment();
        fragment.setLong(longTxt);
        return fragment;
    }

    public PagerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pager, container, false);
        TextView textView = (TextView) view.findViewById(R.id.fragment_txt);
        textView.setText(longTxt ? R.string.text_long : R.string.text_short);
        return view;
    }
}
