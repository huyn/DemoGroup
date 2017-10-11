package com.huyn.demogroup.lottie;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 2017/8/1.
 */

public class LottieActivity extends Activity {

    private LottieAnimationView animationView, animationView2;
    private RecyclerView mStyleList;
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
        //animationView.loop(true);
        animationView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //findViewById(R.id.wipablelayout).invalidate();
            }
        });
        animationView.playAnimation();

        animationView2 = (LottieAnimationView) findViewById(R.id.animation_view2);
        animationView2.loop(true);
        animationView2.playAnimation();

        findViewById(R.id.animation_toggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(animationView2.isAnimating())
                    animationView2.cancelAnimation();
                else
                    animationView2.playAnimation();
            }
        });

        mStyleList = (RecyclerView) findViewById(R.id.stylize_list);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mStyleList.setLayoutManager(manager);
        mStyleList.addItemDecoration(new SpaceDecroation(this));
        mStyleList.setAdapter(new SimpleAdapter(this));
    }
}
