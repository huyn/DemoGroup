package com.huyn.demogroup.particle;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.huyn.demogroup.R;
import com.huyn.demogroup.particle.explosionfield.ExplosionField;

/**
 * Created by huyaonan on 16/11/18.
 */
public class ExplosionFieldActivity extends Activity {

    private ExplosionField mExplosionField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exlposion);
        mExplosionField = ExplosionField.attach2Window(this);
        addListener(findViewById(R.id.root));
    }

    private void addListener(View root) {
        if (root instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) root;
            for (int i = 0; i < parent.getChildCount(); i++) {
                addListener(parent.getChildAt(i));
            }
        } else {
            root.setClickable(true);
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mExplosionField.explode(v);
                    v.setOnClickListener(null);
                }
            });
        }
    }
}
