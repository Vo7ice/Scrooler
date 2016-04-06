package com.guojinhu.scrooler.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.EdgeEffect;
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
    private EdgeEffect mEdgeGlowBottom;


    private int mMaxHeight;


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

        //initilize one minimum fling position
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();

        mEdgeGlowBottom = new EdgeEffect(context);
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
        if (mScroller.computeScrollOffset()) {
            Log.d(TAG, "x==" + mScroller.getCurrX() + ",y==" + mScroller.getCurrY());
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            Log.d(TAG, "### getLeft is " + getLeft() + " ### getRight is " + getRight());
            //refresh UI
            postInvalidate();
        } else {
            Log.i(TAG, "have done scroll...");
        }
    }

    private static final int TOUCH_STATE_RESET = 0;//nothing to do
    private static final int TOUCH_STATE_SCROLLING = 1;//start scrolling
    private static final int TOUCH_STATE_BOTTOM = 2;//scroll to bottom
    private int mTouchState = TOUCH_STATE_RESET;//initialize state

    private static int SNAP_VELOCITY = 600;// minimum velocity
    private int mTouchSlop = 0;// minimum fling distance ,when over config move
    private float[] mLastEventPosition = {0, 0}; //remember the last x position
    // handle touch velocity
    private VelocityTracker mVelocityTracker;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "onInterceptTouchEvent -slop:" + mTouchSlop);
        final int action = ev.getAction();
        if (action == MotionEvent.ACTION_MOVE && mTouchState != TOUCH_STATE_RESET) {
            return true;
        }
        final float x = ev.getX();
        final float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onInterceptTouchEvent down");
                updateLastEventPosition(ev);
                Log.d(TAG, "isFinished--" + mScroller.isFinished());
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_RESET : TOUCH_STATE_SCROLLING;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onInterceptTouchEvent move");
                //when over minimum distance change state
                final int yDiff = (int) Math.abs(mLastEventPosition[1] - y);
                if (yDiff > mTouchSlop) {
                    mTouchState = TOUCH_STATE_SCROLLING;
                }
                Log.d(TAG, "move yDiff---" + yDiff + ",y---" + y);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "onInterceptTouchEvent up or cancel");
                mTouchState = TOUCH_STATE_RESET;
                break;
        }
        Log.e(TAG, mTouchState + "====" + TOUCH_STATE_RESET);
        return mTouchState != TOUCH_STATE_RESET;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent start");

        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "MotionEvent.ACTION_DOWN:" + mScroller.isFinished());
                if (null != mScroller) {
                    if (!mScroller.isFinished()) {
                        mScroller.abortAnimation();
                    }
                }
                updateLastEventPosition(event);
                break;

            case MotionEvent.ACTION_MOVE:
                int deltaY = updatePositionAndComputeDelta(event);
                scrollBy(0, deltaY);
                mEdgeGlowBottom.onPull(deltaY, 1 - event.getX() / getWidth());
                if (!mEdgeGlowBottom.isFinished()) {
                    postInvalidateOnAnimation();
                }
                Log.d(TAG, "MotionEvent.ACTION_MOVE --> detaY is " + deltaY);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "MotionEvent.ACTION_UP:" + (mLastEventPosition[1]));
                if (mLastEventPosition[1] > mMaxHeight) {
                    mTouchState = TOUCH_STATE_BOTTOM;
                } else {
                    mTouchState = TOUCH_STATE_RESET;
                }
                Log.d(TAG, "mTouchState:" + mTouchState);
                stopDrag(mTouchState == TOUCH_STATE_RESET);
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                mEdgeGlowBottom.onRelease();

                break;
        }
        return true;
    }

    private void stopDrag(boolean reset) {
        if (reset) {

        } else {

            if (mListener != null) {
                mListener.onScrolledOffBottom();
                mListener = null;
            }
        }
    }

    private void updateLastEventPosition(MotionEvent event) {
        mLastEventPosition[0] = event.getX();
        mLastEventPosition[1] = event.getY();
    }

    private int updatePositionAndComputeDelta(MotionEvent event) {
        final int VERTICAL = 1;
        final float position = mLastEventPosition[VERTICAL];
        updateLastEventPosition(event);
        return (int) (position - mLastEventPosition[VERTICAL]);
    }

    public interface MultiScrollerListener {
        void onScrolledOffBottom();
    }
}
