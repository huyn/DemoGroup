package com.huyn.demogroup.bahavior.widget;

/**
 * Created by huyaonan on 2017/12/17.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.design.R;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.support.annotation.RestrictTo.Scope.GROUP_ID;

/**
 * AppBarLayout is a vertical {@link LinearLayout} which implements many of the features of
 * material designs app bar concept, namely scrolling gestures.
 * <p>
 * Children should provide their desired scrolling behavior through
 * {@link AppBarLayout.LayoutParams#setScrollFlags(int)} and the associated layout xml attribute:
 * {@code app:layout_scrollFlags}.
 *
 * <p>
 * This view depends heavily on being used as a direct child within a {@link CoordinatorLayout}.
 * If you use AppBarLayout within a different {@link ViewGroup}, most of it's functionality will
 * not work.
 * <p>
 * AppBarLayout also requires a separate scrolling sibling in order to know when to scroll.
 * The binding is done through the {@link AppBarLayout.ScrollingViewBehavior} behavior class, meaning that you
 * should set your scrolling view's behavior to be an instance of {@link AppBarLayout.ScrollingViewBehavior}.
 * A string resource containing the full class name is available.
 *
 * <pre>
 * &lt;CoordinatorLayout
 *         xmlns:android=&quot;http://schemas.android.com/apk/res/android&quot;
 *         xmlns:app=&quot;http://schemas.android.com/apk/res-auto&quot;
 *         android:layout_width=&quot;match_parent&quot;
 *         android:layout_height=&quot;match_parent&quot;&gt;
 *
 *     &lt;android.support.v4.widget.NestedScrollView
 *             android:layout_width=&quot;match_parent&quot;
 *             android:layout_height=&quot;match_parent&quot;
 *             app:layout_behavior=&quot;@string/appbar_scrolling_view_behavior&quot;&gt;
 *
 *         &lt;!-- Your scrolling content --&gt;
 *
 *     &lt;/android.support.v4.widget.NestedScrollView&gt;
 *
 *     &lt;AppBarLayout
 *             android:layout_height=&quot;wrap_content&quot;
 *             android:layout_width=&quot;match_parent&quot;&gt;
 *
 *         &lt;android.support.v7.widget.Toolbar
 *                 ...
 *                 app:layout_scrollFlags=&quot;scroll|enterAlways&quot;/&gt;
 *
 *         &lt;android.support.design.widget.TabLayout
 *                 ...
 *                 app:layout_scrollFlags=&quot;scroll|enterAlways&quot;/&gt;
 *
 *     &lt;/AppBarLayout&gt;
 *
 * &lt;/CoordinatorLayout&gt;
 * </pre>
 *
 * @see <a href="http://www.google.com/design/spec/layout/structure.html#structure-app-bar">
 *     http://www.google.com/design/spec/layout/structure.html#structure-app-bar</a>
 */
@CoordinatorLayout.DefaultBehavior(AppBarLayout.Behavior.class)
public class AppBarLayout extends LinearLayout {

    static final int PENDING_ACTION_NONE = 0x0;
    static final int PENDING_ACTION_EXPANDED = 0x1;
    static final int PENDING_ACTION_COLLAPSED = 0x2;
    static final int PENDING_ACTION_ANIMATE_ENABLED = 0x4;

    /**
     * Interface definition for a callback to be invoked when an {@link AppBarLayout}'s vertical
     * offset changes.
     */
    public interface OnOffsetChangedListener {
        /**
         * Called when the {@link AppBarLayout}'s layout offset has been changed. This allows
         * child views to implement custom behavior based on the offset (for instance pinning a
         * view at a certain y value).
         *
         * @param appBarLayout the {@link AppBarLayout} which offset has changed
         * @param verticalOffset the vertical offset for the parent {@link AppBarLayout}, in px
         */
        void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset);
    }

    private static final int INVALID_SCROLL_RANGE = -1;

    private int mTotalScrollRange = INVALID_SCROLL_RANGE;
    private int mDownPreScrollRange = INVALID_SCROLL_RANGE;
    private int mDownScrollRange = INVALID_SCROLL_RANGE;

    private boolean mHaveChildWithInterpolator;

    private int mPendingAction = PENDING_ACTION_NONE;

    private WindowInsetsCompat mLastInsets;

    private List<AppBarLayout.OnOffsetChangedListener> mListeners;

    private boolean mCollapsible;
    private boolean mCollapsed;

    private final int[] mTmpStatesArray = new int[2];

    private int highMediumGradient = -Integer.MAX_VALUE;
    private int lowMediumGradient = -Integer.MAX_VALUE;
    private int mToolbarHeight = 90;

    public AppBarLayout(Context context) {
        this(context, null);
    }

    public AppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);

        ThemeUtils.checkAppCompatTheme(context);

        if (Build.VERSION.SDK_INT >= 21) {
            // Use the bounds view outline provider so that we cast a shadow, even without a
            // background
            ViewUtilsLollipop.setBoundsViewOutlineProvider(this);

            // If we're running on API 21+, we should reset any state list animator from our
            // default style
            ViewUtilsLollipop.setStateListAnimatorFromAttrs(this, attrs, 0,
                    R.style.Widget_Design_AppBarLayout);
        }

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AppBarLayout,
                0, R.style.Widget_Design_AppBarLayout);
        ViewCompat.setBackground(this, a.getDrawable(R.styleable.AppBarLayout_android_background));
        if (a.hasValue(R.styleable.AppBarLayout_expanded)) {
            setExpanded(a.getBoolean(R.styleable.AppBarLayout_expanded, false));
        }
        if (Build.VERSION.SDK_INT >= 21 && a.hasValue(R.styleable.AppBarLayout_elevation)) {
            ViewUtilsLollipop.setDefaultAppBarLayoutStateListAnimator(
                    this, a.getDimensionPixelSize(R.styleable.AppBarLayout_elevation, 0));
        }
        a.recycle();

        ViewCompat.setOnApplyWindowInsetsListener(this,
                new android.support.v4.view.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(View v,
                                                                  WindowInsetsCompat insets) {
                        return onWindowInsetChanged(insets);
                    }
                });
    }

    public void setMediumGradient(int high, int low) {
        this.highMediumGradient = high;
        this.lowMediumGradient = low;
    }

    public void pinHeaderTopBottomOffset(int newOffset) {
        final CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) getLayoutParams();
        final CoordinatorLayout.Behavior viewBehavior = lp.getBehavior();
        if (viewBehavior != null && viewBehavior instanceof HeaderBehavior) {
            ((HeaderBehavior) viewBehavior).setHeaderTopBottomOffset((CoordinatorLayout) getParent(), this, newOffset);
        }
    }

        /**
         * Add a listener that will be called when the offset of this {@link AppBarLayout} changes.
         *
         * @param listener The listener that will be called when the offset changes.]
         *
         * @see #removeOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener)
         */
    public void addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener listener) {
        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }
        if (listener != null && !mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    /**
     * Remove the previously added {@link AppBarLayout.OnOffsetChangedListener}.
     *
     * @param listener the listener to remove.
     */
    public void removeOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener listener) {
        if (mListeners != null && listener != null) {
            mListeners.remove(listener);
        }
    }

    private OnPreDrawListener mOnPreDrawListener;
    class OnPreDrawListener implements ViewTreeObserver.OnPreDrawListener {
        @Override
        public boolean onPreDraw() {
            pinHeaderTopBottomOffset(-200);
            detatchViewTreeObserver();
            return true;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mOnPreDrawListener == null) {
            mOnPreDrawListener = new OnPreDrawListener();
        }
        final ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnPreDrawListener(mOnPreDrawListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        detatchViewTreeObserver();
    }

    private void detatchViewTreeObserver() {
        if (mOnPreDrawListener != null) {
            final ViewTreeObserver vto = getViewTreeObserver();
            vto.removeOnPreDrawListener(mOnPreDrawListener);
            mOnPreDrawListener = null;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        invalidateScrollRanges();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        invalidateScrollRanges();

        mHaveChildWithInterpolator = false;
        for (int i = 0, z = getChildCount(); i < z; i++) {
            final View child = getChildAt(i);
            final AppBarLayout.LayoutParams childLp = (AppBarLayout.LayoutParams) child.getLayoutParams();
            final Interpolator interpolator = childLp.getScrollInterpolator();

            if (interpolator != null) {
                mHaveChildWithInterpolator = true;
                break;
            }
        }

        updateCollapsible();
    }

    private void updateCollapsible() {
        boolean haveCollapsibleChild = false;
        for (int i = 0, z = getChildCount(); i < z; i++) {
            if (((AppBarLayout.LayoutParams) getChildAt(i).getLayoutParams()).isCollapsible()) {
                haveCollapsibleChild = true;
                break;
            }
        }
        setCollapsibleState(haveCollapsibleChild);
    }

    private void invalidateScrollRanges() {
        // Invalidate the scroll ranges
        mTotalScrollRange = INVALID_SCROLL_RANGE;
        mDownPreScrollRange = INVALID_SCROLL_RANGE;
        mDownScrollRange = INVALID_SCROLL_RANGE;
    }

    @Override
    public void setOrientation(int orientation) {
        if (orientation != VERTICAL) {
            throw new IllegalArgumentException("AppBarLayout is always vertical and does"
                    + " not support horizontal orientation");
        }
        super.setOrientation(orientation);
    }

    /**
     * Sets whether this {@link AppBarLayout} is expanded or not, animating if it has already
     * been laid out.
     *
     * <p>As with {@link AppBarLayout}'s scrolling, this method relies on this layout being a
     * direct child of a {@link CoordinatorLayout}.</p>
     *
     * @param expanded true if the layout should be fully expanded, false if it should
     *                 be fully collapsed
     *
     * @attr ref android.support.design.R.styleable#AppBarLayout_expanded
     */
    public void setExpanded(boolean expanded) {
        setExpanded(expanded, ViewCompat.isLaidOut(this));
    }

    /**
     * Sets whether this {@link AppBarLayout} is expanded or not.
     *
     * <p>As with {@link AppBarLayout}'s scrolling, this method relies on this layout being a
     * direct child of a {@link CoordinatorLayout}.</p>
     *
     * @param expanded true if the layout should be fully expanded, false if it should
     *                 be fully collapsed
     * @param animate Whether to animate to the new state
     *
     * @attr ref android.support.design.R.styleable#AppBarLayout_expanded
     */
    public void setExpanded(boolean expanded, boolean animate) {
        mPendingAction = (expanded ? PENDING_ACTION_EXPANDED : PENDING_ACTION_COLLAPSED)
                | (animate ? PENDING_ACTION_ANIMATE_ENABLED : 0);
        requestLayout();
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof AppBarLayout.LayoutParams;
    }

    @Override
    protected AppBarLayout.LayoutParams generateDefaultLayoutParams() {
        return new AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT, AppBarLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public AppBarLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new AppBarLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected AppBarLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof LinearLayout.LayoutParams) {
            return new AppBarLayout.LayoutParams((LinearLayout.LayoutParams) p);
        } else if (p instanceof MarginLayoutParams) {
            return new AppBarLayout.LayoutParams((MarginLayoutParams) p);
        }
        return new AppBarLayout.LayoutParams(p);
    }

    boolean hasChildWithInterpolator() {
        return mHaveChildWithInterpolator;
    }

    /**
     * Returns the scroll range of all children.
     *
     * @return the scroll range in px
     */
    public final int getTotalScrollRange() {
        if (mTotalScrollRange != INVALID_SCROLL_RANGE) {
            return mTotalScrollRange;
        }

        int range = 0;
        for (int i = 0, z = getChildCount(); i < z; i++) {
            final View child = getChildAt(i);
            final AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams) child.getLayoutParams();
            final int childHeight = child.getMeasuredHeight();
            final int flags = lp.mScrollFlags;

            if ((flags & AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL) != 0) {
                // We're set to scroll so add the child's height
                range += childHeight + lp.topMargin + lp.bottomMargin;

                if ((flags & AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED) != 0) {
                    // For a collapsing scroll, we to take the collapsed height into account.
                    // We also break straight away since later views can't scroll beneath
                    // us
                    range -= ViewCompat.getMinimumHeight(child);
                    break;
                }
            } else {
                // As soon as a view doesn't have the scroll flag, we end the range calculation.
                // This is because views below can not scroll under a fixed view.
                break;
            }
        }
        return mTotalScrollRange = Math.max(0, range - getTopInset()) - mToolbarHeight;
    }

    boolean hasScrollableChildren() {
        return getTotalScrollRange() != 0;
    }

    /**
     * Return the scroll range when scrolling up from a nested pre-scroll.
     */
    int getUpNestedPreScrollRange() {
        return getTotalScrollRange();
    }

    /**
     * Return the scroll range when scrolling down from a nested pre-scroll.
     */
    int getDownNestedPreScrollRange() {
        if (mDownPreScrollRange != INVALID_SCROLL_RANGE) {
            // If we already have a valid value, return it
            return mDownPreScrollRange;
        }

        int range = 0;
        for (int i = getChildCount() - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            final AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams) child.getLayoutParams();
            final int childHeight = child.getMeasuredHeight();
            final int flags = lp.mScrollFlags;

            if ((flags & AppBarLayout.LayoutParams.FLAG_QUICK_RETURN) == AppBarLayout.LayoutParams.FLAG_QUICK_RETURN) {
                // First take the margin into account
                range += lp.topMargin + lp.bottomMargin;
                // The view has the quick return flag combination...
                if ((flags & AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED) != 0) {
                    // If they're set to enter collapsed, use the minimum height
                    range += ViewCompat.getMinimumHeight(child);
                } else if ((flags & AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED) != 0) {
                    // Only enter by the amount of the collapsed height
                    range += childHeight - ViewCompat.getMinimumHeight(child);
                } else {
                    // Else use the full height
                    range += childHeight;
                }
            } else if (range > 0) {
                // If we've hit an non-quick return scrollable view, and we've already hit a
                // quick return view, return now
                break;
            }
        }
        return mDownPreScrollRange = Math.max(0, range);
    }

    /**
     * Return the scroll range when scrolling down from a nested scroll.
     */
    int getDownNestedScrollRange() {
        if (mDownScrollRange != INVALID_SCROLL_RANGE) {
            // If we already have a valid value, return it
            return mDownScrollRange;
        }

        int range = 0;
        for (int i = 0, z = getChildCount(); i < z; i++) {
            final View child = getChildAt(i);
            final AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams) child.getLayoutParams();
            int childHeight = child.getMeasuredHeight();
            childHeight += lp.topMargin + lp.bottomMargin;

            final int flags = lp.mScrollFlags;

            if ((flags & AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL) != 0) {
                // We're set to scroll so add the child's height
                range += childHeight;

                if ((flags & AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED) != 0) {
                    // For a collapsing exit scroll, we to take the collapsed height into account.
                    // We also break the range straight away since later views can't scroll
                    // beneath us
                    range -= ViewCompat.getMinimumHeight(child) + getTopInset();
                    break;
                }
            } else {
                // As soon as a view doesn't have the scroll flag, we end the range calculation.
                // This is because views below can not scroll under a fixed view.
                break;
            }
        }
        return mDownScrollRange = Math.max(0, range);
    }

    void dispatchOffsetUpdates(int offset) {
        // Iterate backwards through the list so that most recently added listeners
        // get the first chance to decide
        if (mListeners != null) {
            for (int i = 0, z = mListeners.size(); i < z; i++) {
                final AppBarLayout.OnOffsetChangedListener listener = mListeners.get(i);
                if (listener != null) {
                    listener.onOffsetChanged(this, offset);
                }
            }
        }
    }

    final int getMinimumHeightForVisibleOverlappingContent() {
        final int topInset = getTopInset();
        final int minHeight = ViewCompat.getMinimumHeight(this);
        if (minHeight != 0) {
            // If this layout has a min height, use it (doubled)
            return (minHeight * 2) + topInset;
        }

        // Otherwise, we'll use twice the min height of our last child
        final int childCount = getChildCount();
        final int lastChildMinHeight = childCount >= 1
                ? ViewCompat.getMinimumHeight(getChildAt(childCount - 1)) : 0;
        if (lastChildMinHeight != 0) {
            return (lastChildMinHeight * 2) + topInset;
        }

        // If we reach here then we don't have a min height explicitly set. Instead we'll take a
        // guess at 1/3 of our height being visible
        return getHeight() / 3;
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] extraStates = mTmpStatesArray;
        final int[] states = super.onCreateDrawableState(extraSpace + extraStates.length);

        extraStates[0] = mCollapsible ? R.attr.state_collapsible : -R.attr.state_collapsible;
        extraStates[1] = mCollapsible && mCollapsed
                ? R.attr.state_collapsed : -R.attr.state_collapsed;

        return mergeDrawableStates(states, extraStates);
    }

    /**
     * Sets whether the AppBarLayout has collapsible children or not.
     *
     * @return true if the collapsible state changed
     */
    private boolean setCollapsibleState(boolean collapsible) {
        if (mCollapsible != collapsible) {
            mCollapsible = collapsible;
            refreshDrawableState();
            return true;
        }
        return false;
    }

    /**
     * Sets whether the AppBarLayout is in a collapsed state or not.
     *
     * @return true if the collapsed state changed
     */
    boolean setCollapsedState(boolean collapsed) {
        if (mCollapsed != collapsed) {
            mCollapsed = collapsed;
            refreshDrawableState();
            return true;
        }
        return false;
    }

    /**
     * @deprecated target elevation is now deprecated. AppBarLayout's elevation is now
     * controlled via a {@link android.animation.StateListAnimator}. If a target
     * elevation is set, either by this method or the {@code app:elevation} attribute,
     * a new state list animator is created which uses the given {@code elevation} value.
     *
     * @attr ref android.support.design.R.styleable#AppBarLayout_elevation
     */
    @Deprecated
    public void setTargetElevation(float elevation) {
        if (Build.VERSION.SDK_INT >= 21) {
            ViewUtilsLollipop.setDefaultAppBarLayoutStateListAnimator(this, elevation);
        }
    }

    /**
     * @deprecated target elevation is now deprecated. AppBarLayout's elevation is now
     * controlled via a {@link android.animation.StateListAnimator}. This method now
     * always returns 0.
     */
    @Deprecated
    public float getTargetElevation() {
        return 0;
    }

    int getPendingAction() {
        return mPendingAction;
    }

    void resetPendingAction() {
        mPendingAction = PENDING_ACTION_NONE;
    }

    @VisibleForTesting
    final int getTopInset() {
        return mLastInsets != null ? mLastInsets.getSystemWindowInsetTop() : 0;
    }

    WindowInsetsCompat onWindowInsetChanged(final WindowInsetsCompat insets) {
        WindowInsetsCompat newInsets = null;

        if (ViewCompat.getFitsSystemWindows(this)) {
            // If we're set to fit system windows, keep the insets
            newInsets = insets;
        }

        // If our insets have changed, keep them and invalidate the scroll ranges...
        if (!ViewUtils.objectEquals(mLastInsets, newInsets)) {
            mLastInsets = newInsets;
            invalidateScrollRanges();
        }

        return insets;
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {

        /** @hide */
        @RestrictTo(GROUP_ID)
        @IntDef(flag=true, value={
                SCROLL_FLAG_SCROLL,
                SCROLL_FLAG_EXIT_UNTIL_COLLAPSED,
                SCROLL_FLAG_ENTER_ALWAYS,
                SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED,
                SCROLL_FLAG_SNAP
        })
        @Retention(RetentionPolicy.SOURCE)
        public @interface ScrollFlags {}

        /**
         * The view will be scroll in direct relation to scroll events. This flag needs to be
         * set for any of the other flags to take effect. If any sibling views
         * before this one do not have this flag, then this value has no effect.
         */
        public static final int SCROLL_FLAG_SCROLL = 0x1;

        /**
         * When exiting (scrolling off screen) the view will be scrolled until it is
         * 'collapsed'. The collapsed height is defined by the view's minimum height.
         *
         * @see ViewCompat#getMinimumHeight(View)
         * @see View#setMinimumHeight(int)
         */
        public static final int SCROLL_FLAG_EXIT_UNTIL_COLLAPSED = 0x2;

        /**
         * When entering (scrolling on screen) the view will scroll on any downwards
         * scroll event, regardless of whether the scrolling view is also scrolling. This
         * is commonly referred to as the 'quick return' pattern.
         */
        public static final int SCROLL_FLAG_ENTER_ALWAYS = 0x4;

        /**
         * An additional flag for 'enterAlways' which modifies the returning view to
         * only initially scroll back to it's collapsed height. Once the scrolling view has
         * reached the end of it's scroll range, the remainder of this view will be scrolled
         * into view. The collapsed height is defined by the view's minimum height.
         *
         * @see ViewCompat#getMinimumHeight(View)
         * @see View#setMinimumHeight(int)
         */
        public static final int SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED = 0x8;

        /**
         * Upon a scroll ending, if the view is only partially visible then it will be snapped
         * and scrolled to it's closest edge. For example, if the view only has it's bottom 25%
         * displayed, it will be scrolled off screen completely. Conversely, if it's bottom 75%
         * is visible then it will be scrolled fully into view.
         */
        public static final int SCROLL_FLAG_SNAP = 0x10;

        /**
         * Internal flags which allows quick checking features
         */
        static final int FLAG_QUICK_RETURN = SCROLL_FLAG_SCROLL | SCROLL_FLAG_ENTER_ALWAYS;
        static final int FLAG_SNAP = SCROLL_FLAG_SCROLL | SCROLL_FLAG_SNAP;
        static final int COLLAPSIBLE_FLAGS = SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
                | SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED;

        int mScrollFlags = SCROLL_FLAG_SCROLL;
        Interpolator mScrollInterpolator;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.AppBarLayout_Layout);
            mScrollFlags = a.getInt(R.styleable.AppBarLayout_Layout_layout_scrollFlags, 0);
            if (a.hasValue(R.styleable.AppBarLayout_Layout_layout_scrollInterpolator)) {
                int resId = a.getResourceId(
                        R.styleable.AppBarLayout_Layout_layout_scrollInterpolator, 0);
                mScrollInterpolator = android.view.animation.AnimationUtils.loadInterpolator(
                        c, resId);
            }
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height, weight);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        public LayoutParams(LinearLayout.LayoutParams source) {
            super(source);
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        public LayoutParams(AppBarLayout.LayoutParams source) {
            super(source);
            mScrollFlags = source.mScrollFlags;
            mScrollInterpolator = source.mScrollInterpolator;
        }

        /**
         * Set the scrolling flags.
         *
         * @param flags bitwise int of {@link #SCROLL_FLAG_SCROLL},
         *             {@link #SCROLL_FLAG_EXIT_UNTIL_COLLAPSED}, {@link #SCROLL_FLAG_ENTER_ALWAYS},
         *             {@link #SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED} and {@link #SCROLL_FLAG_SNAP }.
         *
         * @see #getScrollFlags()
         *
         * @attr ref android.support.design.R.styleable#AppBarLayout_Layout_layout_scrollFlags
         */
        public void setScrollFlags(@AppBarLayout.LayoutParams.ScrollFlags int flags) {
            mScrollFlags = flags;
        }

        /**
         * Returns the scrolling flags.
         *
         * @see #setScrollFlags(int)
         *
         * @attr ref android.support.design.R.styleable#AppBarLayout_Layout_layout_scrollFlags
         */
        @AppBarLayout.LayoutParams.ScrollFlags
        public int getScrollFlags() {
            return mScrollFlags;
        }

        /**
         * Set the interpolator to when scrolling the view associated with this
         * {@link AppBarLayout.LayoutParams}.
         *
         * @param interpolator the interpolator to use, or null to use normal 1-to-1 scrolling.
         *
         * @attr ref android.support.design.R.styleable#AppBarLayout_Layout_layout_scrollInterpolator
         * @see #getScrollInterpolator()
         */
        public void setScrollInterpolator(Interpolator interpolator) {
            mScrollInterpolator = interpolator;
        }

        /**
         * Returns the {@link Interpolator} being used for scrolling the view associated with this
         * {@link AppBarLayout.LayoutParams}. Null indicates 'normal' 1-to-1 scrolling.
         *
         * @attr ref android.support.design.R.styleable#AppBarLayout_Layout_layout_scrollInterpolator
         * @see #setScrollInterpolator(Interpolator)
         */
        public Interpolator getScrollInterpolator() {
            return mScrollInterpolator;
        }

        /**
         * Returns true if the scroll flags are compatible for 'collapsing'
         */
        boolean isCollapsible() {
            return (mScrollFlags & SCROLL_FLAG_SCROLL) == SCROLL_FLAG_SCROLL
                    && (mScrollFlags & COLLAPSIBLE_FLAGS) != 0;
        }
    }

    /**
     * The default {@link AppBarLayout.Behavior} for {@link AppBarLayout}. Implements the necessary nested
     * scroll handling with offsetting.
     */
    public static class Behavior extends HeaderBehavior<AppBarLayout> {
        private static final int MAX_OFFSET_ANIMATION_DURATION = 600; // ms
        private static final int INVALID_POSITION = -1;

        /**
         * Callback to allow control over any {@link AppBarLayout} dragging.
         */
        public static abstract class DragCallback {
            /**
             * Allows control over whether the given {@link AppBarLayout} can be dragged or not.
             *
             * <p>Dragging is defined as a direct touch on the AppBarLayout with movement. This
             * call does not affect any nested scrolling.</p>
             *
             * @return true if we are in a position to scroll the AppBarLayout via a drag, false
             *         if not.
             */
            public abstract boolean canDrag(@NonNull AppBarLayout appBarLayout);
        }

        private int mOffsetDelta;

        private boolean mSkipNestedPreScroll;
        private boolean mWasNestedFlung;

        private ValueAnimatorCompat mOffsetAnimator;

        private int mOffsetToChildIndexOnLayout = INVALID_POSITION;
        private boolean mOffsetToChildIndexOnLayoutIsMinHeight;
        private float mOffsetToChildIndexOnLayoutPerc;

        private WeakReference<View> mLastNestedScrollingChildRef;
        private AppBarLayout.Behavior.DragCallback mOnDragCallback;

        public Behavior() {}

        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child,
                                           View directTargetChild, View target, int nestedScrollAxes) {
            // Return true if we're nested scrolling vertically, and we have scrollable children
            // and the scrolling view is big enough to scroll
            SysoutUtil.sysout("AppBarLayout", "onStartNestedScroll");
            final boolean started = (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0
                    && child.hasScrollableChildren()
                    && parent.getHeight() - directTargetChild.getHeight() <= child.getHeight();

            if (started && mOffsetAnimator != null) {
                // Cancel any offset animation
                mOffsetAnimator.cancel();
            }

            // A new nested scroll has started so clear out the previous ref
            mLastNestedScrollingChildRef = null;

            SysoutUtil.sysout("AppBarLayout", "onStartNestedScroll", "started", "" + started);
            return started;
        }

        @Override
        public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child,
                                      View target, int dx, int dy, int[] consumed) {
            SysoutUtil.sysout("AppBarLayout", "onNestedPreScroll", "getDownNestedPreScrollRange:" + child.getDownNestedPreScrollRange());
            if (dy != 0 && !mSkipNestedPreScroll) {
                int min, max;
                if (dy < 0) {
                    // We're scrolling down
                    min = -child.getTotalScrollRange();
                    max = min + child.getDownNestedPreScrollRange();
                } else {
                    // We're scrolling up
                    min = -child.getUpNestedPreScrollRange();
                    max = 0;
                }
                consumed[1] = scroll(coordinatorLayout, child, dy, min, max);
                SysoutUtil.sysout("AppBarLayout", "onNestedPreScroll", "consumed", "" + consumed[0]);
            }
        }

        @Override
        public void onNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child,
                                   View target, int dxConsumed, int dyConsumed,
                                   int dxUnconsumed, int dyUnconsumed) {
            SysoutUtil.sysout("AppBarLayout", "onNestedScroll");
            if (dyUnconsumed < 0) {
                // If the scrolling view is scrolling down but not consuming, it's probably be at
                // the top of it's content
                scroll(coordinatorLayout, child, dyUnconsumed,
                        -child.getDownNestedScrollRange(), 0);
                // Set the expanding flag so that onNestedPreScroll doesn't handle any events
                mSkipNestedPreScroll = true;
            } else {
                // As we're no longer handling nested scrolls, reset the skip flag
                mSkipNestedPreScroll = false;
            }
        }

        @Override
        public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout abl,
                                       View target) {
            SysoutUtil.sysout("AppBarLayout", "onStopNestedScroll", "snapToChildIfNeeded", "" + (!mWasNestedFlung));
            if (!mWasNestedFlung) {
                // If we haven't been flung then let's see if the current view has been set to snap
                snapToChildIfNeeded(coordinatorLayout, abl);
            }

            // Reset the flags
            mSkipNestedPreScroll = false;
            mWasNestedFlung = false;
            // Keep a reference to the previous nested scrolling child
            mLastNestedScrollingChildRef = new WeakReference<>(target);
        }

        @Override
        public boolean onNestedFling(final CoordinatorLayout coordinatorLayout,
                                     final AppBarLayout child, View target, float velocityX, float velocityY,
                                     boolean consumed) {
            SysoutUtil.sysout("AppBarLayout", "onNestedFling");
            boolean flung = false;

            if (!consumed) {
                // It has been consumed so let's fling ourselves
                flung = fling(coordinatorLayout, child, -child.getTotalScrollRange(),
                        0, -velocityY);
            } else {
                // If we're scrolling up and the child also consumed the fling. We'll fake scroll
                // up to our 'collapsed' offset
                float currentOffset = getTopBottomOffsetForScrollingSibling();
                int targetScroll;
                if (velocityY < 0) {
                    // We're scrolling down
                    targetScroll = -child.getTotalScrollRange() + child.getDownNestedPreScrollRange();
                    if (getTopBottomOffsetForScrollingSibling() < targetScroll) {
                        // If we're currently not expanded more than the target scroll, we'll
                        // animate a fling
                        animateOffsetTo(coordinatorLayout, child, targetScroll, velocityY);
                        flung = true;
                    }
                } else {
                    // We're scrolling up
                    targetScroll = -child.getUpNestedPreScrollRange();
                    if (getTopBottomOffsetForScrollingSibling() > targetScroll) {
                        // If we're currently not expanded less than the target scroll, we'll
                        // animate a fling
                        animateOffsetTo(coordinatorLayout, child, targetScroll, velocityY);
                        flung = true;
                    }
                }
                SysoutUtil.sysout("AppBarLayout", "onNestedFling", "animateOffsetTo", "targetScroll : " + targetScroll, "currentScroll : " + currentOffset, "down : " + (velocityY < 0));
            }

            mWasNestedFlung = flung;
            return flung;
        }

        /**
         * Set a callback to control any {@link AppBarLayout} dragging.
         *
         * @param callback the callback to use, or {@code null} to use the default behavior.
         */
        public void setDragCallback(@Nullable AppBarLayout.Behavior.DragCallback callback) {
            mOnDragCallback = callback;
        }

        private void animateOffsetTo(final CoordinatorLayout coordinatorLayout,
                                     final AppBarLayout child, final int offset, float velocity) {
            int height = child.getHeight();
            int targetOffset = (offset == -height ? offset+child.mToolbarHeight : offset);
            final int distance = Math.abs(getTopBottomOffsetForScrollingSibling() - targetOffset);

            final int duration;
            velocity = Math.abs(velocity);
            if (velocity > 0) {
                duration = 3 * Math.round(1000 * (distance / velocity));
            } else {
                final float distanceRatio = (float) distance / child.getHeight();
                duration = (int) ((distanceRatio + 1) * 150);
            }

            animateOffsetWithDuration(coordinatorLayout, child, targetOffset, duration);
        }

        private void animateOffsetWithDuration(final CoordinatorLayout coordinatorLayout,
                                               final AppBarLayout child, final int offset, final int duration) {
            final int currentOffset = getTopBottomOffsetForScrollingSibling();
            if (currentOffset == offset) {
                if (mOffsetAnimator != null && mOffsetAnimator.isRunning()) {
                    mOffsetAnimator.cancel();
                }
                return;
            }

            if (mOffsetAnimator == null) {
                mOffsetAnimator = ViewUtils.createAnimator();
                mOffsetAnimator.setInterpolator(AnimationUtils.DECELERATE_INTERPOLATOR);
                mOffsetAnimator.addUpdateListener(new ValueAnimatorCompat.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimatorCompat animator) {
                        setHeaderTopBottomOffset(coordinatorLayout, child,
                                animator.getAnimatedIntValue());
                    }
                });
            } else {
                mOffsetAnimator.cancel();
            }

            mOffsetAnimator.setDuration(Math.min(duration, MAX_OFFSET_ANIMATION_DURATION));
            mOffsetAnimator.setIntValues(currentOffset, offset);
            mOffsetAnimator.start();
        }

        private int getChildIndexOnOffset(AppBarLayout abl, final int offset) {
            for (int i = 0, count = abl.getChildCount(); i < count; i++) {
                View child = abl.getChildAt(i);
                if (child.getTop() <= -offset && child.getBottom() >= -offset) {
                    return i;
                }
            }
            return -1;
        }

        private void snapToChildIfNeeded(CoordinatorLayout coordinatorLayout, AppBarLayout abl) {
            SysoutUtil.sysout("AppBarLayout", "snapToChildIfNeeded");
            final int offset = getTopBottomOffsetForScrollingSibling();
            final int offsetChildIndex = getChildIndexOnOffset(abl, offset);
            if (offsetChildIndex >= 0) {
                final View offsetChild = abl.getChildAt(offsetChildIndex);
                final AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams) offsetChild.getLayoutParams();
                final int flags = lp.getScrollFlags();

                if ((flags & AppBarLayout.LayoutParams.FLAG_SNAP) == AppBarLayout.LayoutParams.FLAG_SNAP) {
                    // We're set the snap, so animate the offset to the nearest edge
                    int snapTop = -offsetChild.getTop();
                    int snapBottom = -offsetChild.getBottom();

                    if (offsetChildIndex == abl.getChildCount() - 1) {
                        // If this is the last child, we need to take the top inset into account
                        snapBottom += abl.getTopInset();
                    }

                    if (checkFlag(flags, AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED)) {
                        // If the view is set only exit until it is collapsed, we'll abide by that
                        snapBottom += ViewCompat.getMinimumHeight(offsetChild);
                    } else if (checkFlag(flags, AppBarLayout.LayoutParams.FLAG_QUICK_RETURN
                            | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)) {
                        // If it's set to always enter collapsed, it actually has two states. We
                        // select the state and then snap within the state
                        final int seam = snapBottom + ViewCompat.getMinimumHeight(offsetChild);
                        if (offset < seam) {
                            snapTop = seam;
                        } else {
                            snapBottom = seam;
                        }
                    }

                    SysoutUtil.sysout("AppBarLayout", "snapToChildIfNeeded", "offset:"+offset, "snapBottom:"+snapBottom, "snapTop:"+snapTop);
                    final int newOffset = getNextGradientOffset(offset, snapTop, snapBottom, abl);//constrain(offset, snapTop, snapBottom);
                    SysoutUtil.sysout("AppBarLayout", "snapToChildIfNeeded", "newOffset:"+newOffset, "getTotalScrollRange:" + (-abl.getTotalScrollRange()));
                    int targetOffset = MathUtils.constrain(newOffset, -abl.getTotalScrollRange(), 0);
                    SysoutUtil.sysout("AppBarLayout", "snapToChildIfNeeded", "" + targetOffset);
                    animateOffsetTo(coordinatorLayout, abl, targetOffset, 0);
                }
            }
        }

        private int getNextGradientOffset(int offset, int top, int bottom, AppBarLayout abl) {
            if(abl.lowMediumGradient < bottom || abl.highMediumGradient < bottom) {
                abl.highMediumGradient = bottom * 1 / 3;
                abl.lowMediumGradient = bottom * 2 / 3;
                return constrain(offset, top, bottom+abl.mToolbarHeight);
            }
            if(offset < abl.lowMediumGradient) {
                return constrain(offset, abl.lowMediumGradient, bottom+abl.mToolbarHeight);
            } else if(offset < abl.highMediumGradient) {
                return constrain(offset, abl.highMediumGradient, abl.lowMediumGradient);
            } else {
                return constrain(offset, top, abl.highMediumGradient);
            }
        }

        private int constrain(int offset, int top, int bottom) {
            int newOffset = offset < (top + bottom) / 2
                    ? bottom
                    : top;
            return newOffset;
        }

        private static boolean checkFlag(final int flags, final int check) {
            return (flags & check) == check;
        }

        @Override
        public boolean onMeasureChild(CoordinatorLayout parent, AppBarLayout child,
                                      int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec,
                                      int heightUsed) {
            final CoordinatorLayout.LayoutParams lp =
                    (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            if (lp.height == CoordinatorLayout.LayoutParams.WRAP_CONTENT) {
                // If the view is set to wrap on it's height, CoordinatorLayout by default will
                // cap the view at the CoL's height. Since the AppBarLayout can scroll, this isn't
                // what we actually want, so we measure it ourselves with an unspecified spec to
                // allow the child to be larger than it's parent
                parent.onMeasureChild(child, parentWidthMeasureSpec, widthUsed,
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), heightUsed);
                return true;
            }

            // Let the parent handle it as normal
            return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed,
                    parentHeightMeasureSpec, heightUsed);
        }

        @Override
        public boolean onLayoutChild(CoordinatorLayout parent, AppBarLayout abl,
                                     int layoutDirection) {
            boolean handled = super.onLayoutChild(parent, abl, layoutDirection);

            final int pendingAction = abl.getPendingAction();
            if (pendingAction != PENDING_ACTION_NONE) {
                final boolean animate = (pendingAction & PENDING_ACTION_ANIMATE_ENABLED) != 0;
                if ((pendingAction & PENDING_ACTION_COLLAPSED) != 0) {
                    final int offset = -abl.getUpNestedPreScrollRange();
                    if (animate) {
                        animateOffsetTo(parent, abl, offset, 0);
                    } else {
                        setHeaderTopBottomOffset(parent, abl, offset);
                    }
                } else if ((pendingAction & PENDING_ACTION_EXPANDED) != 0) {
                    if (animate) {
                        animateOffsetTo(parent, abl, 0, 0);
                    } else {
                        setHeaderTopBottomOffset(parent, abl, 0);
                    }
                }
            } else if (mOffsetToChildIndexOnLayout >= 0) {
                View child = abl.getChildAt(mOffsetToChildIndexOnLayout);
                int offset = -child.getBottom();
                if (mOffsetToChildIndexOnLayoutIsMinHeight) {
                    offset += ViewCompat.getMinimumHeight(child);
                } else {
                    offset += Math.round(child.getHeight() * mOffsetToChildIndexOnLayoutPerc);
                }
                setTopAndBottomOffset(offset);
            }

            // Finally reset any pending states
            abl.resetPendingAction();
            mOffsetToChildIndexOnLayout = INVALID_POSITION;

            // We may have changed size, so let's constrain the top and bottom offset correctly,
            // just in case we're out of the bounds
            setTopAndBottomOffset(MathUtils.constrain(getTopAndBottomOffset(), -abl.getTotalScrollRange(), 0));

            // Make sure we dispatch the offset update
            abl.dispatchOffsetUpdates(getTopAndBottomOffset());

            return handled;
        }

        @Override
        boolean canDragView(AppBarLayout view) {
            if (mOnDragCallback != null) {
                // If there is a drag callback set, it's in control
                return mOnDragCallback.canDrag(view);
            }

            // Else we'll use the default behaviour of seeing if it can scroll down
            if (mLastNestedScrollingChildRef != null) {
                // If we have a reference to a scrolling view, check it
                final View scrollingView = mLastNestedScrollingChildRef.get();
                return scrollingView != null && scrollingView.isShown()
                        && !ViewCompat.canScrollVertically(scrollingView, -1);
            } else {
                // Otherwise we assume that the scrolling view hasn't been scrolled and can drag.
                return true;
            }
        }

        @Override
        void onFlingFinished(CoordinatorLayout parent, AppBarLayout layout) {
            // At the end of a manual fling, check to see if we need to snap to the edge-child
            snapToChildIfNeeded(parent, layout);
        }

        @Override
        int getMaxDragOffset(AppBarLayout view) {
            return -view.getDownNestedScrollRange();
        }

        @Override
        int getScrollRangeForDragFling(AppBarLayout view) {
            return view.getTotalScrollRange();
        }

        @Override
        int setHeaderTopBottomOffset(CoordinatorLayout coordinatorLayout,
                                     AppBarLayout appBarLayout, int newOffset, int minOffset, int maxOffset) {
            int topAndBottomOffset = getTopAndBottomOffset();
            int offsetDelta = mOffsetDelta;
            //SysoutUtil.sysout("AppBarLayout", "setHeaderTopBottomOffset", "topAndBottomOffset:" + topAndBottomOffset, "offsetDelta:" + offsetDelta);
            final int curOffset = getTopBottomOffsetForScrollingSibling();
            int consumed = 0;

            if (minOffset != 0 && curOffset >= minOffset && curOffset <= maxOffset) {
                // If we have some scrolling range, and we're currently within the min and max
                // offsets, calculate a new offset
                newOffset = MathUtils.constrain(newOffset, minOffset, maxOffset);
                if (curOffset != newOffset) {
                    final int interpolatedOffset = appBarLayout.hasChildWithInterpolator()
                            ? interpolateOffset(appBarLayout, newOffset)
                            : newOffset;

                    final boolean offsetChanged = setTopAndBottomOffset(interpolatedOffset);

                    // Update how much dy we have consumed
                    consumed = curOffset - newOffset;
                    // Update the stored sibling offset
                    mOffsetDelta = newOffset - interpolatedOffset;

                    if (!offsetChanged && appBarLayout.hasChildWithInterpolator()) {
                        // If the offset hasn't changed and we're using an interpolated scroll
                        // then we need to keep any dependent views updated. CoL will do this for
                        // us when we move, but we need to do it manually when we don't (as an
                        // interpolated scroll may finish early).
                        coordinatorLayout.dispatchDependentViewsChanged(appBarLayout);
                    }

                    // Dispatch the updates to any listeners
                    appBarLayout.dispatchOffsetUpdates(getTopAndBottomOffset());

                    // Update the AppBarLayout's drawable state (for any elevation changes)
                    updateAppBarLayoutDrawableState(coordinatorLayout, appBarLayout, newOffset,
                            newOffset < curOffset ? -1 : 1);
                    //SysoutUtil.sysout("AppBarLayout", "setHeaderTopBottomOffset", "newOffset", "" + newOffset);
                }
            } else {
                // Reset the offset delta
                mOffsetDelta = 0;
            }

            //SysoutUtil.sysout("AppBarLayout", "setHeaderTopBottomOffset", "" + consumed);
            return consumed;
        }

        @VisibleForTesting
        boolean isOffsetAnimatorRunning() {
            return mOffsetAnimator != null && mOffsetAnimator.isRunning();
        }

        private int interpolateOffset(AppBarLayout layout, final int offset) {
            final int absOffset = Math.abs(offset);

            for (int i = 0, z = layout.getChildCount(); i < z; i++) {
                final View child = layout.getChildAt(i);
                final AppBarLayout.LayoutParams childLp = (AppBarLayout.LayoutParams) child.getLayoutParams();
                final Interpolator interpolator = childLp.getScrollInterpolator();

                if (absOffset >= child.getTop() && absOffset <= child.getBottom()) {
                    if (interpolator != null) {
                        int childScrollableHeight = 0;
                        final int flags = childLp.getScrollFlags();
                        if ((flags & AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL) != 0) {
                            // We're set to scroll so add the child's height plus margin
                            childScrollableHeight += child.getHeight() + childLp.topMargin
                                    + childLp.bottomMargin;

                            if ((flags & AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED) != 0) {
                                // For a collapsing scroll, we to take the collapsed height
                                // into account.
                                childScrollableHeight -= ViewCompat.getMinimumHeight(child);
                            }
                        }

                        if (ViewCompat.getFitsSystemWindows(child)) {
                            childScrollableHeight -= layout.getTopInset();
                        }

                        if (childScrollableHeight > 0) {
                            final int offsetForView = absOffset - child.getTop();
                            final int interpolatedDiff = Math.round(childScrollableHeight *
                                    interpolator.getInterpolation(
                                            offsetForView / (float) childScrollableHeight));

                            return Integer.signum(offset) * (child.getTop() + interpolatedDiff);
                        }
                    }

                    // If we get to here then the view on the offset isn't suitable for interpolated
                    // scrolling. So break out of the loop
                    break;
                }
            }

            return offset;
        }

        private void updateAppBarLayoutDrawableState(final CoordinatorLayout parent,
                                                     final AppBarLayout layout, final int offset, final int direction) {
            final View child = getAppBarChildOnOffset(layout, offset);
            if (child != null) {
                final AppBarLayout.LayoutParams childLp = (AppBarLayout.LayoutParams) child.getLayoutParams();
                final int flags = childLp.getScrollFlags();
                boolean collapsed = false;

                if ((flags & AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL) != 0) {
                    final int minHeight = ViewCompat.getMinimumHeight(child);

                    if (direction > 0 && (flags & (AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                            | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED)) != 0) {
                        // We're set to enter always collapsed so we are only collapsed when
                        // being scrolled down, and in a collapsed offset
                        collapsed = -offset >= child.getBottom() - minHeight - layout.getTopInset();
                    } else if ((flags & AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED) != 0) {
                        // We're set to exit until collapsed, so any offset which results in
                        // the minimum height (or less) being shown is collapsed
                        collapsed = -offset >= child.getBottom() - minHeight - layout.getTopInset();
                    }
                }

                final boolean changed = layout.setCollapsedState(collapsed);

                if (changed && Build.VERSION.SDK_INT >= 11
                        && shouldJumpElevationState(parent, layout)) {
                    // If the collapsed state changed, we may need to
                    // jump to the current state if we have an overlapping view
                    layout.jumpDrawablesToCurrentState();
                }
            }
        }

        private boolean shouldJumpElevationState(CoordinatorLayout parent, AppBarLayout layout) {
            // We should jump the elevated state if we have a dependent scrolling view which has
            // an overlapping top (i.e. overlaps us)
            final List<View> dependencies = parent.getDependents(layout);
            for (int i = 0, size = dependencies.size(); i < size; i++) {
                final View dependency = dependencies.get(i);
                final CoordinatorLayout.LayoutParams lp =
                        (CoordinatorLayout.LayoutParams) dependency.getLayoutParams();
                final CoordinatorLayout.Behavior behavior = lp.getBehavior();

                if (behavior instanceof AppBarLayout.ScrollingViewBehavior) {
                    return ((AppBarLayout.ScrollingViewBehavior) behavior).getOverlayTop() != 0;
                }
            }
            return false;
        }

        private static View getAppBarChildOnOffset(final AppBarLayout layout, final int offset) {
            final int absOffset = Math.abs(offset);
            for (int i = 0, z = layout.getChildCount(); i < z; i++) {
                final View child = layout.getChildAt(i);
                if (absOffset >= child.getTop() && absOffset <= child.getBottom()) {
                    return child;
                }
            }
            return null;
        }

        @Override
        int getTopBottomOffsetForScrollingSibling() {
            return getTopAndBottomOffset() + mOffsetDelta;
        }

        @Override
        public Parcelable onSaveInstanceState(CoordinatorLayout parent, AppBarLayout abl) {
            final Parcelable superState = super.onSaveInstanceState(parent, abl);
            final int offset = getTopAndBottomOffset();

            // Try and find the first visible child...
            for (int i = 0, count = abl.getChildCount(); i < count; i++) {
                View child = abl.getChildAt(i);
                final int visBottom = child.getBottom() + offset;

                if (child.getTop() + offset <= 0 && visBottom >= 0) {
                    final AppBarLayout.Behavior.SavedState ss = new AppBarLayout.Behavior.SavedState(superState);
                    ss.firstVisibleChildIndex = i;
                    ss.firstVisibleChildAtMinimumHeight =
                            visBottom == (ViewCompat.getMinimumHeight(child) + abl.getTopInset());
                    ss.firstVisibleChildPercentageShown = visBottom / (float) child.getHeight();
                    return ss;
                }
            }

            // Else we'll just return the super state
            return superState;
        }

        @Override
        public void onRestoreInstanceState(CoordinatorLayout parent, AppBarLayout appBarLayout,
                                           Parcelable state) {
            if (state instanceof AppBarLayout.Behavior.SavedState) {
                final AppBarLayout.Behavior.SavedState ss = (AppBarLayout.Behavior.SavedState) state;
                super.onRestoreInstanceState(parent, appBarLayout, ss.getSuperState());
                mOffsetToChildIndexOnLayout = ss.firstVisibleChildIndex;
                mOffsetToChildIndexOnLayoutPerc = ss.firstVisibleChildPercentageShown;
                mOffsetToChildIndexOnLayoutIsMinHeight = ss.firstVisibleChildAtMinimumHeight;
            } else {
                super.onRestoreInstanceState(parent, appBarLayout, state);
                mOffsetToChildIndexOnLayout = INVALID_POSITION;
            }
        }

        protected static class SavedState extends AbsSavedState {
            int firstVisibleChildIndex;
            float firstVisibleChildPercentageShown;
            boolean firstVisibleChildAtMinimumHeight;

            public SavedState(Parcel source, ClassLoader loader) {
                super(source, loader);
                firstVisibleChildIndex = source.readInt();
                firstVisibleChildPercentageShown = source.readFloat();
                firstVisibleChildAtMinimumHeight = source.readByte() != 0;
            }

            public SavedState(Parcelable superState) {
                super(superState);
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                super.writeToParcel(dest, flags);
                dest.writeInt(firstVisibleChildIndex);
                dest.writeFloat(firstVisibleChildPercentageShown);
                dest.writeByte((byte) (firstVisibleChildAtMinimumHeight ? 1 : 0));
            }

            public static final Parcelable.Creator<AppBarLayout.Behavior.SavedState> CREATOR =
                    ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<AppBarLayout.Behavior.SavedState>() {
                        @Override
                        public AppBarLayout.Behavior.SavedState createFromParcel(Parcel source, ClassLoader loader) {
                            return new AppBarLayout.Behavior.SavedState(source, loader);
                        }

                        @Override
                        public AppBarLayout.Behavior.SavedState[] newArray(int size) {
                            return new AppBarLayout.Behavior.SavedState[size];
                        }
                    });
        }
    }

    /**
     * Behavior which should be used by {@link View}s which can scroll vertically and support
     * nested scrolling to automatically scroll any {@link AppBarLayout} siblings.
     */
    public static class ScrollingViewBehavior extends HeaderScrollingViewBehavior {

        public ScrollingViewBehavior() {}

        public ScrollingViewBehavior(Context context, AttributeSet attrs) {
            super(context, attrs);

            final TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.ScrollingViewBehavior_Layout);
            setOverlayTop(a.getDimensionPixelSize(
                    R.styleable.ScrollingViewBehavior_Layout_behavior_overlapTop, 0));
            a.recycle();
        }

        @Override
        public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
            // We depend on any AppBarLayouts
            return dependency instanceof AppBarLayout;
        }

        @Override
        public boolean onDependentViewChanged(CoordinatorLayout parent, View child,
                                              View dependency) {
            offsetChildAsNeeded(parent, child, dependency);
            return false;
        }

        @Override
        public boolean onRequestChildRectangleOnScreen(CoordinatorLayout parent, View child,
                                                       Rect rectangle, boolean immediate) {
            final AppBarLayout header = findFirstDependency(parent.getDependencies(child));
            if (header != null) {
                // Offset the rect by the child's left/top
                rectangle.offset(child.getLeft(), child.getTop());

                final Rect parentRect = mTempRect1;
                parentRect.set(0, 0, parent.getWidth(), parent.getHeight());

                if (!parentRect.contains(rectangle)) {
                    // If the rectangle can not be fully seen the visible bounds, collapse
                    // the AppBarLayout
                    header.setExpanded(false, !immediate);
                    return true;
                }
            }
            return false;
        }

        private void offsetChildAsNeeded(CoordinatorLayout parent, View child, View dependency) {
            final CoordinatorLayout.Behavior behavior =
                    ((CoordinatorLayout.LayoutParams) dependency.getLayoutParams()).getBehavior();
            if (behavior instanceof AppBarLayout.Behavior) {
                // Offset the child, pinning it to the bottom the header-dependency, maintaining
                // any vertical gap and overlap
                final AppBarLayout.Behavior ablBehavior = (AppBarLayout.Behavior) behavior;
                ViewCompat.offsetTopAndBottom(child, (dependency.getBottom() - child.getTop())
                        + ablBehavior.mOffsetDelta
                        + getVerticalLayoutGap()
                        - getOverlapPixelsForOffset(dependency));
            }
        }

        @Override
        float getOverlapRatioForOffset(final View header) {
            if (header instanceof AppBarLayout) {
                final AppBarLayout abl = (AppBarLayout) header;
                final int totalScrollRange = abl.getTotalScrollRange();
                final int preScrollDown = abl.getDownNestedPreScrollRange();
                final int offset = getAppBarLayoutOffset(abl);

                if (preScrollDown != 0 && (totalScrollRange + offset) <= preScrollDown) {
                    // If we're in a pre-scroll down. Don't use the offset at all.
                    return 0;
                } else {
                    final int availScrollRange = totalScrollRange - preScrollDown;
                    if (availScrollRange != 0) {
                        // Else we'll use a interpolated ratio of the overlap, depending on offset
                        return 1f + (offset / (float) availScrollRange);
                    }
                }
            }
            return 0f;
        }

        private static int getAppBarLayoutOffset(AppBarLayout abl) {
            final CoordinatorLayout.Behavior behavior =
                    ((CoordinatorLayout.LayoutParams) abl.getLayoutParams()).getBehavior();
            if (behavior instanceof AppBarLayout.Behavior) {
                return ((AppBarLayout.Behavior) behavior).getTopBottomOffsetForScrollingSibling();
            }
            return 0;
        }

        @Override
        AppBarLayout findFirstDependency(List<View> views) {
            for (int i = 0, z = views.size(); i < z; i++) {
                View view = views.get(i);
                if (view instanceof AppBarLayout) {
                    return (AppBarLayout) view;
                }
            }
            return null;
        }

        @Override
        int getScrollRange(View v) {
            if (v instanceof AppBarLayout) {
                return ((AppBarLayout) v).getTotalScrollRange();
            } else {
                return super.getScrollRange(v);
            }
        }
    }
}
