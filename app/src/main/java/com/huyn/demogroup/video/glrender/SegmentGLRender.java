package com.huyn.demogroup.video.glrender;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;

import com.huyn.demogroup.video.OpenGlUtils;
import com.huyn.demogroup.video.Rotation;
import com.huyn.demogroup.video.TextureRotationUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by huyaonan on 2017/12/15.
 * Code for rendering a texture onto a surface using OpenGL ES 2.0.
 */
public class SegmentGLRender {

    private static final String TAG = "SegmentGLRender";

    private static final String VERTEX_SHADER = "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "attribute vec4 inputTextureCoordinate2;\n" +
            " \n" +
            "varying vec2 textureCoordinate;\n" +
            "varying vec2 textureCoordinate2;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "    textureCoordinate = inputTextureCoordinate.xy;\n" +
            "    textureCoordinate2 = inputTextureCoordinate2.xy;\n" +
            "}";

    private static final String SEGMENT_FRAGMENT_SHADER = "" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform sampler2D inputImageTexture2;\n" +
            " \n" +
            "varying highp vec2 textureCoordinate;\n" +
            "varying highp vec2 textureCoordinate2;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "    lowp vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "    gl_FragColor = vec4(mix(textureColor.rgb, textureColor2.rgb, 1.0-textureColor2.r), textureColor.a);\n" +
            "}";

    public int mFilterSecondTextureCoordinateAttribute;
    public int mFilterInputTextureUniform2;
    public int mFilterSourceTexture2 = OpenGlUtils.NO_TEXTURE;
    private ByteBuffer mTexture2CoordinatesBuffer;

    private int mProgram;

    protected int mGLAttribPosition;
    protected int mGLUniformTexture;
    protected int mGLAttribTextureCoordinate;

    public SegmentGLRender() {
        setRotation(Rotation.NORMAL, false, false);
    }

    public void setRotation(final Rotation rotation, final boolean flipHorizontal, final boolean flipVertical) {
        float[] buffer = TextureRotationUtil.getRotation(rotation, flipHorizontal, flipVertical);

        ByteBuffer bBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder());
        FloatBuffer fBuffer = bBuffer.asFloatBuffer();
        fBuffer.put(buffer);
        fBuffer.flip();

        mTexture2CoordinatesBuffer = bBuffer;
    }

    protected void onDrawArraysPre() {
        GLES20.glEnableVertexAttribArray(mFilterSecondTextureCoordinateAttribute);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFilterSourceTexture2);
        GLES20.glUniform1i(mFilterInputTextureUniform2, 3);

        mTexture2CoordinatesBuffer.position(0);
        GLES20.glVertexAttribPointer(mFilterSecondTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, mTexture2CoordinatesBuffer);
    }

    /**
     * Draws the external texture in SurfaceTexture onto the current EGL surface.
     */
    public void drawFrame(int mTextureID, final FloatBuffer cubeBuffer, final FloatBuffer textureBuffer, Bitmap bitmap) {
        checkGlError("onDrawFrame start");
        GLES20.glUseProgram(mProgram);
        checkGlError("glUseProgram");
        runPendingOnDrawTasks(bitmap);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        checkGlError("glActiveTexture");
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);

        checkGlError("glBindTexture");

        cubeBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribPosition, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribPosition);
        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0,
                textureBuffer);
        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate);
        if (mTextureID != OpenGlUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);
            GLES20.glUniform1i(mGLUniformTexture, 0);
        }
        onDrawArraysPre();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mGLAttribPosition);
        GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    private void runPendingOnDrawTasks(Bitmap bitmap) {
        if (mFilterSourceTexture2 == OpenGlUtils.NO_TEXTURE) {
            if (bitmap == null || bitmap.isRecycled()) {
                return;
            }
            GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        }
        mFilterSourceTexture2 = OpenGlUtils.loadTexture(bitmap, mFilterSourceTexture2, false);
    }

    /**
     * Initializes GL state.  Call this after the EGL surface has been created and made current.
     */
    public void surfaceCreated() {
        mProgram = OpenGlUtils.loadProgram(VERTEX_SHADER, SEGMENT_FRAGMENT_SHADER);

        if (mProgram == 0) {
            throw new RuntimeException("failed creating program");
        }

        mGLAttribPosition = GLES20.glGetAttribLocation(mProgram, "position");
        mGLUniformTexture = GLES20.glGetUniformLocation(mProgram, "inputImageTexture");
        mGLAttribTextureCoordinate = GLES20.glGetAttribLocation(mProgram,
                "inputTextureCoordinate");

        mFilterSecondTextureCoordinateAttribute = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate2");
        mFilterInputTextureUniform2 = GLES20.glGetUniformLocation(mProgram, "inputImageTexture2"); // This does assume a name of "inputImageTexture2" for second input texture in the fragment shader
        GLES20.glEnableVertexAttribArray(mFilterSecondTextureCoordinateAttribute);

        //GLES20.glUseProgram(mProgram);
    }

    public void surfaceChanged() {
        GLES20.glUseProgram(mProgram);
    }

    public void onDestroy() {
        GLES20.glDeleteTextures(1, new int[]{
                mFilterSourceTexture2
        }, 0);
        mFilterSourceTexture2 = OpenGlUtils.NO_TEXTURE;
    }

    public static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

}
