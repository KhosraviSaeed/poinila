package com.shaya.poinila.android.presentation.view.help.masks;

import android.content.Context;
import android.view.View;

import com.shaya.poinila.android.presentation.R;

/**
 * Created by iran on 5/29/2016.
 */
public class PostsOfCollectionMaskView extends BaseMaskView {

    protected int level = 1;

    public PostsOfCollectionMaskView(Context context, View itemView) {
        super(context, itemView);
    }

    @Override
    protected void init() {
        // TODO
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.mask_posts_of_collection;
    }

    @Override
    protected int getDescViewId() {
        return R.id.mask_posts_of_collection_description;
    }

    @Override
    protected int getNextBtnId() {
        return R.id.mask_posts_of_collection_btn;
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
