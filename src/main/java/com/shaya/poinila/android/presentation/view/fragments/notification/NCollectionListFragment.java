package com.shaya.poinila.android.presentation.view.fragments.notification;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.view.fragments.CollectionListFragment;
import com.shaya.poinila.android.presentation.view.fragments.ListBusFragment;
import com.shaya.poinila.android.presentation.viewholder.EditableCollectionViewHolder;
import com.shaya.poinila.android.presentation.viewholder.notification.NEditableCollectionViewHolder;

import java.util.List;

import data.event.BaseEvent;
import data.model.Collection;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;
import static com.shaya.poinila.android.util.ResourceUtils.getInteger;

/**
 * Created by iran on 6/14/2016.
 */
public class NCollectionListFragment extends CollectionListFragment {

    List<Collection> mData;

    @Override
    protected void initUI() {
//        super.initUI();

        mRecyclerView = new RecyclerViewProvider(mRecyclerView).
                setAdapter(getRecyclerViewAdapter()).
                setGridLayoutManager(VERTICAL, getInteger(R.integer.column_count)).
                bindViewToAdapter();

        if(mData != null){
            getRecyclerViewAdapter().addItems(mData);
        }
    }

    public static NCollectionListFragment newInstance(List list){
        NCollectionListFragment fragment = new NCollectionListFragment();
        fragment.mData = list;
        return fragment;
    }

    @Override
    protected RecyclerView.OnScrollListener getRecyclerViewListener() {
        return null;
    }

    @Override
    public void requestForMoreData() {

    }

    @Override
    public RecyclerViewAdapter createAndReturnRVAdapter() {
        return new RecyclerViewAdapter<Collection, NEditableCollectionViewHolder>(getActivity(), R.layout.collection_editable) {
            @Override
            protected NEditableCollectionViewHolder getProperViewHolder(View v, int viewType) {
                return new NEditableCollectionViewHolder(v, BaseEvent.ReceiverName.CollectionListFragment);
            }
        };
    }

    @Override
    protected boolean sendsRequestAutomatically() {
        return false;
    }

    @Override
    protected void requestInitialData() {

    }

    @Override
    public boolean mustShowProgressView() {
        return false;
    }

    @Override
    public int getLayoutID() {
        return R.layout.recycler_view_full;
    }
}
