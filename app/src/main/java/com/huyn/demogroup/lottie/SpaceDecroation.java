package com.huyn.demogroup.lottie;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by huyaonan on 2017/5/3.
 */

public class SpaceDecroation extends RecyclerView.ItemDecoration {

    private int space=0;

    public SpaceDecroation(Context context) {
        space = 30;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position

        if(position == 0)
            outRect.left = space*2;
        else
            outRect.left = space/2;

        if(position == parent.getAdapter().getItemCount() - 1)
            outRect.right = space*2;
        else
            outRect.right = space/2;

        outRect.top = space/4;
        outRect.bottom = space/4;
    }
}
