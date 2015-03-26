package hwdroid.dialog;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.view.Window;

/**
 * hide
 */
public abstract class Dialog {

    public Dialog(Context context) {
    }

    public Dialog(Context context, int theme) {
    }

    public abstract void setCancelMessage(Message msg);

    public abstract void setCancelable(boolean flag);

    public abstract void setCanceledOnTouchOutside(boolean cancel);

    public abstract void setDismissMessage(Message msg);

    public abstract void setOnCancelListener(DialogInterface.OnCancelListener listener);

    public abstract void setOnDismissListener(DialogInterface.OnDismissListener listener);

    public abstract void setOnKeyListener(DialogInterface.OnKeyListener onKeyListener);

    public abstract void setTitle(int titleId);

    public abstract void setTitle(CharSequence title);

    public abstract void setView(View v);

    public abstract void show();

    public abstract boolean isShowing();

    public abstract void dismiss();

    public abstract Window getWindow();

}
