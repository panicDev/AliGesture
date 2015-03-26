package com.way.gesture.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.way.gesture.bean.GestureObject;
import com.way.util.MyLog;

public class GestureDataManager {
    private static final String DATABASE_CREATE = "create table gesture_object"
            + " (_id integer primary key autoincrement, type integer not null, appName text,"
            + " className text, package text, modelData data, pointData data not null,"
            + " phoneNumber text, userName text, phoneType integer);";
    private static GestureDataManager mGestureDataManager = null;
    private SQLiteDatabase mSQLiteDatabase;
    private GestureLibraryManager mGestureLibrary;

    public GestureDataManager(Context context) {
        mSQLiteDatabase = context.openOrCreateDatabase("gesture.db", 0, null);
        mGestureLibrary = GestureLibraryManager.defaultManager(context);
        try {
            mSQLiteDatabase.execSQL(DATABASE_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void copyFile(InputStream inputStream,
                                 OutputStream outputStream) {
        try {
            byte[] buffer = new byte[4 * 1024];
            int len = -1;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
        }
    }

    public static GestureDataManager defaultManager(Context context) {
        if (mGestureDataManager == null) {
            restoreFiles(context);
            mGestureDataManager = new GestureDataManager(context);
        }
        return mGestureDataManager;
    }

    /**
     * 如果有用户需要内置手势，可以实现此函数
     *
     * @param context
     */
    private static void restoreFiles(Context context) {
        File localFile1 = new File("/data/data/com.way.gesture/databases");
        if (!localFile1.exists()) {
            localFile1.mkdir();
        }
        if (!new File("/system/etc/gesture/gesture.db").exists()) {
            MyLog.d("gesture",
                    "/system/etc/gesture/gesture.db not exists ,no need resture");
        }
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                "way_gesture_restore", false)) {
            return;
        }

        File dbOutputFile;
        FileInputStream gestureFileInputStream;
        File gestureFile;
        FileOutputStream gestureFileOutputStream;
        try {
            FileInputStream dbInputStream = new FileInputStream(new File(
                    "/system/etc/gesture/gesture.db"));
            dbOutputFile = new File(
                    "/data/data/com.way.gesture/databases/gesture.db");
            if (dbOutputFile.exists()) {
                MyLog.d("gesture", "gesture.db exists delete it now ");
                dbOutputFile.delete();
            }
            MyLog.d("gesture", "gesture.db not exists need restore");
            dbOutputFile.createNewFile();
            FileOutputStream dbOutputStream = new FileOutputStream(dbOutputFile);
            copyFile(dbInputStream, dbOutputStream);
            dbInputStream.close();
            dbOutputStream.close();
            gestureFileInputStream = new FileInputStream(new File(
                    "/system/etc/gesture/gesture_librarys"));
            gestureFile = new File(
                    "/data/data/com.way.gesture/files/gesture_librarys");
            if (gestureFile.exists()) {
                MyLog.d("gesture", "gesture_librarys exists delete it now ");
                gestureFile.delete();
            }
            MyLog.d("gesture", "gesture_librarys not exists need restore");
            gestureFileOutputStream = context.openFileOutput(
                    "gesture_librarys", Context.MODE_PRIVATE);
            copyFile(gestureFileInputStream, gestureFileOutputStream);
            gestureFileInputStream.close();
            gestureFileOutputStream.close();
            MyLog.d("gesture",
                    "gesture restore file from /system/etc/gesture  sucess ");

        } catch (IOException e) {
            MyLog.d("gesture", "gesture restore error  ", e);
            try {
                File dbFile = new File(
                        "/data/data/com.way.gesture/databases/gesture.db");
                if (dbFile.exists()) {
                    dbFile.delete();
                }
                File gestureFile4 = new File(
                        "/data/data/com.way.gesture/files/gesture_librarys");
                if (gestureFile4.exists()) {
                    gestureFile4.delete();
                }
            } finally {
                PreferenceManager.getDefaultSharedPreferences(context).edit()
                        .putBoolean("way_gesture_restore", true).apply();
            }

        } finally {
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putBoolean("way_gesture_restore", true).apply();
        }

    }

    public void delete(GestureObject gestureObject) {
        int id = mSQLiteDatabase.delete("gesture_object", "_id="
                + gestureObject.mRecorderID, null);
        MyLog.w("liweiping", "delete :" + id);
        mGestureLibrary.removeGesture(gestureObject);
    }

    public Cursor getAllGestureList() {
        return mSQLiteDatabase
                .query("gesture_object", new String[]{"type", "appName",
                                "package", "pointData", "phoneNumber", "userName",
                                "phoneType", "_id", "className"}, null, null, null,
                        null, null);
    }

    public ArrayList<GestureObject> getGestureArrayList() {
        ArrayList<GestureObject> gestureObjects = new ArrayList<GestureObject>();
        Cursor cursor = getAllGestureList();
        if ((cursor != null) && (cursor.getCount() != 0)) {
            cursor.moveToFirst();
            do {
                GestureObject gestureObject = new GestureObject();
                int gestureType = cursor.getInt(0);
                String appName = cursor.getString(1);
                String packageName = cursor.getString(2);
                String phoneNumber = cursor.getString(4);
                byte[] pointDataBytes = cursor.getBlob(3);
                String userName = cursor.getString(5);
                String phoneType = cursor.getString(6);
                int recorderId = cursor.getInt(7);
                String className = cursor.getString(8);
                gestureObject.mGestureType = gestureType;
                gestureObject.mPackageName = packageName;
                gestureObject.mAppName = appName;
                gestureObject.mClassName = className;
                gestureObject.mPhoneNumber = phoneNumber;
                gestureObject.mPhoneType = phoneType;
                gestureObject.mUserName = userName;
                gestureObject.setPointDataBytes(pointDataBytes);
                gestureObject.mRecorderID = recorderId;
                if (gestureObject.mGestureType >= 3)
                    continue;
                gestureObjects.add(gestureObject);
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        return gestureObjects;
    }

    public int getGestureCount() {
        return getGestureArrayList().size();
    }

    public GestureObject getGestureObject(int id) {
        Cursor cursor = mSQLiteDatabase.query("gesture_object", new String[]{
                        "type", "appName", "package", "className", "pointData",
                        "phoneNumber", "userName", "phoneType", "_id"}, "_id=" + id,
                null, null, null, null);
        if ((cursor == null) || (cursor.getCount() != 1)) {
            if (cursor != null)
                cursor.close();
            return null;
        }
        cursor.moveToFirst();
        GestureObject gestureObject = new GestureObject();
        int gestureType = cursor.getInt(0);
        String appName = cursor.getString(1);
        String packageName = cursor.getString(2);
        String className = cursor.getString(3);
        String phoneNumber = cursor.getString(5);
        byte[] pointDataBytes = cursor.getBlob(4);
        String userName = cursor.getString(6);
        String phoneType = cursor.getString(7);
        int recorderID = cursor.getInt(8);
        gestureObject.mGestureType = gestureType;
        gestureObject.mPackageName = packageName;
        gestureObject.mAppName = appName;
        gestureObject.mClassName = className;
        gestureObject.mPhoneNumber = phoneNumber;
        gestureObject.mPhoneType = phoneType;
        gestureObject.mUserName = userName;
        gestureObject.setPointDataBytes(pointDataBytes);
        gestureObject.mRecorderID = recorderID;
        cursor.close();
        return gestureObject;
    }

    public void insertGestureObject(GestureObject gestureObject) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", Integer.valueOf(gestureObject.mGestureType));
        if (gestureObject.mGestureType == 0) {
            contentValues.put("package", gestureObject.mPackageName);
            contentValues.put("appName", gestureObject.mAppName);
            contentValues.put("className", gestureObject.mClassName);
        } else {
            contentValues.put("phoneNumber", gestureObject.mPhoneNumber);
            contentValues.put("userName", gestureObject.mUserName);
            contentValues.put("phoneType", gestureObject.mPhoneType);
        }
        contentValues.put("pointData", gestureObject.pointDataBytes());
        long id = mSQLiteDatabase.insert("gesture_object", null, contentValues);
        gestureObject.mRecorderID = (int) id;
        mGestureLibrary.updateGesture(gestureObject);
        MyLog.w("liweiping", "insert :" + id);
    }

    public GestureObject searchGesture(GestureObject gestureObject) {
        return searchGesture(gestureObject, 0);
    }

    public GestureObject searchGesture(GestureObject gestureObject, int type) {
        MyLog.i("liweiping", "gestureObject.modelData.length = "
                + gestureObject.mModelDatas.length);
        if (gestureObject.mModelDatas.length == 4) {
            ArrayList<GestureObject> gestureObjects = getGestureArrayList();
            if ((gestureObjects != null) && (gestureObjects.size() > 0))
                // for (int i = 0; i < gestureObjects.size(); i++)
                for (GestureObject item : gestureObjects)
                    if (gestureObject.isSameTo(item))
                        return item;
            return null;
        }
        int i = 0;
        while (i < 5) {// 最多尝试5次查询
            i++;
            String recorderIDStr = mGestureLibrary.searchGesture(
                    gestureObject, type);
            if (TextUtils.isEmpty(recorderIDStr)) {
                break;
            }
            MyLog.d("liweiping", "searchGesture id :" + recorderIDStr);
            GestureObject searchGestureObject = getGestureObject(Integer
                    .valueOf(recorderIDStr).intValue());
            if (searchGestureObject == null) {
                mGestureLibrary.removeGesture(recorderIDStr);
                continue;
                // return null;
            }
            MyLog.d("liweiping", "ges mode data len:"
                    + gestureObject.mModelDatas.length);
            MyLog.d("liweiping",
                    "found gesture :" + searchGestureObject.toString());

            if (!searchGestureObject.isValide()) {
                MyLog.d("liweiping", "found gesture but is invalide");
                delete(searchGestureObject);
                continue;
                // return null;
            }
            return searchGestureObject;
        }
        MyLog.d("liweiping", "searchGesture null 2");
        return null;
    }

    public void updateGesture(GestureObject gestureObject) {
        byte[] pointDataBytes = gestureObject.pointDataBytes();
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", Integer.valueOf(gestureObject.mGestureType));
        if (gestureObject.mGestureType == 0) {
            contentValues.put("package", gestureObject.mPackageName);
            contentValues.put("appName", gestureObject.mAppName);
            contentValues.put("className", gestureObject.mClassName);
        } else {
            contentValues.put("phoneNumber", gestureObject.mPhoneNumber);
            contentValues.put("userName", gestureObject.mUserName);
            contentValues.put("phoneType", gestureObject.mPhoneType);
        }
        if ((pointDataBytes != null) && (pointDataBytes.length > 0))
            contentValues.put("pointData", pointDataBytes);
        mSQLiteDatabase.update("gesture_object", contentValues, "_id="
                + gestureObject.mRecorderID, null);
        mGestureLibrary.updateGesture(gestureObject);
    }
}