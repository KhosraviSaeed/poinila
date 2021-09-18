package com.shaya.poinila.android.presentation.viewholder;

import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.OnOffSettingToggledUIEvent;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.util.BusProvider;

import butterknife.Bind;
import data.model.OnOffSetting;

/**
 * Created by iran on 2015-09-07.
 */
public class SwitchTextViewHolder extends BaseViewHolder<OnOffSetting>{
    @Bind(R.id.switch_btn) SwitchCompat switchBtn;
    @Bind(R.id.label) TextView label;
    ViewGroup viewgroup;

    public SwitchTextViewHolder(View view) {
        super(view);
        viewgroup = (ViewGroup) view;
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BusProvider.getBus().post(new OnOffSettingToggledUIEvent(getAdapterPosition(), isChecked));
            }
        });
    }

    public void fill(OnOffSetting setting){
        switchBtn.setChecked(setting.value == OnOffSetting.ON);
        ViewUtils.setText(label, setting.name);
        ViewUtils.enableLayoutChildes(viewgroup, setting.enabled);
    }
}
