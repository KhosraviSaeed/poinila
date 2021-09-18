package com.shaya.poinila.android.presentation.view.costom_view;
/*
 * Copyright 2014 Mostafa Gazar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/*import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import com.ninja.sms.R;
import com.ninja.sms.utils.Log;*/

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.svgandroid.SVG;
import com.shaya.poinila.android.presentation.svgandroid.SVGParser;

import java.lang.ref.WeakReference;

/**
 * @author Mostafa Gazar <eng.mostafa.gazar@gmail.com>
 */
public class SvgMaskedImageView extends ImageView {

    private static final String TAG = SvgMaskedImageView.class.getSimpleName();

    public static final int DEFAULT_SVG_RAW_RES = R.raw.hexagon;

    private int mSvgRawRes = DEFAULT_SVG_RAW_RES;

    protected Context mContext;

    private static final Xfermode sXfermode = new PorterDuffXfermode(Mode.DST_IN);
    private Bitmap mMaskBitmap;
    private Paint mPaint;
    private WeakReference<Bitmap> mSrcWeakBitmap;

    private int mLastWidth;
    private int mLastHeight;

    public SvgMaskedImageView(Context context) {
        super(context);

        sharedConstructor(context, null);
    }

    public SvgMaskedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        sharedConstructor(context, attrs);
    }

    public SvgMaskedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        sharedConstructor(context, attrs);
    }

    private void sharedConstructor(Context context, AttributeSet attrs) {
        mContext = context;

        mPaint = new Paint();

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SvgMaskedImageView);

            mSvgRawRes = a != null ? a.getResourceId(R.styleable.SvgMaskedImageView_mask, DEFAULT_SVG_RAW_RES) : DEFAULT_SVG_RAW_RES;
            a.recycle();
        }
    }

    public static void drawBitmap(Canvas canvas, Bitmap bitmap,
                                  Paint paint) {
        drawBitmap(canvas, bitmap, paint, 0, 0);
    }

    public static void drawBitmap(Canvas canvas, Bitmap bitmap,
                                  Paint paint, int left, int top) {
        paint.reset();
        paint.setFilterBitmap(false);
        paint.setXfermode(sXfermode);

        canvas.drawBitmap(bitmap, left, top, paint);
    }

    public void invalidate() {
        mSrcWeakBitmap = null;
        if (mMaskBitmap != null) {
            mMaskBitmap.recycle();
        }
        mLastWidth = 0;
        mLastHeight = 0;

        super.invalidate();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (!isInEditMode()) {
            int width = getWidth();
            int height = getHeight();

            int i = canvas.saveLayer(0.0F, 0.0F, width, height, null, Canvas.ALL_SAVE_FLAG);
            try {
                Bitmap srcBitmap = mSrcWeakBitmap != null ? mSrcWeakBitmap.get() : null;
                if (srcBitmap == null || srcBitmap.isRecycled()) {
                    Drawable srcDrawable = getDrawable();
                    if (srcDrawable != null) {
                        srcBitmap = Bitmap.createBitmap(getWidth(),
                                getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas srcBitmapCanvas = new Canvas(srcBitmap);
                        srcDrawable.setBounds(0, 0, getWidth(), getHeight());
                        srcDrawable.draw(srcBitmapCanvas);

                        // Skip and use cached mask.
                        if (mMaskBitmap == null || mMaskBitmap.isRecycled() ||
                                mLastWidth != width || mLastHeight != height) {
                            mMaskBitmap = getMask(width, height);
                        }

                        drawBitmap(srcBitmapCanvas, mMaskBitmap, mPaint);
                        mSrcWeakBitmap = new WeakReference<Bitmap>(srcBitmap);
                    }
                }

                if (srcBitmap != null) {
                    mPaint.setXfermode(null);
                    canvas.drawBitmap(srcBitmap, 0.0F, 0.0F, mPaint);
                }
            } catch (Exception e) {
                System.gc();

                Log.e(TAG, String.format("Unable to draw, view Id :: %s. Error occurred :: %s", getId(), e.toString()));
            } finally {
                canvas.restoreToCount(i);
            }
        } else {
            super.onDraw(canvas);
        }
    }

    private Bitmap getDefaultMask(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        canvas.drawRect(new RectF(0.0F, 0.0F, width, height), paint);

        return bitmap;
    }

    private Bitmap getMask(int width, int height) {
        SVG svgMask = null;
        if (mLastWidth != width || mLastHeight != height) {
            svgMask = SVGParser.getSVGFromInputStream(
                    mContext.getResources().openRawResource(mSvgRawRes), width, height);

            mLastWidth = width;
            mLastHeight = height;
        }

        if (svgMask != null) {
            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.BLACK);

            canvas.drawPicture(svgMask.getPicture());

            return bitmap;
        }

        // In case everything failed, return square.
        return getDefaultMask(width, height);
    }

    public void updateMask(int svgRawRes) {
        if (mSvgRawRes != svgRawRes) {
            mSvgRawRes = svgRawRes;

            invalidate();
        }
    }

}