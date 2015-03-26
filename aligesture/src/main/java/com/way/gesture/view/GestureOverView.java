package com.way.gesture.view;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.way.gesture.GestureController;
import com.way.gesture.GestureViewListener;
import com.way.gesture.OverviewModeListener;
import com.way.gesture.R;
import com.way.gesture.activity.ActivityAddOption;
import com.way.gesture.activity.ActivityGestureList;
import com.way.gesture.bean.GestureObject;
import com.way.gesture.util.GestureDataManager;
import com.way.util.MyLog;

public class GestureOverView extends FrameLayout {
    public static final int MODE_NORMAL = 0;// 普通模式
    public static final int MODE_EDIT = 1;// 编辑模式
    public static final int MODE_PROCESS = 2;// 处理模式
    public static final int MODE_DRAWING = 3;// 正在画手势模式
    private int mBaseLine;
    private Bitmap mSettingBitmap;
    private Paint mBottomPaint;
    private String mDrawString;
    private boolean mIsEnable;
    public GestureObject mGesture = new GestureObject();
    private GestureController mGestureController;
    boolean mCreateMode = false;// 是否为创建手势

    private Runnable mDelayProcess = new Runnable() {
        @Override
        public void run() {
            if (started) {
                modeChangedTo(MODE_PROCESS);
                mGesture.end();
                processGesture();
                invalidate();
            }
        }
    };
    private Handler mHandler = new Handler();
    private int mIconSize = 0;
    protected GestureViewListener mListener;
    private boolean mNeedRedraw = false;
    private OverviewModeListener mOverviewModeListener;
    private int mTextSize = 0;
    protected String message;
    protected int mMode;
    private OptionView mOptionView;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == "gesture_custom_touch") {
                ArrayList<MotionEvent> lists = intent
                        .getParcelableArrayListExtra("motion_event");
                if (lists != null && lists.size() > 0) {
                    for (MotionEvent motionEvent : lists)
                        onTouchEvent(motionEvent);
                }
            }
        }
    };
    private SettingView mSettingView;
    private boolean started;
    private Paint mTitlePaint;

    public GestureOverView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    private void init(Context context) {
        setFocusable(true);
        setFocusableInTouchMode(true);
        mIsEnable = true;
        started = false;
        mListener = null;
        modeChangedTo(MODE_NORMAL);
        mBottomPaint = new Paint();
        mBottomPaint.setColor(Color.WHITE);
        mBottomPaint.setTextAlign(Paint.Align.CENTER);
        mBottomPaint.setTextSize(mTextSize);
        mTitlePaint = new Paint();
        mTitlePaint.setColor(Color.WHITE);
        mTitlePaint.setTextAlign(Paint.Align.LEFT);
        mTitlePaint.setTextSize(mTextSize);
        mTitlePaint.setStrokeWidth(2.0F);
        mTitlePaint.setStrokeCap(Paint.Cap.ROUND);
        mTitlePaint.setAntiAlias(true);
        mTitlePaint.setDither(true);
        mSettingBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.settings);
        mIconSize = mSettingBitmap.getHeight();
        mTextSize = (5 * mIconSize / 9);
        mTitlePaint.setTextSize(mTextSize);
        mBaseLine = (4 * mIconSize / 3);
        mOptionView = new OptionView(context, mTextSize);
        mOptionView.setOnClickListener(new OptionView.OnClickListener() {
            public void onClick(OptionView optionView, int position) {
                if (position == 0) {
                    reDraw();
                    modeChangedTo(MODE_NORMAL);
                    hideOptionView();
                    doneModeChanged();
                } else {
                    startEditJobActivity();
                    MyLog.d("gesture", " option create new gesture");
                }
            }
        });
        mOptionView.setFocusable(true);
        mOptionView.setEnabled(true);
        mSettingView = new SettingView(context);
        mSettingView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),
                        ActivityGestureList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
                disableKeyguard();
            }
        });
        LayoutParams layoutParams = new LayoutParams(
                mIconSize, mIconSize, 53);
        layoutParams.setMargins(0, 15, 10, 0);
        addView(mSettingView, layoutParams);
        setWillNotDraw(false);
        mDrawString = context.getString(R.string.drawgesture);
        MyLog.d("gesture", "textSize :" + mTextSize + " icon height :"
                + mBaseLine);
        setAlpha(0.8F);
        // context.registerReceiver(receiver, new IntentFilter(
        // "gesture_custom_touch"));
    }

    public GestureOverView(Context context, GestureController gestureController) {
        super(context);
        mGestureController = gestureController;
        init(context);
    }

    private void disableKeyguard() {
        try {
            // getContext().unregisterReceiver(receiver);
            mGestureController.tryUnLock();
            mGestureController.hideOverView();
        } catch (Exception e) {
        }
    }

    private void doneModeChanged() {
        if (mOverviewModeListener != null)
            mOverviewModeListener.modeChanged(this);
    }

    private void hideOptionView() {
        removeView(mOptionView);
    }

    private void modeChangedTo(int mode) {
        if (mMode != mode)
            mMode = mode;
    }

    private void setNeedRedraw() {
        invalidate(0, mBaseLine, getWidth(), getHeight());
    }

    private void showOptionView(boolean isRedrawOnly, int mode) {
        mOptionView.setRedrawOnly(isRedrawOnly, mode);
        LayoutParams optionViewLayoutParams = new LayoutParams(
                LayoutParams.MATCH_PARENT, 4 * mTextSize, 80);
        mOptionView.setBackgroundResource(R.drawable.optionview_bg);
        addView(mOptionView, optionViewLayoutParams);
    }

    private void startEditJobActivity() {
        GestureDataManager gestureDataManager = GestureDataManager
                .defaultManager(getContext());
        mGesture.mGestureType = 3;
        gestureDataManager.insertGestureObject(mGesture);
        Intent intent = new Intent(getContext(), ActivityAddOption.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("mode", "Edit");
        intent.putExtra("recorderID", mGesture.mRecorderID);
        getContext().startActivity(intent);
        hideOptionView();
        reDraw();
        modeChangedTo(MODE_NORMAL);
        disableKeyguard();
    }

    public void enable(boolean enable) {
        mIsEnable = enable;
    }

    public int getCurrentMode() {
        return mMode;
    }

    public int getOptionViewHeight() {
        return 4 * mTextSize;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mGesture.setViewSize(getWidth(), getHeight());
        mGesture.draw(canvas);
        if (!mCreateMode) {
            int y = mTextSize / 2 + mBaseLine / 2;
            int x = mBaseLine / 2;
            mTitlePaint.setColor(Color.WHITE);
            mTitlePaint.setAlpha(255);
            canvas.drawText(mDrawString, x, y, mTitlePaint);
            mTitlePaint.setColor(Color.WHITE);
            mTitlePaint.setAlpha(60);
            canvas.drawLine(0.0F, mBaseLine, canvas.getWidth() - 4,
                    mBaseLine, mTitlePaint);
        }
        if (message != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            // GestureToast.showToast(getContext(), message);
            message = null;
        }
        MyLog.d("gesture",
                "Canvas isHardwareAccelerated :"
                        + canvas.isHardwareAccelerated());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mIsEnable)
            return true;
        if (mMode == MODE_EDIT || mMode == MODE_PROCESS)
            return true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:// 0
                if (event.getY() < mBaseLine) {
                    started = false;
                    return true;
                }
                if (mMode == MODE_NORMAL) {
                    modeChangedTo(MODE_DRAWING);
                    mGesture.start();
                    mGesture.addPoint((int) event.getX(), (int) event.getY());
                    setNeedRedraw();
                    started = true;
                } else if (mMode == MODE_DRAWING) {//支持多笔，
                    mGesture.addPoint(0, 0);//用于分割不同笔画的点
                    mGesture.addPoint((int) event.getX(), (int) event.getY());
                    mHandler.removeCallbacks(mDelayProcess);
                    setNeedRedraw();
                }
                return true;
            case MotionEvent.ACTION_UP:// 1
                mGesture.addPoint((int) event.getX(), (int) event.getY());
                mGesture.endPoint();
                setNeedRedraw();
                mHandler.removeCallbacks(mDelayProcess);
                mHandler.postDelayed(mDelayProcess, 500L);
                return true;
            case MotionEvent.ACTION_MOVE:// 2
                mGesture.addPoint((int) event.getX(), (int) event.getY());
                setNeedRedraw();
                return true;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void processGesture() {
        modeChangedTo(MODE_PROCESS);
        // 如果数据过短
        if ((mGesture.mModelDatas == null)
                || (mGesture.mModelDatas.length == 0)) {
            if (mListener != null)
                mListener.onGestureObjectDone(null);
            showOptionView(true, 0);
            modeChangedTo(MODE_EDIT);
            doneModeChanged();
            invalidate();
            return;
        }
        MyLog.i("liweiping", "gesture.modelData = " + mGesture.mModelDatas);

        GestureObject searchGestureObject = GestureDataManager.defaultManager(
                getContext()).searchGesture(mGesture);
        //如果没有查询到匹配的手势
        if ((searchGestureObject == null)
                || (searchGestureObject.mGestureType > 2)) {
            if (mListener != null)
                mListener.onGestureObjectDone(mGesture);
            modeChangedTo(MODE_EDIT);
            showOptionView(false, 0);
            doneModeChanged();
            return;
        }
        if (searchGestureObject.mGestureType == 0) {//启动应用
            if (searchGestureObject.mPackageName == null) {
                GestureDataManager.defaultManager(getContext()).delete(
                        searchGestureObject);
                modeChangedTo(MODE_EDIT);
                showOptionView(false, 0);
                doneModeChanged();
                return;
            }
            ComponentName componentName = new ComponentName(
                    searchGestureObject.mPackageName,
                    searchGestureObject.mClassName);
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("android.intent.action.MAIN");
            intent.addCategory("android.intent.action.LAUNCHER");
            intent.setComponent(componentName);
            MyLog.d("gesture", " packageNmae :"
                    + searchGestureObject.mPackageName + "   className :"
                    + searchGestureObject.mClassName);
            MyLog.d("gesture", " intent :" + intent.toString());
            try {
                getContext().startActivity(intent);
                disableKeyguard();
                modeChangedTo(MODE_NORMAL);
            } catch (Exception e) {
                MyLog.d("gesture", "activity not exists  ", e);
                if (mListener != null)
                    mListener.onGestureObjectDone(mGesture);
                GestureDataManager.defaultManager(getContext()).delete(
                        searchGestureObject);
                modeChangedTo(MODE_EDIT);
                showOptionView(false, 1);
                doneModeChanged();
            }
        } else if (searchGestureObject.mGestureType == 1) {//拨号
            Intent intent = new Intent("android.intent.action.CALL",
                    Uri.parse("tel:" + searchGestureObject.mPhoneNumber));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyLog.d("gesture", "" + intent.toString());
            getContext().startActivity(intent);
            disableKeyguard();
            modeChangedTo(MODE_NORMAL);
        } else {//发短信
            Intent intent = new Intent("android.intent.action.SENDTO",
                    Uri.parse("smsto:" + searchGestureObject.mPhoneNumber));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyLog.d("gesture", "start" + intent.toString());
            try {
                getContext().startActivity(intent);
                disableKeyguard();
                modeChangedTo(MODE_NORMAL);
                MyLog.d("gesture", "end" + intent.toString());
            } catch (Exception e) {
                MyLog.d("gesture", "" + e.toString());
            }
        }
    }

    public void reDraw() {
        started = false;
        mGesture.clearPoint();
        message = null;
        modeChangedTo(MODE_NORMAL);
        invalidate();
    }

    public void setCreateMode() {
        mBaseLine = 0;
        mCreateMode = true;
        removeView(mSettingView);
        //setBackgroundColor(Color.BLACK);
        setAlpha(1.0F);
    }

    public void setEnabled(boolean enable) {
        mIsEnable = enable;
        if (!enable) {
            mGesture.clearPoint();
            removeView(mOptionView);
            modeChangedTo(MODE_NORMAL);
            reDraw();
        }
        invalidate();
    }

    public void setGestureController(GestureController gestureController) {
        mGestureController = gestureController;
    }

    public void setListener(GestureViewListener gestureViewListener) {
        mListener = gestureViewListener;
    }

    public void setModeChangedListener(OverviewModeListener overviewModeListener) {
        mOverviewModeListener = overviewModeListener;
    }
}