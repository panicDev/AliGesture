package com.way.gesture;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.IBinder;
import android.provider.Settings;

import com.way.floatwindow.MyWindowManager;
import com.way.gesture.view.GestureToast;
import com.way.gesture.view.OverviewDialog;
import com.way.util.MyLog;

@SuppressLint("NewApi")
public class GestureService extends Service implements GestureController {
    private OverviewDialog mDialog;
    private ScreenLockController mLockController;

    @Override
    public void hideOverView() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    public boolean isOnLockScreen() {
        return mLockController.isOnLockScreen();
    }

    @Override
    public IBinder onBind(Intent paramIntent) {
        return null;
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        MyLog.d("gesture", "onConfigurationChanged");
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.reLayout();
        }
        super.onConfigurationChanged(configuration);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLockController = new ScreenLockController();
    }

    @Override
    public void onDestroy() {
        MyLog.d("gesture", "onDestory");
        super.onDestroy();
        if (MyWindowManager.isWindowShowing())
            MyWindowManager.removeSmallWindow(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (App.IS_NEED_ONTOUCH && !MyWindowManager.isWindowShowing())
            MyWindowManager.createSmallWindow(getApplicationContext());
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        // String command = intent.getStringExtra("command");
        // if ("pause".equals(command)) {
        // mPause = true;
        // MyLog.d("gesture", "command pause gesture  ");
        // return super.onStartCommand(intent, flags, startId);
        // }
        // if ("resume".equals(command)) {
        // mPause = false;
        // MyLog.d("gesture", "command resume gesture  ");
        // return super.onStartCommand(intent, flags, startId);
        // }
        // MyLog.d("gesture", "command startService  ");
        // if (mPause) {
        // MyLog.d("gesture", " gesture is paused now  ");
        // return super.onStartCommand(intent, flags, startId);
        // }
        boolean gestureEnable = Settings.System.getInt(getContentResolver(),
                "way_gesture_switch", 1) == 1;
        if (!gestureEnable) {
            MyLog.d("gesture", " gesture is disabled now ");
            return super.onStartCommand(intent, flags, startId);
        }
        long intentTime = intent.getLongExtra("time", 0L);
        long currentTime = System.currentTimeMillis();
        if (Math.abs(currentTime - intentTime) > 2000L) {
            MyLog.d("gesture", "onStartConmand     overtime :" + currentTime
                    + " ," + intentTime);
            return super.onStartCommand(intent, flags, startId);
        }
        MyLog.d("gesture", "onStartConmand    difftime :" + currentTime + " ,"
                + intentTime);
        boolean isNeedAnima = intent.getBooleanExtra("animation", true);
        if (!mLockController.canShowDialog()) {
            MyLog.d("gesture", "onStartConmand  KeyguardManager need password");
            GestureToast.showToast(this, getResources().getString(R.string.gesture_secure_toast));
            return super.onStartCommand(intent, flags, startId);
        }
        // if (mLockController.isOnLockScreen()) {
        if (mDialog != null) {
            MyLog.d("gesture", "last dialog is showing ");
            return super.onStartCommand(intent, flags, startId);
        }
        MyLog.d("gesture", "show dialog");
        mDialog = new OverviewDialog(this, this, isNeedAnima);
        mDialog.show();
        mDialog
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public void onDismiss(DialogInterface dialogInterface) {
                        hideOverView();
                    }
                });
        // } else {
        // MyLog.d("gesture", "start activity  OverviewActivity");
        // Intent mainIntent = new Intent(this, OverviewActivity.class);
        // mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
        // Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        // startActivity(mainIntent);
        // }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void showOverView() {
    }

    @Override
    public void tryUnLock() {
        mLockController.unLock();
    }

    private class ScreenLockController {
        private int mCount = 0;
        private KeyguardManager mKeyguardManager = null;
        private boolean mDoUnlocked = false;
        private KeyguardManager.KeyguardLock mLock = null;
        private String mTag = "ScreenLockController";
        private BroadcastReceiver mLockReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                    MyLog.d("gesture", "receive action_screen_off current_count:"
                            + mCount);
                    hideOverView();
                    try {
                        if (mDoUnlocked) {
                            mLock.reenableKeyguard();
                            mDoUnlocked = false;
                            MyLog.d("gesture",
                                    "receive action_screen_off   and do renablekeyguard ");
                        }
                    } catch (Exception e) {
                        MyLog.d("gesture", "lock  exception  ", e);
                    }
                }
            }
        };

        public ScreenLockController() {
            mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

            // 声明键盘锁并初始化键盘锁用于锁定或解开键盘锁
            mLock = mKeyguardManager.newKeyguardLock(mTag);
            GestureService.this.registerReceiver(mLockReceiver, new IntentFilter(
                    Intent.ACTION_SCREEN_OFF));

        }

        private boolean isSecure() {
            // LockPatternUtils mLockPatternUtils = new LockPatternUtils(this);
            // // 图案 true
            // mLockPatternUtils.isLockPatternEnabled();
            // mLockPatternUtils.savedPatternExists();
            // // 密码 true
            // mLockPatternUtils.isLockPasswordEnabled();
            // // 无 true
            // mLockPatternUtils.isLockScreenDiseabled();
            // 以上全false就是滑动

            boolean isSecured = mKeyguardManager.isKeyguardSecure();
            // switch (mLockPatternUtils.getKeyguardStoredPasswordQuality()) {
            // // 图案解锁
            // case DevicePolicyManager.PASSWORD_QUALITY_SOMETHING:
            // // 以下是数字密码
            // case DevicePolicyManager.PASSWORD_QUALITY_NUMERIC:
            // case DevicePolicyManager.PASSWORD_QUALITY_ALPHABETIC:
            // case DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC:
            // case DevicePolicyManager.PASSWORD_QUALITY_COMPLEX:
            // if (!mLockPatternUtils.isLockPatternEnabled()
            // || !mLockPatternUtils.savedPatternExists()) {
            // isSecured = false;
            // } else {
            // isSecured = true;
            // }
            // }
            return isSecured;
        }

        public boolean canShowDialog() {
            boolean isLocked = mKeyguardManager.isKeyguardLocked();
            boolean isSecure = isSecure();
            return !isLocked || !isSecure;
        }

        public boolean isOnLockScreen() {
            return mKeyguardManager.isKeyguardLocked();
        }

        public void unLock() {
            if (mKeyguardManager.isKeyguardLocked() && !isSecure()) {
                if (!mDoUnlocked)
                    try {
                        mLock.disableKeyguard();
                        mDoUnlocked = true;
                        mCount++;
                        MyLog.d("gesture", "unLock  done ");
                    } catch (Exception e) {
                        MyLog.d("gesture", "unLock  exception  ", e);
                    }
            }
            MyLog.d("gesture", " can not unLock");
        }
    }
}
