package com.huyn.demogroup.video;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * http://bigflake.com/mediacodec/EncodeAndMuxTest.java.txt
 * Created by huyaonan on 2017/12/12.
 */

public class EncodeVideoByOpenGL {

    private static final String TAG = "EncodeAndMuxTest";
    private static final boolean VERBOSE = true;           // lots of logging

    // parameters for the encoder
    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
    private static final int FRAME_RATE = 20;               // 20fps
    private static final int IFRAME_INTERVAL = 10;          // 10 seconds between I-frames

    // size of a frame, in pixels
    private int mWidth = -1;
    private int mHeight = -1;
    // bit rate, in bits per second
    private int mBitRate = -1;

    // encoder / muxer state
    private MediaCodec mEncoder;
    private CodecInputSurface mInputSurface;
    private MediaMuxer mMuxer;
    private int mTrackIndex;
    private boolean mMuxerStarted;

    // allocate one of these up front so we don't need to do it every time
    private MediaCodec.BufferInfo mBufferInfo;

    private String outputPath;

    public void testEncodeVideoToMp4(File srcFile, String targetFile) {
        long start = System.currentTimeMillis();
        try {
            File target = new File(targetFile);
            if(target.exists())
                target.delete();
            outputPath = targetFile;
            int width = 0;
            int height = 0;
            File[] files = srcFile.listFiles();
            for (int i = 0;i<files.length; i++) {
                if (!files[i].exists() || !(files[i].getName().endsWith(".png") || files[i].getName().endsWith(".jpg")))
                    break;

                Bitmap frame = BitmapFactory.decodeFile(files[i].getAbsolutePath());
                if(frame != null) {
                    width = frame.getWidth();
                    height = frame.getHeight();
                    break;
                }
            }
            testEncodeVideoToMp4(width, height, files);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "IO", e);
        }
        System.out.println("+++++cost : " + (System.currentTimeMillis() - start));
    }


    /**
     * Tests encoding of AVC video from a Surface.  The output is saved as an MP4 file.
     */
    public void testEncodeVideoToMp4(int width, int height, File[] files) {
        // QVGA at 2Mbps
        mWidth = width;
        mHeight = height;
        mBitRate = 2000000;

        try {
            prepareEncoder();
            mInputSurface.makeCurrent();

            for (int i = 0; i < files.length; i++) {
                // Feed any pending encoder output into the muxer.
                drainEncoder(false);

                System.out.println("encode : " + files[i].getName());

                Bitmap bitmap = BitmapFactory.decodeFile(files[i].getAbsolutePath());
                // Generate a new frame of input.
                generateSurfaceFrame(bitmap);
                mInputSurface.setPresentationTime(computePresentationTimeNsec(i));

                // Submit it to the encoder.  The eglSwapBuffers call will block if the input
                // is full, which would be bad if it stayed full until we dequeued an output
                // buffer (which we can't do, since we're stuck here).  So long as we fully drain
                // the encoder before supplying additional input, the system guarantees that we
                // can supply another frame without blocking.
                if (VERBOSE) Log.d(TAG, "sending frame " + i + " to encoder");
                mInputSurface.swapBuffers();
            }

            // send end-of-stream to encoder, and drain remaining output
            drainEncoder(true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // release encoder, muxer, and input Surface
           //releaseEncoder();
        }
        releaseEncoder();

        // To test the result, open the file with MediaExtractor, and get the format.  Pass
        // that into the MediaCodec decoder configuration, along with a SurfaceTexture surface,
        // and examine the output with glReadPixels.
    }

    /**
     * Configures encoder and muxer state, and prepares the input Surface.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void prepareEncoder() throws IOException {
        mBufferInfo = new MediaCodec.BufferInfo();

        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mWidth, mHeight);

        // Set some properties.  Failing to specify some of these can cause the MediaCodec
        // configure() call to throw an unhelpful exception.
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        if (VERBOSE) Log.d(TAG, "format: " + format);

        // Create a MediaCodec encoder, and configure it with our format.  Get a Surface
        // we can use for input and wrap it with a class that handles the EGL work.
        //
        // If you want to have two EGL contexts -- one for display, one for recording --
        // you will likely want to defer instantiation of CodecInputSurface until after the
        // "display" EGL context is created, then modify the eglCreateContext call to
        // take eglGetCurrentContext() as the share_context argument.
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mInputSurface = new CodecInputSurface(mEncoder.createInputSurface(), mWidth, mHeight);
        mEncoder.start();


        // Create a MediaMuxer.  We can't add the video track and start() the muxer here,
        // because our MediaFormat doesn't have the Magic Goodies.  These can only be
        // obtained from the encoder after it has started processing data.
        //
        // We're not actually interested in multiplexing audio.  We just want to convert
        // the raw H.264 elementary stream we get from MediaCodec into a .mp4 file.
        try {
            mMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException ioe) {
            throw new RuntimeException("MediaMuxer creation failed", ioe);
        }

        mTrackIndex = -1;
        mMuxerStarted = false;
    }

    /**
     * Releases encoder resources.  May be called after partial / failed initialization.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void releaseEncoder() {
        if (VERBOSE) Log.d(TAG, "releasing encoder objects");
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
        if (mInputSurface != null) {
            mInputSurface.release();
            mInputSurface = null;
        }
        if (mMuxer != null) {
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }
    }

    /**
     * Extracts all pending data from the encoder.
     * <p>
     * If endOfStream is not set, this returns when there is no more data to drain.  If it
     * is set, we send EOS to the encoder, and then iterate until we see EOS on the output.
     * Calling this with endOfStream set should be done once, right before stopping the muxer.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void drainEncoder(boolean endOfStream) {
        final int TIMEOUT_USEC = 10000;
        if (VERBOSE) Log.d(TAG, "drainEncoder(" + endOfStream + ")");

        if (endOfStream) {
            if (VERBOSE) Log.d(TAG, "sending EOS to encoder");
            mEncoder.signalEndOfInputStream();
        }

        ByteBuffer[] encoderOutputBuffers = mEncoder.getOutputBuffers();
        while (true) {
            int encoderStatus = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                if (!endOfStream) {
                    break;      // out of while
                } else {
                    if (VERBOSE) Log.d(TAG, "no output available, spinning to await EOS");
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not expected for an encoder
                encoderOutputBuffers = mEncoder.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
                if (mMuxerStarted) {
                    throw new RuntimeException("format changed twice");
                }
                MediaFormat newFormat = mEncoder.getOutputFormat();
                Log.d(TAG, "encoder output format changed: " + newFormat);

                // now that we have the Magic Goodies, start the muxer
                mTrackIndex = mMuxer.addTrack(newFormat);
                mMuxer.start();
                mMuxerStarted = true;
            } else if (encoderStatus < 0) {
                Log.w(TAG, "unexpected result from encoder.dequeueOutputBuffer: " +
                        encoderStatus);
                // let's ignore it
            } else {
                ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if (encodedData == null) {
                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus +
                            " was null");
                }

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // The codec config data was pulled out and fed to the muxer when we got
                    // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                    if (VERBOSE) Log.d(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
                    mBufferInfo.size = 0;
                }

                if (mBufferInfo.size != 0) {
                    if (!mMuxerStarted) {
                        throw new RuntimeException("muxer hasn't started");
                    }

                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                    encodedData.position(mBufferInfo.offset);
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size);

                    mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
                    if (VERBOSE) Log.d(TAG, "sent " + mBufferInfo.size + " bytes to muxer");
                }

                mEncoder.releaseOutputBuffer(encoderStatus, false);

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (!endOfStream) {
                        Log.w(TAG, "reached end of stream unexpectedly");
                    } else {
                        if (VERBOSE) Log.d(TAG, "end of stream reached");
                    }
                    break;      // out of while
                }
            }
        }
    }

    private void generateSurfaceFrame(Bitmap bitmap) {
        mInputSurface.drawImage(bitmap);
    }

    /**
     * Generates the presentation time for frame N, in nanoseconds.
     */
    private static long computePresentationTimeNsec(int frameIndex) {
        final long ONE_BILLION = 1000000000;
        return frameIndex * ONE_BILLION / FRAME_RATE;
    }


    /**
     * Holds state associated with a Surface used for MediaCodec encoder input.
     * <p>
     * The constructor takes a Surface obtained from MediaCodec.createInputSurface(), and uses that
     * to create an EGL window surface.  Calls to eglSwapBuffers() cause a frame of data to be sent
     * to the video encoder.
     * <p>
     * This object owns the Surface -- releasing this will release the Surface too.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static class CodecInputSurface {
        private static final int EGL_RECORDABLE_ANDROID = 0x3142;

        private STextureRender mTextureRender;
        private Surface mSurface;

        EGLDisplay mEGLDisplay;
        EGLContext mEGLContext;
        EGLSurface mEGLSurface;

        int mWidth;
        int mHeight;

        private final FloatBuffer mGLCubeBuffer;
        private final FloatBuffer mGLTextureBuffer;
        static final float CUBE[] = {
                -1.0f, -1.0f,
                1.0f, -1.0f,
                -1.0f, 1.0f,
                1.0f, 1.0f,
        };
        private Rotation mRotation;
        private boolean mFlipHorizontal;
        private boolean mFlipVertical;

        private int mOutputWidth;
        private int mOutputHeight;
        private int mImageWidth;
        private int mImageHeight;

        private float mBackgroundRed = 0;
        private float mBackgroundGreen = 0;
        private float mBackgroundBlue = 0;

        private int mTextureID = OpenGlUtils.NO_TEXTURE;

        /**
         * Creates a CodecInputSurface from a Surface.
         */
        public CodecInputSurface(Surface surface, int w, int h) {
            if (surface == null) {
                throw new NullPointerException();
            }
            mSurface = surface;
            mOutputWidth = w;
            mOutputHeight = h;
            mImageWidth = w;
            mImageHeight = h;
            mWidth = w;
            mHeight = h;

            mGLCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mGLCubeBuffer.put(CUBE).position(0);

            mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            setRotation(Rotation.NORMAL, false, false);

            eglSetup();
            makeCurrent();
            setup();
        }

        public void setRotation(final Rotation rotation) {
            mRotation = rotation;
            adjustImageScaling();
        }

        public void setRotation(final Rotation rotation,
                                final boolean flipHorizontal, final boolean flipVertical) {
            mFlipHorizontal = flipHorizontal;
            mFlipVertical = flipVertical;
            setRotation(rotation);
        }


        private void adjustImageScaling() {
            float outputWidth = mOutputWidth;
            float outputHeight = mOutputHeight;
            if (mRotation == Rotation.ROTATION_270 || mRotation == Rotation.ROTATION_90) {
                outputWidth = mOutputHeight;
                outputHeight = mOutputWidth;
            }

            float ratio1 = outputWidth / mImageWidth;
            float ratio2 = outputHeight / mImageHeight;
            float ratioMax = Math.max(ratio1, ratio2);
            int imageWidthNew = Math.round(mImageWidth * ratioMax);
            int imageHeightNew = Math.round(mImageHeight * ratioMax);

            float ratioWidth = imageWidthNew / outputWidth;
            float ratioHeight = imageHeightNew / outputHeight;

            float[] cube = CUBE;
            float[] textureCords = TextureRotationUtil.getRotation(mRotation, mFlipHorizontal, mFlipVertical);

            float distHorizontal = (1 - 1 / ratioWidth) / 2;
            float distVertical = (1 - 1 / ratioHeight) / 2;
            textureCords = new float[]{
                    addDistance(textureCords[0], distHorizontal), addDistance(textureCords[1], distVertical),
                    addDistance(textureCords[2], distHorizontal), addDistance(textureCords[3], distVertical),
                    addDistance(textureCords[4], distHorizontal), addDistance(textureCords[5], distVertical),
                    addDistance(textureCords[6], distHorizontal), addDistance(textureCords[7], distVertical),
            };

            mGLCubeBuffer.clear();
            mGLCubeBuffer.put(cube).position(0);
            mGLTextureBuffer.clear();
            mGLTextureBuffer.put(textureCords).position(0);
        }

        private float addDistance(float coordinate, float distance) {
            return coordinate == 0.0f ? distance : 1 - distance;
        }

        /**
         * Creates interconnected instances of TextureRender, SurfaceTexture, and Surface.
         */
        private void setup() {
            mTextureRender = new STextureRender(mWidth, mHeight);
            onSurfaceCreated();
            onSurfaceChanged();
        }

        /**
         * Prepares EGL.  We want a GLES 2.0 context and a surface that supports recording.
         */
        private void eglSetup() {
            mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
            if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
                throw new RuntimeException("unable to get EGL14 display");
            }
            int[] version = new int[2];
            if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
                throw new RuntimeException("unable to initialize EGL14");
            }

            // Configure EGL for recording and OpenGL ES 2.0.
            int[] attribList = {
                    EGL14.EGL_RED_SIZE, 8,
                    EGL14.EGL_GREEN_SIZE, 8,
                    EGL14.EGL_BLUE_SIZE, 8,
                    EGL14.EGL_ALPHA_SIZE, 8,
                    EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                    EGL_RECORDABLE_ANDROID, 1,
                    EGL14.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] numConfigs = new int[1];
            EGL14.eglChooseConfig(mEGLDisplay, attribList, 0, configs, 0, configs.length,
                    numConfigs, 0);
            checkEglError("eglCreateContext RGB888+recordable ES2");

            // Configure context for OpenGL ES 2.0.
            int[] attrib_list = {
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                    EGL14.EGL_NONE
            };
            mEGLContext = EGL14.eglCreateContext(mEGLDisplay, configs[0], EGL14.EGL_NO_CONTEXT,
                    attrib_list, 0);
            checkEglError("eglCreateContext");

            // Create a window surface, and attach it to the Surface we received.
            int[] surfaceAttribs = {
                    EGL14.EGL_NONE
            };
            mEGLSurface = EGL14.eglCreateWindowSurface(mEGLDisplay, configs[0], mSurface,
                    surfaceAttribs, 0);
            checkEglError("eglCreateWindowSurface");
        }

        public void onSurfaceCreated() {
            GLES20.glClearColor(mBackgroundRed, mBackgroundGreen, mBackgroundBlue, 1);
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            mTextureRender.surfaceCreated();
        }

        public void onSurfaceChanged() {
            GLES20.glViewport(0, 0, mWidth, mHeight);
            mTextureRender.surfaceChanged();
            adjustImageScaling();
        }

        /**
         * Discards all resources held by this class, notably the EGL context.  Also releases the
         * Surface that was passed to our constructor.
         */
        public void release() {
            try {
                if (mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
                    EGL14.eglMakeCurrent(mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                            EGL14.EGL_NO_CONTEXT);
                    EGL14.eglDestroySurface(mEGLDisplay, mEGLSurface);
                    EGL14.eglDestroyContext(mEGLDisplay, mEGLContext);
                    EGL14.eglReleaseThread();
                    EGL14.eglTerminate(mEGLDisplay);
                }

                mSurface.release();

                mEGLDisplay = EGL14.EGL_NO_DISPLAY;
                mEGLContext = EGL14.EGL_NO_CONTEXT;
                mEGLSurface = EGL14.EGL_NO_SURFACE;

                mSurface = null;
                mTextureRender = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Draws the data from SurfaceTexture onto the current EGL surface.
         */
        public void drawImage(Bitmap bitmap) {
            checkGlError("onDrawFrame clear");
            mTextureID = OpenGlUtils.loadTexture(bitmap, mTextureID, false);
            if (bitmap != null) {
                bitmap.recycle();
            }

            checkGlError("loadTexture");
            mTextureRender.drawFrame(mTextureID, mGLCubeBuffer, mGLTextureBuffer);
        }

        /**
         * Makes our EGL context and surface current.
         */
        public void makeCurrent() {
            EGL14.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext);
            checkEglError("eglMakeCurrent");
        }

        /**
         * Calls eglSwapBuffers.  Use this to "publish" the current frame.
         */
        public boolean swapBuffers() {
            boolean result = EGL14.eglSwapBuffers(mEGLDisplay, mEGLSurface);
            checkEglError("eglSwapBuffers");
            return result;
        }

        /**
         * Sends the presentation time stamp to EGL.  Time is expressed in nanoseconds.
         */
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void setPresentationTime(long nsecs) {
            EGLExt.eglPresentationTimeANDROID(mEGLDisplay, mEGLSurface, nsecs);
            checkEglError("eglPresentationTimeANDROID");
        }

    }

        /**
     * Code for rendering a texture onto a surface using OpenGL ES 2.0.
     */
    private static class STextureRender {

        public static final String NO_FILTER_VERTEX_SHADER = "" +
                "attribute vec4 position;\n" +
                "attribute vec4 inputTextureCoordinate;\n" +
                " \n" +
                "varying vec2 textureCoordinate;\n" +
                " \n" +
                "void main()\n" +
                "{\n" +
                "    gl_Position = position;\n" +
                "    textureCoordinate = inputTextureCoordinate.xy;\n" +
                "}";
        public static final String NO_FILTER_FRAGMENT_SHADER = "" +
                "varying highp vec2 textureCoordinate;\n" +
                " \n" +
                "uniform sampler2D inputImageTexture;\n" +
                " \n" +
                "void main()\n" +
                "{\n" +
                "     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "}";

        private int mProgram;
        private int mWidth, mHeight;

        protected int mGLAttribPosition;
        protected int mGLUniformTexture;
        protected int mGLAttribTextureCoordinate;

        public STextureRender(int w, int h) {
            mWidth = w;
            mHeight = h;
        }

        /**
         * Draws the external texture in SurfaceTexture onto the current EGL surface.
         */
        public void drawFrame(int mTextureID, final FloatBuffer cubeBuffer, final FloatBuffer textureBuffer) {
            checkGlError("onDrawFrame start");

            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            GLES20.glUseProgram(mProgram);
            checkGlError("glUseProgram");

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
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            GLES20.glDisableVertexAttribArray(mGLAttribPosition);
            GLES20.glDisableVertexAttribArray(mGLAttribTextureCoordinate);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }

        /**
         * Initializes GL state.  Call this after the EGL surface has been created and made current.
         */
        public void surfaceCreated() {
            mProgram = OpenGlUtils.loadProgram(NO_FILTER_VERTEX_SHADER, NO_FILTER_FRAGMENT_SHADER);

            if (mProgram == 0) {
                throw new RuntimeException("failed creating program");
            }

            mGLAttribPosition = GLES20.glGetAttribLocation(mProgram, "position");
            mGLUniformTexture = GLES20.glGetUniformLocation(mProgram, "inputImageTexture");
            mGLAttribTextureCoordinate = GLES20.glGetAttribLocation(mProgram,
                    "inputTextureCoordinate");

            //GLES20.glUseProgram(mProgram);
        }

        public void surfaceChanged() {
            GLES20.glUseProgram(mProgram);
        }

    }

    /**
     * Checks for EGL errors.  Throws an exception if one is found.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static void checkEglError(String msg) {
        checkGlError(msg);
    }

    public static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

}
