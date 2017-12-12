package com.huyn.demogroup.video;

import android.hardware.Camera;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by huyaonan on 17/2/8.
 */
public class CamParaUtil {

    private static final String TAG = "CamParaUtil";
    private CameraSizeComparator sizeComparator = new CameraSizeComparator();
    private static CamParaUtil myCamPara = null;

    private CamParaUtil() {

    }

    public static CamParaUtil getInstance() {
        if (myCamPara == null) {
            myCamPara = new CamParaUtil();
            return myCamPara;
        } else {
            return myCamPara;
        }
    }

    public int[] getPropVideoSize(List<Camera.Size> list, float th) {
        Collections.sort(list, sizeComparator);

        int MINWIDTH = 400;

        Camera.Size result=null;
        int differ = 1000;
        for (Camera.Size s : list) {
            Log.i(TAG, "RecorderSize:w = " + s.width + "h = " + s.height + "..." + MINWIDTH);
            if ((s.height >= MINWIDTH) && equalRate(s, th)) {
                if(Math.abs(s.height - MINWIDTH) < differ) {
                    differ = Math.abs(s.height - MINWIDTH);
                    result = s;
                }
            }
        }
        if(result == null)
            result = list.get(0);
        Log.i(TAG, "RecorderSize result :w = " + result.width + "h = " + result.height);
        return new int[]{result.height, result.width};
    }

    public Camera.Size getPropPreviewSize(List<Camera.Size> list, float th, int minWidth) {
        Collections.sort(list, sizeComparator);

        int i = 0;
        for (Camera.Size s : list) {
            Log.i(TAG, "PreviewSize : w = " + s.width + " h = " + s.height);
            if ((s.width >= minWidth) && equalRate(s, th)) {
                //Log.i(TAG, "PreviewSize:w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }
        if (i == list.size()) {
            i = 0;//如果没找到，就选最小的size
        }
        return list.get(i);
    }

    public Camera.Size getPropPictureSize(List<Camera.Size> list, float th, int minWidth, int screenWidth) {
        Collections.sort(list, sizeComparator);

        List<Camera.Size> targetSizes = new ArrayList<>();
        for (Camera.Size s : list) {
            //Log.i(TAG, "all pictureSizes:width = "+ s.width +" height = " + s.height + " rate=" + (s.width*1f/s.height) + "/" + th);
            if ((s.width >= minWidth) && equalRate(s, th)) {
                targetSizes.add(s);
            }
        }
        Camera.Size target = list.get(0);
        for(int i=0; i < targetSizes.size(); i++) {
            //Log.i(TAG, "pictureSizes:width = "+ targetSizes.get(i).width +" height = " + targetSizes.get(i).height + " screenWidth=" + screenWidth);
            if(i == 0)
                target = targetSizes.get(i);
            else {
                if(Math.abs(target.width-screenWidth) > Math.abs(targetSizes.get(i).width-screenWidth))
                    target = targetSizes.get(i);
            }
        }
        Log.i(TAG, "PictureSize : w = " + target.width + " h = " + target.height);
        return target;
    }

    public boolean equalRate(Camera.Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        if (Math.abs(r - rate) <= 0.03) {
            return true;
        } else {
            return false;
        }
    }

    public class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            if (lhs.width == rhs.width) {
                return 0;
            } else if (lhs.width > rhs.width) {
                return -1;
            } else {
                return 1;
            }
        }

    }

    /**
     * 打印支持的previewSizes
     *
     * @param params
     */
    public List<Camera.Size> printSupportPreviewSize(Camera.Parameters params) {
        List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
        for (int i = 0; i < previewSizes.size(); i++) {
            Camera.Size size = previewSizes.get(i);
            Log.i(TAG, "previewSizes:width = "+size.width+" height = "+size.height);
        }
        return previewSizes;
    }

    /**
     * 打印支持的pictureSizes
     *
     * @param params
     */
    public List<Camera.Size> printSupportPictureSize(Camera.Parameters params) {
        List<Camera.Size> pictureSizes = params.getSupportedPictureSizes();
        for (int i = 0; i < pictureSizes.size(); i++) {
            Camera.Size size = pictureSizes.get(i);
            Log.i(TAG, "pictureSizes:width = "+ size.width +" height = " + size.height);
        }
        return pictureSizes;
    }

    /**
     * 打印支持的聚焦模式
     *
     * @param params
     */
    public void printSupportFocusMode(Camera.Parameters params) {
        List<String> focusModes = params.getSupportedFocusModes();
        for (String mode : focusModes) {
            Log.i(TAG, "focusModes--" + mode);
        }
    }
}
