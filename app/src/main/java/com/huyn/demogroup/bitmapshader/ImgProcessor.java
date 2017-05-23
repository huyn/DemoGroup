package com.huyn.demogroup.bitmapshader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;

/**
 * Created by huyaonan on 2017/5/22.
 */

public class ImgProcessor {

    private int resId;
    public ImgProcessor(int resId) {
        this.resId = resId;
    }

    public static Bitmap cropByRect(Context context, int resId, Rect rect) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        Bitmap target = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.right-rect.left, rect.bottom-rect.top);
        return target;
    }

    public void startTask(Context context, OnProcessListener listener) {
        new ConvertTask(context.getResources(), listener).execute();
    }

    private class ConvertTask extends AsyncTask<Void, Void, Bitmap> {

        private OnProcessListener listener;
        private Resources resources;
        public ConvertTask(Resources resources, OnProcessListener listener) {
            this.listener = listener;
            this.resources = resources;
        }

        private byte[] R;
        private byte[] G;
        private byte[] B;

        private int width;
        private int height;

        private int padding = 200;

        private void init(int[] pixels, int width, int height) {
            this.width = width;
            this.height = height;
            int size = width * height;
            R = new byte[size];
            G = new byte[size];
            B = new byte[size];
            backFillData(pixels);
        }

        private void backFillData(int[] input) {
            int c=0, r=0, g=0, b=0;
            int length = input.length;
            for(int i=0; i<length; i++) {
                c = input[i];
                r = (c&0xff0000)>>16;
                g = (c&0xff00)>>8;
                b = c&0xff;
                R[i] = (byte)r;
                G[i] = (byte)g;
                B[i] = (byte)b;
            }
        }

        private int[] getPixels() {
            int size = width * height;
            int[] pixels = new int[size];
            for (int i=0; i < size; i++) {
                int alpha = 0xff000000;
                int x = i%width;
                int y = i/width;

                //if(x > padding && x < width-padding && y > padding && y < height - padding)
                //    continue;

                int gradient=0;
                if(x <= padding) {
                    if(y <= padding) {
                        gradient = Math.max(x, y);
                    } else if(y >= height-padding) {
                        gradient = Math.max(x, height-y);
                    } else {
                        gradient = x;
                    }
                } else if(x >= width - padding) {
                    if(y <= padding) {
                        gradient = Math.max(width-x, y);
                    } else if(y >= height-padding) {
                        gradient = Math.max(width-x, height-y);
                    } else {
                        gradient = width-x;
                    }
                } else {
                    if(y <= padding) {
                        gradient = y;
                    } else {
                        gradient = height-y;
                    }
                }

                /*if(gradient < 200)
                alpha = (int) (gradient*255f/200) << 24;
                if(gradient > width-400)
                    alpha = (int) ((width-gradient)*255f/400) << 24;
                if(colum < 200 || colum > height-200)
                    alpha = (int) (colum*255f/200) << 24;*/
                //alpha = (int) (gradient * 255f / padding);
                //alpha = alpha << 24;
                pixels[i] = alpha | ((R[i] & 0xff) << 16) | ((G[i] & 0xff) << 8) | B[i] & 0xff;
            }

            return pixels;
        }

        private void parse(int[] input) {
            padding=50;
            int value;
            System.out.println(input.length + "+++++++" + (input.length%width) + "--" + (input.length/width));
            for (int i=0; i < input.length; i++) {
                int x = i%width;
                int y = i/width;
                if(x > padding && x < width-padding && y > padding && y < height - padding)
                    continue;

                int alpha = 0xff000000;
//                if(x < 200)
//                    alpha = (int) (x*255f/padding) << 24;
//                if(x > width-padding)
//                    alpha = (int) ((width-x)*255f/padding) << 24;

                int gradient=0;
                if(x <= padding) {
                    if(y <= padding) {
                        gradient = Math.min(x, y);
                    } else if(y >= height-padding) {
                        gradient = Math.min(x, height-y);
                    } else {
                        gradient = x;
                    }
                } else if(x >= width - padding) {
                    if(y <= padding) {
                        gradient = Math.min(width-x, y);
                    } else if(y >= height-padding) {
                        gradient = Math.min(width-x, height-y);
                    } else {
                        gradient = width-x;
                    }
                } else {
                    if(y <= padding) {
                        gradient = y;
                    } else {
                        gradient = height-y;
                    }
                }

                /*if(y < padding && x > padding && x < width-padding) {
                    alpha = (int) (y * 255f / padding);
                    //System.out.println("---------x:" + x + "/y:" + y + "++++" + alpha);
                    alpha = alpha << 24;
                }

                if(y > height-padding && x > padding && x < width-padding) {
                    alpha = (int) ((height - y) * 255f / padding);
                    //System.out.println("---------x:" + x + "/y:" + y + "++++" + alpha);
                    alpha = alpha << 24;
                }*/
                alpha = (int) (gradient * 255f / padding);
                alpha = alpha << 24;
                value =  input[i] & 0x00ffffff;
                input[i] = alpha | value;
            }
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                long start = System.currentTimeMillis();
                Bitmap bitmap = BitmapFactory.decodeResource(resources, resId);
                width = bitmap.getWidth();
                height = bitmap.getHeight();
                int[] input = new int[width * height];
                bitmap.getPixels(input, 0, width, 0, 0, width, height);
                //init(input, width, height);

                System.out.println("----width:"+ width + "/" + height);

                parse(input);
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                bitmap.setPixels(input, 0, width, 0, 0, width, height);
                System.out.println("----cost time:" + (System.currentTimeMillis()-start));

                R=null;
                G=null;
                B=null;
                input=null;
                System.gc();
                return bitmap;
            } catch (Exception e) {
                e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap == null) {
                listener.onFail();
                return;
            }
            listener.onSuccess(bitmap);
        }
    }

    public interface OnProcessListener {
        public void onSuccess(Bitmap bitmap);
        public void onFail();
    }

}
