package com.shaya.poinila.android.presentation.view.activity;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.costom_view.TouchImageView;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.squareup.picasso.Picasso;

import butterknife.Bind;

public class FullImageActivity extends ToolbarActivity {

    @Bind(R.id.image_view)
    TouchImageView imageView;

    private String imageUrl;

    @Override
    protected void initUI() {
    }

    @Override
    protected void onStart() {
        super.onStart();

        Picasso.with(this).load(imageUrl).into(imageView);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_full_image;
    }

    @Override
    protected void handleIntentExtras() {
        imageUrl = getIntent().getStringExtra(ConstantsUtils.KEY_CONTENT_URI);
    }

    @Override
    protected View getActivityView() {
        ViewGroup vp = (ViewGroup) getLayoutInflater().inflate(getLayoutResourceId(), null);
        toolbar = (Toolbar) getLayoutInflater().inflate(R.layout.toolbar_black, vp, false);
        handleToolbar();
        vp.addView(toolbar, 0);
        return vp;
    }
}
