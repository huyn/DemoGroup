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
public class SegmentAndMixGLRender {

    private static final String TAG = "SegmentGLRender";

    private static final String VERTEX_SHADER = "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "attribute vec4 inputTextureCoordinate2;\n" +
            "attribute vec4 inputTextureCoordinate3;\n" +
            " \n" +
            "varying vec2 textureCoordinate;\n" +
            "varying vec2 textureCoordinate2;\n" +
            "varying vec2 textureCoordinate3;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "    textureCoordinate = inputTextureCoordinate.xy;\n" +
            "    textureCoordinate2 = inputTextureCoordinate2.xy;\n" +
            "    textureCoordinate3 = inputTextureCoordinate3.xy;\n" +
            "}";

    private static final String SEGMENT_AND_MIX_FRAGMENT_SHADER = "" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform sampler2D inputImageTexture2;\n" +
            "uniform sampler2D inputImageTexture3;\n" +
            " \n" +
            "varying highp vec2 textureCoordinate;\n" +
            "varying highp vec2 textureCoordinate2;\n" +
            "varying highp vec2 textureCoordinate3;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "    lowp vec4 textureColor2 = texture2D(inputImageTexture2, textureCoordinate2);\n" +
            "    lowp vec4 textureColor3 = texture2D(inputImageTexture3, textureCoordinate3);\n" +
            "    gl_FragColor = vec4(mix(textureColor3.rgb, textureColor2.rgb, 1.0-textureColor2.r), textureColor3.a);\n" +
            "    gl_FragColor = vec4(mix(textureColor.rgb, gl_FragColor.rgb, textureColor2.r * 0.9), textureColor.a);\n" +
            "}";

    public int mFilterSecondTextureCoordinateAttribute;
    public int mFilterInputTextureUniform2;
    public int mFilterSourceTexture2 = OpenGlUtils.NO_TEXTURE;
    private ByteBuffer mTexture2CoordinatesBuffer;

    public int mFilterThirdTextureCoordinateAttribute;
    public int mFilterInputTextureUniform3;
    public int mFilterSourceTexture3 = OpenGlUtils.NO_TEXTURE;
    private ByteBuffer mTexture3CoordinatesBuffer;

    private int mProgram;

    protected int mGLAttribPosition;
    protected int mGLUniformTexture;
    protected int mGLAttribTextureCoordinate;

    public SegmentAndMixGLRender() {
        setRotation(Rotation.NORMAL, false, false);
    }

    public void setRotation(final Rotation rotation, final boolean flipHorizontal, final boolean flipVertical) {
        float[] buffer = TextureRotationUtil.getRotation(rotation, flipHorizontal, flipVertical);

        ByteBuffer bBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder());
        FloatBuffer fBuffer = bBuffer.asFloatBuffer();
        fBuffer.put(buffer);
        fBuffer.flip();

        mTexture2CoordinatesBuffer = bBuffer;

        float[] buffer2 = TextureRotationUtil.getRotation(rotation, flipHorizontal, flipVertical);

        ByteBuffer bBuffer2 = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder());
        FloatBuffer fBuffer2 = bBuffer2.asFloatBuffer();
        fBuffer2.put(buffer2);
        fBuffer2.flip();

        mTexture3CoordinatesBuffer = bBuffer2;
    }

    protected void onDrawArraysPre() {
        GLES20.glEnableVertexAttribArray(mFilterSecondTextureCoordinateAttribute);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFilterSourceTexture2);
        GLES20.glUniform1i(mFilterInputTextureUniform2, 3);

        mTexture2CoordinatesBuffer.position(0);
        GLES20.glVertexAttribPointer(mFilterSecondTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, mTexture2CoordinatesBuffer);

        GLES20.glEnableVertexAttribArray(mFilterThirdTextureCoordinateAttribute);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFilterSourceTexture3);
        GLES20.glUniform1i(mFilterInputTextureUniform3, 4);

        mTexture3CoordinatesBuffer.position(0);
        GLES20.glVertexAttribPointer(mFilterThirdTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, mTexture3CoordinatesBuffer);
    }

    /**
     * Draws the external texture in SurfaceTexture onto the current EGL surface.
     */
    public void drawFrame(int mTextureID, final FloatBuffer cubeBuffer, final FloatBuffer textureBuffer, Bitmap bitmap, Bitmap styledBitmap) {
        checkGlError("onDrawFrame start");
        GLES20.glUseProgram(mProgram);
        checkGlError("glUseProgram");
        runPendingOnDrawTasks(bitmap, styledBitmap);

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

    private void runPendingOnDrawTasks(Bitmap bitmap, Bitmap styledBitmap) {
        if (mFilterSourceTexture2 == OpenGlUtils.NO_TEXTURE) {
            if (bitmap == null || bitmap.isRecycled()) {
                return;
            }
            GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        }
        mFilterSourceTexture2 = OpenGlUtils.loadTexture(bitmap, mFilterSourceTexture2, false);

        if (mFilterSourceTexture3 == OpenGlUtils.NO_TEXTURE) {
            if (styledBitmap == null || styledBitmap.isRecycled()) {
                return;
            }
            GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
        }
        mFilterSourceTexture3 = OpenGlUtils.loadTexture(styledBitmap, mFilterSourceTexture3, false);
    }

    /**
     * Initializes GL state.  Call this after the EGL surface has been created and made current.
     */
    public void surfaceCreated() {
        mProgram = OpenGlUtils.loadProgram(VERTEX_SHADER, SEGMENT_AND_MIX_FRAGMENT_SHADER);

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

        mFilterThirdTextureCoordinateAttribute = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate3");
        mFilterInputTextureUniform3 = GLES20.glGetUniformLocation(mProgram, "inputImageTexture3"); // This does assume a name of "inputImageTexture3" for second input texture in the fragment shader
        GLES20.glEnableVertexAttribArray(mFilterThirdTextureCoordinateAttribute);

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

        GLES20.glDeleteTextures(1, new int[]{
                mFilterSourceTexture3
        }, 0);
        mFilterSourceTexture3 = OpenGlUtils.NO_TEXTURE;
    }

    public static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

}
