package com.way.gesture.activity;

import hwdroid.dialog.AlertDialog;
import hwdroid.dialog.DialogInterface;
import hwdroid.widget.ActionSheet;
import hwdroid.widget.ActionSheet.ActionButton;
import hwdroid.widget.ActionSheet.DisplayStyle;
import hwdroid.widget.FooterBar.FooterBarMenu;
import hwdroid.widget.FooterBar.FooterBarType.OnFooterItemClick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.way.gesture.GestureCommond;
import com.way.gesture.GestureController;
import com.way.gesture.GestureViewListener;
import com.way.gesture.R;
import com.way.gesture.bean.GestureObject;
import com.way.gesture.util.GestureDataManager;
import com.way.gesture.view.GestureView;
import com.way.ui.swipeback.SwipeBackActivity;

public class ActivityCreateGesture extends SwipeBackActivity implements
        GestureViewListener, GestureController {
    private static final int MENU_LEFT = 100;
    private static final int MENU_RIGHT = 101;
    private static final int MODE_CREATE = 0;
    private static final int MODE_EDIT = 1;
    private String mActivityName;
    private String mAppName;
    private int mCommond;
    private GestureView mGestureView;
    private GestureDataManager mGestureDataManager;
    private int mCurrentMode = 0;
    private String mPackageName;
    private String mPhoneNumber;
    private int mRecorderID;
    private String mUserName;
    private FooterBarMenu mFooterBarMenu;
    private ActionSheet mActionSheet;

    private void onDeleteMenuItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.gestureDelete);
        builder.setMessage(R.string.gestureDeleteMessage);
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface,
                                        int position) {
                        mGestureDataManager.delete(mGestureView.mGesture);
                        dialogInterface.dismiss();
                        setResult(GestureCommond.GestureSucess);
                        finish();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    private void startEditGesture() {
        Intent intent = new Intent();
        intent.setClass(this, ActivityCreateGesture.class);
        intent.putExtra("AppName", mAppName);
        intent.putExtra("mode", "");
        intent.putExtra("packageName", mPackageName);
        intent.putExtra("userName", mUserName);
        intent.putExtra("phoneNumber", mPhoneNumber);
        intent.putExtra("commond", mCommond);
        intent.putExtra("activityName", mActivityName);
        intent.putExtra("recorderID", mRecorderID);
        startActivityForResult(intent, GestureCommond.GestureEdit);
    }

    private void startEditJob() {
        Intent intent = new Intent();
        intent.setClass(this, ActivityAddOption.class);
        intent.putExtra("AppName", mAppName);
        intent.putExtra("mode", "Edit");
        intent.putExtra("packageName", mPackageName);
        intent.putExtra("userName", mUserName);
        intent.putExtra("phoneNumber", mPhoneNumber);
        intent.putExtra("commond", mCommond);
        intent.putExtra("activityName", mActivityName);
        intent.putExtra("recorderID", mRecorderID);
        startActivityForResult(intent, GestureCommond.GestureEditJob);
    }

    private void onEditMenuItem() {
        mActionSheet = new ActionSheet(this);
        mActionSheet.setActionButtons(
                new ActionButton(getResources().getString(
                        R.string.gestureEditGesture), getResources()
                        .getColorStateList(android.R.color.black),
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startEditGesture();
                            }
                        }),
                new ActionButton(getResources().getString(
                        R.string.gestureEditJob), getResources()
                        .getColorStateList(android.R.color.black),
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startEditJob();
                            }
                        }));
        mActionSheet.setDispalyStyle(DisplayStyle.TABLET);
        mActionSheet.show(mFooterBarMenu.getItem(MENU_RIGHT));
    }

    private void onOkMenuItem() {
        mGestureView.mGesture.mGestureType = 0;
        mGestureView.mGesture.mAppName = mAppName;
        mGestureView.mGesture.mClassName = mActivityName;
        mGestureView.mGesture.mPackageName = mPackageName;
        mGestureView.mGesture.mGestureType = mCommond;
        mGestureView.mGesture.mUserName = mUserName;
        mGestureView.mGesture.mPhoneNumber = mPhoneNumber;
        mGestureDataManager.insertGestureObject(mGestureView.mGesture);
        setResult(GestureCommond.GestureSucess);
        finish();
    }

    private void onRedoMenuItem() {
        mGestureView.reDraw();
        mFooterBarMenu.setItemEnable(MENU_RIGHT, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GestureCommond.GestureEdit:// 104
                if (resultCode == GestureCommond.GestureSucess) {
                    mGestureDataManager.delete(mGestureView.mGesture);
                }
                setResult(GestureCommond.GestureSucess);
                finish();
                break;
            case GestureCommond.GestureEditJob:// 105
                setResult(GestureCommond.GestureSucess);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        // AppUtil.transWindows(this,
        // getResources().getColor(R.color.indicator_selected_color));
        setActivityContentView(R.layout.creategetsture);
        showBackKey(true);

        mGestureDataManager = GestureDataManager.defaultManager(this);
        mGestureView = new GestureView(this, this);

        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");
        mRecorderID = intent.getIntExtra("recorderID", -1);
        mAppName = intent.getStringExtra("AppName");
        mActivityName = intent.getStringExtra("activityName");
        mPackageName = intent.getStringExtra("packageName");
        mUserName = intent.getStringExtra("userName");
        mPhoneNumber = intent.getStringExtra("phoneNumber");
        mCommond = intent.getIntExtra("commond", 0);
        TextView titleTextView = (TextView) findViewById(R.id.textview1);
        if ((mode != null) && (mode.equalsIgnoreCase("Edit"))) {
            mCurrentMode = MODE_EDIT;
            GestureObject gestureObject = mGestureDataManager
                    .getGestureObject(mRecorderID);
            mGestureView.mGesture = gestureObject;
            mAppName = gestureObject.mAppName;
            mActivityName = gestureObject.mClassName;
            mPackageName = gestureObject.mPackageName;
            mUserName = gestureObject.mUserName;
            mPhoneNumber = gestureObject.mPhoneNumber;
            mCommond = gestureObject.mGestureType;
            mGestureView.enable(false);
        } else {
            mCurrentMode = MODE_CREATE;
            mGestureView.mGesture.mAppName = mAppName;
            mGestureView.mGesture.mGestureType = mCommond;
            mGestureView.mGesture.mPackageName = mPackageName;
            mGestureView.mGesture.mClassName = mActivityName;
            mGestureView.mGesture.mPhoneNumber = mPhoneNumber;
            mGestureView.setEnabled(true);
        }

        if (mCommond == 0) {
            String startApp = getString(R.string.startapp);
            titleTextView.setText(startApp + mAppName);
        } else if (mCommond == 1) {
            String callto = getString(R.string.gesturelistcallto);
            titleTextView.setText(callto + mUserName);
        } else {
            String callto = getString(R.string.gesturelistsendsms);
            titleTextView.setText(callto + mUserName);
        }
        mGestureView.setCreateMode();
        mGestureView.setListener(this);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout1);
        linearLayout.addView(mGestureView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        addFooterBarMenu();
    }

    private void addFooterBarMenu() {
        mFooterBarMenu = new FooterBarMenu(this);
        if (mCurrentMode == MODE_EDIT) {
            mFooterBarMenu.addItem(MENU_LEFT,
                    getResources().getString(R.string.gestureDelete),
                    getResources().getDrawable(R.drawable.delete_menu));
            mFooterBarMenu.addItem(MENU_RIGHT,
                    getResources().getString(R.string.gestureEdit),
                    getResources().getDrawable(R.drawable.edit_menu));
        } else {
            mFooterBarMenu.addItem(MENU_LEFT,
                    getResources().getString(R.string.gestureRedo),
                    getResources().getDrawable(R.drawable.redo_menu));
            mFooterBarMenu.addItem(MENU_RIGHT,
                    getResources().getString(R.string.gestureOK),
                    getResources().getDrawable(R.drawable.confirm_menu));
            mFooterBarMenu.setItemEnable(MENU_RIGHT, false);
        }
        mFooterBarMenu.setPrimaryItemCount(2);
        mFooterBarMenu.updateItems();
        getFooterBarImpl().addView(mFooterBarMenu);
        getFooterBarImpl().setVisibility(View.VISIBLE);

        mFooterBarMenu.setOnFooterItemClick(new OnFooterItemClick() {
            @Override
            public void onFooterItemClick(View view, int id) {
                switch (id) {
                    case MENU_LEFT:
                        if (mCurrentMode == MODE_EDIT) {
                            onDeleteMenuItem();
                        } else {
                            onRedoMenuItem();
                        }
                        break;
                    case MENU_RIGHT:
                        if (mCurrentMode == MODE_EDIT) {
                            onEditMenuItem();
                        } else {
                            onOkMenuItem();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onGestureObjectDone(GestureObject gestureObject) {
        if (gestureObject != null) {
            mFooterBarMenu.setItemEnable(MENU_RIGHT, true);
        } else {
            mFooterBarMenu.setItemEnable(MENU_RIGHT, false);
        }
    }

    @Override
    public void hideOverView() {
    }

    @Override
    public boolean isOnLockScreen() {
        return true;
    }

    @Override
    public void showOverView() {
    }

    @Override
    public void tryUnLock() {
    }
}