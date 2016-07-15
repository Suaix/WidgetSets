package com.summerxia.widgetlibray.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.summerxia.widgetlibray.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SummerXia on 2016/6/29.
 */
public class VerticalSlideLayout extends FrameLayout {

    private String TAG = "VerticalSlideLayout";

    private Context mContext;
    private LayoutInflater inflater;
    /**
     * 默认的item高度为300dp
     */
    private final int DEFAULT_ITEMT_HEIGHT = 300;
    /**
     * 所有子控件的集合
     */
    private List<View> allViews;
    /**
     * 滑动到上面的子控件的集合
     */
    private List<View> upViews;
    /**
     * 滑动下方的子控件的集合
     */
    private List<View> downViews;
    /**
     * 填充数据的集合
     */
    private List<String> dataList;
    /**
     * 最上方View的区域
     */
    private Rect topRect;
    /**
     * 每个子布局的高度
     */
    private float itemHeight = 0;
    /**
     * 最大位移的距离
     */
    private float bigTransY = 0;
    /**
     * 中等位移的距离
     */
    private float middleTransY = 0;
    /**
     * 最小位移的距离
     */
    private float smallTransY = 0;
    /**
     * 当前能看到的正在放大的view的位置
     */
    private int currentViewPosition = 0;
    /**
     * 是否正在滑动，如果不是可以进行滑动；如果正在滑动则不进行处理，确保一次只能滑动一个item
     */
    private boolean isScrolling = false;
    /**
     * 是否处理点击未展开item的点击事件
     */
    private boolean clickExpandSmallItem = false;
    /**
     * 最后的可见且不能展开的Item的个数，默认为3个
     */
    private int lastVisiableCount = 3;
    /**
     * 本页还剩多少个item未看时触发加载更多的操作，默认为四个
     */
    private int loadMoreCount = 4;

    /**
     * 位移动画的时间
     */
    private long animationDuration = 200;
    /**
     * 上一次触发手势事件的时间
     */
    private long lastTouchTime = 0;
    /**
     * 可以开始滑动的位移阀值
     */
    private int moveDY = 80;
    private float downY;
    private float downX;
    /**
     * 加载更多的监听
     */
    private OnLoadMoreListener listener;
    /**
     * 填充数据内容的监听
     */
    private OnInflateContentListener inflateContentListener;

    private OnItemClickListener onItemClickListener;

    private OnScrollDirectionChangeListener directionChangeListener;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (listener != null) {
                listener.onLoadMore();
            }
        }
    };

    /**
     * 监听手势滑动的方向
     */
    public interface OnScrollDirectionChangeListener {
        /**
         * 手势向上滑动
         */
        public final static int DIRECTION_SCROLL_UP = 1;
        /**
         * 手势向下滑动
         */
        public final static int DIRECTION_SCROLL_DOWN = 2;

        /**
         * 滑动方向发生变化
         *
         * @param flag
         * @param dY
         */
        abstract void onScrollDirectionChanged(int flag, float dY);
    }

    /**
     * 加载更多的监听
     */
    public interface OnLoadMoreListener {
        abstract void onLoadMore();
    }

    /**
     * 填充布局内容的监听
     */
    public interface OnInflateContentListener {
        abstract void onInflateContent(View view, String data);
    }

    /**
     * Item点击事件的监听
     */
    public interface OnItemClickListener {
        abstract void onItemClickListener(String data);
    }

    public VerticalSlideLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public VerticalSlideLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;
        topRect = new Rect();
        inflater = LayoutInflater.from(mContext);
        allViews = new ArrayList<>();
        upViews = new ArrayList<>();
        downViews = new ArrayList<>();

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.VerticalSlideLayout);
        itemHeight = mTypedArray.getDimension(R.styleable.VerticalSlideLayout_itemHeight, DEFAULT_ITEMT_HEIGHT);
        clickExpandSmallItem = mTypedArray.getBoolean(R.styleable.VerticalSlideLayout_clickExpandSmallItem, false);
        lastVisiableCount = mTypedArray.getInteger(R.styleable.VerticalSlideLayout_lastVisiableCount, 3);
        loadMoreCount = mTypedArray.getInteger(R.styleable.VerticalSlideLayout_loadMoreCount, 4);

        bigTransY = itemHeight;
        middleTransY = itemHeight * 2 / 3;
        smallTransY = itemHeight / 3;
    }

    public void setDataList(List<String> list, int itemLayoutId) {
        this.removeAllViews();
        if (allViews != null) {
            allViews.clear();
        }
        if (upViews != null) {
            upViews.clear();
        }
        if (downViews != null) {
            downViews.clear();
        }
        if (dataList != null) {
            dataList.clear();
        }
        if (list == null) {
            dataList = new ArrayList<>();
        } else {
            dataList = list;
        }
        for (int i = 0; i < dataList.size(); i++) {
            View view = inflate(mContext, itemLayoutId, null);
            String content = list.get(i);
            if (inflateContentListener != null) {
                inflateContentListener.onInflateContent(view, content);
            }
            this.addView(view);
            //暂时手动设置item的宽高
            view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) itemHeight));
            allViews.add(view);
        }

        for (int i = 0; i < allViews.size(); i++) {
            if (i == 0) {
                upViews.add(allViews.get(i));
            } else if (i == 1) {
                downViews.add(allViews.get(i));
                allViews.get(i).setTranslationY(bigTransY);
            } else {
                //从第三个开始，每个view上移itemHeight
                downViews.add(allViews.get(i));
                allViews.get(i).setTranslationY(bigTransY + smallTransY * (i - 1));
            }
        }
        this.invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (System.currentTimeMillis() - lastTouchTime < animationDuration / 2) {
            //滑动太频繁，拦截不进行处理
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                downX = event.getX();
//                Log.e(TAG, "downY:" + downY);
                break;
            case MotionEvent.ACTION_MOVE:
                float dY = event.getY() - downY;
//                Log.e(TAG, "dY:" + dY);
                if (dY < -moveDY && !isScrolling) {
                    //向上滑动
                    if (directionChangeListener != null) {
                        directionChangeListener.onScrollDirectionChanged(OnScrollDirectionChangeListener.DIRECTION_SCROLL_UP, dY);
                    }
                    goUp();
                } else if (dY > moveDY && !isScrolling) {
                    if (directionChangeListener != null) {
                        directionChangeListener.onScrollDirectionChanged(OnScrollDirectionChangeListener.DIRECTION_SCROLL_DOWN, dY);
                    }
                    goDown();
                }
                break;
            case MotionEvent.ACTION_UP:
//                Log.e(TAG, "isScrolling:" + isScrolling);
//                Log.e(TAG, "upY:" + (event.getY() - downY));
                if (isScrolling) {
                    isScrolling = false;
                } else {
                    /*没有滚动，用户点击了页面，当做点击事件处理；
                    * 判断点击时的点是否在正在展示的View上，
                    * 如果是则是点击事件，如果不是则不进行处理
                    * */
                    View view = allViews.get(currentViewPosition);
                    topRect = getHitRect(topRect, view);
                    if (topRect.contains((int) downX, (int) downY)) {
                        //当做点击事件处理
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClickListener(dataList.get(currentViewPosition));
                        }
                    } else if (topRect.contains((int) downX, (int) (downY - smallTransY))) {
                        //点击了downViews第一个view区域
                        if (clickExpandSmallItem) {
                            transYUpAndDown(1);
                        } else if (onItemClickListener != null) {
                            onItemClickListener.onItemClickListener(dataList.get(currentViewPosition + 1));
                        }
                    } else if (topRect.contains((int) downX, (int) (downY - 2 * smallTransY))) {
                        //点击了downViews第二个view区域
                        if (clickExpandSmallItem) {
                            transYUpAndDown(2);
                        } else if (onItemClickListener != null) {
                            onItemClickListener.onItemClickListener(dataList.get(currentViewPosition + 2));
                        }
                    } else if (topRect.contains((int) downX, (int) (downY - 3 * smallTransY))) {
                        //点击了downViews第三个view的区域
                        if (clickExpandSmallItem) {
                            transYUpAndDown(3);
                        } else if (onItemClickListener != null) {
                            onItemClickListener.onItemClickListener(dataList.get(currentViewPosition + 3));
                        }
                    }
                }
                lastTouchTime = System.currentTimeMillis();
                break;
        }
        return true;
    }

    /**
     * 点击了downView的第downPosition个view，将其上下的view都进行位移处理，本身也进行位移处理
     *
     * @param downPosition downViews的view的位置，从1开始；
     */
    private void transYUpAndDown(int downPosition) {
        //1.将点击的view进行位移，添加动画
        View targetView = downViews.get(downPosition - 1);
        ViewPropertyAnimator targetAnim = ViewPropertyAnimator.animate(targetView).translationY(ViewHelper.getTranslationY(targetView) - (downPosition - 1) * smallTransY - bigTransY).setDuration(animationDuration);
        targetAnim.start();
        //2。将点击后的view向上进行位移,添加动画
        for (int i = downPosition; i < downViews.size(); i++) {
            View downView = downViews.get(i);
            ViewPropertyAnimator animator = ViewPropertyAnimator.animate(downView).translationY(ViewHelper.getTranslationY(downView) - downPosition * smallTransY).setDuration(animationDuration);
            animator.start();
        }
        //3.将upViews中的view向上进行位移，
        for (int n = 0; n < upViews.size(); n++) {
            View upView = upViews.get(n);
            upView.setTranslationY(ViewHelper.getTranslationY(upView) - downPosition * bigTransY);
        }
        //4.将点击view的之上且尚未展开的view进行位移;
        for (int j = 0; j < downPosition - 1; j++) {
            View middleView = downViews.get(j);
            middleView.setTranslationY(ViewHelper.getTranslationY(middleView) - 2 * bigTransY - (downPosition - 2) * smallTransY);
        }
        //5.将点击view之上且尚未展开的view添加到upViews里，并从downViews中移除；
        for (int m = 0; m < downPosition; m++) {
            upViews.add(downViews.get(0));
            downViews.remove(0);
        }
        currentViewPosition += downPosition;
    }

    private Rect getHitRect(Rect topRect, View view) {
        topRect.left = view.getLeft();
        topRect.right = view.getRight();
        topRect.top = (int) (view.getTop() + ViewHelper.getTranslationY(view));
        topRect.bottom = (int) (view.getBottom() + ViewHelper.getTranslationY(view));
        return topRect;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 向下位移
     */
    private void goDown() {
        isScrolling = true;
        //1、判断upViews里是否有多于1个view
        if (upViews.size() <= 1) {
            return;
        }
        //2、将upviews中的每个view向下位移bigTransY，只为最后两个添加动画
        View topView = upViews.get(upViews.size() - 1);
        ViewPropertyAnimator anim = ViewPropertyAnimator.animate(topView).translationY(ViewHelper.getTranslationY(topView) + bigTransY).setDuration(animationDuration);
        anim.start();
        View shouldView = upViews.get(upViews.size() - 2);
        ViewPropertyAnimator anim2 = ViewPropertyAnimator.animate(shouldView).translationY(ViewHelper.getTranslationY(shouldView) + bigTransY).setDuration(animationDuration);
        anim2.start();
        for (int i = upViews.size() - 3; i >= 0; i--) {
            View view = upViews.get(i);
            view.setTranslationY(ViewHelper.getTranslationY(view) + bigTransY);
        }
        //3、downviews中每个view向下位移smallTransY,
        for (int i = 0; i < downViews.size(); i++) {
            View downView = downViews.get(i);
            if (i < 3) {
                ViewPropertyAnimator anim3 = ViewPropertyAnimator.animate(downView).translationY(ViewHelper.getTranslationY(downView) + smallTransY).setDuration(animationDuration);
                anim3.start();
            } else {
                downView.setTranslationY(ViewHelper.getTranslationY(downView) + smallTransY);
            }
        }
        downViews.add(0, upViews.get(upViews.size() - 1));
        upViews.remove(upViews.size() - 1);
        currentViewPosition--;
    }

    /**
     * 向上位移
     */
    private void goUp() {
        isScrolling = true;
        //1、判断downViews是否没有view了；
        if (downViews.size() == 0 || downViews.size() <= lastVisiableCount) {
            return;
        }
        //2、将upViews中每个View向上的位移添加bigTransY,只为最后一个添加动画
        View topView = upViews.get(upViews.size() - 1);
//        Log.e(TAG, "topView_transY:" + ViewHelper.getTranslationY(topView));
        ViewPropertyAnimator anim = ViewPropertyAnimator
                .animate(topView)
                .translationY(ViewHelper.getTranslationY(topView) - bigTransY)
                .setDuration(animationDuration);
        anim.start();
        for (int i = 0; i < upViews.size() - 1; i++) {
            View view = upViews.get(i);
            view.setTranslationY(ViewHelper.getTranslationY(view) - bigTransY);
        }
        //3、将downViews中第一个view向上的位移添加bigTransY，增加位移动画
        View firstDownView = downViews.get(0);
        ViewPropertyAnimator animator = ViewPropertyAnimator.animate(firstDownView).translationY(ViewHelper.getTranslationY(firstDownView) - bigTransY).setDuration(animationDuration);
        animator.start();
        //4、将downViews中其他的view依次向上位移smallTransY,
        for (int i = 1; i < downViews.size(); i++) {
            View view1 = downViews.get(i);
            //只让可见的View使用动画，其他的使用动画，减少内存消耗
            if (i < 4) {
                ViewPropertyAnimator animator2 = ViewPropertyAnimator.animate(view1).translationY(ViewHelper.getTranslationY(view1) - smallTransY).setDuration(animationDuration);
                animator2.start();
            } else {
                view1.setTranslationY(ViewHelper.getTranslationY(view1) - smallTransY);
            }
        }
        //5、将downViews中的第一个view移除到upViews最后一个
        upViews.add(downViews.get(0));
        downViews.remove(0);
        currentViewPosition++;
        //如果滑动到倒数第三个位置则加载更多
        if (currentViewPosition == dataList.size() - loadMoreCount) {
            //延迟调用加载更多，保证动画完成后计算到准确的位移
            handler.sendEmptyMessageDelayed(0, animationDuration*2);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void addDataList(List<String> list, int layoutId) {
        int lastBottomPositon = allViews.size() - 1;
        float lastItemTransY = downViews.get(downViews.size() - 1).getTranslationY();
        dataList.addAll(list);
        for (int i = 0; i < list.size(); i++) {
            View view = inflate(mContext, layoutId, null);
            String content = list.get(i);
            if (inflateContentListener != null) {
                inflateContentListener.onInflateContent(view, content);
            }
            this.addView(view);
            //暂时手动设置item的宽高
            view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) itemHeight));
            allViews.add(view);
        }

        for (int i = lastBottomPositon + 1; i < allViews.size(); i++) {
            //每个view下移itemHeight
            downViews.add(allViews.get(i));
            allViews.get(i).setTranslationY(lastItemTransY + smallTransY * (i - lastBottomPositon));
        }
    }

    public void setOnloadMoreListener(OnLoadMoreListener listener) {
        this.listener = listener;
    }

    public void setOnInflateContentListener(OnInflateContentListener inflateContentListener) {
        this.inflateContentListener = inflateContentListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnScrollDirectionChangedListener(OnScrollDirectionChangeListener directionChangedListener) {
        this.directionChangeListener = directionChangedListener;
    }

    /**
     * 设置Item的高度
     *
     * @param itemHeight
     */
    public void setItemHeight(float itemHeight) {
        this.itemHeight = itemHeight;
        bigTransY = itemHeight;
        middleTransY = itemHeight * 2 / 3;
        smallTransY = itemHeight / 3;
    }
}
