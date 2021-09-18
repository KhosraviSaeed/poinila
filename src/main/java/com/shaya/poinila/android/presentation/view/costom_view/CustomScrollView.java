package com.shaya.poinila.android.presentation.view.costom_view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by iran on 2015-09-01.
 */
public class CustomScrollView extends ScrollView {
    private ScrollViewListener scrollViewListener = null;
    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    /*---Post activity. related post----*/
    public void setLastNoneListViewItem(View lastNoneListViewItem) {
        this.lastNoneListViewItem = lastNoneListViewItem;
    }

    View lastNoneListViewItem;

    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent ev) {
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */
        if (lastNoneListViewItem != null) {
            int diff = (getScrollY() - lastNoneListViewItem.getBottom());
            boolean lastScrollViewItemVisible = (diff < 0);
            if (!lastScrollViewItemVisible || !canScrollVertically(-1)){
                return false;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }
}