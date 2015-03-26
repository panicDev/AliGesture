package com.way.gesture.view;

import com.way.gesture.R;
import com.way.gesture.R.id;
import com.way.gesture.R.layout;
import com.way.gesture.R.string;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CheckBox;

public class FragmentHelp extends DialogFragment implements
        DialogInterface.OnClickListener {
    private CheckBox mCheckBox;
    private WebView mWebView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int id) {
        boolean isChecked = mCheckBox.isChecked();
        PreferenceManager.getDefaultSharedPreferences(getDialog().getContext())
                .edit().putBoolean("not_show_gesture_help", isChecked).apply();
        dismiss();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        View rootView = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_help, null);
        mCheckBox = ((CheckBox) rootView.findViewById(R.id.check_box));
        boolean isCheck = PreferenceManager.getDefaultSharedPreferences(
                getActivity()).getBoolean("not_show_gesture_help", false);
        mCheckBox.setChecked(isCheck);
        rootView.findViewById(R.id.checkLayout).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        mCheckBox.setChecked(!mCheckBox.isChecked());
                    }
                });
        this.mWebView = ((WebView) rootView.findViewById(R.id.web_view));
        this.mWebView.loadUrl("file:///android_asset/480.gif");
        WebSettings webSettings = this.mWebView.getSettings();
        webSettings.setUseWideViewPort(false);
        webSettings
                .setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);
        this.mWebView.setHorizontalScrollBarEnabled(false);
        this.mWebView.setVerticalScrollBarEnabled(false);
        this.mWebView.setBackgroundColor(0);
        this.mWebView.setFocusable(false);
        this.mWebView.setClickable(false);
        this.mWebView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                return true;
            }
        });
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.getsure_help_menu).setView(rootView)
                .setPositiveButton(android.R.string.ok, this).create();
    }

    @Override
    public void onDestroy() {
        if (this.mWebView != null) {
            this.mWebView.loadUrl("about:blank");
            this.mWebView.stopLoading();
            this.mWebView = null;
        }
        this.mCheckBox = null;
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().getDecorView().setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                FragmentHelp.this.getDialog().getWindow().getDecorView()
                        .setVisibility(View.VISIBLE);
            }
        }, 500L);
    }

    public void show(FragmentManager fragmentManager, Context context,
                     boolean notShow) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                "not_show_gesture_help", false)
                && notShow)
            return;
        super.show(fragmentManager, "help");
    }
}
