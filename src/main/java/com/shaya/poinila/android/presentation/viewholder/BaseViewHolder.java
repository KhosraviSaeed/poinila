package com.shaya.poinila.android.presentation.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * @author Alireza Farahani
 * Created by iran on 2015-06-10.
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder{ // with butterknife no need
                                                                    // to implement click listerners
                                                                    // implements View.OnClickListener{
    protected View rootView;
    public BaseViewHolder(View view) {
        super(view);
        this.rootView = view;
        ButterKnife.bind(this, view);
    }

    public abstract void fill(T t);

    public static class EmptyViewHolder extends BaseViewHolder{

        public EmptyViewHolder(View view) {
            super(view);
        }

        @Override
        public void fill(Object o) {

        }
    }
}

