package com.huyn.demogroup.glassbreak.view;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.Arrays;

public class BrokenAnimator extends ValueAnimator {

    /**
     * SEGMENT is the base of Circle-Rifts radius,and it's also
     * the step when warp the Beeline-Rifts.
     * Set it to zero to disable the Circle-Rifts effect.
     */
    private int SEGMENT;

    private BrokenView mBrokenView;
    private View mView;
    private BrokenConfig mConfig;
    private Bitmap mBitmap;
    private Point mTouchPoint;

    // Used in onDraw()
    private Paint onDrawPaint;
    private boolean hasCircleRifts = true;

    private LinePath[] lineRifts;
    private Path[] circleRifts;
    private int[] circleWidth;
    private ArrayList<Path> pathArray;
    private Piece[] pieces;

    // The touch position relative to mView
    private int offsetX;
    private int offsetY;

    public BrokenAnimator(BrokenView brokenView, View view, Bitmap bitmap, Point point, BrokenConfig config){
        mBrokenView = brokenView;
        mView = view;
        mBitmap = bitmap;
        mTouchPoint = point;
        mConfig = config;
        
        pathArray = new ArrayList<>();
        lineRifts = new LinePath[mConfig.complexity];
        circleRifts = new Path[mConfig.complexity];
        circleWidth = new int[mConfig.complexity];
        SEGMENT = mConfig.circleRiftsRadius;
        if(SEGMENT == 0) {
            hasCircleRifts = false;
            SEGMENT = 66;
        }

        Rect r = new Rect();
        mView.getGlobalVisibleRect(r);
        offsetX = mTouchPoint.x - r.left;
        offsetY = mTouchPoint.y - r.top;
        // Make the touchPoint be the origin of coordinates
        r.offset(-mTouchPoint.x, -mTouchPoint.y);

        // The touchPoint is original location on the screen,
        // but the BrokenView canvas may be not full screen (under status bar),
        // to do this we can translate the canvas correctly.
        Rect brokenViewR = new Rect();
        mBrokenView.getGlobalVisibleRect(brokenViewR);
        mTouchPoint.x -= brokenViewR.left;
        mTouchPoint.y -= brokenViewR.top;

        buildBrokenLines(r);
        buildBrokenAreas(r);
        buildPieces();
        buildPaintShader();
        warpStraightLines();

        setFloatValues(0f,1f);
        //setInterpolator(new AccelerateInterpolator(2.0f));
        setInterpolator(new LinearInterpolator());
        setDuration(mConfig.breakDuration);
    }

    /**
     * Build warped-lines according to the baselines, like the DiscretePathEffect.
     */
    private void buildBrokenLines(Rect r) {
        LinePath[] baseLines = new LinePath[mConfig.complexity];
        buildBaselines(baseLines, r);
        PathMeasure pmTemp = new PathMeasure();
        for (int i = 0; i < mConfig.complexity; i++) {
            lineRifts[i] = new LinePath();
            lineRifts[i].moveTo(0, 0);
            lineRifts[i].setEndPoint(baseLines[i].getEndPoint());

            pmTemp.setPath(baseLines[i], false);
            float length = pmTemp.getLength();
            final int THRESHOLD = SEGMENT + SEGMENT / 2;

            if (length > BrokenUtils.dp2px(THRESHOLD)) {
                lineRifts[i].setStraight(false);
                // First, line to the point at SEGMENT of baseline;
                // Second, line to the random-point at (SEGMENT+SEGMENT/2) of baseline;
                // So when we set the start-draw-length to SEGMENT and the paint style is "FILL",
                // we can make the line become visible faster(exactly, the triangle)
                float[] pos = new float[2];
                pmTemp.getPosTan(BrokenUtils.dp2px(SEGMENT), pos, null);
                lineRifts[i].lineTo(pos[0], pos[1]);

                lineRifts[i].points.add(new Point((int)pos[0], (int)pos[1]));

                int xRandom, yRandom;
                int step = BrokenUtils.dp2px(THRESHOLD);
                do{
                    pmTemp.getPosTan(step, pos, null);
                    // !!!
                    // Here determine the stroke width of lineRifts
                    xRandom = (int) (pos[0] + BrokenUtils.nextInt(-BrokenUtils.dp2px(3),BrokenUtils.dp2px(2)));
                    yRandom = (int) (pos[1] + BrokenUtils.nextInt(-BrokenUtils.dp2px(2),BrokenUtils.dp2px(3)));
                    lineRifts[i].lineTo(xRandom, yRandom);
                    lineRifts[i].points.add(new Point(xRandom, yRandom));
                    step += BrokenUtils.dp2px(SEGMENT);
                } while (step < length);
                lineRifts[i].lineToEnd();
            } else {
                // Too short, it's still a beeline, so we must warp it later {@warpStraightLines()},
                // to make sure it is visible in "FILL" mode.
                lineRifts[i] = baseLines[i];
                lineRifts[i].setStraight(true);
            }
            lineRifts[i].points.add(lineRifts[i].getEndPoint());
        }
    }

    /**
     * Build beelines according to the angle
     */
    private void buildBaselines(LinePath[] baseLines,Rect r){
        for(int i = 0; i < mConfig.complexity; i++){
            baseLines[i] = new LinePath();
            baseLines[i].moveTo(0,0);
        }
        buildFirstLine(baseLines[0], r);

        // First angle
        int angle = (int)(Math.toDegrees(Math.atan((float)(-baseLines[0].getEndY()) / baseLines[0].getEndX())));

        // The four diagonal angle base
        int[] angleBase = new int[4];
        angleBase[0] = (int)(Math.toDegrees(Math.atan((float)(-r.top) / (r.right))));
        angleBase[1] = (int)(Math.toDegrees(Math.atan((float)(-r.top) / (-r.left))));
        angleBase[2] = (int)(Math.toDegrees(Math.atan((float)(r.bottom) / (-r.left))));
        angleBase[3] = (int)(Math.toDegrees(Math.atan((float)(r.bottom) / (r.right))));

        if(baseLines[0].getEndX() < 0) // 2-quadrant,3-quadrant
            angle += 180;
        else if(baseLines[0].getEndX() > 0 && baseLines[0].getEndY() > 0) // 4-quadrant
            angle += 360;

        // Random angle range
        int range = 360 / mConfig.complexity / 3;
        int angleRandom;

        for(int i = 1; i<mConfig.complexity; i++) {
            angle = angle + 360 / mConfig.complexity;
            if (angle >= 360)
                angle -= 360;

            angleRandom = angle + BrokenUtils.nextInt(-range, range);
            if (angleRandom >= 360)
                angleRandom -= 360;
            else if (angleRandom < 0)
                angleRandom += 360;

            baseLines[i].obtainEndPoint(angleRandom,angleBase,r);
            baseLines[i].lineToEnd();
        }
    }

    /**
     * Line to the the farthest boundary, in case appear a super big piece.
     */
    private void buildFirstLine(LinePath path, Rect r){
        int[] range=new int[]{-r.left,-r.top,r.right,r.bottom};
        int max = -1;
        int maxId = 0;
        for(int i = 0; i < 4; i++) {
            if(range[i] > max) {
                max = range[i];
                maxId = i;
            }
        }
        switch (maxId){
            case 0:
                path.setEndPoint(r.left, BrokenUtils.nextInt(r.height()) + r.top);
                break;
            case 1:
                path.setEndPoint(BrokenUtils.nextInt(r.width()) + r.left, r.top);
                break;
            case 2:
                path.setEndPoint(r.right, BrokenUtils.nextInt(r.height()) + r.top);
                break;
            case 3:
                path.setEndPoint(BrokenUtils.nextInt(r.width()) + r.left, r.bottom);
                break;
        }
        path.lineToEnd();
    }

    /**
     * Build broken area into path
     */
    private void buildBrokenAreas(Rect r){
        final int SEGMENT_LESS = SEGMENT * 7 / 9;
        final int START_LENGTH = (int)(SEGMENT * 1.1);

        // The Circle-Rifts is just some isosceles triangles,
        // "linkLen" is the length of oblique side
        float linkLen = 0;
        int repeat = 0;

        PathMeasure pmNow = new PathMeasure();
        PathMeasure pmPre = new PathMeasure();

        for(int i = 0; i < mConfig.complexity; i++) {

            lineRifts[i].setStartLength(BrokenUtils.dp2px(START_LENGTH));

            if (repeat > 0) {
                repeat--;
            } else {
                linkLen = BrokenUtils.nextInt(BrokenUtils.dp2px(SEGMENT_LESS),BrokenUtils.dp2px(SEGMENT));
                repeat = BrokenUtils.nextInt(3);
            }

            int iPre = (i - 1) < 0 ? mConfig.complexity - 1 : i - 1;
            pmNow.setPath(lineRifts[i],false);
            pmPre.setPath(lineRifts[iPre], false);

            if (hasCircleRifts && pmNow.getLength() > linkLen && pmPre.getLength() > linkLen) {

                float[] pointNow = new float[2];
                float[] pointPre = new float[2];
                circleWidth[i] = BrokenUtils.nextInt(BrokenUtils.dp2px(1)) + 1;
                circleRifts[i] = new Path();
                pmNow.getPosTan(linkLen, pointNow, null);
                circleRifts[i].moveTo(pointNow[0], pointNow[1]);
                pmPre.getPosTan(linkLen, pointPre, null);
                circleRifts[i].lineTo(pointPre[0], pointPre[1]);

                // The area outside Circle-Rifts
                Path pathArea = new Path();
                pmPre.getSegment(linkLen, pmPre.getLength(), pathArea, true);
                pathArea.rLineTo(0, 0); // KITKAT(API 19) and earlier need it
                drawBorder(pathArea,lineRifts[iPre].getEndPoint(),
                        lineRifts[i].points.get(lineRifts[i].points.size() - 1),r);
                for (int j =  lineRifts[i].points.size() - 2; j >= 0; j--)
                    pathArea.lineTo(lineRifts[i].points.get(j).x, lineRifts[i].points.get(j).y);
                pathArea.lineTo(pointNow[0], pointNow[1]);
                pathArea.lineTo(pointPre[0], pointPre[1]);
                pathArea.close();
                pathArray.add(pathArea);

                // The area inside Circle-Rifts, it's a isosceles triangles
                pathArea = new Path();
                pathArea.moveTo(0,0);
                pathArea.lineTo(pointPre[0],pointPre[1]);
                pathArea.lineTo(pointNow[0],pointNow[1]);
                pathArea.close();
                pathArray.add(pathArea);
            }
            else{
                // Too short, there is no Circle-Rifts
                Path pathArea = new Path(lineRifts[iPre]);
                drawBorder(pathArea, lineRifts[iPre].getEndPoint(), lineRifts[i].points.get( lineRifts[i].points.size()-1),r);
                for (int j = lineRifts[i].points.size() - 2; j >= 0; j--)
                    pathArea.lineTo(lineRifts[i].points.get(j).x,  lineRifts[i].points.get(j).y);
                pathArea.close();
                pathArray.add(pathArea);
            }
        }
    }

    /**
     * Build the final bitmap-pieces to draw in animation
     */
    private void buildPieces(){
        pieces = new Piece[pathArray.size()];
        Paint paint = new Paint();
        Matrix matrix = new Matrix();
        Canvas canvas = new Canvas();
        for(int i = 0; i < pieces.length; i++) {
            int shadow = BrokenUtils.nextInt(BrokenUtils.dp2px(2),BrokenUtils.dp2px(9));
            Path path = pathArray.get(i);
            RectF r = new RectF();
            path.computeBounds(r, true);

            Bitmap pBitmap = BrokenUtils.createBitmapSafely((int)r.width() + shadow * 2,
                    (int)r.height() + shadow * 2, Bitmap.Config.ARGB_4444,1);
            if(pBitmap == null){
                pieces[i] = new Piece(-1, -1, null, shadow);
                continue;
            }
            pieces[i] = new Piece((int)r.left + mTouchPoint.x - shadow,
                    (int)r.top + mTouchPoint.y - shadow, pBitmap, shadow);
            canvas.setBitmap(pieces[i].bitmap);
            BitmapShader mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            matrix.reset();
            matrix.setTranslate(-r.left - offsetX + shadow, -r.top - offsetY + shadow);
            mBitmapShader.setLocalMatrix(matrix);

            paint.reset();
            Path offsetPath = new Path();
            offsetPath.addPath(path, -r.left + shadow, -r.top + shadow);

            // Draw shadow
            paint.setStyle(Paint.Style.FILL);
            /*paint.setShadowLayer(shadow,0,0,0x00000000);
            canvas.drawPath(offsetPath,paint);*/
            paint.setShadowLayer(0,0,0,0);

            // In case the view has alpha channel
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
            canvas.drawPath(offsetPath,paint);
            paint.setXfermode(null);

            // Draw bitmap
            paint.setShader(mBitmapShader);
            //paint.setAlpha(0xcc);
            paint.setAlpha(255);
            canvas.drawPath(offsetPath, paint);
        }
        // Sort by shadow
        Arrays.sort(pieces);
    }

    private void buildPaintShader(){
        if(mConfig.paint == null) {
            onDrawPaint = new Paint();
            BitmapShader shader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Matrix matrix = new Matrix();
            // Refraction effect
            matrix.setTranslate(-offsetX - 10, -offsetY - 7);
            shader.setLocalMatrix(matrix);
            ColorMatrix cMatrix = new ColorMatrix();
            // Increase saturation and brightness
            cMatrix.set(new float[]{
                    2.5f, 0, 0, 0, 100,
                    0, 2.5f, 0, 0, 100,
                    0, 0, 2.5f, 0, 100,
                    0, 0, 0, 1, 0});
            onDrawPaint.setColorFilter(new ColorMatrixColorFilter(cMatrix));
            onDrawPaint.setShader(shader);
            onDrawPaint.setStyle(Paint.Style.FILL);
        }
        else
            onDrawPaint = mConfig.paint;
    }

    /**
     *  Make sure it can be seen in "FILL" mode
     */
    private void warpStraightLines() {
        PathMeasure pmTemp = new PathMeasure();
        for (int i = 0; i < mConfig.complexity; i++) {
            if(lineRifts[i].isStraight())
            {
                pmTemp.setPath(lineRifts[i], false);
                lineRifts[i].setStartLength(pmTemp.getLength() / 2);
                float[] pos = new float[2];
                pmTemp.getPosTan(pmTemp.getLength() / 2, pos, null);
                int xRandom = (int) (pos[0] + BrokenUtils.nextInt(-BrokenUtils.dp2px(1), BrokenUtils.dp2px(1)));
                int yRandom = (int) (pos[1] + BrokenUtils.nextInt(-BrokenUtils.dp2px(1), BrokenUtils.dp2px(1)));
                lineRifts[i].reset();
                lineRifts[i].moveTo(0,0);
                lineRifts[i].lineTo(xRandom,yRandom);
                lineRifts[i].lineToEnd();
            }
        }
    }

    public void drawBorder(Path path, Point pointStart, Point pointEnd, Rect r){
        if(pointStart.x == r.right) {
            if(pointEnd.x == r.right)
                path.lineTo(pointEnd.x, pointEnd.y);
            else if(pointEnd.y == r.top) {
                path.lineTo(r.right, r.top);
                path.lineTo(pointEnd.x, pointEnd.y);
            }
            else if(pointEnd.x == r.left){
                path.lineTo(r.right, r.top);
                path.lineTo(r.left, r.top);
                path.lineTo(pointEnd.x, pointEnd.y);
            }else if(pointEnd.y == r.bottom){
                path.lineTo(r.right, r.top);
                path.lineTo(r.left, r.top);
                path.lineTo(r.left, r.bottom);
                path.lineTo(pointEnd.x, pointEnd.y);
            }
        }
        else if(pointStart.y == r.top) {
            if(pointEnd.y == r.top){
                path.lineTo(pointEnd.x, pointEnd.y);
            }else if(pointEnd.x == r.left){
                path.lineTo(r.left,r.top);
                path.lineTo(pointEnd.x, pointEnd.y);
            }else if(pointEnd.y == r.bottom){
                path.lineTo(r.left,r.top);
                path.lineTo(r.left,r.bottom);
                path.lineTo(pointEnd.x, pointEnd.y);
            }else if(pointEnd.x == r.right){
                path.lineTo(r.left,r.top);
                path.lineTo(r.left,r.bottom);
                path.lineTo(r.right,r.bottom);
                path.lineTo(pointEnd.x, pointEnd.y);
            }
        }
        else if(pointStart.x == r.left) {
            if(pointEnd.x == r.left){
                path.lineTo(pointEnd.x, pointEnd.y);
            }else if(pointEnd.y == r.bottom){
                path.lineTo(r.left,r.bottom);
                path.lineTo(pointEnd.x, pointEnd.y);
            }else if(pointEnd.x == r.right){
                path.lineTo(r.left,r.bottom);
                path.lineTo(r.right,r.bottom);
                path.lineTo(pointEnd.x, pointEnd.y);
            }else if(pointEnd.y == r.top){
                path.lineTo(r.left,r.bottom);
                path.lineTo(r.right,r.bottom);
                path.lineTo(r.right,r.top);
                path.lineTo(pointEnd.x, pointEnd.y);
            }
        }
        else if(pointStart.y == r.bottom) {
            if(pointEnd.y == r.bottom) {
                path.lineTo(pointEnd.x, pointEnd.y);
            }
            else if(pointEnd.x == r.right){
                path.lineTo(r.right,r.bottom);
                path.lineTo(pointEnd.x, pointEnd.y);
            }else if(pointEnd.y == r.top){
                path.lineTo(r.right,r.bottom);
                path.lineTo(r.right,r.top);
                path.lineTo(pointEnd.x, pointEnd.y);
            }else if(pointEnd.x == r.left){
                path.lineTo(r.right,r.bottom);
                path.lineTo(r.right,r.top);
                path.lineTo(r.left,r.top);
                path.lineTo(pointEnd.x, pointEnd.y);
            }
        }
    }

    @Override
    public void start() {
        super.start();
        mBrokenView.invalidate();
    }

    public boolean draw(Canvas canvas) {
        if (!isStarted()) {
            return false;
        }

        float fraction = getAnimatedFraction();
            int piecesNum = pieces.length;
            for(Piece p : pieces){
                if (p.bitmap != null && p.advance(fraction))
                    canvas.drawBitmap(p.bitmap, p.matrix, null);
                else
                    piecesNum--;
            }
            if(piecesNum == 0) {
                mBrokenView.onBrokenFallingEnd(mView);
            }
        mBrokenView.invalidate();
        return true;
    }
}