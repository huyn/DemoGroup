package com.huyn.demogroup.particle;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.huyn.demogroup.R;
import com.huyn.demogroup.particle.leonids.ParticleSystem;

/**
 * Created by huyaonan on 16/11/18.
 */
public class LeonidsFireworksActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particle_system_example);
        findViewById(R.id.button1).setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        ParticleSystem ps = new ParticleSystem(this, 100, R.drawable.star_pink, 800);
        ps.setScaleRange(0.7f, 1.3f);
        ps.setSpeedRange(0.5f, 0.75f);
        ps.setRotationSpeedRange(90, 180);
        ps.setFadeOut(200, new AccelerateInterpolator());
        ps.oneShot(arg0, 70);

        ParticleSystem ps2 = new ParticleSystem(this, 100, R.drawable.star_white, 800);
        ps2.setScaleRange(0.7f, 1.3f);
        ps2.setSpeedRange(0.5f, 0.75f);
        ps.setRotationSpeedRange(90, 180);
        ps2.setFadeOut(200, new AccelerateInterpolator());
        ps2.oneShot(arg0, 70);
    }
}
