package com.huyn.demogroup.paint;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 16/11/15.
 */
public class TestWebViewActivity extends Activity {
    private WebView mWeb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        mWeb = (WebView) findViewById(R.id.webview);

        WebSettings webSettings = mWeb.getSettings();

        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        mWeb.setWebViewClient(new WebViewClient() {
            @Override
            public void onFormResubmission(WebView view, Message dontResend, Message resend) {
                dontResend.sendToTarget();
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            }
        });
        mWeb.setWebChromeClient(new WebChromeClient());

        mWeb.loadUrl("http://www.baidu.com");
//        mWeb.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        mWeb.setBackgroundColor(Color.RED);
//        mWeb.getBackground().setAlpha(1);
    }
}
