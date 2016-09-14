package com.huyn.demogroup.emoji;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.huyn.demogroup.R;

/**
 * Created by huyaonan on 16/9/14.
 */
public class EmojiActivity extends Activity {

    public EmoticonsUtils emoticonUtils;
    private EditText mEdit;
    private ImageView mEmojiState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_emoji);

        mEdit = (EditText) findViewById(R.id.emoji_edit);
        mEmojiState = (ImageView) findViewById(R.id.emoji_state);

        emoticonUtils = new EmoticonsUtils();

        initEmojiUtils();

        mEmojiState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emoticonUtils.showEmoticonsView();
            }
        });

        findViewById(R.id.emoji_keyboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emoticonUtils.showKeyboard();
            }
        });

        mEdit.clearFocus();
        mEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        mEdit.requestFocus();
                        emoticonUtils.showKeyboard();
                        break;

                }
                return false;
            }
        });

        mEdit.setVisibility(View.INVISIBLE);
        findViewById(R.id.emoji_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEdit.getVisibility() != View.VISIBLE) {
                    mEdit.setVisibility(View.VISIBLE);

                    mEdit.requestFocus();
                    emoticonUtils.showKeyboard();
                }
            }
        });
    }

    private EmoticonsUtils.OnEmoticonsStateListener emotionsListener = new EmoticonsUtils.OnEmoticonsStateListener() {

        @Override
        public void onShowing() {
//            walaFace.setImageResource(R.drawable.icon_keyboard);
//            walaFace.setTag("key");
        }

        @Override
        public void onDismiss() {
//            walaFace.setImageResource(R.drawable.icon_walaemo);
//            walaFace.setTag("face");
        }
    };

    private void initEmojiUtils() {
        emoticonUtils.init(this, findViewById(R.id.emoji_root), findViewById(R.id.emoji_container),
                mEdit, findViewById(R.id.emoji_blank));

        emoticonUtils.setOnEmoticonsStateListener(emotionsListener);
        emoticonUtils.setOnKeyboardStateListener(new EmoticonsUtils.OnKeyboardStateListener() {

            @Override
            public void onShowing() {
            }

            @Override
            public void onDismiss() {
//                if (finish) {
//                    transitionHelper.finishWala((int) ptf.getCurrentTranslationY());
//                }
            }
        });
        emoticonUtils.setOnKeyboardStateListenerEx(new EmoticonsUtils.OnEmoticonsStateListener() {

            @Override
            public void onShowing() {
//                toggleTipView(false);
            }

            @Override
            public void onDismiss() {
//                toggleTipView(true);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (emoticonUtils.isKeyBoardshowing()) {
            emoticonUtils.hideKeyboard();
            return;
        }
        if (emoticonUtils.hideEmoticonsView(true))
            return;
        super.onBackPressed();
    }
}
