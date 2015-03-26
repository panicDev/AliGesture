package hwdroid.util;

import com.hw.droid.R;

import android.graphics.Color;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class DialogUtils {

    public static View buildCustomView(Context context, String title) {
        TextView view = new TextView(context);
        view.setText(title);
        view.setTextColor(Color.parseColor("#333333"));
        view.setTextAppearance(context, R.style.TextAppearance_Ali_Medium);
        view.setGravity(1);
        view.setPadding(0, 25, 0, 25);
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        return view;
    }

    public static View buildCustomView(Context context, int titleRes) {
        return buildCustomView(context, context.getResources().getString(titleRes));
    }

    @SuppressWarnings("deprecation")
    public static void fromBottomToTop(Dialog dialog) {
           /*WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.gravity = Gravity.BOTTOM;
		lp.softInputMode =
                WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION;
		lp.windowAnimations = R.style.HWDroid_DialogPushUpInAnimation;
		dialog.getWindow().setAttributes(lp);*/
    }

    @SuppressWarnings("deprecation")
    public static void fromTopToBottom(Dialog dialog) {
        Window w = dialog.getWindow();
        WindowManager windowManager = w.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth());
        lp.x = 0;
        lp.y = 0;
        lp.gravity = Gravity.TOP;
        lp.softInputMode =
                WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION;
        lp.windowAnimations = R.style.HWDroid_DialogPushDownInAnimation;
        dialog.getWindow().setAttributes(lp);
    }
}
