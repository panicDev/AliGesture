package com.way.gesture.util;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;

import com.way.gesture.view.GestureImageView;
import com.way.util.MyLog;

public class GestureImageViewHandler extends Handler {
    private static final int STEP_DRAW = 1;
    private static final int STEP_TIME = 30;
    public static GestureImageViewHandler mGestureImageViewHandler = null;
    boolean mIsStarted = false;
    ArrayList<GestureImageView> mGestureImageViewLists = new ArrayList<GestureImageView>();

    public GestureImageViewHandler() {
        mGestureImageViewHandler = this;
    }

    public void addGestureImageView(GestureImageView gestureImageView) {
        mGestureImageViewLists.add(gestureImageView);
        if (!mIsStarted) {
            mIsStarted = true;
            sendEmptyMessageDelayed(STEP_DRAW, STEP_TIME);
        }
    }

    @Override
    public void handleMessage(Message message) {
        super.handleMessage(message);
        switch (message.what) {
            case STEP_DRAW:
                MyLog.i("way2", "handleMessage mGestureImageViewLists.size() = "
                        + mGestureImageViewLists.size());
                if (mGestureImageViewLists.size() > 0) {
                    if (mGestureImageViewLists.get(0).stepDraw())
                        mGestureImageViewLists.remove(0);
                    sendEmptyMessageDelayed(STEP_DRAW, STEP_TIME);

                } else {
                    mIsStarted = false;
                }
                break;

            default:
                break;
        }
    }

    public void removeAll() {
        mGestureImageViewLists.clear();
    }
}