package com.huyn.demogroup.crop;

import android.graphics.RectF;

/**
 * Created by huyaonan on 2017/5/23.
 */

public interface OverlayViewChangeListener {

    public void onCropRectUpdated(RectF cropRect);
    public void onConfirmed();

}
