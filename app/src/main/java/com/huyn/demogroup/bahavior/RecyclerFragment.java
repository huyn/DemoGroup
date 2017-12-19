package com.huyn.demogroup.bahavior;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huyn.demogroup.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huyaonan on 2017/12/18.
 */

public class RecyclerFragment extends Fragment {

    private int size = 10;

    void setSize(int size) {
        this.size = size;
    }

    public static Fragment newInstance(int size) {
        RecyclerFragment fragment = new RecyclerFragment();
        fragment.setSize(size);
        return fragment;
    }

    public RecyclerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new DynamicAdapter(getSampleData(size)));
        return view;
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
