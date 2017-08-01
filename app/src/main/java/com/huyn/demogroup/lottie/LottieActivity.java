package com.huyn.demogroup.lottie;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 2017/8/1.
 */

public class LottieActivity extends Activity {

    private LottieAnimationView animationView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottie);

        animationView = (LottieAnimationView) findViewById(R.id.animation_view);
        animationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                System.out.println("++++onAnimationEnd");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                System.out.println("++++onAnimationRepeat");
            }

            @Override
            public void onAnimationStart(Animator animation) {
                System.out.println("++++onAnimationStart");
            }
        });
        animationView.playAnimation();
    }
}
