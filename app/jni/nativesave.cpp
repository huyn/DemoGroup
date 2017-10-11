//
// Created by 胡耀楠 on 2017/8/16.
//

#ifdef __cplusplus
extern "C" {
#endif

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <android/bitmap.h>
#include <android/log.h>
#include"jpeglib.h"

#define TAG "JPEGTEST"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)

static void myjpeg_error_exit(j_common_ptr jcs)
{
    jpeg_error_mgr* error = (jpeg_error_mgr*)jcs->err;
    (*error->output_message) (jcs);
    jpeg_destroy(jcs);
    exit(EXIT_FAILURE);
}

static void android_output_message(j_common_ptr cinfo) {
    char buffer[2048];
    /* Create the message */
    (*cinfo->err->format_message)(cinfo, buffer);
    LOGI("%s", buffer);
}

JNIEXPORT jint Java_com_huyn_demogroup_jni_NativeJniUtil_save
  (JNIEnv *env, jclass thiz, jobject bmpObj,jstring filepath,jint quality)
{
    const char *imgPath = env->GetStringUTFChars(filepath, 0);
    AndroidBitmapInfo bmpinfo = {0};
    if (AndroidBitmap_getInfo(env, bmpObj, &bmpinfo) < 0)
    {
        LOGI("read failed");
        return JNI_FALSE;
    }

    int width = bmpinfo.width;
    int height =bmpinfo.height;
    int widthStep = (width*3+3)/4*4;
    if(bmpinfo.width <= 0 || bmpinfo.height <= 0 ||
       bmpinfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888)
    {
        LOGI("format error");
        return JNI_FALSE;
    }
    void* bmpFromJObject = NULL;
    if (AndroidBitmap_lockPixels(env,bmpObj,(void**)&bmpFromJObject) < 0)
    {
        LOGI("lockPixels failed");
        return JNI_FALSE;
    }
    unsigned char*imageData= (unsigned char*)malloc(sizeof(unsigned char)*(width*3+3)/4*4*height);
    unsigned char* pBuff = (unsigned char*)bmpFromJObject;
    unsigned char* pImgData = imageData;
    for (int y = 0; y < height; y++)
    {
        unsigned char* p1 = pImgData;
        unsigned char* p2 = pBuff;
        for (int x = 0; x < width; x++)
        {
            p1[0] = p2[0];    //R
            p1[1] = p2[1];    //G
            p1[2] = p2[2];    //B
            p1 += 3;
            p2 += 4;
        }
        pImgData +=widthStep;
        pBuff += bmpinfo.stride;
    }
    struct jpeg_compress_struct cinfo;
    struct jpeg_error_mgr jerr;
    cinfo.err = jpeg_std_error(&jerr);
    jerr.output_message=android_output_message;
    jerr.error_exit=myjpeg_error_exit;
    jpeg_create_compress(&cinfo);

    cinfo.image_width = width;
    cinfo.image_height = height;
    cinfo.input_components = 3;
    cinfo.in_color_space = JCS_RGB;

    FILE * outfile;
    if ((outfile = fopen(imgPath, "wb")) == NULL) {
        fprintf(stderr, "can't open %s\n", imgPath);
        return JNI_FALSE;
    }

    jpeg_set_defaults(&cinfo);
    jpeg_stdio_dest(&cinfo, outfile);
    jpeg_set_quality(&cinfo,quality,TRUE);

    jpeg_start_compress(&cinfo, TRUE);
    unsigned char * srcImg=(unsigned char *)imageData;
    while (cinfo.next_scanline < cinfo.image_height) {
        JSAMPROW row_pointer[1];    /* pointer to JSAMPLE row[s] */
        row_pointer[0] = srcImg;
        (void) jpeg_write_scanlines(&cinfo, row_pointer, 1);
        srcImg+=widthStep;
    }
    jpeg_finish_compress(&cinfo);
    jpeg_destroy_compress(&cinfo);
    env->ReleaseStringUTFChars(filepath, imgPath);
    return JNI_TRUE;
}

#ifdef __cplusplus
}
#endif