package com.huyn.demogroup.clipchild;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.huyn.demogroup.R;

public class AnimatorHelper {

    public static void voteAnimation(final ImageView view, final boolean isSelected, final boolean isRed) {
        Animation anim = AnimationUtils.loadAnimation(view.getContext(), R.anim.breath_anim);
        anim.setDuration(150);
        if (!isSelected) {
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    view.setImageResource(isRed ? R.drawable.icon_thumbreds : R.drawable.icon_thumblues);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setImageResource(isRed ? R.drawable.icon_thumbred : R.drawable.icon_thumblue);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        view.startAnimation(anim);
    }

    public static void alphaAnim(final View view, final float from, final float to) {
        alphaAnim(view, from, to, true);
    }

    public static void alphaAnim(final View view, final float from, final float to, final boolean useGone) {
        AlphaAnimation anim = new AlphaAnimation(from, to);
        anim.setDuration(150);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (to > from)
                    view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (from > to)
                    view.setVisibility(useGone ? View.GONE : View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(anim);
    }

}
