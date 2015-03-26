package com.way.gesture.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.way.gesture.GestureController;
import com.way.gesture.OverviewModeListener;
import com.way.gesture.R;
import com.way.gesture.R.id;
import com.way.gesture.R.layout;
import com.way.gesture.view.GestureOverView;
import com.way.gesture.view.OverviewLinealayout;
import com.way.util.MyLog;

public class OverviewActivity extends Activity implements OverviewModeListener,
        GestureController {
    private WindowManager mWindowManager;
    private DisplayMetrics mDisplayMetrics;
    private OverviewLinealayout mRootView;
    private int mDismissCount = 0;
    private int mMargins = 100;
    private int mMarginsUP = 100;
    private GestureOverView mOverview;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                OverviewActivity.this.superFinish();
                MyLog.d("gesture", "screen off finish ");
            }
        }
    };
    private boolean onCreated = false;

    @Override
    public void finish() {
        MyLog.d("gesture", " finish  mDismissCount = " + mDismissCount);
        if (mDismissCount > 0) {
            super.finish();
            return;
        }
        mDismissCount++;
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0F, 1.0F);
        alphaAnimation.setDuration(200L);
        animationSet.addAnimation(alphaAnimation);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0F, 0.8F, 1.0F,
                0.8F, 1, 0.5F, 1, 0.5F);
        scaleAnimation.setDuration(200L);
        animationSet.addAnimation(scaleAnimation);
        TranslateAnimation translateAnimation = new TranslateAnimation(0.0F,
                0.0F, 0.0F, -getWindow().getAttributes().height);
        translateAnimation.setDuration(200L);
        animationSet.addAnimation(translateAnimation);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                MyLog.d("gesture", " onAnimationEnd ");
                OverviewActivity.this.superFinish();
            }

            public void onAnimationRepeat(Animation animation) {
                MyLog.d("gesture", " onAnimationRepeat ");
            }

            public void onAnimationStart(Animation animation) {
                MyLog.d("gesture", " onAnimationStart ");
                // new Handler().postDelayed(new Runnable() {
                // public void run() {
                // OverviewActivity.this.finish();
                // }
                // }, 400L);
            }
        });
        mRootView.startAnimation(animationSet);
    }

    public void hideOverView() {
        superFinish();
    }

    public boolean isOnLockScreen() {
        return false;
    }

    public void modeChanged(GestureOverView gestureOverView) {
        OverviewLinealayout rootView = (OverviewLinealayout) findViewById(R.id.rootview);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        if (gestureOverView.getCurrentMode() == GestureOverView.MODE_EDIT) {
            layoutParams.setMargins(0, this.mMarginsUP, 0, this.mMargins
                    - gestureOverView.getOptionViewHeight());
            rootView.updateViewLayout(this.mOverview, layoutParams);
            MyLog.d("gesture", "modeChanged  to mode_edit");
        } else {
            layoutParams.setMargins(0, this.mMarginsUP, 0, this.mMargins);
            rootView.updateViewLayout(this.mOverview, layoutParams);
            MyLog.d("gesture", "modeChanged  to mode_normal");
        }
    }

    public void onConfigurationChanged(Configuration paramConfiguration) {
        super.onConfigurationChanged(paramConfiguration);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Settings.System.getInt(getContentResolver(), "way_gesture_switch",
                1) == 0) {
            MyLog.d("gesture",
                    "OverviewActivity onCreate  gesture is disable or is in  KeyguardLocked");
            super.finish();
            return;
        }
        onCreated = false;
        setContentView(R.layout.overview_dialog);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mDisplayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(mDisplayMetrics);
        int width = 6 * mDisplayMetrics.widthPixels / 7;
        int height = 6 * mDisplayMetrics.heightPixels / 7;
        MyLog.d("gesture", "OverviewDialog.onCreate : "
                + mDisplayMetrics.widthPixels + " , "
                + mDisplayMetrics.heightPixels);
        if (width > height)
            width = height;
        mRootView = (OverviewLinealayout) findViewById(R.id.rootview);
        this.mOverview = new GestureOverView(this, this);
        this.mMargins = (100 + this.mOverview.getOptionViewHeight());
        int mariginWidth = width + this.mMargins + width / 10;
        this.mOverview.setEnabled(true);
        this.mOverview.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0, mDisplayMetrics.heightPixels - mariginWidth,
                0, this.mMargins);
        this.mMarginsUP = (mDisplayMetrics.heightPixels - mariginWidth);
        mRootView.addView(this.mOverview, layoutParams);
        WindowManager.LayoutParams windowLayoutParams = getWindow()
                .getAttributes();
        windowLayoutParams.width = width;
        windowLayoutParams.height = mDisplayMetrics.heightPixels;
        windowLayoutParams.y = mDisplayMetrics.heightPixels;
        getWindow().setAttributes(windowLayoutParams);
        this.mOverview.setModeChangedListener(this);
        mRootView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                MyLog.d("gesture", "linear layout ontouchListener : " + event);
                OverviewActivity.this.finish();
                return false;
            }
        });
        mRootView.setVisibility(View.INVISIBLE);
        registerReceiver(this.mReceiver, new IntentFilter(
                "android.intent.action.SCREEN_OFF"));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.d("gesture", "onDestory");
        try {
            unregisterReceiver(this.mReceiver);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLog.d("gesture", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        MyLog.d("gesture", "onWindowFocusChanged  :" + hasFocus);
        if ((onCreated) || (!hasFocus))
            return;
        this.onCreated = true;

        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.4F, 1.0F);
        alphaAnimation.setDuration(300L);
        animationSet.addAnimation(alphaAnimation);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.8F, 1.0F, 0.8F,
                1.0F, 1, 0.5F, 1, 0.5F);
        scaleAnimation.setDuration(300L);
        animationSet.addAnimation(scaleAnimation);
        TranslateAnimation translateAnimation = new TranslateAnimation(0.0F,
                0.0F, -mDisplayMetrics.heightPixels, 0.0F);
        translateAnimation.setDuration(300L);
        translateAnimation.setRepeatCount(0);
        animationSet.addAnimation(translateAnimation);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                MyLog.d("gesture", "onAnimationEnd");
                mRootView.setVisibility(View.VISIBLE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
                MyLog.d("gesture", "onAnimationStart");
            }
        });
        mRootView.startAnimation(animationSet);
    }

    public void reLayout() {
        int width = 4 * mDisplayMetrics.widthPixels / 5;
        int height = 3 * mDisplayMetrics.heightPixels / 4;
        if (mDisplayMetrics.widthPixels > mDisplayMetrics.heightPixels)
            width = height;
        MyLog.d("gesture", "reLayout :" + width + " , " + height);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = width;
        layoutParams.height = height;
        getWindow().setAttributes(layoutParams);
    }

    @Override
    public void showOverView() {
    }

    public void superFinish() {
        super.finish();
    }

    @Override
    public void tryUnLock() {
    }
}