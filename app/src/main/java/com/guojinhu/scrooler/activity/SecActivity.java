package com.guojinhu.scrooler.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;

import com.guojinhu.scrooler.R;
import com.guojinhu.scrooler.widget.MultiScroller;

public class SecActivity extends AppCompatActivity {

    MultiScroller mScroller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        mScroller = (MultiScroller) findViewById(R.id.root_scroller);

        if (mScroller != null) {
            mScroller.initialize(mListener);
        }
    }

    MultiScroller.MultiScrollerListener mListener = new MultiScroller.MultiScrollerListener() {
        @Override
        public void onScrolledOffBottom() {
            SecActivity.this.finish();
        }
    };

}
