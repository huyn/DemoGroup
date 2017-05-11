package com.huyn.demogroup.paint;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 16/11/14.
 */
public class CardViewActivity extends Activity {

    private RelativeLayout mRelativeLayout;
    private View add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_card_top);

//        setContentView(R.layout.activity_card_relative);
//
//        mRelativeLayout = (RelativeLayout) findViewById(R.id.root);
//        add = findViewById(R.id.add);
//        add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                int pos = getPos();
//                generateParamsStyle(params, pos);
//                TextView textView = new TextView(CardViewActivity.this);
//                textView.setText("" + pos);
//                textView.setBackgroundColor(Color.RED);
//                mRelativeLayout.addView(textView, params);
//            }
//        });
    }

    private int start = 1;
    private int getPos() {
        if(start > 8)
            start-=9;
        int result = start;
        start++;
        return result;
    }

    public static void generateParamsStyle(RelativeLayout.LayoutParams params, int relativeMode) {
        switch (relativeMode) {
            case 0:
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                break;
            case 1:
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                break;
            case 2:
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                break;
            case 3:
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                break;
            case 4:
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                break;
            case 5:
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            case 6:
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            case 7:
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                break;
            case 8:
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                break;
        }
    }
}
