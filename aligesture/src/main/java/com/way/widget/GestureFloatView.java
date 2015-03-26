//package com.android.systemui.statusbar;
package com.way.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

//add by liweiping 20141229 for gesture
public class GestureFloatView extends View {
    private WindowManager mWindowManager;
    private LayoutParams mLayoutParams;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals("com.way.gesture.SWITCH_CHANGE",
                    intent.getAction())) {
                Log.d("way", "switch is change");
                if (Settings.System.getInt(getContext().getContentResolver(),
                        "way_gesture_switch", 0) == 0) {
                    mLayoutParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE;
                } else {
                    mLayoutParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
                }
                mWindowManager.updateViewLayout(GestureFloatView.this,
                        mLayoutParams);
            }
        }
    };

    public GestureFloatView(Context context) {
        super(context);
        final Resources res = context.getResources();
        mWindowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);

        mLayoutParams = new LayoutParams();
        mLayoutParams.width = 100;
//		mLayoutParams.height = res
//				.getDimensionPixelSize(com.android.internal.R.dimen.status_bar_height);
        mLayoutParams.format = PixelFormat.RGBA_8888;

        // mLayoutParams.type = LayoutParams.TYPE_PHONE;
        // mLayoutParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
        // | LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.type = LayoutParams.TYPE_STATUS_BAR_PANEL;
        mLayoutParams.flags |= LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        // setBackgroundColor(Color.BLUE);
        mWindowManager.addView(this, mLayoutParams);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getContext().registerReceiver(this.mReceiver,
                new IntentFilter("com.way.gesture.SWITCH_CHANGE"));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try {
            getContext().unregisterReceiver(this.mReceiver);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (Settings.System.getInt(getContext().getContentResolver(),
                "way_gesture_switch", 0) == 0) {
            Log
                    .i("way",
                            "GestureFloatView disable onTouchEvent way_gesture_switch=0");
            return super.onTouchEvent(event);
        }
        return true;
    }

}
