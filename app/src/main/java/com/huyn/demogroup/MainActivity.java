package com.huyn.demogroup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.huyn.demogroup.emoji.EmojiActivity;
import com.huyn.demogroup.infinitecycleviewpager.screens.InfiniteCyclerViewPagerActivity;
import com.huyn.demogroup.photo.PhotoMainActivity;
import com.huyn.demogroup.relativetop.RelativeTopActivity;

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
    }
}
