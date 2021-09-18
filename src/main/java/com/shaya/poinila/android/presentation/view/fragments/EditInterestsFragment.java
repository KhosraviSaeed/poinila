package com.shaya.poinila.android.presentation.view.fragments;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.uievent.RemoveTagEvent;
import com.shaya.poinila.android.presentation.viewholder.RemovableInterestViewHolder;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.ResourceUtils;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.OnClick;
import data.PoinilaNetService;
import data.event.BaseEvent;
import data.event.UserInterestsReceivedEvent;
import data.model.ImageTag;
import data.model.Tag;
import manager.DataRepository;

/**
 * Created by iran on 2015-09-08.
 */
public class EditInterestsFragment extends ListBusFragment<ImageTag> {

    @Bind(R.id.new_interest) View newInterest;
    private String actorID;

    @Override
    protected RecyclerView.OnScrollListener getRecyclerViewListener() {
        return new RecyclerView.OnScrollListener() {};
    }

/*    @Override
    protected RecyclerView.OnScrollListener getRecyclerViewListener() {
        return RecyclerViewProvider.gridListEndDetectionListener(getRecyclerViewAdapter(), this);
    }*/

    @Override
    public int getLayoutID() {
        return R.layout.activity_edit_interests;
    }

    @Override
    protected void initUI() {
        actorID = getArguments().getString(ConstantsUtils.KEY_ENTITY);

        mRecyclerView = new RecyclerViewProvider(mRecyclerView).
                setGridLayoutManager(StaggeredGridLayoutManager.VERTICAL,
                        ResourceUtils.getInteger(R.integer.column_count)).
                setAdapter(getRecyclerViewAdapter()).
                bindViewToAdapter();
    }

    public static android.support.v4.app.Fragment newInstance(String actorID) {
        Bundle b = new Bundle();
        b.putString(ConstantsUtils.KEY_ENTITY, actorID);
        EditInterestsFragment f = new EditInterestsFragment();
        f.setArguments(b);
        return f;
    }

    @OnClick(R.id.new_interest) public void onNewInterest(){
        initDataResponseReceived = false;
        DataRepository.getInstance().putTempModel(getRecyclerViewAdapter().getItems());
        PageChanger.goToSelectInterest(getActivity(), false);
    }

    @Subscribe public void onUserInterestesReceived(UserInterestsReceivedEvent event){
        onGettingInitDataResponse(event);
    }

    @Subscribe public void onDeleteInterest(RemoveTagEvent event){
        Tag tag = getRecyclerViewAdapter().getItem(event.adapterPosition);
        PoinilaNetService.removeInterest(tag);
        getRecyclerViewAdapter().removeItem(event.adapterPosition);
    }



    /*-------------------*/

    @Override
    public void onSuccessfulInitData(BaseEvent baseEvent) {
        super.onSuccessfulInitData(baseEvent);
        getRecyclerViewAdapter().resetData(((UserInterestsReceivedEvent) baseEvent).userInterests);
    }

    @Override
    protected boolean sendsRequestAutomatically() {
        return true;
    }

    @Override
    protected void requestInitialData() {
        PoinilaNetService.getMemberInterests(actorID);
    }

    @Override
    public boolean mustShowProgressView() {
        return true;
    }

    @Override
    public void requestForMoreData() {

    }

    @Override
    public RecyclerViewAdapter<ImageTag, RemovableInterestViewHolder> createAndReturnRVAdapter() {
        return new RecyclerViewAdapter<ImageTag, RemovableInterestViewHolder>(getActivity(), R.layout.removable_imaged_interest) {
            @Override
            protected RemovableInterestViewHolder getProperViewHolder(View v, int viewType) {
                return new RemovableInterestViewHolder(v);
            }
        };
    }//implements LoaderList {

}
