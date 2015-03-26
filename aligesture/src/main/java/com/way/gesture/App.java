package com.way.gesture;

import java.lang.reflect.Method;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Debug;
import android.provider.Settings;
import android.text.TextUtils;

/**
 * This class represents the Application core.
 */
@ReportsCrashes(
        mailTo = "way.ping.li@gmail.com",
        mode = ReportingInteractionMode.DIALOG,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.color.transparent,
        resDialogTitle = R.string.crash_dialog_title,
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
        resDialogOkToast = R.string.crash_dialog_ok_toast)
public class App extends Application {

    private static final String TAG = App.class.getSimpleName();
    public static final boolean IS_NEED_ONTOUCH = isNotYunOS();
    private static Context sApplicationContext;

    @Override
    public void onCreate() {
        if (!Debug.isDebuggerConnected()) {
            ACRA.init(this);
            // ACRA.getErrorReporter().setReportSender(new
            // GestureExceptionReporter());
        }
        if (Settings.System.getInt(getContentResolver(), "way_gesture_switch",
                1) == 1)
            startService(new Intent(this, GestureService.class));
        super.onCreate();
        sApplicationContext = getApplicationContext();
    }

    public static Context getContext() {
        return sApplicationContext;
    }

    private static final String KEY_YUNOS_VERSION = "ro.yunos.version";

    public static final boolean isNotYunOS() {
        boolean result = true;
        try {
            @SuppressWarnings("rawtypes")
            Class classz = Class.forName("android.os.SystemProperties");
            @SuppressWarnings("unchecked")
            Method method = classz.getMethod("get", new Class[]{String.class, String.class});
            Object obj = method.invoke(null, new Object[]{KEY_YUNOS_VERSION, ""});
            result = TextUtils.isEmpty((String) obj);
        } catch (Exception e) {
        }
        return result;
    }

}
