package com.way.gesture.view;

import android.content.Context;

import com.way.gesture.GestureController;
import com.way.gesture.R;
import com.way.gesture.bean.GestureObject;
import com.way.gesture.util.GestureDataManager;

public class GestureView extends GestureOverView {
    public GestureView(Context context, GestureController gestureController) {
        super(context, gestureController);
        setCreateMode();
    }

    @Override
    public void processGesture() {
        mMode = MODE_PROCESS;
        // 数据过短
        if ((mGesture.mModelDatas == null)
                || (mGesture.mModelDatas.length == 0)) {
            message = getContext().getString(R.string.gestureMessage3);
            invalidate();
            if (mListener != null)
                mListener.onGestureObjectDone(null);
            mMode = MODE_NORMAL;
            return;
        }

        GestureObject gestureObject = GestureDataManager.defaultManager(
                getContext()).searchGesture(mGesture);
        // 没有存过对应匹配的手势
        if (gestureObject == null || gestureObject.mGestureType > 2) {
            invalidate();
            if (mListener != null)
                mListener.onGestureObjectDone(mGesture);
            mMode = MODE_NORMAL;
            return;
        }
        Context context = getContext();
        if (gestureObject.mGestureType == 0) {// 启动应用
            message = (context.getString(R.string.gestureMessageStart)
                    + context.getString(R.string.startapp)
                    + gestureObject.mAppName + context
                    .getString(R.string.gestureMessageEnd));
        } else if (gestureObject.mGestureType == 1) {// 拨号
            message = (context.getString(R.string.gestureMessageStart)
                    + context.getString(R.string.gesturelistcallto)
                    + gestureObject.mUserName + context
                    .getString(R.string.gestureMessageEnd));
        } else if (gestureObject.mGestureType == 2) {// 与发短信重合
            message = (context.getString(R.string.gestureMessageStart)
                    + context.getString(R.string.gesturelistsendsms)
                    + gestureObject.mUserName + context
                    .getString(R.string.gestureMessageEnd));
        }
        invalidate();
        if (mListener != null)
            mListener.onGestureObjectDone(null);
        mMode = MODE_NORMAL;
    }
}