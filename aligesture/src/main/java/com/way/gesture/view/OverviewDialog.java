package com.way.gesture.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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
import com.way.util.MyLog;

public class OverviewDialog extends Dialog implements OverviewModeListener {
    private boolean mAnimationEnable;
    private int mDismissCount = 0;
    private GestureController mGestureController;
    private int mMargins = 100;
    private int mMarginsUP = 100;
    private GestureOverView mGestureOverView;

    public OverviewDialog(Context context, GestureController gestureController,
                          boolean animationEnable) {
        super(context, R.style.dialog);
        mGestureController = gestureController;
        mAnimationEnable = animationEnable;
    }

    private void superCancel() {
        super.cancel();
        mDismissCount = 0;
        MyLog.d("gesture", " superCancel ");
    }

    @Override
    public void cancel() {
        // MyLog.d("gesture", " cancel  ", new Exception("cancel"));
        if (!mAnimationEnable) {
            superCancel();
            return;
        }
        if (mDismissCount > 0) {
            //      superCancel();
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
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.rootview);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                MyLog.d("gesture", " onAnimationEnd ");
                superCancel();
            }

            public void onAnimationRepeat(Animation animation) {
                MyLog.d("gesture", " onAnimationRepeat ");
            }

            public void onAnimationStart(Animation animation) {
                MyLog.d("gesture", " onAnimationStart ");
                // new Handler().postDelayed(new Runnable() {
                // public void run() {
                // cancel();
                // }
                // }, 400L);
            }
        });
        linearLayout.startAnimation(animationSet);
    }

    public boolean isInAnimation() {
        return mDismissCount != 0;
    }

    public void modeChanged(GestureOverView gestureOverView) {
        OverviewLinealayout rootView = (OverviewLinealayout) findViewById(R.id.rootview);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        if (gestureOverView.getCurrentMode() == GestureOverView.MODE_EDIT) {
            layoutParams.setMargins(0, mMarginsUP, 0, mMargins
                    - gestureOverView.getOptionViewHeight());
            rootView.updateViewLayout(mGestureOverView, layoutParams);
            MyLog.d("gesture", "modeChanged  to mode_edit");
        } else {
            layoutParams.setMargins(0, mMarginsUP, 0, mMargins);
            rootView.updateViewLayout(mGestureOverView, layoutParams);
            MyLog.d("gesture", "modeChanged  to mode_normal");
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        // requestWindowFeature(1);
        if (mGestureController.isOnLockScreen()) {
            getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
        } else {
            getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        setContentView(R.layout.overview_dialog);
        mGestureOverView = new GestureOverView(getContext(),
                mGestureController);
        WindowManager windowManager = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int width = 6 * displayMetrics.widthPixels / 7;
        int height = 6 * displayMetrics.heightPixels / 7;
        MyLog.d("gesture", "OverviewDialog.onCreate : "
                + displayMetrics.widthPixels + " , "
                + displayMetrics.heightPixels);
        if (width > height) {
            width = height;
        }
        mMargins = (100 + mGestureOverView.getOptionViewHeight());
        int mariginWidth = width + mMargins + width / 10;
        mGestureOverView.setEnabled(true);
        mGestureOverView.setVisibility(View.VISIBLE);
        OverviewLinealayout overviewLinealayout = (OverviewLinealayout) findViewById(R.id.rootview);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        mMarginsUP = (displayMetrics.heightPixels - mariginWidth);
        linearLayoutParams.setMargins(0, mMarginsUP, 0, mMargins);
        overviewLinealayout.addView(mGestureOverView, linearLayoutParams);
        WindowManager.LayoutParams windowLayoutParams = getWindow()
                .getAttributes();
        windowLayoutParams.width = width;
        windowLayoutParams.height = displayMetrics.heightPixels;
        windowLayoutParams.y = 0;
        getWindow().setAttributes(windowLayoutParams);
        mGestureOverView.setModeChangedListener(this);
        mGestureOverView.setBackgroundResource(R.drawable.gesture_bg);
        overviewLinealayout.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                MyLog.d("gesture", "linear layout ontouchListener : " + event);
                cancel();
                return false;
            }
        });
        // new Handler().postDelayed(new Runnable() {
        // public void run() {
        // ((OverviewLinealayout) OverviewDialog.this
        // .findViewById(R.id.rootview)).invalidate();
        // }
        // }, 400L);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        MyLog.d("gesture", "onWindowFocusChanged");
        WindowManager windowManager = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.4F, 1.0F);
        alphaAnimation.setDuration(300L);
        animationSet.addAnimation(alphaAnimation);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.8F, 1.0F, 0.8F,
                1.0F, 1, 0.5F, 1, 0.5F);
        scaleAnimation.setDuration(300L);
        animationSet.addAnimation(scaleAnimation);
        TranslateAnimation translateAnimation = new TranslateAnimation(0.0F,
                0.0F, -displayMetrics.heightPixels, 0.0F);
        translateAnimation.setDuration(300L);
        translateAnimation.setRepeatCount(0);
        animationSet.addAnimation(translateAnimation);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                MyLog.d("gesture", "onAnimationEnd");
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
                MyLog.d("gesture", "onAnimationStart");
            }
        });
        OverviewLinealayout overviewLinealayout = (OverviewLinealayout) findViewById(R.id.rootview);
        if (mAnimationEnable) {
            overviewLinealayout.startAnimation(animationSet);
        }
    }

    public void reLayout() {
    }

    public void show() {
        super.show();
    }
}
