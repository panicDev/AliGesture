package com.way.gesture.view;

import hwdroid.dialog.AlertDialog;
import hwdroid.dialog.Dialog;
import hwdroid.dialog.DialogInterface;
import hwdroid.dialog.DialogInterface.OnCancelListener;
import hwdroid.dialog.DialogInterface.OnDismissListener;

import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.way.gesture.R;

public class HelpDialog implements DialogInterface.OnClickListener,
        OnDismissListener, OnCancelListener {
    private Context mContext;
    private CheckBox mCheckBox;
    private Dialog mDialog;

    public void show(Context context, boolean notShow) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                "not_show_gesture_help", false)
                && notShow)
            return;
        mContext = context;
        if (mDialog == null)
            mDialog = onCreateDialog(context);
        mDialog.setOnDismissListener(this);
        mDialog.setOnCancelListener(this);
        mDialog.show();

    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
    }

    public Dialog onCreateDialog(Context context) {
        View rootView = LayoutInflater.from(context).inflate(
                R.layout.help_dialog_layout, null);
        mCheckBox = ((CheckBox) rootView.findViewById(R.id.check_box));
        boolean isCheck = PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(
                        "not_show_gesture_help", false);
        mCheckBox.setChecked(isCheck);
        rootView.findViewById(R.id.checkLayout).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        mCheckBox.setChecked(!mCheckBox.isChecked());
                    }
                });
        return new AlertDialog.Builder(context)
                .setTitle(R.string.getsure_help_menu).setCancelable(true)
                .setView(rootView).setPositiveButton(android.R.string.ok, this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        boolean isChecked = mCheckBox.isChecked();
        PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                .putBoolean("not_show_gesture_help", isChecked).apply();
        dialog.dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        this.mCheckBox = null;
        mDialog = null;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        this.mCheckBox = null;
        mDialog = null;
    }
}
