package com.huyn.demogroup.opengl;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by huyaonan on 16/11/4.
 */
public class FlatColorSquare extends Square {
    @Override
    public void draw(GL10 gl) {
        gl.glColor4f(0.5f, 0.5f, 1f, 1f);
        super.draw(gl);
    }
}
