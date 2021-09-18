package com.shaya.poinila.android.presentation.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.view.LoaderList;

import butterknife.Bind;
import data.event.BaseEvent;
import data.model.Loading;

/**
 * Created by iran on 2015-08-06.
 */
public abstract class ListBusFragment<T> extends BusFragment implements LoaderList{
    private static final String CLICKED_ITEM_POSITION = "clicked item position";
    protected boolean requestingIsLocked = false;
    public String bookmark;
    protected int clickedItemPosition = -1;
    @Bind(R.id.recycler_view) protected RecyclerView mRecyclerView;
    private RecyclerViewAdapter<T, ?> mAdapter;
    protected boolean hasLoading = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            clickedItemPosition = savedInstanceState.getInt(CLICKED_ITEM_POSITION);
    }

    @Override
    protected void initUI() {
        mRecyclerView.removeAllViews(); // clear view after rotation and other forms of activity recreation
        // but may be it's better to just call notifyDataSetChange on related adapter
        if(getRecyclerViewListener() != null)
            mRecyclerView.addOnScrollListener(getRecyclerViewListener());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CLICKED_ITEM_POSITION, clickedItemPosition);
    }

    /**
     * Called in {@link #onStart()} after {@link #initUI()} so it's safe to assume class variables
     * are initialized.
     * @return
     */
    protected abstract RecyclerView.OnScrollListener getRecyclerViewListener();

    public void onLoadMore(){
        if (!requestingIsLocked) {
            requestingIsLocked = true;
            requestForMoreData();
        }
    }

    public void onSuccessfulListData(BaseEvent baseEvent, String newBookmark){
        requestingIsLocked = false;
        bookmark = newBookmark;
    }

    public void onEndListData(){
        removeLoading();
    }

    public void setLoading(T loading){
        if(!hasLoading || !(getRecyclerViewAdapter().getItem(getRecyclerViewAdapter().getItemCount() - 1) instanceof Loading)) {
            hasLoading = true;
            getRecyclerViewAdapter().setLoading(loading);
        }
    }

    public void removeLoading(){
        if(hasLoading) {
            hasLoading = false;
            getRecyclerViewAdapter().removeLoading();
        }
    }

    public abstract void requestForMoreData();

    @Override
    public ViewGroup getLoadableView() {
        return mRecyclerView;
    }

    public RecyclerViewAdapter<T, ?> getRecyclerViewAdapter(){
        if (mAdapter == null)
            mAdapter = createAndReturnRVAdapter();
        return mAdapter;
    }

    protected void onGettingListDataResponse(BaseEvent event, String responseBookmark) {
        if (isListDataResponseValid(event, responseBookmark))
            onSuccessfulListData(event, responseBookmark);
        else
            onEndListData();
    }

    protected boolean isListDataResponseValid(BaseEvent baseEvent, String responseBookmark){
        // bookmark != null &&
        return checkBookMark(this.bookmark, responseBookmark); // this.bookmark may be null
    }

    public boolean checkBookMark(String pageBookmark, String serverBookmark) {
        return serverBookmark == null || !serverBookmark.equals(pageBookmark);
    }

    public abstract RecyclerViewAdapter<T, ?> createAndReturnRVAdapter();
}

