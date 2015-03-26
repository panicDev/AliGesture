package com.way.gesture.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.way.gesture.bean.GestureObject;
import com.way.gesture.bean.Point;
import com.way.gesture.util.GestureImageViewHandler;
import com.way.util.MyLog;

public class GestureImageView extends View {
    private int mAddtionX = 0;
    private int mAddtionY = 0;
    private Paint mPaint;
    private Paint mBottomPaint;
    private Canvas mCanvas;
    private int mCurrentDrawIndex;
    private GestureObject mGesture;
    private int mWidth;
    private int mHeight;
    private Bitmap mBitmap;
    private GestureObjectSmall mGestureDraw;
    private boolean mStartDraw = true;
    private int mOffsetX = 5;
    private int mOffsetY = 5;
    private float mScalX;
    private float mScalY;

    public GestureImageView(Context context) {
        super(context);
        init(context);
    }

    public GestureImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public GestureImageView(Context context, AttributeSet attributeSet,
                            int style) {
        super(context, attributeSet, style);
        init(context);
    }

    private void init(Context context) {
        int size = (int) (0.5F + 50.0F * context.getResources()
                .getDisplayMetrics().density);
        mWidth = size;
        mHeight = size;
        mBottomPaint = new Paint();
        mBottomPaint.setColor(Color.WHITE);
        mBottomPaint.setTextAlign(Paint.Align.CENTER);
        mBottomPaint.setTextSize(34.0F);
        mBottomPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mGestureDraw != null) {
            mGestureDraw.setViewSize(getWidth(), getHeight());
            mGestureDraw.draw(canvas);
        }
        if (!mStartDraw) {
            GestureImageViewHandler.mGestureImageViewHandler
                    .addGestureImageView(this);
            mStartDraw = true;
        }
    }

    public void setGesture(GestureObject gestureObject) {
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(mWidth, mHeight,
                    Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            mPaint = new Paint();
            mPaint.setColor(Color.BLACK);
            mPaint.setStrokeWidth(2.0F);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
        }
        ArrayList<Point> pointLists = gestureObject.mAllPoints;
        int minX = pointLists.get(0).x;
        int maxX = pointLists.get(0).x;
        int minY = pointLists.get(0).y;
        int maxY = pointLists.get(0).y;
        for (Point point : pointLists) {
            if (point.x < minX)
                minX = point.x;
            if (point.x > maxX)
                maxX = point.x;
            if (point.y < minY)
                minY = point.y;
            if (point.y > maxY)
                maxY = point.y;
        }
        mAddtionX = 10;
        mAddtionY = 10;
        mOffsetX = minX;
        mOffsetY = minY;

        if (maxX == minX) {
            mScalX = 1.0F;
        } else {
            mScalX = (1.0F * (-20 + mWidth) / Math.abs(maxX - minX));
        }
        if (maxY == minY) {
            mScalY = 1.0F;
        } else {
            mScalY = (1.0F * (-20 + mHeight) / Math.abs(maxY - minY));
        }
        if (mScalY >= mScalX) {
            mScalY = mScalX;
            float minYRadio = (minY - mOffsetY) * mScalX;
            float maxYRadio = (maxY - mOffsetY) * mScalX;
            mAddtionY = ((int) (mHeight - (maxYRadio - minYRadio)) / 2);
        } else {
            mScalX = mScalY;
            float minXRadio = (minX - mOffsetX) * mScalX;
            float maxXRadio = (maxX - mOffsetX) * mScalX;
            mAddtionX = ((int) (mWidth - (maxXRadio - minXRadio)) / 2);
        }
        MyLog.d("gesture", "GestureImageView  scal :" + mScalX + "  , "
                + mScalY);
        mGesture = gestureObject;
        mCanvas.drawColor(Color.BLACK);
        mBottomPaint.setColor(Color.WHITE);
        mCurrentDrawIndex = 0;
        mStartDraw = false;
        mGestureDraw = new GestureObjectSmall();
        mGestureDraw.start();
        mGestureDraw.setViewSize(mWidth, mHeight);
        mGestureDraw.setDrawCache(true);
        mGestureDraw.draw(mCanvas);
        invalidate();
    }

    public boolean stepDraw() {

        if (mGesture == null)
            return true;
        ArrayList<Point> pointArrayList = mGesture.mAllPoints;
        if (pointArrayList.size() == 0)
            return true;
        MyLog.i("way2", "drawIndex = " + mCurrentDrawIndex);
        int i = 0;
        while (i < 3 && mCurrentDrawIndex < pointArrayList.size()) {
            Point point = pointArrayList.get(mCurrentDrawIndex);
            if (point.x == 0 && point.y == 0) {
                mGestureDraw.addPoint(0, 0);
            } else {
                int x = (int) (point.x * mScalX - mOffsetX
                        * mScalX + mAddtionX);
                int y = (int) (point.y * mScalY - mOffsetY
                        * mScalY + mAddtionY);
                mGestureDraw.addPoint(x, y);
            }

            mCurrentDrawIndex++;
            i++;
        }
        invalidate();
        if (mCurrentDrawIndex >= pointArrayList.size()) {
            return true;
        }
        return false;
    }

    class GestureObjectSmall extends GestureObject {
        GestureObjectSmall() {
            setResolution(2);
        }
    }
}