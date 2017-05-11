package com.huyn.demogroup.particle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.huyn.demogroup.R;
import com.huyn.demogroup.particle.leonids.ParticleSystem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * see
 * https://github.com/tyrantgit/ExplosionField
 * https://github.com/plattysoft/Leonids
 * Created by huyaonan on 16/11/18.
 */
public class ParticleEffectsActivity extends Activity {

    @BindView(R.id.left)
    ImageView left;
    @BindView(R.id.right)
    ImageView right;
    @BindView(R.id.start)
    View mView;

    @OnClick(R.id.start)
    void submit() {
        left.offsetLeftAndRight(50);
        right.offsetLeftAndRight(-50);

        ParticleSystem ps = new ParticleSystem(this, 100, R.drawable.star_pink, 800);
        ps.setScaleRange(0.7f, 1.3f);
        ps.setSpeedRange(0.1f, 0.25f);
        ps.setRotationSpeedRange(90, 180);
        ps.setFadeOut(200, new AccelerateInterpolator());
        ps.oneShot(mView, 70);

        ParticleSystem ps2 = new ParticleSystem(this, 100, R.drawable.star_white, 800);
        ps2.setScaleRange(0.7f, 1.3f);
        ps2.setSpeedRange(0.1f, 0.25f);
        ps.setRotationSpeedRange(90, 180);
        ps2.setFadeOut(200, new AccelerateInterpolator());
        ps2.oneShot(mView, 70);
    }

    @OnClick(R.id.btn_explosion)
    void clickToExplosion() {
        startActivity(new Intent(this, ExplosionFieldActivity.class));
    }

    @OnClick(R.id.btn_leonids)
    void clickToLeonids() {
        startActivity(new Intent(this, LeonidsFireworksActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particle);

        ButterKnife.bind(this);
    }
}
