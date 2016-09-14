package com.huyn.demogroup.emoji;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.huyn.demogroup.R;
import com.huyn.demogroup.util.SharedPrefUtil;
import com.huyn.demogroup.util.SoftInputUtil;

/**
 * 包含blankview的布局一定要用LinearLayout，目前暂时只能这样
 * @author yaonan.hu
 *
 */

public class EmoticonsUtils {

	private View anchorView;
	private EditText contentView;
	private View blankView;
	private View rootView;

	private RelativeLayout popConView;
	private View popupView;
	private PopupWindow emoticonsPop;

	private int keyboardHeight;

	private Context context;

	private boolean isKeyBoardVisible = false;
	private boolean isSetEditListener = true;
	public static final String KEYBOARD_HEIGHT = "KEYBOARD_HEIGHT";
	private OnEmoticonsStateListener keyboardChange;
	private boolean animating;

	public boolean hideEmoticonsView() {
		return hideEmoticonsView(false);
	}

	public boolean hideEmoticonsView(boolean isNeedAnim) {
		int blankHeight = 0;
		if(blankView != null) {
			LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) blankView.getLayoutParams();
			blankHeight = llp.height;
			blankView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
		}

		if(emoticonsPop != null && (emoticonsPop.isShowing()||blankHeight > 0)) {
			if(isNeedAnim) {
				AnimatorSet set = getFadeAnimator(false);
				if (set != null) {
					set.start();
				}
			} else {
				emoticonsPop.dismiss();
			}


			if(mListener != null)
				mListener.onDismiss();

			return true;
		}

		return false;
	}
	public void hideKeyboard()
	{
		if(blankView != null) {
			blankView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
		}
		SoftInputUtil.hideShow(contentView);
		if(mListener != null)
			mListener.onDismiss();
		if(emoticonsPop != null && emoticonsPop.isShowing()) {
			emoticonsPop.dismiss();
		}
	}

	public void showKeyboard() {
		if(emoticonsPop != null && emoticonsPop.isShowing()) {
			emoticonsPop.dismiss();

			if(mListener != null)
				mListener.onDismiss();
		}

		if(blankView != null) {
		    LayoutParams params = blankView.getLayoutParams();
			params.height = keyboardHeight;
			rootView.requestLayout();
		}

		contentView.requestFocus();
		SoftInputUtil.showSoftInput(contentView);
	}

//	public void showKeyboardOverlay() {
//		if(emoticonsPop != null && emoticonsPop.isShowing()) {
//			emoticonsPop.dismiss();
//
//			if(mListener != null)
//				mListener.onDismiss();
//		}
//
//		if(blankView != null) {
//		    LayoutParams params = (LayoutParams) blankView.getLayoutParams();
//			params.height = keyboardHeight;
//			rootView.requestLayout();
//		}
//
//		contentView.requestFocus();
//		SoftInputUtil.showSoftInput(contentView);
//	}

	public void showEmoticonsView() {
		if(emoticonsPop == null)
			return;
		emoticonsPop.dismiss();
		if(!isKeyBoardVisible && blankView != null) {
//		    LayoutParams params = (LayoutParams) blankView.getLayoutParams();
//			params.height = keyboardHeight;
//			blankView.setLayoutParams(params);
//			rootView.requestLayout();
			AnimatorSet set = getFadeAnimator(true);
			if (set != null) {
				set.start();
			}
		}

		emoticonsPop.setHeight(keyboardHeight);

//		emoticonsPop.showAtLocation(anchorView, Gravity.BOTTOM, 0, 0);
		popConView.removeAllViews();
		popConView.addView(popupView);
		emoticonsPop.setContentView(popConView);
		emoticonsPop.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);

		if(mListener != null)
			mListener.onShowing();
	}

	public boolean isEmoticonsShowing() {
		int blankHeight = 0;
		if(blankView != null) {
			LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) blankView.getLayoutParams();
			blankHeight = llp.height;
		}
		return emoticonsPop != null && (emoticonsPop.isShowing()||blankHeight > 0);
	}
	public boolean isKeyBoardshowing(){
		return isKeyBoardVisible;
	}
	public void setEditListenerEnable(boolean set){  //设置FALSE，屏蔽init中对于edit的listener
		isSetEditListener = set;
	}
	public void init(Context context, View rootView, View anchor, EditText editor, View blank) {
		anchorView = anchor;
		contentView = editor;
		blankView = blank;
		this.rootView = rootView;
		this.context = context;

		if(context == null || rootView == null)
			return;

		keyboardHeight = SharedPrefUtil.getInstance(context).getInt(KEYBOARD_HEIGHT, context.getResources().getDimensionPixelSize(R.dimen.keyboard_height));

		popupView = LayoutInflater.from(context).inflate(R.layout.wala_emoticon_layout, null);
		popConView = new RelativeLayout(context);
		popConView.setBackgroundColor(Color.WHITE);

		emoticonsPop = new PopupWindow(popConView, LayoutParams.MATCH_PARENT, keyboardHeight, false);

		ExpressionViewNew expressionView = (ExpressionViewNew) popupView.findViewById(R.id.wala_expression_view);
		expressionView.initExpression();
		expressionView.bindEditText(editor);

		//CircleFlowIndicator flowIndicator = (CircleFlowIndicator) popupView.findViewById(R.id.wala_flow_indicator);
		//flowIndicator.setViewFlow(expressionView, 4);
		//expressionView.setViewSwitchListener(flowIndicator);

		checkKeyboardHeight(rootView);
		if (!isSetEditListener) {
			return;
		}
		contentView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showKeyboard();
			}
		});

		contentView.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus && v.isEnabled()) {
					showKeyboard();
				} else {
//					showEmoticonsView();
				}
			}
		});
	}

	/**
	 * Checking keyboard height and keyboard visibility
	 */
	int previousHeightDiffrence = 0;
	private void checkKeyboardHeight(final View parentLayout) {
		parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {

						Rect r = new Rect();
						parentLayout.getWindowVisibleDisplayFrame(r);

						int screenHeight = parentLayout.getRootView().getHeight();
						int heightDifference = screenHeight - (r.bottom);

//						Utils.Log("keyboardheight", ""+heightDifference);
						if (previousHeightDiffrence - heightDifference > 50) {
							hideEmoticonsView(true);
						}

						previousHeightDiffrence = heightDifference;
						if (heightDifference > 200) {
						    if (!isKeyBoardVisible && mKeyboardListener != null)
                            {
                                mKeyboardListener.onDismiss();
                            }
						    changeKeyboardHeight(heightDifference);
							if (!isKeyBoardVisible && keyboardChange != null) {
								keyboardChange.onShowing();
							}
							isKeyBoardVisible = true;

						} else {
						    if (isKeyBoardVisible && mKeyboardListener != null)
                            {
                                mKeyboardListener.onDismiss();
                            }
							if (isKeyBoardVisible && keyboardChange != null)
							{
								keyboardChange.onDismiss();
							}
							isKeyBoardVisible = false;
						}
					}
				});
	}

	private void changeKeyboardHeight(int height) {
		if (height > 200 && context != null) {
			if(keyboardHeight == height)
				return;
			keyboardHeight = height;
			SharedPrefUtil.getInstance(context).putInt(KEYBOARD_HEIGHT, keyboardHeight);
			resizeLayoutHeight();

//			walaTagGroup.setHeight(keyboardHeight);
//			walaTagGroup.setData();
		}
	}

	//重置工具栏的位置
	private void resizeLayoutHeight() {
		if(!isKeyBoardVisible && blankView != null) {
			LayoutParams params = blankView.getLayoutParams();
			params.height = keyboardHeight;
			blankView.setLayoutParams(params);
			rootView.requestLayout();
		}

		if(emoticonsPop != null)
			emoticonsPop.setHeight(keyboardHeight);
	}

	public boolean onKeyDown(int keyCode) {
		return hideEmoticonsView();
	}

//	private void deleteEmoticon() {
//		KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
//		contentView.dispatchKeyEvent(event);	
//	}

	public interface OnEmoticonsStateListener {
		public void onShowing();
		public void onDismiss();
	}

	public void setOnKeyboardStateListenerEx(OnEmoticonsStateListener listener) {
		keyboardChange = listener;
	}

	private OnEmoticonsStateListener mListener;

	public void setOnEmoticonsStateListener(OnEmoticonsStateListener listener) {
		mListener = listener;
	}

	public interface OnKeyboardStateListener{
	    public void onShowing();
        public void onDismiss();
	}

	private OnKeyboardStateListener mKeyboardListener;

    public void setOnKeyboardStateListener(OnKeyboardStateListener keyboardListener)
    {
        this.mKeyboardListener = keyboardListener;
    }

	public boolean isShowing(){
		return emoticonsPop != null && (emoticonsPop.isShowing()||isKeyBoardVisible);
	}

	private AnimatorSet getFadeAnimator(final  boolean isIn) {
		if(animating) return null;
		AnimatorSet animator = new AnimatorSet();
		ObjectAnimator animSize = ObjectAnimator.ofInt(this, "size", isIn ? 0 : keyboardHeight, isIn ? keyboardHeight : 0);
		ValueAnimator translateInAnim = ObjectAnimator.ofFloat(popConView, "translationY", isIn ? keyboardHeight : 0, isIn ? 0 : keyboardHeight);
		translateInAnim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				animating = true;
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				if(!isIn) {
						emoticonsPop.dismiss();
				}
				popConView.setTranslationY(0);
				animating = false;
			}
		});

		animator.playTogether(animSize,translateInAnim);
		animator.setDuration(200l);
		return animator;
	}

	public void setSize (int size) {
		LinearLayout.LayoutParams rl = (LinearLayout.LayoutParams) blankView.getLayoutParams();
		if(rl != null) {
			rl.height =size;
			blankView.setLayoutParams(rl);
		}

		//		ViewGroup.LayoutParams rl1 = (ViewGroup.LayoutParams) popConView.getLayoutParams();
//		if(rl1 != null) {
//			rl.height =size;
//			popConView.setLayoutParams(rl);
//		}
	}

}
