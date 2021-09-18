package com.shaya.poinila.android.presentation.view.costom_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import com.shaya.poinila.android.presentation.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by iran on 12/6/2015.
 */
public class BackForthButtonsBox extends LinearLayout {

    String lastButtonText = null;
    String nextButtonText = null;

    @Bind(R.id.next_button)
    Button nextButton;
    @Bind(R.id.last_button)
    Button lastButton;

    public BackForthButtonsBox(Context context) {
        super(context);

        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setWeightSum(2);

        LayoutInflater.from(context).inflate(R.layout.next_last_buttons, this, true);
        ButterKnife.bind(this, this);

        nextButton.setText(nextButtonText == null ? context.getString(R.string.next_phase) : nextButtonText);
        lastButton.setText(lastButtonText == null ? context.getString(R.string.last_phase) : lastButtonText);
    }

    public BackForthButtonsBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray;
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.BackForthButtonsBox);
        lastButtonText = typedArray.getString(R.styleable.BackForthButtonsBox_last_button_text);
        nextButtonText = typedArray.getString(R.styleable.BackForthButtonsBox_next_button_text);

        typedArray.recycle();

        init(context);
    }

    @OnClick(R.id.next_button)
    public void onForth() {
        if (listener != null) {
            listener.onForth();
        }
    }

    @OnClick(R.id.last_button)
    public void onBack() {
        if (listener != null) {
            listener.onBack();
        }
    }

    public OnBackForthListener getBackForthListener() {
        return listener;
    }

    public void setBackForthListener(OnBackForthListener listener) {
        this.listener = listener;
    }

    private OnBackForthListener listener;

    public interface OnBackForthListener {
        void onBack();

        void onForth();
    }
}
