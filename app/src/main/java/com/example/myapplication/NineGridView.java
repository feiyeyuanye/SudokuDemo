package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 九宫格
 * 一个问题，手指必须划过圆
 */
public class NineGridView extends View {

    private Paint paint;
    public static final int raduis = 60;
    public static final int raduisS = 30;
    private float curr_x, curr_y;
    private boolean isOver = false;
    private List<Point> points = new ArrayList<>();
    //当前用户选中的圆 0-8
    private List<Integer> mList = new ArrayList<>();

    /**
     * 构造函数
     * @param context
     */
    public NineGridView(Context context) {
        super(context);
        init();
    }

    public NineGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化画笔
     */
    public void init() {
        paint = new Paint();
        // 抗锯齿
        paint.setAntiAlias(true);
        // 防抖动
        paint.setDither(true);
        paint.setStrokeWidth(10);
        // 画笔颜色  #5AA9FE
        paint.setColor(Color.rgb(0x5a, 0xa9, 0xfe));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getWidth() != 0 && getHeight() != 0) {
            //算出9个点
            int width = getWidth() / 4;
            int height = getHeight() / 4;
            //第一个点 =  width，height；
            //第二个点 = width*2 height;
            //。。。
            //第四个点 = width / height*2;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    Point point = new Point((j + 1) * width, (i + 1) * height);
                    points.add(point);
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //判断mList.size>0;
        if (mList.size() > 0) {
            //从最后一个圆中引一条线到 手指地方
            Point point = points.get(mList.get(mList.size() - 1));
            canvas.drawLine(point.x, point.y, curr_x, curr_y, paint);
        }
        //如果mList>1
        if (mList.size() > 1) {
            //0,1
            for (int i = 0; i < mList.size() - 1; i++) {
                Point start = points.get(mList.get(i));
                Point end = points.get(mList.get(i + 1));
                canvas.drawLine(start.x, start.y, end.x, end.y, paint);
            }
        }
        paint.setColor(Color.WHITE);
        for (Point point : points) {
            canvas.drawCircle(point.x, point.y, raduis, paint);
        }
        paint.setColor(Color.rgb(0x5a, 0xa9, 0xfe));//#5AA9FE
        paint.setStyle(Paint.Style.STROKE);
        //先画圆变成了最后画圆 //遮住后面的线
        paint.setStrokeWidth(2);
        for (Point point : points) {
            canvas.drawCircle(point.x, point.y, raduis, paint);
        }
        //画实心圆
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        for (Integer i : mList) {
            Point point = points.get(i);
            canvas.drawCircle(point.x, point.y, raduisS, paint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isOver = false;
                //判断是否在圆心内，并且画圆
                int index = isOnClircle(x, y);
                if (index != -1) {
                    mList.add(index);
                }
                curr_x = x;
                curr_y = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isOver) {
                    curr_x = x;
                    curr_y = y;
                    //引一条线到手指的地方，如果手指滑动到圆中，并且该圆还没有被选中，那用一条线连接两个圆，在从该圆引一条线到与手指
                    int i = isOnClircle(x, y);
                    if (i != -1 && !mList.contains(i)) {
                        mList.add(i);
                    }
                    if (mList.size() == 9) {
                        returnData();
                        mList.clear();
                        isOver = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //密码设置完毕，需要回调
                //密码验证完毕, 需要回调
                //密码返回回去
                if (mList.size() > 4) {//56789
                    returnData();
                }
                mList.clear();
                break;
                default:
                    break;
        }
        invalidate();
        return true;
    }


    public void returnData() {
        if (onPasswordFinishListener != null) {
            String password = "";
            for (int i = 0; i < mList.size(); i++) {
                password += mList.get(i) + 1;
            }
            onPasswordFinishListener.onPasswrodFinish(password);
        }
    }

    /**
     * 用户按下的地方
     * @param x
     * @param y
     * @return
     */
    public int isOnClircle(float x, float y) {
        for (int i = 0; i < points.size(); i++) {
            //判断圆心到点的距离
            Point point = points.get(i);
            if ((x - point.x) * (x - point.x) + (y - point.y) * (y - point.y) < raduis * raduis) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 回调
     */
    private OnPasswordFinishListener onPasswordFinishListener;

    public void setOnPasswordFinishListener(OnPasswordFinishListener onPasswordFinishListener) {
        this.onPasswordFinishListener = onPasswordFinishListener;
    }

    public interface OnPasswordFinishListener {
        void onPasswrodFinish(String password);
    }
}
