package com.huyn.demogroup;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.huyn.demogroup.anim.ThreeDRotateActivity;
import com.huyn.demogroup.bitmapshader.BitmapShaderActivity;
import com.huyn.demogroup.clipchild.ClipChildAnimActivity;
import com.huyn.demogroup.crop.CropActivity;
import com.huyn.demogroup.emoji.EmojiActivity;
import com.huyn.demogroup.event.TestEventDeliveryActivity;
import com.huyn.demogroup.infinitecycleviewpager.screens.InfiniteCyclerViewPagerActivity;
import com.huyn.demogroup.opengl.TutorialPartOne;
import com.huyn.demogroup.paint.PaintShaderActivity;
import com.huyn.demogroup.particle.ParticleEffectsActivity;
import com.huyn.demogroup.photo.PhotoMainActivity;
import com.huyn.demogroup.relativetop.RelativeTopActivity;
import com.huyn.demogroup.sensor.RotationVectorDemo;
import com.huyn.demogroup.zoomageview.ZoomageViewActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.click_relativetop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RelativeTopActivity.class));
            }
        });

        findViewById(R.id.click_bitmapshader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BitmapShaderActivity.class));
            }
        });

        findViewById(R.id.click_emoji).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EmojiActivity.class));
            }
        });

        findViewById(R.id.click_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PhotoMainActivity.class));
            }
        });

        findViewById(R.id.click_infinitecycleviewpager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InfiniteCyclerViewPagerActivity.class));
            }
        });

        findViewById(R.id.click_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TestEventDeliveryActivity.class));
            }
        });

        findViewById(R.id.click_rotateanim).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ThreeDRotateActivity.class));
            }
        });

        findViewById(R.id.click_rotatevector).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RotationVectorDemo.class));
            }
        });

        findViewById(R.id.click_zoomageview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ZoomageViewActivity.class));
            }
        });

        /**
         * opengl http://blog.csdn.net/column/details/apidemoopengl.html?&page=1
         */

        findViewById(R.id.click_opengl1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TutorialPartOne.class));
            }
        });

        findViewById(R.id.click_paint_shader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PaintShaderActivity.class));
            }
        });

        findViewById(R.id.click_particle_effects).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ParticleEffectsActivity.class));
            }
        });

        findViewById(R.id.click_clipchildanim).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ClipChildAnimActivity.class));
            }
        });

        findViewById(R.id.click_crop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CropActivity.class));
            }
        });

        printCacheDirs();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void printCacheDirs() {
        System.out.println("getCacheDir++++" + getCacheDir().getPath());
        System.out.println("getExternalCacheDir++++" + getExternalCacheDir().getPath());
        System.out.println("getExternalCacheDirs++++");
        File[] cacheDirs = getExternalCacheDirs();
        if(cacheDirs != null && cacheDirs.length > 0) {
            for(File file : cacheDirs) {
                System.out.println("--" + file.getPath());
            }
        }
        System.out.println("getFilesDir++++" + getFilesDir().getPath());
        System.out.println("getExternalFilesDir++++" + getExternalFilesDir(null).getPath());
        System.out.println("getExternalFilesDirs++++");
        cacheDirs = getExternalFilesDirs(null);
        if(cacheDirs != null && cacheDirs.length > 0) {
            for(File file : cacheDirs) {
                System.out.println("--" + file.getPath());
            }
        }
    }

}
