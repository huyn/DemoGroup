package com.huyn.demogroup.paint;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 16/11/24.
 */
public class TestStripeAnimActivity extends Activity {

    private StripeCompoundLayout mItem;
    private DetailLayout mDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stripe);

        mItem = (StripeCompoundLayout) findViewById(R.id.item);
        mDetail = (DetailLayout) findViewById(R.id.detail);

        mItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int width = mItem.getSize();
                int[] position = mItem.getPosition();
                mDetail.setupPreAnimLayout(width, position[0], position[1], new DetailLayout.IAnimEndListener() {
                    @Override
                    public void onAnimEnd() {

                    }

                    @Override
                    public void onStartFinishAnim() {

                    }
                });
                mDetail.startAnim();
            }
        });
    }
}
