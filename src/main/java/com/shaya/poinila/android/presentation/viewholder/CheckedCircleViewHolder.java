package com.shaya.poinila.android.presentation.viewholder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.CheckBoxClickUIEvent;
import com.shaya.poinila.android.util.BusProvider;

import butterknife.Bind;
import data.model.Circle;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;

/**
 * Created by AlirezaF on 11/16/2015.
 */
public class CheckedCircleViewHolder extends BaseViewHolder<Circle> {
    @Bind(R.id.text)
    public TextView textView;

    @Bind(R.id.checkbox)
    public CheckBox checkBox;

    public CheckedCircleViewHolder(View view) {
        super(view);
    }

    private CompoundButton.OnCheckedChangeListener getCheckBoxListener() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BusProvider.getBus().post(
                        new CheckBoxClickUIEvent(isChecked, getAdapterPosition()));
            }
        };
    }

    public void fill(Circle circle){
        setText(textView, circle.name);
        //checkBox.setChecked(interest.selected);
        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(circle.selected);
        itemView.setBackgroundResource(circle.selected ?
                R.drawable.bordered_rounded_rect_checked : R.drawable.bordered_rounded_rect);
        checkBox.setOnCheckedChangeListener(getCheckBoxListener());
    }
}
