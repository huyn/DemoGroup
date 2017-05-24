package com.huyn.demogroup.crop;

import android.graphics.RectF;

public class RectUtils {

    /**
     * Gets a float array of the 2D coordinates representing a rectangles
     * corners.
     * The order of the corners in the float array is:
     * 0---1--->2
     * ^        |
     * |        3
     * 7        |
     * |        v
     * 6<---5---4
     *
     * @param r the rectangle to get the corners of
     * @return the float array of corners (16 floats)
     */
    public static float[] getCornersFromRect(RectF r) {
        return new float[]{
                r.left, r.top,
                (r.right+r.left)/2, r.top,
                r.right, r.top,
                r.right, (r.bottom+r.top)/2,
                r.right, r.bottom,
                (r.right+r.left)/2, r.bottom,
                r.left, r.bottom,
                r.left, (r.bottom+r.top)/2
        };
    }

    public static float[] getCenterFromRect(RectF r) {
        return new float[]{r.centerX(), r.centerY()};
    }

}