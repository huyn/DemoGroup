package com.huyn.demogroup.perspective;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.Log;

import com.caverock.androidsvg.PreserveAspectRatio;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Util class to init and get paths from svg.
 */
public class SvgUtils {
    /**
     * It is for logging purposes.
     */
    private static final String LOG_TAG = "SVGUtils";
    /**
     * All the paths with their attributes from the svg.
     */
    private final List<SvgPath> mPaths = new ArrayList<>();
    /**
     * The init svg.
     */
    private SVG mSvg;

    /**
     * Loading the svg from the resources.
     *
     * @param context     Context object to get the resources.
     * @param svgResource int resource id of the svg.
     */
    public void load(Context context, int svgResource) {
        if (mSvg != null) 
            return;
        try {
            long loadStart = System.currentTimeMillis();
            mSvg = SVG.getFromResource(context, svgResource);
            mSvg.setDocumentPreserveAspectRatio(PreserveAspectRatio.UNSCALED);
            System.out.println("++++load coast : " + (System.currentTimeMillis() - loadStart));
        } catch (SVGParseException e) {
            Log.e(LOG_TAG, "Could not load specified SVG resource", e);
        }
    }

    /**
     * Render the svg to canvas and catch all the paths while rendering.
     *
     * @param width  - the width to scale down the view to,
     * @param height - the height to scale down the view to,
     * @return All the paths from the svg.
     */
    public List<SvgPath> getPathsForViewport(final int width, final int height) {
        long getPathStart = System.currentTimeMillis();
        Canvas canvas = new Canvas() {
            private final Matrix mMatrix = new Matrix();

            @Override
            public int getWidth() {
                return width;
            }

            @Override
            public int getHeight() {
                return height;
            }

            @Override
            public void drawPath(Path path, Paint paint) {
                System.out.println("+++++draw path..." + getWidth() + "/" + getHeight());
                Path dst = new Path();

                //noinspection deprecation
                getMatrix(mMatrix);
                path.transform(mMatrix, dst);
                mPaths.add(new SvgPath(dst));
            }
        };

        rescaleCanvas(width, height, canvas);
        System.out.println("+++++get path cost : " + (System.currentTimeMillis() - getPathStart));
        return mPaths;
    }

    /**
     * Rescale the canvas with specific width and height.
     *
     * @param width       The width of the canvas.
     * @param height      The height of the canvas.
     * @param canvas      The canvas to be drawn.
     */
    private void rescaleCanvas(int width, int height, Canvas canvas) {
        if (mSvg == null) 
            return;
        final RectF viewBox = mSvg.getDocumentViewBox();

        float wScale = width / (viewBox.width());
        float hScale = height / (viewBox.height());

        System.out.println("=======scale : " + wScale + '/' + hScale);
        System.out.println("=======width/height : " + width + '/' + height);
        System.out.println("=======viewBox.width/height : " + viewBox.width() + '/' + viewBox.height());

        canvas.translate((width - viewBox.width() * wScale) / 2.0f,
                (height - viewBox.height() * hScale) / 2.0f);
        canvas.scale(wScale, hScale);

        mSvg.renderToCanvas(canvas);
    }

    /**
     * Path with bounds for scalling , length and paint.
     */
    public static class SvgPath {

        /**
         * Region of the path.
         */
        private static final Region REGION = new Region();
        /**
         * This is done for clipping the bounds of the path.
         */
        private static final Region MAX_CLIP =
                new Region(Integer.MIN_VALUE, Integer.MIN_VALUE,
                        Integer.MAX_VALUE, Integer.MAX_VALUE);
        /**
         * The path itself.
         */
        final Path path;
        /**
         * The length of the path.
         */
        float length;
        /**
         * The measure of the path, we can use it later to get segment of it.
         */
        final PathMeasure measure;

        /**
         * Constructor to add the path and the paint.
         *
         * @param path  The path that comes from the rendered svg.
         */
        SvgPath(Path path) {
            this.path = path;

            measure = new PathMeasure(path, false);
            this.length = measure.getLength();

            REGION.setPath(path, MAX_CLIP);
        }

    }

}
