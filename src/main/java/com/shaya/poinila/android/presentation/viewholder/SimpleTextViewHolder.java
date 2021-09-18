package com.shaya.poinila.android.presentation.viewholder;

import android.view.View;
import android.widget.TextView;

import com.shaya.poinila.android.presentation.R;

import butterknife.Bind;

/**
 * Created by iran on 2015-08-19.
 */
public abstract class SimpleTextViewHolder<T> extends BaseViewHolder<T>{
    @Bind(R.id.text)
    public TextView textView;

    public SimpleTextViewHolder(View view) {
        super(view);
    }
}
