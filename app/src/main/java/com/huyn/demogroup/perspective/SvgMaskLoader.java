package com.huyn.demogroup.perspective;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huyaonan on 2017/10/27.
 */

public class SvgMaskLoader {

    public static void loadSvgMasks(Context context, int resId, int width, int height, OnSvgLoadListener listener) {
        new SvgLoader(context, resId, width, height, listener).execute();
    }

    public static void loadSvgMasks(Context context, int[] resId, int width, int height, OnSvgLoadListener listener) {
        new SvgListLoader(context, resId, width, height, listener).execute();
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

    private static class SvgListLoader extends AsyncTask<Void, Void, List<List<SvgUtils.SvgPath>>> {

        private int mWidth, mHeight;
        private int[] mResIds;
        private Context mContext;
        private OnSvgLoadListener mListener;

        public SvgListLoader(Context context, int[] resIds, int width, int height, OnSvgLoadListener listener) {
            this.mContext = context;
            this.mListener = listener;
            this.mResIds = resIds;
            this.mWidth = width;
            this.mHeight = height;
        }

        @Override
        protected List<List<SvgUtils.SvgPath>> doInBackground(Void... params) {
            try {
                List<List<SvgUtils.SvgPath>> result = new ArrayList<>();
                for(int index=0; index<mResIds.length; index++) {
                    SvgUtils svgUtils = new SvgUtils();
                    svgUtils.load(mContext, mResIds[index]);

                    List<SvgUtils.SvgPath> paths = svgUtils.getPathsForViewport(mWidth, mHeight);

                    final int count = paths.size();
                    for (int i = 0; i < count; i++) {
                        SvgUtils.SvgPath svgPath = paths.get(i);
                        svgPath.path.reset();
                        svgPath.measure.getSegment(0.0f, svgPath.length, svgPath.path, true);
                        // Required only for Android 4.4 and earlier
                        svgPath.path.rLineTo(0.0f, 0.0f);
                    }

                    result.add(paths);
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<List<SvgUtils.SvgPath>> paths) {
            if(mListener == null)
                return;
            if(paths != null) {
                mListener.onSvgListLoadSuccess(paths);
            } else {
                mListener.onSvgLoadFail();
            }
        }

    }

    public interface OnSvgLoadListener {
        public void onSvgLoadSuccess(List<SvgUtils.SvgPath> paths);
        public void onSvgListLoadSuccess(List<List<SvgUtils.SvgPath>> paths);
        public void onSvgLoadFail();
    }

}
