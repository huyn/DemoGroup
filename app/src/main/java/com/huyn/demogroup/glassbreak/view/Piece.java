package com.huyn.demogroup.glassbreak.view;

import android.graphics.Bitmap;
import android.graphics.Matrix;

class Piece implements Comparable {
    Bitmap bitmap;
    Matrix matrix;
    private int x;
    private int y;
    private int rotateX;
    private int rotateY;
    private float angle;
    private float speed;
    private int shadow;
    private int limitY;
    public Piece(int x, int y, Bitmap bitmap, int shadow){
        this.bitmap = bitmap;
        this.x = x;
        this.y = y;
        this.shadow = shadow;

        if(bitmap != null) {
            matrix = new Matrix();
            matrix.postTranslate(x, y);

            speed = BrokenUtils.nextFloat(1,4);
            rotateX = BrokenUtils.nextInt(bitmap.getWidth());
            rotateY = BrokenUtils.nextInt(bitmap.getHeight());
            angle = BrokenUtils.nextFloat(0.6f) * (BrokenUtils.nextBoolean() ? 1 : -1);

            int bitmapW = bitmap.getWidth();
            int bitmapH = bitmap.getHeight();
            limitY = bitmapW > bitmapH ? bitmapW : bitmapH;
            limitY += BrokenUtils.screenHeight;
        }
    }

    @Override
    public int compareTo(Object another) {
        return shadow - ((Piece)another).shadow;
    }

    public boolean advance(float fraction){
        float s = (float) Math.pow(fraction * 1.1226f, 2) * 8 * speed;
        float zy =  y + s * BrokenUtils.screenHeight / 10;
        float r = fraction * fraction;

        matrix.reset();
        matrix.setRotate(angle * r * 360, rotateX, rotateY);
        matrix.postTranslate(x, zy);
        if(zy <= limitY)
            return true;
        else
            return false;
    }
}
