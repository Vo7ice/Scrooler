package com.guojinhu.scrooler.widget;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by guojin.hu on 2016/4/5.
 */
public class TouchlessScrollerView extends ScrollView {
    public TouchlessScrollerView(Context context) {
        this(context,null);
    }

    public TouchlessScrollerView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public TouchlessScrollerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TouchlessScrollerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        // Do not save the current scroll position. Always store scrollY=0 and delegate
        // responsibility of saving state to the MultiShrinkScroller.
        final int scrollY = getScrollY();
        setScrollY(0);
        final Parcelable returnValue = super.onSaveInstanceState();
        setScrollY(scrollY);
        return returnValue;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}
