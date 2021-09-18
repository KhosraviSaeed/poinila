package com.shaya.poinila.android.presentation.view.help.masks;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.help.OnNextButtonListener;

/**
 * Created by iran on 5/24/2016.
 */
public abstract class BaseMaskView extends RelativeLayout implements View.OnClickListener {

    protected Button nextBtn;
    protected TextView descView;

    private int location[] = new int[2];
    private boolean hasLocation = false;

    protected OnNextButtonListener btnListener;

    protected Paint transparentPaint;
    private View itemView;

    private int statusBarHeight = 0;

    public BaseMaskView(Context context, View itemView) {
        super(context);

        inflate(context, getLayoutResource(), this);

        descView = (TextView)findViewById(getDescViewId());
        nextBtn = (Button)findViewById(getNextBtnId());
        nextBtn.setOnClickListener(this);
        init();

        this.itemView = itemView;

        transparentPaint = new Paint();
        transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

    }

    protected abstract void init();
    protected abstract int getLayoutResource();
    protected abstract int getDescViewId();
    protected abstract int getNextBtnId();


    public BaseMaskView setOnNextBtnListener(OnNextButtonListener btnListener) {
        this.btnListener = btnListener;
        return this;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        if(location == null || itemView == null) return;

        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas osCanvas = new Canvas(bitmap);

        RectF outerRectangle = new RectF(0, 0, getWidth(), getHeight());

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(R.color.help_bg));
        osCanvas.drawRect(outerRectangle, paint);

        paint.setColor(Color.TRANSPARENT);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
//        float centerX = getWidth() / 2;
//        float centerY = getHeight() / 2;
//        float radius = 100;


        RectF itemRect = new RectF(location[0], location[1] - statusBarHeight, location[0] + itemView.getWidth(), (location[1] +  itemView.getHeight()) - statusBarHeight);
        osCanvas.drawRect(itemRect, paint);
        canvas.drawBitmap(bitmap, 0, 0, null);

        super.dispatchDraw(canvas);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(!hasLocation && itemView != null){
            itemView.getLocationInWindow(location);
            hasLocation = true;
        }

    }

    public BaseMaskView setStatusBarHeight(int statusBarHeight) {
        this.statusBarHeight = statusBarHeight;
        return this;
    }
}
