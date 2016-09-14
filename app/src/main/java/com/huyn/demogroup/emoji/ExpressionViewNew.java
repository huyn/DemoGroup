package com.huyn.demogroup.emoji;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.huyn.demogroup.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * 表情视图
 * 
 * @author junjie.Li
 * 
 */
public class ExpressionViewNew extends ViewPager {

	private static final String[] strArray = {"\uD83D\uDE04", "\uD83D\uDE0A", "\uD83D\uDE03", "\uD83D\uDE09", "\uD83D\uDE0D", "\uD83D\uDE18", "\uD83D\uDE1A", "\uD83D\uDE33", "\uD83D\uDE01", "\uD83D\uDE0C", "\uD83D\uDE1C",
			"\uD83D\uDE1D", "\uD83D\uDE12", "\uD83D\uDE0F", "\uD83D\uDE13", "\uD83D\uDE14", "\uD83D\uDE1E", "\uD83D\uDE16", "\uD83D\uDE25", "\uD83D\uDE30", "\uD83D\uDE28", "\uD83D\uDE23",
			"\uD83D\uDE22", "\uD83D\uDE2D", "\uD83D\uDE02", "\uD83D\uDE32", "\uD83D\uDE31", "\uD83D\uDE20", "\uD83D\uDE21", "\uD83D\uDE2A", "\uD83D\uDE37", "\uD83D\uDC7F", "\uD83D\uDC7D",
			"\uD83D\uDC94", "\uD83D\uDC98", "\uD83C\uDF1F", "\uD83D\uDCA4", "\uD83D\uDCA6", "\uD83C\uDFB5", "\uD83D\uDD25", "\uD83D\uDCA9",
			"\uD83D\uDC4A", "\uD83D\uDC4D", "\uD83D\uDC4E", "\uD83D\uDC46", "\uD83D\uDC47", "\uD83D\uDC49", "\uD83D\uDCAA", "\uD83D\uDC8F", "\uD83D\uDC91", "\uD83D\uDC66", "\uD83D\uDC67",
			"\uD83D\uDC69", "\uD83D\uDC68", "\uD83D\uDC7C", "\uD83D\uDC80", "\uD83C\uDF19", "\uD83C\uDF0A", "\uD83D\uDC31", "\uD83D\uDC36", "\uD83D\uDC2D",
			"\uD83D\uDC39", "\uD83D\uDC30", "\uD83D\uDC3A", "\uD83D\uDC38", "\uD83D\uDC2F", "\uD83D\uDC28", "\uD83D\uDC3B", "\uD83D\uDC37", "\uD83D\uDC2E", "\uD83D\uDC17", "\uD83D\uDC35",
			"\uD83D\uDC34", "\uD83D\uDC0D", "\uD83D\uDC26", "\uD83D\uDC14", "\uD83D\uDC27", "\uD83D\uDC1B", "\uD83D\uDC19", "\uD83D\uDC20", "\uD83D\uDC33", "\uD83D\uDC2C", "\uD83C\uDF39",
			"\uD83C\uDF3A", "\uD83C\uDF34", "\uD83C\uDF35", "\uD83D\uDC9D", "\uD83C\uDF83", "\uD83D\uDC7B", "\uD83C\uDF85", "\uD83C\uDF84", "\uD83C\uDF81", "\uD83D\uDD14", "\uD83C\uDF89",
			"\uD83C\uDF88", "\uD83D\uDCBF", "\uD83D\uDCF7", "\uD83C\uDFA5", "\uD83D\uDCBB", "\uD83D\uDCFA", "\uD83D\uDCDE", "\uD83D\uDD13", "\uD83D\uDD12", "\uD83D\uDD11", "\uD83D\uDD28",
			"\uD83D\uDCA1", "\uD83D\uDCEB", "\uD83D\uDEC0", "\uD83D\uDCB0", "\uD83D\uDCA3", "\uD83D\uDD2B", "\uD83D\uDC8A", "\uD83C\uDFC8", "\uD83C\uDFC0",
			"\uD83C\uDFC6", "\uD83D\uDC7E", "\uD83C\uDFA4", "\uD83C\uDFB8", "\uD83D\uDC59", "\uD83D\uDC51", "\uD83C\uDF02", "\uD83D\uDC5C", "\uD83D\uDC84", "\uD83D\uDC8D", "\uD83D\uDC8E",
			"\uD83C\uDF7A", "\uD83C\uDF7B", "\uD83C\uDF78", "\uD83C\uDF54", "\uD83C\uDF5F", "\uD83C\uDF5D", "\uD83C\uDF63", "\uD83C\uDF5C", "\uD83C\uDF73", "\uD83C\uDF66",
			"\uD83C\uDF82", "\uD83C\uDF4F", "\uD83D\uDE80", "\uD83D\uDEB2", "\uD83D\uDE84", "\uD83C\uDFC1", "\uD83D\uDEB9", "\uD83D\uDEBA", "\uD83D\uDE4F"};

	private int columns = 0;// 每页的列数
	private int row = 0;// 每页的行数
	private Context context;
	private int verticalSpacing = 0;
	private ViewSwitchListener viewSwitchListener = null;
	private EditText bindEditText = null;
	private List<View> listView = null;
	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			if (viewSwitchListener != null) {
				viewSwitchListener.onSwitched(listView.get(position), position);
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

	public ExpressionViewNew(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		columns = 8;
		row = 7;
		verticalSpacing = 40;
		setOnPageChangeListener(pageChangeListener);
	}

	public void setViewSwitchListener(ViewSwitchListener viewSwitchListener) {
		this.viewSwitchListener = viewSwitchListener;
	}

	/**
	 * 将editText与表情视图绑定
	 * 
	 * @param bindEditText
	 */
	public void bindEditText(EditText bindEditText) {
		this.bindEditText = bindEditText;
	}

	/**
	 * 添加记录
	 * 
	 * @param str
	 */
	private void addExpression(CharSequence str) {
		if (bindEditText != null) {
			int currentIndex = bindEditText.getSelectionStart();
			Editable editable = bindEditText.getText();
			editable.insert(currentIndex, str);
		}
	}

	public void input(String str) {
		if (bindEditText == null) {
			return;
		}
		SpannableString ss = new SpannableString(str);

		int start = bindEditText.getSelectionStart();
		int end = bindEditText.getSelectionEnd();
		if (start < 0) {
			bindEditText.append(ss);
		} else {
			bindEditText.getText().replace(Math.min(start, end),
					Math.max(start, end), ss, 0, ss.length());
		}
	}

	/**
	 * 删除离光标最近的一个字符或图片
	 */
	private void deleteChat() {
		if (bindEditText != null) {
			KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
			bindEditText.dispatchKeyEvent(event);
		}
	}

	/**
	 * 对表情数据进行处理
	 */
	private Vector<List<String>> initDatas() {
		final int count = columns * row - 1;
		Vector<List<String>> datas = new Vector<>();
		try {
			int index = 0;
			List<String> expressionItems = new ArrayList<>();
			datas.add(expressionItems);
			for(int i=0; i<strArray.length; i++) {
				if (index != 0 && index % count == 0) {
					expressionItems = new ArrayList<>();
					datas.add(expressionItems);
				}
				expressionItems.add(strArray[i]);
				index++;
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return datas;
	}

	/**
	 * 实例化表情视图
	 */
	public void initExpression() {
		listView = new ArrayList<>();
		Vector<List<String>> datas = initDatas();
		if (!datas.isEmpty()) {
			for (List<String> data : datas) {
				View view = createItemView(data);
				listView.add(view);
			}
		}
		setAdapter(new MyPageAdapter(listView));
	}

	@Override
	public void setOnTouchListener(OnTouchListener l) {
		super.setOnTouchListener(l);
	}

	/**
	 * 创建一个删除表情视图的数据
	 * 
	 * @return
	 */
	private String createDeleteExpressionView() {
		return "DELETE";
	}

	/**
	 * 创建页面View
	 * 
	 * @param expressionItems
	 * @return
	 */
	private View createItemView(List<String> expressionItems) {
		// 添加删除视图
		expressionItems.add(createDeleteExpressionView());
		final GridView gridView = new GridView(context);
		final ExpressionAdapter adapter = new ExpressionAdapter(expressionItems, context);
		gridView.setAdapter(adapter);
		gridView.setNumColumns(columns);
		gridView.setHorizontalSpacing(1);
		gridView.setVerticalSpacing(verticalSpacing);
		gridView.setSelector(android.R.color.transparent);
		gridView.setGravity(Gravity.CENTER);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER_VERTICAL;
		params.topMargin = 40;
		FrameLayout layout = new FrameLayout(context);
		layout.addView(gridView, params);
		return layout;
	}

	final class ExpressionAdapter extends BaseAdapter implements OnClickListener {
		private List<String> expressionItems;

		private Context context;

		ExpressionAdapter(List<String> expressionItems, Context context) {
			this.expressionItems = expressionItems;
			this.context = context;
		}

		@Override
		public int getCount() {
			return expressionItems.size();
		}

		@Override
		public Object getItem(int position) {
			return expressionItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.face_cell, parent, false);
				holder = new ViewHolder();
				holder.textView = (TextView) convertView.findViewById(R.id.emoji);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			String item = expressionItems.get(position);
			holder.textView.setText(item);
			holder.textView.setTag(position);
			holder.textView.setOnClickListener(this);
			return convertView;
		}

		final class ViewHolder {
			TextView textView;
		}

		@Override
        public void onClick(View v)
        {
		    int position = (Integer)v.getTag();
		    if ((expressionItems.size() - 1) == position) {
                deleteChat();
            } else {
                input(expressionItems.get(position));
            }
        }
	}

	class MyPageAdapter extends PagerAdapter {
		private List<View> listView;

		public MyPageAdapter(List<View> listView) {
			this.listView = listView;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return listView.size();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(listView.get(position));
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(listView.get(position));
			return listView.get(position);
		}

	}

	public int getSize() {
		return listView != null ? listView.size() : 0;
	}

}
