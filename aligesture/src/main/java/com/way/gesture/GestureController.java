package com.way.gesture;

public abstract interface GestureController {
    public abstract void hideOverView();

    public abstract boolean isOnLockScreen();

    public abstract void showOverView();

    public abstract void tryUnLock();
}