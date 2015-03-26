package com.way.util;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.way.gesture.R;

public class AppUtil {
    private static final String TAG = AppUtil.class.getName();
    private static final String[] EXCLUDE_PACKAGE_NAMES = {"com.coco.themes", "com.way.gesture"};

    // 安全启动应用，避免未找到应用程序时崩溃
    public static boolean startActivitySafely(Activity activity, Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            activity.startActivity(intent);
            activity.finish();
            return true;
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, R.string.getsure_option_info2,
                    Toast.LENGTH_SHORT).show();
            MyLog.e(TAG, "Unable to launch intent = " + intent, e);
        } catch (SecurityException e) {
            Toast.makeText(activity, R.string.getsure_option_info2,
                    Toast.LENGTH_SHORT).show();
            MyLog.e(TAG, "does not have the permission to launch intent = "
                    + intent, e);
        } catch (Exception e) {
            MyLog.e(TAG, "catch Exception ", e);
        }
        return false;
    }

    public static ArrayList<ApplicationInfo> getAllApps(Context context) {
        ArrayList<ApplicationInfo> allApps = new ArrayList<ApplicationInfo>();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> apps = packageManager.queryIntentActivities(
                mainIntent, 0);
        allApps.clear();
        // 过滤部分应用
        boolean isExclude = false;
        for (ResolveInfo app : apps) {
            isExclude = false;
            for (int i = 0; i < EXCLUDE_PACKAGE_NAMES.length; i++) {
                if (app.activityInfo.applicationInfo.packageName
                        .startsWith(EXCLUDE_PACKAGE_NAMES[i])) {
                    MyLog.d(TAG, "exclude packageName="
                            + app.activityInfo.applicationInfo.packageName);
                    isExclude = true;
                    break;
                }
            }
            // 如果有需要过滤的应用，结束此次循环
            if (isExclude)
                continue;

            allApps.add(new ApplicationInfo(context, app));
        }
        Collections.sort(allApps, APP_NAME_COMPARATOR);
        return allApps;
    }

    private static final Comparator<ApplicationInfo> APP_NAME_COMPARATOR = new Comparator<ApplicationInfo>() {
        public final int compare(ApplicationInfo a, ApplicationInfo b) {
            int result = Collator.getInstance().compare(a.title.toString(),
                    b.title.toString());
            if (result == 0) {
                result = a.componentName.compareTo(b.componentName);
            }
            return result;
        }
    };

    public static void transWindows(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(activity, true);
            setTranslucentNavigation(activity, true);
            SystemBarTintManager mTintManager = new SystemBarTintManager(
                    activity);
            mTintManager.setStatusBarTintEnabled(true);
            // mTintManager.setNavigationBarTintEnabled(true);
            mTintManager.setTintDrawable(new ColorDrawable(color));
            // mTintManager.setNavigationBarTintDrawable(new
            // ColorDrawable(color));
        }
    }

    @TargetApi(19)
    private static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @TargetApi(19)
    private static void setTranslucentNavigation(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
