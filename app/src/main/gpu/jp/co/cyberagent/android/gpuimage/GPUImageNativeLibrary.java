/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.cyberagent.android.gpuimage;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;

public class GPUImageNativeLibrary {
    static {
        System.loadLibrary("versa");
    }

    public static native void YUVtoRBGA(byte[] yuv, int width, int height, int[] out);

    public static native void YUVtoARBG(byte[] yuv, int width, int height, int[] out);

    public static native void operateBitmap(Bitmap bitmap);

    public static native void changeBitmapOutline(Bitmap bitmap, int color);

    public static native void segmentAll(Bitmap bitmap);
    public static native void segmentItem(Bitmap bitmap, int type);
    public static native void segmentGroup(Bitmap bitmap, int[] types, int size);

    public static native void updateBitmap(Bitmap bitmap, int[] buffer);

    public static native void updateBitmapNative(Bitmap bitmap, int x, int y, int width, int height, int format, int type);

    public static native void updateBitmapInShort(Bitmap bitmap, short[] buffer);

    public static native void updateBitmapInByte(Bitmap bitmap, byte[] buffer);

    public static native void glReadPixels(int x, int y, int width, int height, int format, int type);

    public static native ByteBuffer storeBitmapData(Bitmap bitmap);

    public static native void mergeYUVToNewBitmap(Bitmap bitmap, ByteBuffer ySrc, ByteBuffer uvSrc);

    public static native void segmentBitmap(Bitmap bitmap, ByteBuffer origin, ByteBuffer mask);

    public static native void freeBitmapData(ByteBuffer byteBuffer);

}
