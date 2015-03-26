package com.way.gesture.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.way.gesture.R;

public class SettingView extends View {
    private Bitmap mBitmapSetting = BitmapFactory.decodeResource(
            getResources(), R.drawable.settings);
    private OnClickListener mClickListener = null;
    private boolean mDowning = false;
    private Paint mPaint = new Paint();

    public SettingView(Context context) {
        super(context);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(26.0F);
        mPaint.setStrokeWidth(2.0F);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDowning)
            canvas.drawColor(Color.argb(128, 51, 204, 255));
        else
            canvas.drawColor(Color.TRANSPARENT);
        Rect rect1 = new Rect(0, 0, mBitmapSetting.getWidth(),
                mBitmapSetting.getHeight());
        Rect rect2 = new Rect(0, 0, getWidth(), getHeight());
        canvas.drawBitmap(mBitmapSetting, rect1, rect2, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:// 0
                mDowning = true;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:// 1
                if ((mDowning) && (mClickListener != null))
                    mClickListener.onClick(this);
                mDowning = false;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:// 2
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (x > getWidth() || x < 0 || y > getHeight() || y < 0) {
                    mDowning = false;
                    invalidate();
                }
                break;
            default:
                break;
        }
        return true;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mClickListener = onClickListener;
    }
}