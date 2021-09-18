package com.shaya.poinila.android.presentation.presenter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.shaya.poinila.android.presentation.view.LoaderList;

/**
 * Created by iran on 2015-06-23.
 * @author Alireza Farahani
 */
public class RecyclerViewProvider {
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;

    public RecyclerViewProvider(RecyclerView recyclerView){
        mRecyclerView = recyclerView;
    }

    /*public RecyclerViewProvider setRecyclerView(RecyclerView recyclerView){
        mRecyclerView = recyclerView;
        return this;
    }*/

    /**
     *
     * @param direction use constans from {@link StaggeredGridLayoutManager}
     * @param columnCount get value from xml resources
     */
    public RecyclerViewProvider setStaggeredLayoutManager(int direction, int columnCount){
        mLayoutManager = new StaggeredGridLayoutManager(columnCount, direction);
        //((StaggeredGridLayoutManager) mLayoutManager).setGapStrategy(GAP_HANDLING_NONE);
        return this;
    }

    /**
     *
     * @param direction use constans from {@link GridLayoutManager}
     * @param columnCount get value from xml resources
     * @return
     */
    public RecyclerViewProvider setGridLayoutManager(int direction, int columnCount) {
        mLayoutManager = new GridLayoutManager(null, columnCount, direction, false);
//        mRecyclerView.setHasFixedSize(true);
        return this;
    }

    public RecyclerViewProvider setGridLayoutManager(
            int direction, int columnCount, GridLayoutManager.SpanSizeLookup spanSizeLookup) {
        ((GridLayoutManager)setGridLayoutManager(direction, columnCount).mLayoutManager)
                .setSpanSizeLookup(spanSizeLookup);
        return this;
    }

    /**
     *
     * @param direction use constants from (@link LinearLayoutManager}
     * @return
     */
    public RecyclerViewProvider setLinearLayoutManager(int direction){
        return setLinearLayoutManager(direction, false);
    }

    public RecyclerViewProvider setLinearLayoutManager(int direction, boolean reverse) {
        mLayoutManager = new LinearLayoutManager(null, direction, reverse);
        return this;
    }

    public RecyclerViewProvider setAdapter(RecyclerViewAdapter adapter){
        mAdapter = adapter;
        return this;
    }

    /**
     * Sets layoutmanager and adapter for already given recyclerview and then returns it
     * @return
     */
    public RecyclerView bindViewToAdapter() {
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mAdapter);
            return mRecyclerView;
        }
        else return null;
    }

    public static RecyclerView.OnScrollListener linearListEndDetectorListener(final RecyclerView.Adapter adapter, final LoaderList list) {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                boolean endOfList = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition() ==
                        adapter.getItemCount() - 1;
                if (endOfList && dy != 0){
                    list.onLoadMore();
                }
            }
        };
    }

    public static RecyclerView.OnScrollListener staggeredListEndDetectorListener(final RecyclerView.Adapter adapter, final LoaderList list){
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int[] lastVisibleItems = ((StaggeredGridLayoutManager)recyclerView.getLayoutManager()).
                        findLastVisibleItemPositions(null);
                int itemCount = adapter.getItemCount() - 1;
                if ((lastVisibleItems[0] == itemCount ||
                        lastVisibleItems[1] == itemCount) && dy != 0){
                    list.onLoadMore();
                }
            }
        };
    }

    public static RecyclerView.OnScrollListener gridListEndDetectionListener(final RecyclerView.Adapter adapter, final LoaderList list){
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                boolean endOfList = ((GridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() ==
                        adapter.getItemCount() - 1;
                if (endOfList && dy != 0) {
                    list.onLoadMore();
                }
            }
        };
    }
}
