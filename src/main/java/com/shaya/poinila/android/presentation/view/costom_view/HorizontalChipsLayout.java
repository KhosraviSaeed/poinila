package com.shaya.poinila.android.presentation.view.costom_view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.viewholder.RemovableTagViewHolder;

/**
 * Created by iran on 12/16/2015.
 */
public class HorizontalChipsLayout extends RecyclerView {

    private RecyclerViewAdapter<String, RemovableTagViewHolder<String>> mAdapter;

    public HorizontalChipsLayout(Context context) {
        this(context, null);
    }

    public HorizontalChipsLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalChipsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // Load attributes
        //final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RemovableChipView, defStyle, 0);
        //a.recycle();

        mAdapter = new RecyclerViewAdapter<String, RemovableTagViewHolder<String>>(context, R.layout.removable_chip) {
            @Override
            protected RemovableTagViewHolder<String> getProperViewHolder(View v, int viewType) {
                return new RemovableTagViewHolder<String>(v) {
                    @Override
                    public void fill(String s) {
                        textView.setText(s);
                    }
                };
            }
        };
        new RecyclerViewProvider(new RecyclerView(context)).
                setAdapter(mAdapter).
                setLinearLayoutManager(VERTICAL).
                bindViewToAdapter();

        for (int i = 0; i < 4; i++) {
            mAdapter.addItem("fsdlkjf");
        }
    }



    /*private static class ChipsAdapter extends RecyclerView.Adapter{


        public ChipsAdapter(){

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }*/


    //public static class RemovableChip extends {}
}
