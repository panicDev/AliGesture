package com.way.gesture.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class OverviewLinealayout extends LinearLayout {
    private Bitmap mBitmap = null;
    private int mHeight;
    private Paint mPaint = new Paint();
    private int mWidth;

    public OverviewLinealayout(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public OverviewLinealayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null) {
            canvas.save();
            canvas.clipRect(0, 0, mWidth, mHeight);
            canvas.drawBitmap(mBitmap, 0.0F, 0.0F, mPaint);
            canvas.restore();
        }
    }

    public void setBitmap(Bitmap bitmap, int width, int height) {
        mBitmap = bitmap;
        mWidth = width;
        mHeight = height;
        invalidate();
    }

    public void updateHeight(int height) {
        mHeight = height;
        invalidate();
    }
}