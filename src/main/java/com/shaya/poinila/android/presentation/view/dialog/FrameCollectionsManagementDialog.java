package com.shaya.poinila.android.presentation.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.uievent.CollectionFrameToggledEvent;
import com.shaya.poinila.android.presentation.viewholder.FrameCollectionViewHolder;
import com.squareup.otto.Subscribe;

import java.util.List;

import data.PoinilaNetService;
import data.event.BaseEvent;
import data.event.CollectionsReceivedEvent;
import data.model.Collection;
import manager.DataRepository;

/**
 * Created by iran on 2015-07-28.
 */
public class FrameCollectionsManagementDialog extends ListBusDialogFragment<Collection>{
    private static final java.lang.String KEY_FRAME_ID = "frame id";
    private  String frameID;

    public static FrameCollectionsManagementDialog newInstance(String frameId) {
        Bundle args = new Bundle();
        FrameCollectionsManagementDialog fragment = new FrameCollectionsManagementDialog();
        fragment.frameID = frameId;
        fragment.saveStateToBundle(args);
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe
    public void onToggle(CollectionFrameToggledEvent event){
        Collection collection = getRecyclerViewAdapter().getItem(event.adapterPosition);
        collection.selected ^= true;
        if (collection.selected) {
            PoinilaNetService.addCollectionToFrame(frameID, collection.getId());
            // TODO: request for removing this collectionSpinner from frame
        }else {
            PoinilaNetService.removeCollectionFromFrame(frameID, collection.getId());
        }
        getRecyclerViewAdapter().notifyItemChanged(event.adapterPosition);
    }

    @Override
    protected void initUI(Context context) {
        super.initUI(context);
        mRecyclerView = new RecyclerViewProvider(mRecyclerView).
                setLinearLayoutManager(LinearLayoutManager.VERTICAL).
                setAdapter(getRecyclerViewAdapter()).bindViewToAdapter();
    }

    @Override
    protected RecyclerView.OnScrollListener getRecyclerViewListener() {
        return RecyclerViewProvider.linearListEndDetectorListener(getRecyclerViewAdapter(), this);
    }

    @Override
    protected boolean sendsRequestAutomatically() {
        return true;
    }

    @Override
    protected void requestInitialData() {
        requestForMoreData();
    }

    @Override
    public boolean mustShowProgressView() {
        return true;
    }

    @Override
    public void requestForMoreData() {
        DataRepository.getInstance().getMyFollowedCollections(null, bookmark);
    }

    @Override
    public RecyclerViewAdapter<Collection, ?> createAndReturnRVAdapter() {
        return new RecyclerViewAdapter<Collection, FrameCollectionViewHolder>(
                getActivity(), R.layout.rounded_image_title_subtitle_icon) {
            @Override
            protected FrameCollectionViewHolder getProperViewHolder(View v, int viewType) {
                return new FrameCollectionViewHolder(v);
            }
        };
    }

    @Subscribe public void OnCollectionsReceived(CollectionsReceivedEvent event){
        onGettingInitDataResponse(event);
        onGettingListDataResponse(event, event.bookmark);
    }

    @Override
    public void onSuccessfulListData(BaseEvent baseEvent, String newBookmark) {
        super.onSuccessfulListData(baseEvent, newBookmark);
        List<Collection> collections = ((CollectionsReceivedEvent) baseEvent).collections;
        for (Collection collection : collections) {
            collection.selected = collection.frameIDs != null && collection.frameIDs.contains(Integer.parseInt(frameID));
        }
        getRecyclerViewAdapter().addItems(collections);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.recycler_view_weighted_full;
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {
        super.loadStateFromBundle(savedInstanceState);
        frameID = savedInstanceState.getString(KEY_FRAME_ID);
    }

    @Override
    protected void saveStateToBundle(Bundle outState) {
        super.saveStateToBundle(outState);
        outState.putString(KEY_FRAME_ID, frameID);
    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(R.string.frame_members_management, RESOURCE_NONE, R.string.finish, RESOURCE_NONE, RESOURCE_NONE);
    }
}
