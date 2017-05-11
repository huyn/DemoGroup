package com.huyn.demogroup.paint;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 16/11/14.
 */
public class RadioView extends LinearLayout {
    public RadioView(Context context) {
        this(context, null);
    }

    public RadioView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadioView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.radio_item, this, true);
    }

    public void setSelected(boolean selected) {

    }

    public void update() {

    }

}
