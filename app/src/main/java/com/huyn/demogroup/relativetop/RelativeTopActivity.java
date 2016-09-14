package com.huyn.demogroup.relativetop;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huyn.demogroup.R;
import com.huyn.demogroup.util.SysUtil;

/**
 * Created by huyaonan on 16/9/14.
 */
public class RelativeTopActivity extends Activity {

    private ImageView mBlurBg, mHeadImg;
    private ViewPager mRankVP;

    private View mBlurLayout;

//    private ParallaxRankTopLayout mTop;

    private RankViewAdpater mAdapter;

    private int mColor;

    private int mLastScrollY=0;
    private int targetHeight=0;
    private int statusHeight = 0;
    private int topbarheight = 200;
    private boolean headFilled = false;

    int rootHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_rank);

//        mTop = (ParallaxRankTopLayout) findViewById(R.id.rank_parallax_top);
//        mTop.setIndicatorColor(mColor);

        mBlurBg = (ImageView) findViewById(R.id.blur_bg);
        mHeadImg = (ImageView) findViewById(R.id.rank_head_img);

        mBlurLayout = findViewById(R.id.blur_layout);

        mRankVP = (ViewPager) findViewById(R.id.rank_viewpager);

        targetHeight = getResources().getDimensionPixelSize(R.dimen.category_list_head_height);
        statusHeight = SysUtil.getStatusHeight(this);
        topbarheight = getResources().getDimensionPixelOffset(R.dimen.actionbar_height);

//        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//            @Override
//            public void onScrollChanged() {
//                int y = mScrollView.getScrollY();
//                if (y == mLastScrollY)
//                    return;
//                mLastScrollY = y;
//
//                int targetY = targetHeight - y;
//                mBlurView.setTranslationY(targetY < 0 ? 0 : targetY);
//                mBlurView.invalidate();
//
//                if (y >= targetHeight - topbarheight - statusHeight) {
//                    mTop.updateTitleOffset(targetHeight - topbarheight - statusHeight);
//                    mTop.updateTitleAlpha(0f);
//                } else {
//                    mTop.updateTitleOffset(y);
//                    float alpha = y*1f/(targetHeight - topbarheight - statusHeight)*2;
//                    mTop.updateTitleAlpha(alpha < 1 ? 1-alpha : 0f);
//                }
//            }
//        });
//
//        mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                int height = mTop.getIndicatorHeight();
//                if(height > 0 && !headFilled) {
//                    mHeadLayout.removeAllViews();
//                    View header = new View(that);
//                    mHeadLayout.addView(header, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, targetHeight-topbarheight-statusHeight));
//                    headFilled = true;
//
//                    mScrollView.setPadding(0, topbarheight+statusHeight+height, 0, 0);
//                }
//            }
//        });
//
//        mBlurView.setTranslationY(targetHeight);


//        mTop.setOnParallaxTopEventListener(new OnParallaxTopEventListener() {
//            @Override
//            public void onBackPressed() {
//                finish();
//            }
//
//            @Override
//            public void onShare() {
//            }
//
//            @Override
//            public void onRefresh() {
//            }
//        });

        setupData();
    }

    private void setupData() {
        if(mAdapter == null) {
            mAdapter = new RankViewAdpater(this);
            mRankVP.setAdapter(mAdapter);
            mRankVP.setOffscreenPageLimit(4);

            headFilled = false;
//            mTop.setViewPager(mRankVP, new PagerSlidingTabStrip.IconAndTextTabProvider() {
//                @Override
//                public int[] getPageIconRes() {
//                    return null;
//                }
//
//                @Override
//                public String[] getPageText() {
//                    return null;
//                }
//
//                @Override
//                public Bitmap getBitmap(int res) {
//                    return null;
//                }
//            });
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    protected class RankViewAdpater extends PagerAdapter {

        private Context context;

        public RankViewAdpater(Context context) {
            this.context = context;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_rank, null, false);

            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
            setupRecyclerView(recyclerView, position == 0);

            container.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object obj) {
            container.removeView((View) obj);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }

    private int totalHeightMain = 0, totalHeightSub = 0;
    private RecyclerView mRecyclerView, mSubRecyclerview;
    public void setupRecyclerView(RecyclerView view, boolean main) {
        if (main) {
            mRecyclerView = view;
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if(rootHeight <= 0)
                        rootHeight = mRankVP.getHeight();

                    totalHeightMain += dy;
                    int y = totalHeightMain;
                    if (y == mLastScrollY)
                        return;
                    mLastScrollY = y;
                    if (y >= targetHeight) {
                        mHeadImg.setTranslationY(-targetHeight);
                    } else {
                        mHeadImg.setTranslationY(-y);
                    }
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if(newState == 0) {
                        if(mSubRecyclerview.getHeight() < rootHeight) {
                            if(totalHeightMain > targetHeight)
                                mSubRecyclerview.setTranslationY(-targetHeight);
                            else
                                mSubRecyclerview.setTranslationY(-totalHeightMain);
                        } else {
                            if (totalHeightMain > totalHeightSub && totalHeightSub < targetHeight) {
                                if (targetHeight > totalHeightMain)
                                    mSubRecyclerview.scrollBy(0, totalHeightMain - totalHeightSub);
                                else
                                    mSubRecyclerview.scrollBy(0, targetHeight - totalHeightSub);
                            } else if (totalHeightMain < totalHeightSub && totalHeightMain < targetHeight)
                                mSubRecyclerview.scrollBy(0, totalHeightMain - totalHeightSub);
                        }
                    }
                }
            });
        } else {
            mSubRecyclerview = view;
            mSubRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if(rootHeight <= 0)
                        rootHeight = mRankVP.getHeight();

                    totalHeightSub += dy;
                    int y = totalHeightSub;
                    if (y == mLastScrollY)
                        return;
                    mLastScrollY = y;
                    if (y >= targetHeight) {
                        mHeadImg.setTranslationY(-targetHeight);
                    } else {
                        mHeadImg.setTranslationY(-y);
                    }
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if(newState == 0) {
                        if(mRecyclerView.getHeight() < rootHeight) {
                            if(totalHeightSub > targetHeight)
                                mRecyclerView.setTranslationY(-targetHeight);
                            else
                                mRecyclerView.setTranslationY(-totalHeightSub);
                        } else {
                            if (totalHeightMain < totalHeightSub && totalHeightMain < targetHeight) {
                                if (targetHeight > totalHeightSub)
                                    mRecyclerView.scrollBy(0, totalHeightSub - totalHeightMain);
                                else
                                    mRecyclerView.scrollBy(0, targetHeight - totalHeightMain);
                            } else if (totalHeightMain > totalHeightSub && totalHeightSub < targetHeight)
                                mRecyclerView.scrollBy(0, totalHeightSub - totalHeightMain);
                        }
                    }
                }
            });
        }
        view.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        view.setLayoutManager(manager);

        view.setAdapter(new RecyclerAdapter(main ? 20 : 2));
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private int count = 10;

        public RecyclerAdapter(int count) {
            this.count = count;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == 0) {
                return new BlankHeader(LayoutInflater.from(RelativeTopActivity.this).inflate(R.layout.item_blank, parent, false));
            } else {
                return new SimpleItem(LayoutInflater.from(RelativeTopActivity.this).inflate(R.layout.item_simple, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(position > 0)
                ((SimpleItem) holder).update(position);
        }

        @Override
        public int getItemCount() {
            return count;
        }

        @Override
        public int getItemViewType(int position) {
            if(position == 0)
                return 0;
            return 1;
        }
    }

    private class BlankHeader extends RecyclerView.ViewHolder {

        public BlankHeader(View itemView) {
            super(itemView);
        }
    }

    private class SimpleItem extends RecyclerView.ViewHolder {

        private TextView textView;

        public SimpleItem(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
        }

        public void update(int index) {
//            textView.setText("hahahahahaha---" + getEmoji() + "/\uD83D\uDE04---" + index);
            StringBuilder sb = new StringBuilder();
            for(int i=0; i<strArray.length; i++)
                sb.append(strArray[i]);
            textView.setText(sb.toString());
        }

        private String getEmoji(){
            int unicodeJoy = 0x1F602;
            return getEmojiStringByUnicode(unicodeJoy);
        }

        private String getEmojiStringByUnicode(int unicode){
            return new String(Character.toChars(unicode));
        }

        private String[] strArray = {"\uD83D\uDE04", "\uD83D\uDE0A", "\uD83D\uDE03", "\uD83D\uDE09", "\uD83D\uDE0D", "\uD83D\uDE18", "\uD83D\uDE1A", "\uD83D\uDE33", "\uD83D\uDE01", "\uD83D\uDE0C", "\uD83D\uDE1C",
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
        
    }

//    private String str = "const faceList = ['\uD83D\uDE04', '\uD83D\uDE0A', '\uD83D\uDE03', '\uD83D\uDE09', '\uD83D\uDE0D', '\uD83D\uDE18', '\uD83D\uDE1A', '\uD83D\uDE33', '\uD83D\uDE01', '\uD83D\uDE0C', '\uD83D\uDE1C',\n" +
//            "    '\uD83D\uDE1D', '\uD83D\uDE12', '\uD83D\uDE0F', '\uD83D\uDE13', '\uD83D\uDE14', '\uD83D\uDE1E', '\uD83D\uDE16', '\uD83D\uDE25', '\uD83D\uDE30', '\uD83D\uDE28', '\uD83D\uDE23',\n" +
//            "    '\uD83D\uDE22', '\uD83D\uDE2D', '\uD83D\uDE02', '\uD83D\uDE32', '\uD83D\uDE31', '\uD83D\uDE20', '\uD83D\uDE21', '\uD83D\uDE2A', '\uD83D\uDE37', '\uD83D\uDC7F', '\uD83D\uDC7D',\n" +
//            "    '\uD83D\uDC94', '\uD83D\uDC98', '✨', '\uD83C\uDF1F', '❕', '❔', '\uD83D\uDCA4', '\uD83D\uDCA6', '\uD83C\uDFB5', '\uD83D\uDD25', '\uD83D\uDCA9',\n" +
//            "    '\uD83D\uDC4A', '\uD83D\uDC4D', '\uD83D\uDC4E', '\uD83D\uDC46', '\uD83D\uDC47', '\uD83D\uDC49', '\uD83D\uDCAA', '\uD83D\uDC8F', '\uD83D\uDC91', '\uD83D\uDC66', '\uD83D\uDC67',\n" +
//            "    '\uD83D\uDC69', '\uD83D\uDC68', '\uD83D\uDC7C', '\uD83D\uDC80', '⛄', '\uD83C\uDF19', '⚡', '\uD83C\uDF0A', '\uD83D\uDC31', '\uD83D\uDC36', '\uD83D\uDC2D',\n" +
//            "    '\uD83D\uDC39', '\uD83D\uDC30', '\uD83D\uDC3A', '\uD83D\uDC38', '\uD83D\uDC2F', '\uD83D\uDC28', '\uD83D\uDC3B', '\uD83D\uDC37', '\uD83D\uDC2E', '\uD83D\uDC17', '\uD83D\uDC35',\n" +
//            "    '\uD83D\uDC34', '\uD83D\uDC0D', '\uD83D\uDC26', '\uD83D\uDC14', '\uD83D\uDC27', '\uD83D\uDC1B', '\uD83D\uDC19', '\uD83D\uDC20', '\uD83D\uDC33', '\uD83D\uDC2C', '\uD83C\uDF39',\n" +
//            "    '\uD83C\uDF3A', '\uD83C\uDF34', '\uD83C\uDF35', '\uD83D\uDC9D', '\uD83C\uDF83', '\uD83D\uDC7B', '\uD83C\uDF85', '\uD83C\uDF84', '\uD83C\uDF81', '\uD83D\uDD14', '\uD83C\uDF89',\n" +
//            "    '\uD83C\uDF88', '\uD83D\uDCBF', '\uD83D\uDCF7', '\uD83C\uDFA5', '\uD83D\uDCBB', '\uD83D\uDCFA', '\uD83D\uDCDE', '\uD83D\uDD13', '\uD83D\uDD12', '\uD83D\uDD11', '\uD83D\uDD28',\n" +
//            "    '\uD83D\uDCA1', '\uD83D\uDCEB', '\uD83D\uDEC0', '\uD83D\uDCB0', '\uD83D\uDCA3', '\uD83D\uDD2B', '\uD83D\uDC8A', '\uD83C\uDFC8', '\uD83C\uDFC0', '⚽', '⛳',\n" +
//            "    '\uD83C\uDFC6', '\uD83D\uDC7E', '\uD83C\uDFA4', '\uD83C\uDFB8', '\uD83D\uDC59', '\uD83D\uDC51', '\uD83C\uDF02', '\uD83D\uDC5C', '\uD83D\uDC84', '\uD83D\uDC8D', '\uD83D\uDC8E',\n" +
//            "    '☕', '\uD83C\uDF7A', '\uD83C\uDF7B', '\uD83C\uDF78', '\uD83C\uDF54', '\uD83C\uDF5F', '\uD83C\uDF5D', '\uD83C\uDF63', '\uD83C\uDF5C', '\uD83C\uDF73', '\uD83C\uDF66',\n" +
//            "    '\uD83C\uDF82', '\uD83C\uDF4F', '\uD83D\uDE80', '\uD83D\uDEB2', '\uD83D\uDE84', '\uD83C\uDFC1', '\uD83D\uDEB9', '\uD83D\uDEBA', '⭕', '❌', '\uD83D\uDE4F'];"

}
