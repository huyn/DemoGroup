package com.huyn.demogroup.perspective;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by huyaonan on 2017/10/27.
 */

public class SvgMaskLoader {

    public static void loadSvgMasks(Context context, int resId, int width, int height, OnSvgLoadListener listener) {
        new SvgLoader(context, resId, width, height, listener).execute();
    }

    private static class SvgLoader extends AsyncTask<Void, Void, List<SvgUtils.SvgPath>> {

        private int mWidth, mHeight;
        private int mResId;
        private Context mContext;
        private OnSvgLoadListener mListener;

        public SvgLoader(Context context, int resId, int width, int height, OnSvgLoadListener listener) {
            this.mContext = context;
            this.mListener = listener;
            this.mResId = resId;
            this.mWidth = width;
            this.mHeight = height;
        }

        @Override
        protected List<SvgUtils.SvgPath> doInBackground(Void... params) {
            try {
                SvgUtils svgUtils = new SvgUtils();
                svgUtils.load(mContext, mResId);

                List<SvgUtils.SvgPath> paths = svgUtils.getPathsForViewport(mWidth, mHeight);

                final int count = paths.size();
                for (int i = 0; i < count; i++) {
                    SvgUtils.SvgPath svgPath = paths.get(i);
                    svgPath.path.reset();
                    svgPath.measure.getSegment(0.0f, svgPath.length, svgPath.path, true);
                    // Required only for Android 4.4 and earlier
                    svgPath.path.rLineTo(0.0f, 0.0f);
                }

                return paths;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<SvgUtils.SvgPath> paths) {
            if(mListener == null)
                return;
            if(paths != null) {
                mListener.onSvgLoadSuccess(paths);
            } else {
                mListener.onSvgLoadFail();
            }
        }

    }

    public static interface OnSvgLoadListener {
        public void onSvgLoadSuccess(List<SvgUtils.SvgPath> paths);
        public void onSvgLoadFail();
    }

}
