package com.huyn.demogroup.perspective;

import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.helper.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static void loadSvgMasks(String path, int width, int height, OnSvgLoadListener listener) {
        new SvgFilesLoader(path, width, height, listener).execute();
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

    private static class SvgFilesLoader extends AsyncTask<Void, Void, List<List<SvgUtils.SvgPath>>> {

        private int mWidth, mHeight;
        private String mPath;
        private OnSvgLoadListener mListener;

        public SvgFilesLoader(String path, int width, int height, OnSvgLoadListener listener) {
            this.mListener = listener;
            this.mPath = path;
            this.mWidth = width;
            this.mHeight = height;
        }

        @Override
        protected List<List<SvgUtils.SvgPath>> doInBackground(Void... params) {
            try {
                List<List<SvgUtils.SvgPath>> result = new ArrayList<>();
                File file = new File(mPath);
                if(!file.exists() || file.listFiles() == null || file.listFiles().length == 0)
                    return null;
                File[] childFiles = file.listFiles();

                List<File> files = sort(childFiles);

                for(int index=0; index<files.size(); index++) {
                    file = files.get(index);
                    if(file.isDirectory())
                        continue;
                    System.out.println("+++build svg from " + file.getName());
                    SvgUtils svgUtils = new SvgUtils();
                    svgUtils.load(file);

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

        private List<File> sort(File[] files) {
            List<File> result = new ArrayList<>();
            for(int i=0 ;i< files.length; i++) {
                String name = files[i].getName();
                if(files[i].isDirectory() || StringUtil.isBlank(name) || name.startsWith("."))
                    continue;
                result.add(files[i]);
            }
            Collections.sort(result, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    String name1 = o1.getName();
                    String name2 = o2.getName();
                    String regEx="[^0-9]";
                    Pattern p = Pattern.compile(regEx);
                    Matcher m1 = p.matcher(name1);
                    Matcher m2 = p.matcher(name2);
                    String num1 = m1.replaceAll("").trim();
                    String num2 = m2.replaceAll("").trim();
                    int n1 = Integer.parseInt(num1);
                    int n2 = Integer.parseInt(num2);
                    return n1 > n2 ? 1 : -1;
                }
            });
            return result;
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
