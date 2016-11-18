package com.huyn.demogroup.particle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.huyn.demogroup.R;

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

    @OnClick(R.id.start)
    void submit() {
        left.offsetLeftAndRight(50);
        right.offsetLeftAndRight(-50);
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
