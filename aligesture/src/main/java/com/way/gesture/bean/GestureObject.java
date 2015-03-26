package com.way.gesture.bean;

import java.util.ArrayList;
import java.util.Iterator;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.way.util.MyLog;

/**
 * @author way
 */
public class GestureObject {
    private static final String TAG = "GestureObject";
    public ArrayList<Point> mAllPoints = new ArrayList<Point>();// 所有的点
    ArrayList<Point> mProcessPoints = new ArrayList<Point>();// 多笔画时，存储单笔画点
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private boolean mDrawCache = false;
    private int mPointDiff;
    private int mResolution;// 画笔粗细
    private int mViewHeight;
    private int mViewWidth;
    public int[] mModelDatas;// 模型数据，所有点经过处理后的最终角度数组
    Paint mPaint = new Paint();
    public int mGestureType;// 有3中：启动应用、拨号、发短信
    public String mAppName;// 应用名
    public String mClassName;// 类名
    public String mPhoneNumber;// 电话号码
    public String mPhoneType;// 电话类型
    public int mRecorderID;// 手势数据库中的id
    public String mPackageName;// 包名
    public String mUserName;// 用户名
    Paint mRedPaint = new Paint(4);
    boolean mIsStarted = false;// 是否开始画点
    int[] mTempAngels;// 临时数组，用来存放角度

    public GestureObject() {
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(7.0F);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mRedPaint.setColor(Color.RED);
        mRedPaint.setStrokeWidth(8.0F);
        mRedPaint.setStyle(Paint.Style.FILL);
        mRedPaint.setAntiAlias(true);
        mRedPaint.setDither(true);
        mTempAngels = new int[300];
        setResolution(8);
    }

    public GestureObject(GestureObject gestureObject) {
        this();
        start();
        for (int i = 0; i < gestureObject.mAllPoints.size(); i++)
            mAllPoints.add(gestureObject.mAllPoints.get(i));
        end();
    }

    public GestureObject(int[] points) {
        this();
        start();
        for (int i = 0; i < points.length; i += 2)
            addPoint(points[i], points[(i + 1)]);
        end();
    }

    /**
     * 将点转化成线，并画在画板上 暂时用addProcessPointBezier代替
     *
     * @param point
     */
    private void addProcessPoint(Point point) {
        if ((point.x == 0) && (point.y == 0)) {
            if ((mDrawCache) && (mCanvas != null)
                    && (mProcessPoints.size() > 1)) {
                Point lastPoint = mProcessPoints
                        .get(-1 + mProcessPoints.size());
                Point nearPoint = clcNearPoint(lastPoint,
                        mProcessPoints.get(-2 + mProcessPoints.size()));
                mCanvas.drawLine(nearPoint.x, nearPoint.y, lastPoint.x,
                        lastPoint.y, mPaint);
            }
            mProcessPoints.clear();
        }
        if (!mDrawCache || mCanvas == null) {
            mProcessPoints.add(point);
            return;
        }
        mProcessPoints.add(point);
        if (mProcessPoints.size() == 1) {
            mCanvas.drawPoint((mProcessPoints.get(0)).x,
                    (mProcessPoints.get(0)).y, mPaint);
            return;
        }
        if (mProcessPoints.size() == 2) {
            Point secondPoint = (Point) mProcessPoints.get(1);
            Point firstPoint = (Point) mProcessPoints.get(0);
            Point nearPoint = clcNearPoint(secondPoint, firstPoint);
            mCanvas.drawLine(firstPoint.x, firstPoint.y, nearPoint.x,
                    nearPoint.y, mPaint);
            return;
        }
        Point lastPoint = mProcessPoints.get(-1 + mProcessPoints.size());
        Point lastSecondPoint = mProcessPoints.get(-2 + mProcessPoints.size());
        Point nearSecondPoint = clcNearPoint(lastSecondPoint,
                mProcessPoints.get(-3 + mProcessPoints.size()));
        Point nearLastPoint = clcNearPoint(lastSecondPoint, lastPoint);
        Path path = new Path();
        path.moveTo(nearSecondPoint.x, nearSecondPoint.y);
        path.cubicTo((lastSecondPoint.x + nearSecondPoint.x) / 2,
                (lastSecondPoint.y + nearSecondPoint.y) / 2,
                (lastSecondPoint.x + nearLastPoint.x) / 2,
                (lastSecondPoint.y + nearLastPoint.y) / 2, nearLastPoint.x,
                nearLastPoint.y);
        mCanvas.drawPath(path, mPaint);
        Point nearPoint5 = clcNearPoint(lastPoint, lastSecondPoint);
        mCanvas.drawLine(nearLastPoint.x, nearLastPoint.y, nearPoint5.x,
                nearPoint5.y, mPaint);
    }

    /**
     * 将点与点处理后，连接成线
     *
     * @param point
     */
    private void addProcessPointBezier(Point point) {
        if ((point.x == 0) && (point.y == 0)) {// 多笔画之间的间隔点
            if ((mDrawCache) && (mCanvas != null)
                    && (mProcessPoints.size() > 1)) {
                Point lastPoint = mProcessPoints
                        .get(-1 + mProcessPoints.size());
                Point centerPoint = centerPoint(lastPoint,
                        mProcessPoints.get(-2 + mProcessPoints.size()));
                mCanvas.drawLine(centerPoint.x, centerPoint.y, lastPoint.x,
                        lastPoint.y, mPaint);
            }
            mProcessPoints.clear();
            return;
        }
        if (!mDrawCache || mCanvas == null) {
            mProcessPoints.add(point);
            return;
        }
        mProcessPoints.add(point);
        if (mProcessPoints.size() == 1) {
            mCanvas.drawPoint((mProcessPoints.get(0)).x,
                    (mProcessPoints.get(0)).y, mPaint);
            return;
        }

        if (mProcessPoints.size() == 2) {
            Point secondPoint = mProcessPoints.get(1);
            Point firstPoint = mProcessPoints.get(0);
            Point centerPoint = centerPoint(secondPoint, firstPoint);
            mCanvas.drawLine(firstPoint.x, firstPoint.y, centerPoint.x,
                    centerPoint.y, mPaint);
            return;
        }
        Point lastPoint = mProcessPoints.get(-1 + mProcessPoints.size());
        Point lastSecondPoint = mProcessPoints.get(-2 + mProcessPoints.size());
        Point lastCenterSecondPoint = centerPoint(lastSecondPoint,
                mProcessPoints.get(-3 + mProcessPoints.size()));
        Point lastCenterPoint = centerPoint(lastSecondPoint, lastPoint);
        Path path = new Path();
        path.moveTo(lastCenterSecondPoint.x, lastCenterSecondPoint.y);
        path.quadTo(lastSecondPoint.x, lastSecondPoint.y, lastCenterPoint.x,
                lastCenterPoint.y);
        mCanvas.drawPath(path, mPaint);
    }

    /**
     * 计算两个点中间的点
     *
     * @param point1
     * @param point2
     * @return
     */
    private Point centerPoint(Point point1, Point point2) {
        Point point = new Point();
        point.x = ((point1.x + point2.x) / 2);
        point.y = ((point2.y + point1.y) / 2);
        return point;
    }

    /**
     * 计算两个点临近的点，暂时使用centerPoint代替
     *
     * @param point1
     * @param point2
     * @return
     */
    private Point clcNearPoint(Point point1, Point point2) {
        Point point = new Point();
        if (point1.x - point2.x == 0) {
            point.x = point1.x;
            if (point1.y > point2.y)
                point.y -= mResolution * 2;
            else
                point.y += mResolution * 2;
            return point;
        }
        int width = Math.abs(point1.x - point2.x);
        int height = Math.abs(point1.y - point2.y);
        float strokeWidth = (float) Math.sqrt(width * width + height * height);
        float f2 = width / strokeWidth;
        float f3 = height / strokeWidth;
        if (point1.x > point2.x)
            point.x -= (int) (f2 * mResolution * 2);
        else
            point.x += (int) (f2 * mResolution * 2);
        if (point1.y > point2.y)
            point.y -= (int) (f3 * mResolution * 2);
        else
            point.y += (int) (f3 * mResolution * 2);
        return point;
    }

    /**
     * 画在画板上
     */
    private void drawProcess() {
        if (mAllPoints.size() > 0) {
            mProcessPoints.clear();
            fillBitmap();
            for (Point point : mAllPoints) {
                Point newPoint = new Point();
                newPoint.x = point.x;
                newPoint.y = point.y;
                if (mProcessPoints.size() == 0) {
                    addProcessPointBezier(newPoint);
                } else {
                    if (!samePoint(newPoint,
                            mProcessPoints.get(-1 + mProcessPoints.size())))
                        addProcessPointBezier(newPoint);
                }
            }
            endPoint();
            MyLog.d(TAG, "drawProcess ");
            return;
        }
        fillBitmap();
    }

    /**
     * 画背景，
     */
    private void fillBitmap() {
        if (mCanvas == null)
            return;
        MyLog.d(TAG, "mCanvas height :" + mCanvas.getHeight());
        float roundRadio = 5.0F;
        if (mCanvas.getWidth() > 300) {
            roundRadio = 15.0F;
        } else if (mCanvas.getWidth() > 100) {
            roundRadio = 10.0F;
        }
        RectF rectF = new RectF(0.0F, 0.0F, -1 + mCanvas.getWidth(), -1
                + mCanvas.getHeight());
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.BLACK);
        mCanvas.drawRoundRect(rectF, roundRadio, roundRadio, paint);
    }

    /**
     * 增加一个点
     *
     * @param x
     * @param y
     */
    public void addPoint(int x, int y) {
        if (!mIsStarted)
            return;
        Point point = new Point();
        point.x = x;
        point.y = y;
        // array.add(point);
        if (mProcessPoints.size() == 0) {
            mAllPoints.add(point);
            addProcessPointBezier(point);
            return;
        }
        if (!samePoint(point, mProcessPoints.get(-1 + mProcessPoints.size())))
            addProcessPointBezier(point);
        if (!samePoint(point, mAllPoints.get(-1 + mAllPoints.size())))
            mAllPoints.add(point);
    }

    /**
     * 计算两个点之间的角度
     *
     * @param point1
     * @param point2
     * @return
     */
    public double clcAngel(Point point1, Point point2) {
        int width = point2.x - point1.x;
        int height = point2.y - point1.y;
        double thridLength = Math.sqrt(width * width + height * height);// 斜边边长
        double angel = 180.0D * Math.asin(Math.abs(height) / thridLength)
                / Math.PI;
        // Math.to
        if ((width < 0) && (height > 0))
            angel = 180.0D - angel;
        if ((width < 0) && (height < 0))
            angel += 180.0D;
        if ((width > 0) && (height < 0))
            angel = 360.0D - angel;
        if (width == 0) {
            if (height <= 0)
                angel = 270.0D;
            else
                angel = 90.0D;
        }
        if (height == 0) {
            if (width <= 0)
                angel = 180.0D;
            else
                angel = 0.0D;
        }
        return angel;
    }

    /**
     * 复位
     */
    public void clearPoint() {
        mAllPoints.clear();
        mProcessPoints.clear();
        if (mDrawCache && mCanvas != null) {
            fillBitmap();
            MyLog.d(TAG, "clear mCanvas ");
        }
    }

    /**
     * 类似onDraw函数
     *
     * @param canvas
     */
    public void draw(Canvas canvas) {
        setDrawCache(true);
        if (mDrawCache) {
            MyLog.d("lwp", " gestureObject draw " + mIsStarted
                    + ", needResize() = " + needResize());
            if (!mIsStarted && needResize()) {// 是否需要调整位置大小
                drawResize(canvas);
                return;
            }
            canvas.drawBitmap(mBitmap, 1.0F, 1.0F, mPaint);
            return;
        }
        MyLog.d("lwp", "draw normal ");
        int size = -1 + mAllPoints.size();
        for (int i = 0; i < size; i++) {
            Point point1 = mAllPoints.get(i);
            Point point2 = mAllPoints.get(i + 1);
            if (((point1.x != 0) || (point1.y != 0))
                    && ((point2.x != 0) || (point2.y != 0)))
                canvas.drawLine(point1.x, point1.y, point2.x, point2.y,
                        mPaint);
        }
    }

    /**
     * 调整位置，避免有些点可能落在view边界外，
     *
     * @param canvas
     */
    public void drawResize(Canvas canvas) {
        MyLog.i("lwp", "drawResize...");
        int minX = canvas.getWidth();
        int minY = canvas.getHeight();
        int maxX = 0;
        int maxY = 0;
        for (Point point : mAllPoints) {
            if (point.x != 0 && point.y != 0) {
                if (minX > point.x)
                    minX = point.x;
                if (minY > point.y)
                    minY = point.y;
                if (maxX < point.x)
                    maxX = point.x;
                if (maxY < point.y)
                    maxY = point.y;
            }
        }
        int width = 40 - minX;
        int height = 40 - minY;
        int canvasWidth = -80 + canvas.getWidth();
        int maxWidth = maxX + width;
        int maxHeight = maxY + height;
        int minWidth = minX + width;
        int minHeight = minY + height;
        float maxWidthRadio = 1.0F * canvasWidth / maxWidth;
        float maxHeightRadio = 1.0F * canvasWidth / maxHeight;
        float radio;
        if (maxWidthRadio > maxHeightRadio)
            radio = maxHeightRadio;
        else
            radio = maxWidthRadio;
        int realMinWidthRadio = (int) (radio * minWidth);
        int realMinHeightRadio = (int) (radio * minHeight);
        int realMaxWidthRadio = (int) (radio * maxWidth);
        int realMaxHeightRadio = (int) (radio * maxHeight);
        int realWidthRadio = (canvas.getWidth() - (realMaxWidthRadio - realMinWidthRadio))
                / 2 - realMinWidthRadio;
        int realHeightRadio = (canvas.getHeight() - (realMaxHeightRadio - realMinHeightRadio))
                / 3 - realMinHeightRadio;
        GestureObject gestureObject = new GestureObject();
        gestureObject.setViewSize(mViewWidth, mViewHeight);
        gestureObject.setDrawCache(true);
        gestureObject.start();

        for (Point p : mAllPoints) {
            if (p.x != 0 && p.y != 0) {
                int x = realWidthRadio + (int) (radio * (width + p.x));
                int y = realHeightRadio + (int) (radio * (height + p.y));
                gestureObject.addPoint(x, y);
                MyLog.d(TAG, "drawResize : " + x + " , " + y);
            } else {
                gestureObject.addPoint(0, 0);
            }
        }
        gestureObject.endPoint();
        gestureObject.draw(canvas);
        MyLog.d(TAG, "drawResize ");
    }

    /**
     * 结束画点
     */
    public void end() {
        mIsStarted = false;
        parserLine();
    }

    public void endPoint() {
        if (!mIsStarted)
            return;
    }

    /**
     * 是否为相同的手势
     *
     * @param gestureObject
     * @return
     */
    public boolean isSameTo(GestureObject gestureObject) {
        if (mModelDatas.length == gestureObject.mModelDatas.length) {
            for (int i = 0; i < mModelDatas.length; i++)
                if (mModelDatas[i] != gestureObject.mModelDatas[i])
                    return false;
            return true;
        }
        return false;
    }

    /**
     * 手势是否可用
     *
     * @return
     */
    public boolean isValide() {
        if (mGestureType == 0) {
            if ((mPackageName == null) || (mPackageName.length() <= 0)
                    || (mClassName == null) || (mClassName.length() <= 0))
                return false;
        } else {
            if ((mPhoneNumber == null) || (mPhoneNumber.length() <= 0))
                return false;
        }
        return true;
    }

    public byte[] modelDataBytes() {
        if (mModelDatas == null || mModelDatas.length == 0)
            return null;
        byte[] bytes = new byte[2 * mModelDatas.length];
        // int m = 0;
        // for (int i = 0; i < modelData.length; i++) {
        // bytes[m] = (byte) (0xFF & modelData[i] >> 8);
        // bytes[(m + 1)] = (byte) (0xFF & modelData[i]);
        // m += 2;
        // }
        int i = 0;
        int j = 0;
        while (j < mModelDatas.length) {
            bytes[i] = (byte) (0xFF & mModelDatas[j] >> 8);
            bytes[(i + 1)] = (byte) (0xFF & mModelDatas[j]);
            j++;
            i += 2;
        }
        return bytes;
    }

    /**
     * 是否需要调整位置，因为有些点可能落在view的外面
     *
     * @return
     */
    public boolean needResize() {

        if (mBitmap == null) {
            MyLog.d("gesture", "needResize  Bitmap = null");
            return false;
        }
        Iterator<Point> iterator = mAllPoints.iterator();
        while (iterator.hasNext()) {
            Point point = iterator.next();
            if ((point.x != 0)
                    && (point.y != 0)
                    && ((point.x < 0) || (point.y < 0)
                    || (point.x > mBitmap.getWidth()) || (point.y > mBitmap
                    .getHeight())))
                return true;
        }
        MyLog.d("gesture", "needResize  false");
        return false;
    }

    /**
     * 处理轨迹，非常重要的一个函数， 将点的数据转换成为角度信息
     */
    public void parserLine() {

        MyLog.w("parserLine",
                "parserLine start +++++++++++++++\n array.size() ="
                        + mAllPoints.size());
        ArrayList<Integer> angelLists = new ArrayList<Integer>();
        if (mTempAngels.length < mAllPoints.size())
            mTempAngels = new int[mAllPoints.size()];
        for (int i = 0; i < -1 + mAllPoints.size(); i++) {
            mTempAngels[i] = (int) clcAngel(mAllPoints.get(i),
                    mAllPoints.get(i + 1));
            mTempAngels[i] = (45 * ((22 + mTempAngels[i]) / 45));
            if (mTempAngels[i] >= 350)
                mTempAngels[i] = 0;
            MyLog.w("parserLine", "parserLine temp: " + mTempAngels[i]);
            angelLists.add(mTempAngels[i]);
        }
        if (angelLists.size() > 0)
            for (int i = 0; i < angelLists.size(); i++) {
                int isBreak = 0;
                int angelBefore = -1;
                if (i > 0)
                    angelBefore = angelLists.get(i - 1);
                int angelAfter = -1;
                if (i < -1 + angelLists.size())
                    angelAfter = angelLists.get(i + 1);
                int angel = angelLists.get(i);
                if ((angel != angelBefore) && (angel != angelAfter)) {
                    MyLog.w("liweiping",
                            "parserLine remove1: " + angelLists.get(i));
                    angelLists.remove(i);
                    isBreak = 1;
                }
                if (isBreak != 0)
                    break;
                for (int j = -1 + angelLists.size(); j > 0; j--) {
                    if (sameAngel(angelLists.get(j), angelLists.get(j - 1))) {
                        MyLog.w("liweiping",
                                "parserLine remove2: " + angelLists.get(j));
                        angelLists.remove(j);
                    }
                }
            }
        MyLog.w("parserLine", "parserLine angelList begin");
        mModelDatas = new int[4 * angelLists.size()];
        for (int i = 0; i < angelLists.size(); i++) {
            MyLog.w("parserLine", "parserLine last  " + angelLists.get(i));
            mModelDatas[i] = angelLists.get(i);
        }
        MyLog.w("parserLine", "parserLine angelList end");
        if (angelLists.size() == 1) {
            Point point1 = mAllPoints.get(0);
            Point point2 = mAllPoints.get(-1 + mAllPoints.size());
            if ((point1.x - point2.x) * (point1.x - point2.x)
                    + (point1.y - point2.y) * (point1.y - point2.y) < 90000)
                mModelDatas = null;
        }
        MyLog.w("parserLine", "parserLine end ========================\n");
    }

    /**
     * 序列化，将所有点序列化转化成二进制数据以便存入数据库中
     *
     * @return 二进制数据
     */
    public byte[] pointDataBytes() {
        byte[] bytes = new byte[4 * (2 * mAllPoints.size())];
        int i = 0;
        int j = 0;
        while (j < mAllPoints.size()) {
            Point point = mAllPoints.get(j);
            int x = point.x;
            bytes[i] = (byte) (0xFF & x >> 24);
            bytes[(i + 1)] = (byte) (0xFF & x >> 16);
            bytes[(i + 2)] = (byte) (0xFF & x >> 8);
            bytes[(i + 3)] = (byte) (x & 0xFF);
            int y = point.y;
            bytes[(i + 4)] = (byte) (0xFF & y >> 24);
            bytes[(i + 5)] = (byte) (0xFF & y >> 16);
            bytes[(i + 6)] = (byte) (0xFF & y >> 8);
            bytes[(i + 7)] = (byte) (y & 0xFF);
            j++;
            i += 8;
        }
        return bytes;
    }

    /**
     * 反序列化，将数据库中存储的二进制点转换成点
     *
     * @param pointDataBytes
     */
    public void setPointDataBytes(byte[] pointDataBytes) {
        start();
        for (int i = 0; i < pointDataBytes.length; i += 8)
            addPoint(0 + (0xFF000000 & pointDataBytes[i] << 24)
                    + (0xFF0000 & pointDataBytes[(i + 1)] << 16)
                    + (0xFF00 & pointDataBytes[(i + 2)] << 8)
                    + (0xFF & pointDataBytes[(i + 3)]), 0
                    + (0xFF000000 & pointDataBytes[(i + 4)] << 24)
                    + (0xFF0000 & pointDataBytes[(i + 5)] << 16)
                    + (0xFF00 & pointDataBytes[(i + 6)] << 8)
                    + (0xFF & pointDataBytes[(i + 7)]));
        end();
    }

    /**
     * 判断两个角是否相等
     *
     * @param angel1
     * @param angel2
     * @return
     */
    public boolean sameAngel(double angel1, double angel2) {
        if (Math.abs(angel1 - angel2) < 22.0D) {
            double maxAngel = Math.max(angel1, angel2);
            double minAngel = Math.min(angel1, angel2);
            if ((maxAngel <= 270.0D) || (minAngel >= 90.0D))
                return false;
            if (360.0D + minAngel - maxAngel >= 22.0D)
                return true;
        }
        return false;
    }

    /**
     * 判断两个点是否相同
     *
     * @param point1
     * @param point2
     * @return
     */
    public boolean samePoint(Point point1, Point point2) {
        int width = point1.x - point2.x;
        int height = point1.y - point2.y;
        return width * width + height * height < mPointDiff * mPointDiff;
    }

    public void setDrawCache(boolean isDrawCache) {
        int width = mViewWidth;
        int height = mViewHeight;
        mDrawCache = isDrawCache;
        int resolution = (width + 40) / 80;
        if (resolution < 4)
            resolution *= 2;
        if (resolution > 9)
            resolution += 4;
        setResolution(resolution);
        if (isDrawCache) {
            if (mBitmap == null)
                mBitmap = Bitmap.createBitmap(width - 2, height - 2,
                        Bitmap.Config.ARGB_8888);
            if ((mBitmap.getWidth() != width - 2)
                    || (mBitmap.getHeight() != height - 2))
                mBitmap = Bitmap.createBitmap(width - 2, height - 2,
                        Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            fillBitmap();
            drawProcess();
        }
    }

    /**
     * 设置线条粗细
     *
     * @param resolution
     */
    public void setResolution(int resolution) {
        mResolution = resolution;
        mPaint.setStrokeWidth(mResolution);
        mPointDiff = (resolution * 2);
    }

    /**
     * 设置画板的大小
     *
     * @param width
     * @param height
     */
    public void setViewSize(int width, int height) {
        mViewHeight = height;
        mViewWidth = width;
    }

    /**
     * 开始画点
     */
    public void start() {
        mAllPoints.clear();
        mProcessPoints.clear();
        fillBitmap();
        mIsStarted = true;
    }

    @Override
    public String toString() {
        return "GestureObject [GestureType=" + mGestureType + ", appName="
                + mAppName + ", className=" + mClassName + ", packageName="
                + mPackageName + ", phoneNumber=" + mPhoneNumber
                + ", recorderID=" + mRecorderID + "]";
    }

}