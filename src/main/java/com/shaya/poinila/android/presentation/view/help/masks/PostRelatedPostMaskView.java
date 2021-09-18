package com.shaya.poinila.android.presentation.view.help.masks;

import android.content.Context;
import android.view.View;

import com.shaya.poinila.android.presentation.R;

/**
 * Created by iran on 5/24/2016.
 */
public class PostRelatedPostMaskView extends BaseMaskView {

    protected int level = 1;

    public PostRelatedPostMaskView(Context context, View itemView) {
        super(context, itemView);
    }

    @Override
    protected void init() {
        // TODO
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.mask_post_related_posts;
    }

    @Override
    protected int getDescViewId() {
        return R.id.mask_post_related_posts_description;
    }

    @Override
    protected int getNextBtnId() {
        return R.id.mask_post_related_posts_btn;
    }


    @Override
    public void onClick(View v) {

        level++;

        switch (level){
            default:
                btnListener.onClick(nextBtn);
        }


    }

}
