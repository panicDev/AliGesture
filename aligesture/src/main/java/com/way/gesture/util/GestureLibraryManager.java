package com.way.gesture.util;

import java.util.ArrayList;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GesturePoint;
import android.gesture.GestureStore;
import android.gesture.GestureStroke;
import android.gesture.Prediction;

import com.way.gesture.bean.GestureObject;
import com.way.gesture.bean.Point;
import com.way.util.MyLog;

public class GestureLibraryManager {
    private static GestureLibraryManager mGestureLibraryManager = null;
    private GestureLibrary mGestureLibrary;

    private GestureLibraryManager(Context context) {
        mGestureLibrary = GestureLibraries.fromPrivateFile(context,
                "gesture_librarys");
        /*
	    public static final int SEQUENCE_INVARIANT = 1;
	    // when SEQUENCE_SENSITIVE is used, only single stroke gestures are currently allowed
	    public static final int SEQUENCE_SENSITIVE = 2;//默认

	    // ORIENTATION_SENSITIVE and ORIENTATION_INVARIANT are only for SEQUENCE_SENSITIVE gestures
	    public static final int ORIENTATION_INVARIANT = 1;
	    // at most 2 directions can be recognized
	    public static final int ORIENTATION_SENSITIVE = 2;//默认两个方向
	    // at most 4 directions can be recognized
	    static final int ORIENTATION_SENSITIVE_4 = 4;
	    // at most 8 directions can be recognized
	    static final int ORIENTATION_SENSITIVE_8 = 8;
	    */
        mGestureLibrary.setOrientationStyle(4);//8个方向，识别条件会更苛刻一点
        mGestureLibrary.setSequenceType(GestureStore.SEQUENCE_SENSITIVE);
        mGestureLibrary.load();
    }

    private GestureLevel convertGesture(GestureObject gestureObject) {
        GestureLevel gestureLevel = new GestureLevel();
        ArrayList<Point> points = gestureObject.mAllPoints;
        if (points != null) {
            ArrayList<GesturePoint> gesturePoints = new ArrayList<GesturePoint>();
            int lastX = 0;
            int lastY = 0;
            for (int i = 0; i < points.size(); i++) {
                Point point = points.get(i);

                if (!isSamePoint(point, lastX, lastY)) {
                    GesturePoint gesturePoint = new GesturePoint(point.x,
                            point.y, i);
                    if (point.x != 0 || point.y != 0) {
                        gesturePoints.add(gesturePoint);
                        lastX = point.x;
                        lastY = point.y;
                    }
                }
            }
            gestureLevel.addStroke(new GestureStroke(gesturePoints));
        }

        return gestureLevel;
    }

    public static GestureLibraryManager defaultManager(Context context) {
        if (mGestureLibraryManager == null)
            mGestureLibraryManager = new GestureLibraryManager(context);
        return mGestureLibraryManager;
    }

    private boolean isSamePoint(Point point, int x, int y) {
        if ((x == 0) && (y == 0))
            return false;
        if ((point.x == 0) && (point.y == 0))//多笔之间的分割点，
            return true;
        int width = x - point.x;
        int height = y - point.y;
        if (width * width + height * height < 25)
            return true;
        return false;
    }

    public void removeGesture(GestureObject gestureObject) {
        if (gestureObject != null)
            removeGesture(String.valueOf(gestureObject.mRecorderID));
    }

    public void removeGesture(String title) {
        ArrayList<Gesture> gestures = mGestureLibrary.getGestures(title);
        if ((gestures == null) || (gestures.size() == 0))
            return;
        Gesture gesture = gestures.get(0);
        mGestureLibrary.removeGesture(title, gesture);
        mGestureLibrary.save();
    }

    public String searchGesture(GestureObject gestureObject, int type) {
        GestureLevel gestureLevel = convertGesture(gestureObject);
        ArrayList<Prediction> predictions = mGestureLibrary
                .recognize(gestureLevel);
        float level = gestureLevel.getLevel();
        MyLog.d("gesture", "gesture length :" + gestureLevel.getLength()
                + " level: " + level);
        if ((predictions != null) && (predictions.size() > 0)) {
            for (int i = 0; i < predictions.size(); i++) {
                Prediction prediction = (Prediction) predictions.get(i);
                if (i < 3)
                    MyLog.d("gesture", "searchGesture score :" + prediction.score);
            }
            Prediction bestPrediction = (Prediction) predictions.get(0);
            if (bestPrediction.score > level) {
                MyLog.d("gesture", "searchGesture  name :" + bestPrediction.name);
                return bestPrediction.name;
            }
            MyLog.d("gesture", "searchGesture  first score  :"
                    + bestPrediction.score);
        }
        return null;
    }

    public boolean updateGesture(GestureObject gestureObject) {
        if (gestureObject != null) {
            mGestureLibrary.addGesture(
                    String.valueOf(gestureObject.mRecorderID),
                    convertGesture(gestureObject));
            mGestureLibrary.save();
            return true;
        }
        return false;
    }
}