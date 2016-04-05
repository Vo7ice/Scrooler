package com.guojinhu.scrooler.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import com.guojinhu.scrooler.R;

/**
 * Created by guojin.hu on 2016/4/5.
 */
public class MultiScroller extends FrameLayout {

    private static final String TAG = "MultiScroller";

    private Scroller mScroller;
    private ScrollView mScrollView;
    private TextView mTextView;

    private float[] mLastEventPosition = {0, 0};
    private int mMaxHeight;
    private int mTouchSlop;

    private MultiScrollerListener mListener;

    public MultiScroller(Context context) {
        this(context, null);
    }

    public MultiScroller(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiScroller(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
        mMaxHeight = (int) getResources().getDimension(R.dimen.empty_height);

        //初始化一个最小滑动距离
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    public MultiScroller(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void initialize(MultiScrollerListener multiScrollerListener) {
        mScrollView = (ScrollView) findViewById(R.id.cotainer);
        mTextView = (TextView) findViewById(R.id.text_content);
        mListener = multiScrollerListener;
    }

    @Override
    public void computeScroll() {
        Log.d(TAG, "computeScroll");

        if (mScroller.computeScrollOffset()){
            Log.d(TAG,"x=="+mScroller.getCurrX()+",y=="+mScroller.getCurrY());
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            Log.d(TAG,"### getLeft is "+getLeft()+" ### getRight is "+getRight());
            //refresh UI
            postInvalidate();
        }else{
            Log.i(TAG,"have done scroll...");
        }
    }

    private static final int TOUCH_STATE_RESET = 0;//nothing to do
    private static final int TOUCH_STATE_SCROLLING = 1;//start scrolling
    private static final int TOUCH_STATE_BOTTOM = 2;//scroll to bottom
    private int mTouchState = TOUCH_STATE_RESET;//initialize state

    private static int SNAP_VELOCITY = 600;// minimum velocity
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                updateLastEventPosition(event);
                break;
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_MOVE:

                break;
        }
        return super.onTouchEvent(event);
    }

    private void updateLastEventPosition(MotionEvent event){
        mLastEventPosition[0] = event.getX();
        mLastEventPosition[1] = event.getY();
     }

    @Override
    public void scrollTo(int x, int y) {
        final int delta = y - mScrollView.getScrollY();
        if (delta > 0){
            
        }else{

        }   
    }

    public interface MultiScrollerListener {
        void onScrolledOffBottom();
    }
}
