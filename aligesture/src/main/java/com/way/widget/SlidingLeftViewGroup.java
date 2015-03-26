package com.way.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.way.util.MyLog;

public class SlidingLeftViewGroup extends ViewGroup {
    private Context mContext;
    private static String TAG = "SlidingLeftViewGroup";
    private static final boolean DEBUG = false;

    private static final int TOUCH_MODE_IDLE = 0;
    private static final int TOUCH_MODE_DOWN = 1;
    private static final int TOUCH_MODE_DRAGGING = 2;

    private int mTouchMode;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    private int mMinFlingVelocity;
    private float mTouchX;
    private float mStartX;

    private static final int SCREEN_ID_NORMAL = 0;
    private static final int SCREEN_ID_LEFT = 1;
    private int mCurScreen = SCREEN_ID_NORMAL;

    private Scroller mScroller = null;
    private int mScrollWidth;

    public SlidingLeftViewGroup(Context context) {
        this(context, null);
    }

    public SlidingLeftViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mScroller = new Scroller(mContext);
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        mMinFlingVelocity = config.getScaledMinimumFlingVelocity();
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
    }

    public void MoveBack(boolean quick) {
        int descScreen = SCREEN_ID_NORMAL;
        if (quick) {
            if (mScroller != null) {
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                    scrollTo(descScreen * mScrollWidth, 0);
                    mScroller.forceFinished(true);
                    mCurScreen = descScreen;
                } else {
                    scrollTo(descScreen * mScrollWidth, 0);
                    mCurScreen = descScreen;
                }
            }
            if (mOnSlideListener != null) {
                mOnSlideListener.onSlideBack();
            }
        } else {
            snapToScreen(descScreen);
        }
        mTouchMode = TOUCH_MODE_IDLE;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        if (DEBUG) {
            MyLog.d(TAG, "onInterceptTouchEvent action " + action);
        }

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            if (DEBUG)
                MyLog.d(TAG, "onInterceptTouchEvent ACTION_UP");
            if (mTouchMode == TOUCH_MODE_DRAGGING) {
                stopDrag(ev);
            }

            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
            if (DEBUG)
                MyLog.d(TAG, "onInterceptTouchEvent return " + false);
            return false;
        }

        // Nothing more to do here if we have decided whether or not we
        // are dragging.
        if (action != MotionEvent.ACTION_DOWN) {
            if (mTouchMode == TOUCH_MODE_DRAGGING) {
                if (DEBUG)
                    MyLog.v(TAG, "Intercept returning true!");
                if (DEBUG)
                    MyLog.d(TAG, "onInterceptTouchEvent return " + true);
                return true;
            }
        }

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                switch (mTouchMode) {
                    case TOUCH_MODE_IDLE:
                        break;

                    case TOUCH_MODE_DOWN: {
                        final float x = ev.getX();
                        if (Math.abs(x - mTouchX) > mTouchSlop) {
                            mTouchMode = TOUCH_MODE_DRAGGING;
                            getParent().requestDisallowInterceptTouchEvent(true);
                            mTouchX = x;
                            if (DEBUG)
                                MyLog.d(TAG, "onInterceptTouchEvent return " + true);
                            return true;
                        }
                        break;
                    }

                    case TOUCH_MODE_DRAGGING: {
                        final float x = ev.getX();
                        final float dx = mTouchX - x;
                        mStartX = Math.abs(getScrollX());
                        float newPos = Math.max(0, Math.min(mStartX + dx, mScrollWidth));
                        if (newPos != mStartX) {
                            scrollTo((int) newPos, 0);
                            mTouchX = x;
                            invalidate();
                        }
                    }
                }
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                if (mOnSlideListener != null) {
                    mOnSlideListener.onSlidingStart(this);
                }
                final float x = ev.getX();
                mTouchMode = TOUCH_MODE_DOWN;
                mTouchX = x;
            }
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        if (DEBUG)
            MyLog.d(TAG, "onInterceptTouchEvent return " + (mTouchMode == TOUCH_MODE_DRAGGING));
        return mTouchMode == TOUCH_MODE_DRAGGING;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        final int action = event.getActionMasked();
        if (DEBUG)
            MyLog.d(TAG, "onTouchEvent action " + action);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final float x = event.getX();
                mTouchMode = TOUCH_MODE_DOWN;
                mTouchX = x;
            }

            case MotionEvent.ACTION_MOVE: {
                switch (mTouchMode) {
                    case TOUCH_MODE_IDLE:
                        break;

                    case TOUCH_MODE_DOWN: {
                        final float x = event.getX();
                        if (Math.abs(x - mTouchX) > mTouchSlop) {
                            mTouchMode = TOUCH_MODE_DRAGGING;
                            getParent().requestDisallowInterceptTouchEvent(true);
                            mTouchX = x;
                            return true;
                        }
                        break;
                    }

                    case TOUCH_MODE_DRAGGING: {
                        final float x = event.getX();
                        final float dx = mTouchX - x;
                        mStartX = Math.abs(getScrollX());
                        float newPos = Math.max(0, Math.min(mStartX + dx, mScrollWidth));
                        MyLog.d(TAG, "mStartX " + mStartX + " newPos " + newPos);
                        if (newPos != mStartX) {
                            scrollTo((int) newPos, 0);
                            mTouchX = x;
                            invalidate();
                        }
                        return true;
                    }
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mTouchMode == TOUCH_MODE_DRAGGING) {
                    stopDrag(event);
                }

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
            }
        }
        return true;
    }

    private void stopDrag(MotionEvent ev) {
        mTouchMode = TOUCH_MODE_IDLE;

        if (mCurScreen == SCREEN_ID_LEFT) {
            snapToScreen(SCREEN_ID_NORMAL);
            return;
        }
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            mVelocityTracker.computeCurrentVelocity(1000);
            float xvel = mVelocityTracker.getXVelocity();

            if (xvel > mMinFlingVelocity && mCurScreen > 0) {
                snapToScreen(mCurScreen - 1);
            } else if (xvel < -mMinFlingVelocity && mCurScreen < (getChildCount() - 1)) {
                snapToScreen(mCurScreen + 1);
            } else {
                snapToDestination();
            }
        }
    }

    private void snapToDestination() {
        int destScreen = (getScrollX() + mScrollWidth / 2) / mScrollWidth;
        snapToScreen(destScreen);
    }

    private void snapToScreen(int whichScreen) {
        mCurScreen = whichScreen;

        if (mCurScreen > getChildCount() - 1)
            mCurScreen = getChildCount() - 1;

        int dx = mCurScreen * mScrollWidth - getScrollX();
        mScroller.startScroll(getScrollX(), 0, dx, 0, Math.abs(dx));

        if (mCurScreen == SCREEN_ID_NORMAL) {
            if (mOnSlideListener != null) {
                mOnSlideListener.onSlideBack();
            }
        } else if (mCurScreen == SCREEN_ID_LEFT) {
            if (mOnSlideListener != null) {
                mOnSlideListener.onSlideToLeft(this);
            }
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
                getDefaultSize(0, heightMeasureSpec));

        int childWidthSize = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int childHeightSize = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        int childCount = getChildCount();
        if (childCount > 2) {
            MyLog.e(TAG, "child is too much err!! " + childCount);
        }
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child != null && child.getVisibility() != GONE) {
                if (i == 1) {
                    // The right screen max width is phone screen.
                    mScrollWidth = Math.min(child.getMeasuredWidth(), childWidthSize);
                    child.measure(child.getMeasuredWidth(), childHeightSize);
                    break;
                }
                child.measure(childWidthSize, childHeightSize);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int height = getHeight();
        int width = getWidth();

        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child != null && child.getVisibility() != View.GONE) {
                if (i == 0) {
                    child.layout(0, 0, width, height);
                } else if (i == 1) {
                    child.layout(width, 0, width + mScrollWidth, height);
                }
            }
        }
    }

    private OnSlideListener mOnSlideListener;

    public interface OnSlideListener {
        public void onSlideToLeft(SlidingLeftViewGroup item);

        public void onSlideBack();

        public void onSlidingStart(SlidingLeftViewGroup item);
    }

    public void setSlidingListener(OnSlideListener l) {
        mOnSlideListener = l;
    }
}
