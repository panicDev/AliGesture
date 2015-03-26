package com.way.gesture.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import com.way.gesture.R;
import com.way.util.MyLog;

public class OptionView extends View {
    private int mDownIndex = -1;
    private Drawable mDrawableLeft;
    private Drawable mDrawableRight;
    private Drawable mDrawableRightDiable;
    private OnClickListener mListener = null;
    private int mCurrentMode = 0;
    private Paint mPaint = new Paint();
    private boolean mRedrawOnly = false;
    private int mTextSizeBig;
    private int mTextSizeSmall;
    private String mStringAdd;
    private String mStringInfo;
    private String mStringInfo2;
    private String mStringRedraw;
    private String mStringTooShort = null;

    public OptionView(Context context, int size) {
        super(context);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(26.0F);
        mPaint.setStrokeWidth(2.0F);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mStringInfo = context.getString(R.string.getsure_option_info);
        mStringInfo2 = context.getString(R.string.getsure_option_info2);
        mStringRedraw = context.getString(R.string.getsure_option_redraw);
        mStringAdd = context.getString(R.string.getsure_option_add);
        mStringTooShort = context.getString(R.string.gestureMessage3);
        mTextSizeSmall = (size * 5 / 7);
        mTextSizeBig = (size * 6 / 7);
        mDrawableLeft = getResources().getDrawable(
                R.drawable.corners_bg_left);
        mDrawableRight = getResources().getDrawable(
                R.drawable.corners_bg_right);
        mDrawableRightDiable = getResources().getDrawable(
                R.drawable.corners_bg_redraw);
        MyLog.d("gesture", "option view textSize big: " + mTextSizeBig
                + " small: " + mTextSizeSmall);
    }

    public static int dip2px(Context context, int ratio) {
        return (int) (0.5F + context.getResources().getDisplayMetrics().density
                * ratio);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int textSize = 2 * mTextSizeSmall;
        mPaint.setTextSize(mTextSizeSmall);
        int minHeight = textSize + (height - textSize) / 2 + mTextSizeBig
                / 2;
        int minWidth = width / 4;
        if (mRedrawOnly) {
            canvas.drawText(mStringTooShort, width / 2,
                    mTextSizeSmall, mPaint);
        } else {
            if (mCurrentMode == 0)
                canvas.drawText(mStringInfo, width / 2,
                        mTextSizeSmall, mPaint);
            else
                canvas.drawText(mStringInfo2, width / 2,
                        mTextSizeSmall, mPaint);
        }
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(60);
        mPaint.setStrokeWidth(2.0F);
        canvas.drawLine(0.0F, textSize, width - 1, textSize, mPaint);
        if (!mRedrawOnly) {// 如果不是单按钮模式，中间分割线
            canvas.drawLine(width / 2, textSize + 2, width / 2, height,
                    mPaint);
            if (mDownIndex == 0) {
                Rect leftRect = new Rect(0, textSize + 1, width / 2 - 1, height);
                mPaint.setColor(Color.argb(128, 51, 204, 255));
                mPaint.setStyle(Paint.Style.FILL);
                mDrawableLeft.setBounds(leftRect.left, leftRect.top,
                        leftRect.right, leftRect.bottom);
                mDrawableLeft.draw(canvas);
            } else if (mDownIndex == 1) {
                Rect rightRect = new Rect(1 + width / 2, textSize + 1, width,
                        height);
                mPaint.setColor(Color.argb(128, 51, 204, 255));
                mPaint.setStyle(Paint.Style.FILL);
                mDrawableRight.setBounds(rightRect.left, rightRect.top,
                        rightRect.right, rightRect.bottom);
                mDrawableRight.draw(canvas);
            }
            mPaint.setAlpha(255);
            mPaint.setColor(Color.WHITE);
            canvas.drawText(mStringRedraw, minWidth, minHeight, mPaint);
            canvas.drawText(mStringAdd, width - minWidth, minHeight,
                    mPaint);
        } else {
            Rect rect = new Rect(0, textSize + 1, width, height);
            if (mDownIndex == 0) {
                mPaint.setColor(Color.argb(128, 51, 204, 255));
                mPaint.setStyle(Paint.Style.FILL);
                mDrawableRightDiable.setBounds(rect.left, rect.top, rect.right,
                        rect.bottom);
                mDrawableRightDiable.draw(canvas);
            }
            mPaint.setAlpha(255);
            mPaint.setColor(Color.WHITE);
            canvas.drawText(mStringRedraw, width / 2, minHeight,
                    mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:// 0
                if (((int) event.getY() <= 40) || (mListener == null))
                    return super.onTouchEvent(event);
                if (!mRedrawOnly) {
                    if (((int) event.getX()) < (getWidth() / 2))
                        mDownIndex = 0;
                    else
                        mDownIndex = 1;
                } else {
                    mDownIndex = 0;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:// 1
                if ((int) event.getY() > 40) {
                    if ((mListener != null) && (mDownIndex != -1))
                        mListener.onClick(this, mDownIndex);
                    mDownIndex = -1;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:// 2
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (y <= 40) {
                    mDownIndex = -1;
                    invalidate();
                } else {
                    if (!mRedrawOnly) {
                        if (mDownIndex == 0) {
                            if (x > getWidth() / 2) {
                                mDownIndex = -1;
                                invalidate();
                            }
                        } else if (mDownIndex == 1) {
                            if (x < getWidth() / 2) {
                                mDownIndex = -1;
                                invalidate();
                            }
                        }
                    }
                }

                break;
            default:
                break;
        }

        return true;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mListener = onClickListener;
    }

    public void setRedrawOnly(boolean redrawOnly, int mode) {
        mRedrawOnly = redrawOnly;
        mCurrentMode = mode;
        invalidate();
    }

    static abstract interface OnClickListener {
        public abstract void onClick(OptionView optionView, int downIndex);
    }
}