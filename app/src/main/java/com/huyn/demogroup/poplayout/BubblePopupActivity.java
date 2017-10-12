package com.huyn.demogroup.poplayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.huyn.demogroup.R;

public class BubblePopupActivity extends AppCompatActivity {

    private BubblePopupWindow leftTopWindow;
    private BubblePopupWindow rightTopWindow;
    private BubblePopupWindow leftBottomWindow;
    private BubblePopupWindow rightBottomWindow;
    private BubblePopupWindow centerWindow;

    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        leftTopWindow = new BubblePopupWindow(BubblePopupActivity.this);
        rightTopWindow = new BubblePopupWindow(BubblePopupActivity.this);
        leftBottomWindow = new BubblePopupWindow(BubblePopupActivity.this);
        rightBottomWindow = new BubblePopupWindow(BubblePopupActivity.this);
        centerWindow = new BubblePopupWindow(BubblePopupActivity.this);

        inflater = LayoutInflater.from(this);
    }

    public void leftTop(View view) {
        View bubbleView = inflater.inflate(R.layout.layout_popup_view, null);
        TextView tvContent = (TextView) bubbleView.findViewById(R.id.tvContent);
        tvContent.setText("HelloWorld");
        leftTopWindow.setBubbleView(bubbleView);
        leftTopWindow.show(view, Gravity.BOTTOM);
    }

    public void rightTop(View view) {
        View bubbleView = inflater.inflate(R.layout.layout_popup_view, null);
        rightTopWindow.setBubbleView(bubbleView);
        rightTopWindow.show(view, Gravity.LEFT, 0);
    }

    public void leftBottom(View view) {
        View bubbleView = inflater.inflate(R.layout.layout_popup_view, null);
        leftBottomWindow.setBubbleView(bubbleView);
        leftBottomWindow.show(view, Gravity.TOP, 100);
    }

    public void rightBottom(View view) {
        View bubbleView = inflater.inflate(R.layout.layout_popup_view, null);
        rightBottomWindow.setBubbleView(bubbleView);
        rightBottomWindow.show(view, Gravity.RIGHT, 0);
    }

    public void center(View view) {
        View bubbleView = inflater.inflate(R.layout.layout_popup_view, null);
        centerWindow.setBubbleView(bubbleView);
        centerWindow.show(view, Gravity.BOTTOM, 0);
    }
}
