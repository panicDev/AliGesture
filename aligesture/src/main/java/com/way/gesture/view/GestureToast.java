package com.way.gesture.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.way.gesture.R;

public class GestureToast extends Dialog {
    private String mMessage;

    private GestureToast(Context context, String msg) {
        super(context, R.style.toastDialog);
        mMessage = msg;
    }

    public static void showToast(Context ontext, String msg) {
        new GestureToast(ontext, msg).show();
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
        Toast toast = Toast
                .makeText(getContext(), mMessage, Toast.LENGTH_SHORT);
        toast.getView();
        setContentView(toast.getView());
    }

    public void show() {
        super.show();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                dismiss();
            }
        }, 1200L);
    }
}