package com.shaya.poinila.android.presentation.viewholder;

import android.view.View;
import android.widget.TextView;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.ViewUtils;

import butterknife.Bind;

/**
 * Created by iran on 2015-08-15.
 */
public class DateHeaderViewHolder extends BaseViewHolder<String> {
    @Bind(R.id.title)
    TextView dayNameView;

    public DateHeaderViewHolder(View inflatedView) {
        super(inflatedView);
    }

    public void fill(String dayName){
        ViewUtils.setText(dayNameView, dayName);
    }
}
