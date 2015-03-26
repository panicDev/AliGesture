package com.way.gesture;

import com.way.gesture.bean.GestureObject;

public abstract interface GestureViewListener {
    public abstract void onGestureObjectDone(GestureObject gestureObject);
}