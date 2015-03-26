
package hwdroid.widget.indicator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hw.droid.R;

import hwdroid.widget.MarkView;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import java.util.Locale;

public class HWTabPageSimpleIndicator extends HorizontalScrollView {

    /**
     * Interface for a callback when the selected tab has been reselected.
     */
    public interface OnTabReselectedListener {
        /**
         * Callback when the selected tab has been reselected.
         *
         * @param position Position of the current center item.
         */
        void onTabReselected(int position);
    }

    public interface IconTabProvider {
        public int getPageIconResId(int position);
    }

    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private LinearLayout.LayoutParams expandedTabLayoutParams;

    private final PageListener pageListener = new PageListener();

    public OnPageChangeListener delegatePageListener;
    private OnTabReselectedListener mTabReselectedListener;

    private LinearLayout tabsContainer;
    private ViewPager pager;

    private int tabCount;

    private int currentPosition = 0;
    private float currentPositionOffset = 0f;

    private Paint rectPaint;
    private Paint dividerPaint;

    private int indicatorColor = 0xFF52B800;
    private int underlineColor = 0x1A000000;
    private int dividerColor = 0x1A000000;
    private int textSelectedColor = 0xFF52B800;
    private int textUnselectColor = 0xFF808080;

    private boolean shouldExpand = true;
    private boolean enableDivider = false;
    private boolean indicatorOnTop = false;

    private int tabTextSize = 16;
    private int scrollOffset = 52;
    private float indicatorHeight = (float) 1.5;
    private float underlineHeight = (float) 1.0;
    private int dividerPadding = 12;
    private int tabPadding = 24;
    private int dividerWidth = 1;
    private int lastScrollX = 0;

    private Locale locale;
    private boolean viewPagerScrollWithAnimation = true;

    public HWTabPageSimpleIndicator(Context context) {
        this(context, null);
    }

    public HWTabPageSimpleIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HWTabPageSimpleIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFillViewport(true);
        setWillNotDraw(false);
        setOverScrollMode(OVER_SCROLL_NEVER);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        textSelectedColor = getResources().getColor(R.color.hw_vp_tab_indicator_selected_color);
        textUnselectColor = getResources().getColor(R.color.hw_vp_tab_indicator_unselected_color);
        tabTextSize = context.getResources().getConfiguration().screenWidthDp <= 320 ? 14 : tabTextSize;

        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, dm);
        tabsContainer.setLayoutParams(params);
        addView(tabsContainer);

        scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
        dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
        indicatorHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        underlineHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Style.FILL);

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);

        defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

        if (locale == null) {
            locale = getResources().getConfiguration().locale;
        }
    }

    public void setViewPager(ViewPager pager) {
        this.pager = pager;

        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        pager.setOnPageChangeListener(pageListener);

        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

    public void setOnTabReselectedListener(OnTabReselectedListener listener) {
        mTabReselectedListener = listener;
    }

    public void notifyDataSetChanged() {

        tabsContainer.removeAllViews();

        tabCount = pager.getAdapter().getCount();

        for (int i = 0; i < tabCount; i++) {

            if (pager.getAdapter() instanceof IconTabProvider) {
                addIconTab(i, ((IconTabProvider) pager.getAdapter()).getPageIconResId(i));
            } else {
                addTextTab(i, pager.getAdapter().getPageTitle(i).toString());
            }

        }

        updateTabStyles();

        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {

                getViewTreeObserver().removeGlobalOnLayoutListener(this);

                currentPosition = pager.getCurrentItem();
                scrollToChild(currentPosition, 0);
            }
        });

    }

    private class TabView extends RelativeLayout {
        private TextView mTabText;
        private MarkView mMarkView;

        public TabView(Context context) {
            super(context);
            init();
        }

        public TabView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public TabView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        private void init() {
            mTabText = new TextView(getContext());
            mTabText.setTextAppearance(getContext(), android.R.attr.textAppearanceMedium);
            mTabText.setTextColor(getResources().getColor(R.color.hw_vp_tab_indicator_text_color));
            int textSize = 16;
            DisplayMetrics dm = getResources().getDisplayMetrics();
            if (480 == dm.widthPixels) {
                textSize = 14;
            }
            mTabText.setTextSize(textSize / getResources().getConfiguration().fontScale);
            mTabText.setSingleLine(true);
            mTabText.setGravity(Gravity.CENTER);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            this.addView(mTabText, params);

            mMarkView = new MarkView(getContext(), mTabText);
        }

        public TextView getTextView() {
            return mTabText;
        }

        public MarkView getMarkView() {
            return mMarkView;
        }
    }

    private void addTextTab(final int position, String title) {

        TabView tab = new TabView(getContext());
        tab.getTextView().setText(title);

        addTab(position, tab);
    }

    private void addIconTab(final int position, int resId) {

        ImageButton tab = new ImageButton(getContext());
        tab.setImageResource(resId);

        addTab(position, tab);

    }

    private void addTab(final int position, View tab) {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final int oldSelected = pager.getCurrentItem();
                if (oldSelected != position && mTabReselectedListener != null) {
                    mTabReselectedListener.onTabReselected(position);
                }

                pager.setCurrentItem(position, viewPagerScrollWithAnimation);
            }
        });

        if (!shouldExpand) {
            tab.setPadding(tabPadding, 0, tabPadding, 0);
        }
        tabsContainer.addView(tab, position, shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
    }

    /**
     * set indicator color
     *
     * @param indicator color
     */
    public HWTabPageSimpleIndicator setTabIndicatorColor(int color) {
        indicatorColor = color;
        return this;
    }

    /**
     * set indicator color
     *
     * @param indicator color
     */
    public HWTabPageSimpleIndicator setTabUnderlineColor(int color) {
        underlineColor = color;
        return this;
    }

    /**
     * set TextSelected color
     *
     * @param TextSelected color
     */
    public HWTabPageSimpleIndicator setTabTextSelectedColor(int color) {
        textSelectedColor = color;
        return this;
    }

    /**
     * set TextUnselect color
     *
     * @param TextUnselect color
     */
    public HWTabPageSimpleIndicator setTabTextUnselectColor(int color) {
        textUnselectColor = color;
        return this;
    }

    /**
     * set the indicator on the tabs top or bottom, default locate at bottom
     *
     * @param enable if true indicator on top of tabs, otherwise on bottom of tabs
     */
    public HWTabPageSimpleIndicator setIndicatorOnTop(boolean enable) {
        this.indicatorOnTop = enable;
        invalidate();
        return this;
    }

    /**
     * dynamic change the tab text
     *
     * @param position start from 0
     * @param text
     */
    public void setTabText(int position, String text) {
        if (position < 0 || position > (tabsContainer.getChildCount() - 1))
            throw new RuntimeException("tabs does not have this position.");

        View tab = tabsContainer.getChildAt(position);
        if (tab instanceof TextView) {
            ((TextView) tab).setText(text);
        }
    }

    /**
     * show tab mark view by position
     *
     * @param position the tab index
     * @param text     the text on mark view, limit 3 characters. eg: 99+
     */
    public void showTabMarkView(int position, String text) {
        View view = tabsContainer.getChildAt(position);

        if (view instanceof TabView) {
            TabView tabView = (TabView) view;
            tabView.getMarkView().setText(text);
            tabView.getMarkView().show();
        }
    }

    /**
     * hide tab mark view by position
     *
     * @param position the tab index
     */
    public void hideTabMarkView(int position) {
        View view = tabsContainer.getChildAt(position);

        if (view instanceof TabView) {
            TabView tabView = (TabView) view;
            tabView.getMarkView().hide();
        }
    }

    public void setViewPagerScrollWithAnimation(boolean enable) {
        this.viewPagerScrollWithAnimation = enable;
    }

    public boolean getViewPagerScrollWithAnimation() {
        return this.viewPagerScrollWithAnimation;
    }

    public void setCurrentItem(int item) {
        pager.setCurrentItem(item, viewPagerScrollWithAnimation);
    }

    private void tabSelect(int index) {
        final int tabCount = tabsContainer.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            final View child = tabsContainer.getChildAt(i);
            final boolean isSelected = (i == index);
            child.setSelected(isSelected);
            if (isSelected) {
                ((TabView) child).getTextView().setTextColor(textSelectedColor);
            } else {
                ((TabView) child).getTextView().setTextColor(textUnselectColor);
            }
        }
    }

    private void updateTabStyles() {
        for (int i = 0; i < tabCount; i++) {
            View v = tabsContainer.getChildAt(i);
            v.setBackgroundColor(Color.TRANSPARENT);
        }
        tabSelect(pager.getCurrentItem());
    }

    private void scrollToChild(int position, int offset) {

        if (tabCount == 0) {
            return;
        }

        int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || tabCount == 0) {
            return;
        }

        final int height = getHeight();

        // draw underline
        rectPaint.setColor(underlineColor);
        if (indicatorOnTop) {
            canvas.drawRect(0, 0, tabsContainer.getWidth(), underlineHeight, rectPaint);
        } else {
            canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);
        }

        // draw indicator line
        rectPaint.setColor(indicatorColor);
        // default: line below current tab
        View currentTab = tabsContainer.getChildAt(currentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

            View nextTab = tabsContainer.getChildAt(currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
            lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
        }

        if (indicatorOnTop) {
            canvas.drawRect(lineLeft, 0, lineRight, indicatorHeight, rectPaint);
        } else {
            canvas.drawRect(lineLeft, height - indicatorHeight, lineRight, height, rectPaint);
        }


        // draw divider
        if (enableDivider) {
            dividerPaint.setColor(dividerColor);
            for (int i = 0; i < tabCount - 1; i++) {
                View tab = tabsContainer.getChildAt(i);
                canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), height - dividerPadding, dividerPaint);
            }
        }
    }

    private class PageListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            currentPosition = position;
            currentPositionOffset = positionOffset;

            scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));

            invalidate();

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                scrollToChild(pager.getCurrentItem(), 0);
            }

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            tabSelect(position);

            if (delegatePageListener != null) {
                delegatePageListener.onPageSelected(position);
            }
        }

    }

}
