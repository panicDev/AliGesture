package com.way.gesture.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.way.gesture.GestureCommond;
import com.way.gesture.R;
import com.way.gesture.bean.GestureObject;
import com.way.gesture.util.GestureDataManager;
import com.way.ui.swipeback.SwipeBackActivity;
import com.way.util.MyLog;

public class ActivityAddOption extends SwipeBackActivity implements
        OnItemClickListener {
    private int mCommond;
    private int mMode;
    private int mRecorderID;

    private Intent parseData(Intent intent) {
        Uri uri = intent.getData();
        MyLog.i("way", "uri = " + uri.toString());
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor.moveToFirst()) {
            String name = cursor
                    .getString(cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNum = getContactPhone(cursor);
            intent.putExtra("android.intent.extra.shortcut.NAME", name);
            intent.putExtra("number", phoneNum);
        }
        if (!cursor.isClosed())
            cursor.close();
        return intent;
    }

    private String getContactPhone(Cursor cursor) {
        String phoneNumber = "";
        // 存在的电话号码个数
        int hasPhoneNum = cursor.getInt(cursor
                .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

        if (hasPhoneNum < 1)
            return phoneNumber;

        // 获得联系人的ID号
        String contactId = cursor.getString(cursor
                .getColumnIndex(ContactsContract.Contacts._ID));
        // 获得联系人电话的cursor
        Cursor phone = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
                        + contactId, null, null);
        // 取第一个号码
        if (phone.moveToFirst())
            phoneNumber = phone
                    .getString(phone
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        if (!phone.isClosed())
            phone.close();

        MyLog.i("way", "phoneNum = " + hasPhoneNum + ", result = " + phoneNumber);
        return phoneNumber;
    }

    private void pickContacts() {
        try {
            Intent pickNumIntent = new Intent(Intent.ACTION_PICK,
                    ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(pickNumIntent,
                    GestureCommond.GestureSelectPhone);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GestureCommond.GestureLaunchApp:// 101
                if (data == null)
                    return;
                if (mMode == 0) {
                    data.putExtra("mode", "");
                    startActivityForResult(data, GestureCommond.GestureCreateNew);
                } else {
                    GestureDataManager gestureDataManager = GestureDataManager
                            .defaultManager(this);
                    GestureObject gestureObject = gestureDataManager
                            .getGestureObject(mRecorderID);
                    if (gestureObject != null) {
                        gestureObject.mAppName = data.getStringExtra("AppName");
                        gestureObject.mPackageName = data
                                .getStringExtra("packageName");
                        gestureObject.mClassName = data
                                .getStringExtra("activityName");
                        MyLog.d("add option", " appName:" + gestureObject.mAppName
                                + " packageName:" + gestureObject.mPackageName
                                + "   " + gestureObject.mClassName);
                        gestureObject.mGestureType = 0;
                        gestureDataManager.updateGesture(gestureObject);
                        setResult(GestureCommond.GestureSucess);
                    }
                    finish();
                }
                break;
            case GestureCommond.GestureSelectPhone:// 102
                if (data == null)
                    return;
                parseData(data);
                String name = data
                        .getStringExtra("android.intent.extra.shortcut.NAME");
                String number = data.getStringExtra("number");
                if (mMode == 0) {
                    Intent intent = new Intent();
                    intent.setClass(this, ActivityCreateGesture.class);
                    intent.putExtra("commond", mCommond);
                    intent.putExtra("userName", name);
                    intent.putExtra("phoneNumber", number);
                    startActivityForResult(intent, GestureCommond.GestureCreateNew);
                } else {
                    GestureDataManager gestureDataManager = GestureDataManager
                            .defaultManager(this);
                    GestureObject gestureObject = gestureDataManager
                            .getGestureObject(mRecorderID);
                    if (gestureObject != null) {
                        gestureObject.mUserName = name;
                        gestureObject.mPhoneNumber = number;
                        gestureObject.mGestureType = mCommond;
                        gestureDataManager.updateGesture(gestureObject);
                        setResult(GestureCommond.GestureSucess);
                    }
                    finish();
                }
                break;
            case GestureCommond.GestureCreateNew:// 103
                if (resultCode == GestureCommond.GestureSucess) {
                    MyLog.d("add new gesture", "add new gesture");
                    setResult(GestureCommond.GestureSucess);
                }
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
        setActivityContentView(R.layout.add_option);
        showBackKey(true);
        String[] datas = new String[]{getString(R.string.launchApp),
                getString(R.string.callother), getString(R.string.smstoother)};
        ListView listview = ((ListView) findViewById(android.R.id.list));

        listview.setAdapter(new ArrayAdapter(this, R.layout.add_option_item,
                datas));
        listview.setOnItemClickListener(this);
        mCommond = 0;
        mMode = 0;
        Intent intent = getIntent();
        String mode = intent.getStringExtra("mode");
        if ((mode != null) && (mode.equalsIgnoreCase("Edit"))) {
            mMode = 1;
            mRecorderID = intent.getIntExtra("recorderID", 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.d("gesture", "ActivityAddOption onResume");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (position == 0) {
            Intent intent = new Intent();
            intent.setClass(this, ActivitySelectApp.class);
            startActivityForResult(intent, GestureCommond.GestureLaunchApp);
            return;
        }
        mCommond = position;
        pickContacts();
    }
}