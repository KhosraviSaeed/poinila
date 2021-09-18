package com.shaya.poinila.android.presentation.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.RemoveTagEvent;
import com.shaya.poinila.android.util.BusProvider;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by iran on 2015-07-25.
 */
public class RemovableTagViewHolder<T> extends BaseViewHolder<T>{
    @Bind(R.id.removeButton) public ImageView removeBtn;
    @Bind(R.id.tag) public TextView textView;

    public RemovableTagViewHolder(View view) {
        super(view);
    }

    @Override
    public void fill(T t) {

    }

    @OnClick(R.id.removeButton) public void onRemove(){
        if(getAdapterPosition() != RecyclerView.NO_POSITION)
            BusProvider.getBus().post(new RemoveTagEvent(getAdapterPosition()));
    }
}
