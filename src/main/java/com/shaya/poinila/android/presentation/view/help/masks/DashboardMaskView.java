package com.shaya.poinila.android.presentation.view.help.masks;

import android.content.Context;
import android.view.View;

import com.shaya.poinila.android.presentation.R;

/**
 * Created by iran on 5/24/2016.
 */
public class DashboardMaskView extends BaseMaskView{

    protected int level = 1;

    public DashboardMaskView(Context context, View itemView) {
        super(context, itemView);
    }

    @Override
    protected void init() {
        //TODO
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.mask_dashboard;
    }

    @Override
    protected int getDescViewId() {
        return R.id.mask_dashboard_description;
    }

    @Override
    protected int getNextBtnId() {
        return R.id.mask_dashboard_btn;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }


    @Override
    public void onClick(View v) {

        level++;

        switch (level){
            case 2:
                descView.setText(R.string.help_dashboard_level_two_description);
                break;
            default:
                btnListener.onClick(nextBtn);
        }


    }
}
