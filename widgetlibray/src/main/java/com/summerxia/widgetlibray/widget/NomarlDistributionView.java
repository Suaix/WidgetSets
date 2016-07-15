package com.summerxia.widgetlibray.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.summerxia.widgetlibray.R;
import com.summerxia.widgetlibray.utils.CommonUtils;

/**
 * NormarlDistribution
 * Created by Summer_xia on 2015/7/21.
 * Descripation:正太分布图
 */
public class NomarlDistributionView extends View {

    private Context mContext;
    /**
     * X轴的画笔
     */
    private Paint xAsixPaint;
    /**
     * X轴刻度的画笔
     */
    private Paint xAsixDotPaint;
    /**
     * X轴坐标轴值的画笔
     */
    private Paint xAsixDotTextPaint;
    /**
     * 坐标系中背景线的画笔
     */
    private Paint scaleGridLinePaint;
    /**
     * 移动的刻度线的画笔
     */
    private Paint scaleValueLinePaint;
    /**
     * 移动的刻度值的画笔
     */
    private Paint scaleValuePaint;
    /**
     * X轴刻度线的位置的数组
     */
    private float[] xAsixsLines = new float[44];
    /**
     * X轴刻度的值的数组
     */
    private float[] xAsixsLineDots = new float[11];
    /**
     * 纵向的背景线
     */
    private float[] yGridLines = new float[16];
    /**
     * 横向的背景线
     */
    private float[] xGridLines = new float[20];
    /**
     * X轴上刻度线之间的间隔
     */
    private float xOffset;
    private float currNum;
    private float startY;
    private float endY;
    private float offsetYGridLine;
    private float offsetXGridLine;
    private int screen;
    /**
     * 正态分布图图片
     */
    private Bitmap bitmap;
    /**
     * Value值的背景图片
     */
    private Bitmap textBackgrounBitmap;
    /**
     * Value值到达最右侧时切换的背景图片
     */
    private Bitmap textBackgroundLeftBitmap;
    /**
     * X轴的刻度值距离刻度线的偏移量
     */
    private static final float xValueBelowXAsix = 55;
    private float marginLeftAndRight = 20;
    private float nomarlDistributionBitmapLeft = 50;
    private float valueLineXPostion = 20;
    private float valueBackgroundBitmapLeft = 60;
    private int value = 0;
    /**
     * X轴上刻度的值的数组
     */
    private static final int[] numValue = new int[101];
    private float startX = 0;
    private float endX = 0;
    private float dX = 0;
    /**
     * 默认的背景颜色
     */
    private int backgrounColor = Color.BLACK;
    /**
     * 监听正态分布图数值的变化的监听器
     */
    private OnValueChagedListener mListener;

    /**
     * 监听正态分布图数值变化的接口
     */
    public interface OnValueChagedListener {
        abstract void onValueChaged(int value);
    }

    public NomarlDistributionView(Context context, AttributeSet attrs,
                                       int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        this.setClickable(true);
    }

    public NomarlDistributionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        this.setClickable(true);
    }

    public NomarlDistributionView(Context context) {
        super(context);
        initView(context);
        this.setClickable(true);
    }

    /**
     * 初始化数值和变量
     * @param context
     */
    private void initView(Context context) {

        for (int i = 0; i < numValue.length; i++) {
            numValue[i] = i;
        }

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);

        mContext = context;
        screen = dm.widthPixels;
        startY = dm.heightPixels / 4;
        endY = dm.heightPixels / 4;
        xOffset = screen / 12;
        currNum = xOffset;
        offsetYGridLine = (screen - 40) / 3;
        offsetXGridLine = (endY - 5) / 5;

        valueLineXPostion = xOffset;
        valueBackgroundBitmapLeft = xOffset + 20;

        bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.zheng_tai_fen_bu);
        textBackgrounBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_nomarl_distrabution_right_marker);
        textBackgroundLeftBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_nomarl_discration_left_marker_view);
        setLinesFloat();

        xAsixPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xAsixPaint.setStyle(Paint.Style.FILL);
        xAsixPaint.setColor(Color.rgb(94, 94, 94));
        xAsixPaint.setStrokeWidth(4);

        scaleGridLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scaleGridLinePaint.setStyle(Paint.Style.FILL);
        scaleGridLinePaint.setColor(Color.rgb(39, 39, 39));
        scaleGridLinePaint.setStrokeWidth(2);

        xAsixDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xAsixDotPaint.setStyle(Paint.Style.FILL);
        xAsixDotPaint.setColor(Color.rgb(71, 71, 71));
        xAsixDotPaint.setStrokeWidth(3);

        xAsixDotTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xAsixDotTextPaint.setStyle(Paint.Style.FILL);
        xAsixDotTextPaint.setColor(Color.rgb(91, 91, 91));
        xAsixDotTextPaint.setStrokeWidth(5);
        xAsixDotTextPaint.setTextSize(20);
        xAsixDotTextPaint.setTextAlign(Paint.Align.CENTER);

        scaleValueLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scaleValueLinePaint.setStyle(Paint.Style.FILL);
        scaleValueLinePaint.setColor(Color.rgb(155, 102, 40));
        scaleValueLinePaint.setStrokeWidth(3);

        scaleValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scaleValuePaint.setStyle(Paint.Style.FILL);
        scaleValuePaint.setColor(Color.rgb(87, 58, 23));
        scaleValuePaint.setStrokeWidth(5);
        scaleValuePaint.setTextSize(25);
        scaleValuePaint.setTextAlign(Paint.Align.CENTER);

    }

    /**
     * 设置线的数组的值
     */
    private void setLinesFloat() {

        xAsixsLines = new float[44];

        for (int j = 0, i = 0; j < xAsixsLines.length; j++) {
            switch (j % 4) {
                case 0:
                    xAsixsLines[j] = currNum;
                    xAsixsLineDots[i] = currNum;
                    i++;
                    break;
                case 1:
                    xAsixsLines[j] = startY;
                    break;
                case 2:
                    xAsixsLines[j] = currNum;
                    break;
                case 3:
                    xAsixsLines[j] = endY + 20;
                    currNum = currNum + xOffset;
                    break;
            }
        }

        float yGridNum = 20;
        for (int i = 0; i < yGridLines.length; i++) {
            switch (i % 4) {
                case 0:
                    yGridLines[i] = yGridNum;
                    break;
                case 1:
                    yGridLines[i] = endY;
                    break;
                case 2:
                    yGridLines[i] = yGridNum;
                    yGridNum += offsetYGridLine;
                    break;
                case 3:
                    yGridLines[i] = 5;
                    break;
            }
        }

        float xGridNum = 5;
        for (int i = 0; i < xGridLines.length; i++) {
            switch (i % 4) {
                case 0:
                    xGridLines[i] = 20;
                    break;
                case 1:
                    xGridLines[i] = xGridNum;
                    break;
                case 2:
                    xGridLines[i] = screen - 20;
                    break;
                case 3:
                    xGridLines[i] = xGridNum;
                    xGridNum += offsetXGridLine;
                    break;
            }
        }
    }

    /***
     * 处理正态分布图的点击滑动事件
     * @param event
     * @return
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = 0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                endX = event.getX();
                dX = endX - startX;
                index = (int) ((valueLineXPostion + dX - xOffset) * 100 / (screen - 2 * xOffset));
                if (index <= 0) {
                    index = 0;
                } else if (index >= 100) {
                    index = 100;
                }
                value = numValue[index];
                if (null != mListener) {
                    mListener.onValueChaged(value);
                }
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                endX = event.getX();
                valueLineXPostion = valueLineXPostion + endX - startX;
                valueBackgroundBitmapLeft = valueBackgroundBitmapLeft + endX
                        - startX;
                if (valueLineXPostion <= xOffset) {
                    valueLineXPostion = xOffset;
                    valueBackgroundBitmapLeft = 20 + xOffset;
                } else if (valueLineXPostion >= screen - xOffset) {
                    valueLineXPostion = screen - xOffset;
                    valueBackgroundBitmapLeft = screen + 20 - xOffset;
                }
                dX = 0;
                postInvalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(backgrounColor);
        canvas.drawLines(xAsixsLines, xAsixDotPaint);
        for (int i = 0, j = 0; i < 11; i++) {
            canvas.drawText(j + "", xAsixsLineDots[i], endY + xValueBelowXAsix,
                    xAsixDotTextPaint);
            j = j + 10;
        }
        canvas.drawLines(yGridLines, scaleGridLinePaint);
        canvas.drawLines(xGridLines, scaleGridLinePaint);
        canvas.drawLine(marginLeftAndRight, startY,
                screen - marginLeftAndRight, endY, xAsixPaint);
        canvas.drawBitmap(bitmap, nomarlDistributionBitmapLeft,
                endY - bitmap.getHeight() - 2, xAsixPaint);

        if (valueBackgroundBitmapLeft + dX + textBackgrounBitmap.getWidth() >= screen) {
            canvas.drawLine(valueLineXPostion + dX, 0, valueLineXPostion + dX,
                    endY + 2 * marginLeftAndRight, scaleValueLinePaint);
            canvas.drawBitmap(textBackgroundLeftBitmap, valueLineXPostion + dX
                            - textBackgroundLeftBitmap.getWidth() - 20,
                    textBackgroundLeftBitmap.getHeight() / 2, scaleValuePaint);
            canvas.drawText(
                    value + "",
                    textBackgroundLeftBitmap.getWidth() / 2 + valueLineXPostion
                            + dX - textBackgroundLeftBitmap.getWidth() - 20,
                    textBackgroundLeftBitmap.getHeight()
                            + CommonUtils.px2Dip(mContext, 15), scaleValuePaint);
            return;
        }
        if (valueLineXPostion + dX > 0 && valueLineXPostion + dX < screen) {
            canvas.drawLine(valueLineXPostion + dX, 0, valueLineXPostion + dX,
                    endY + 2 * marginLeftAndRight, scaleValueLinePaint);
            canvas.drawBitmap(textBackgrounBitmap, valueBackgroundBitmapLeft
                    + dX, textBackgrounBitmap.getHeight() / 2, scaleValuePaint);
            canvas.drawText(
                    value + "",
                    textBackgrounBitmap.getWidth() / 2
                            + valueBackgroundBitmapLeft + dX,
                    textBackgrounBitmap.getHeight()
                            + CommonUtils.px2Dip(mContext, 15), scaleValuePaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = screen;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = (int) (endY + 90);
        }

        setMeasuredDimension(width, height);
    }

    /**
     * 设置正态分布图最高点所在数值
     * @param num 最高点对应的数值
     */
    public void setNomarlDistributionPosition(int num) {
        int n = num * 10 / 100;
        nomarlDistributionBitmapLeft = (n + 1) * xOffset
                + (float) ((num - n * 10) * xOffset / 10)
                - (float) bitmap.getWidth() / 2;
        postInvalidate();
    }

    /**
     * 设置Value改变的监听器
     * @param l
     */
    public void setOnValueChagedListener(OnValueChagedListener l) {
        this.mListener = l;
    }

    /**
     * 设置正态分布图的背景色
     * @param color 色值
     */
    public void setBackgroundColor(int color) {
        this.backgrounColor = color;
        postInvalidate();
    }

    /**
     * 设置初始化时ValueLine所在的位置
     * @param num
     */
    public void setValueLinePosition(int num) {
        int n = num * 10 / 100;
        value = num;
        valueLineXPostion = (n+1)* xOffset + (num - n*10)*xOffset/10;
        valueBackgroundBitmapLeft = valueLineXPostion + 20;
        if(null != mListener){
            mListener.onValueChaged(num);
        }
        postInvalidate();
    }
}
