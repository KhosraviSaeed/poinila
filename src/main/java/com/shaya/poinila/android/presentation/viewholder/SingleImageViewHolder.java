package com.shaya.poinila.android.presentation.viewholder;

import android.view.View;
import android.widget.ImageView;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.ImageClickedUIEvent;
import com.shaya.poinila.android.util.BusProvider;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by iran on 2015-07-15.
 */
public abstract class SingleImageViewHolder<VH> extends BaseViewHolder<VH>{
    @Bind(R.id.image)
    public ImageView imageView;
    public SingleImageViewHolder(View view) {
        super(view);
    }

    @OnClick(R.id.image) public void onImageClick(){
        BusProvider.getBus().post(new ImageClickedUIEvent(getAdapterPosition()));
    }
}
