package com.shaya.poinila.android.presentation.viewholder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.CheckBoxClickUIEvent;
import com.shaya.poinila.android.util.BusProvider;

import butterknife.Bind;
import data.model.Tag;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;

/**
 * Created by iran on 2015-07-11.
 */
public class CheckedTextViewHolder<T extends Tag> extends BaseViewHolder<T>{
    @Bind(R.id.text)
    public TextView textView;

    @Bind(R.id.checkbox)
    public CheckBox checkBox;

    public CheckedTextViewHolder(View view) {
        super(view);
    }

    private CompoundButton.OnCheckedChangeListener getCheckBoxListener(){
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BusProvider.getBus().post(
                        new CheckBoxClickUIEvent(isChecked, getAdapterPosition()));
            }
        };
    }

    public void fill(Tag interest) {
        setText(textView, interest.name);
        //checkBox.setChecked(interest.selected);
        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(interest.selected);
//        itemView.setBackgroundResource(interest.selected ?
//                R.drawable.bordered_rounded_rect_checked : R.drawable.bordered_rounded_rect);
        checkBox.setOnCheckedChangeListener(getCheckBoxListener());
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BusProvider.getBus().post(
                        new CheckBoxClickUIEvent(!checkBox.isChecked(), getAdapterPosition()));
            }
        });
    }
}
