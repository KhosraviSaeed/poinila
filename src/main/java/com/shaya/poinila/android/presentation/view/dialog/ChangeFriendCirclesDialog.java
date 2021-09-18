package com.shaya.poinila.android.presentation.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.uievent.CheckBoxClickUIEvent;
import com.shaya.poinila.android.presentation.uievent.FriendCirclesUpdated;
import com.shaya.poinila.android.presentation.viewholder.CheckedCircleViewHolder;
import com.shaya.poinila.android.util.BusProvider;
import com.squareup.otto.Subscribe;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import data.PoinilaNetService;
import data.model.Circle;
import data.model.Member;
import manager.DBFacade;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by iran on 2015-08-19.
 */
public class ChangeFriendCirclesDialog extends ListBusDialogFragment<Circle> {
    private static final String KEY_FRIEND = "friend";

    private Member friend;

    @Override
    public int getLayoutResId() {
        return R.layout.recycler_view_full;
    }

    public static ChangeFriendCirclesDialog newInstance(Member friend) {
        Bundle args = new Bundle();
        ChangeFriendCirclesDialog fragment = new ChangeFriendCirclesDialog();
        fragment.friend = friend;
        fragment.saveStateToBundle(args);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void initUI(final Context context) {
        super.initUI(context);
        mRecyclerView = new RecyclerViewProvider(mRecyclerView).
                setLinearLayoutManager(VERTICAL).
                setAdapter(getRecyclerViewAdapter()).
                bindViewToAdapter();
        if (friend!= null)
            fill();
    }

    private void selectFriendCurrentCircles(List<Circle> allCircles, List<Integer> friendCircleIDS) {
        if (friendCircleIDS == null)
            return;
        for (Circle circle : allCircles) {
            if (friendCircleIDS.contains(circle.id)){
                circle.selected = true;
            }
        }
    }

    @Subscribe public void onCircleSelected(CheckBoxClickUIEvent event){
        getRecyclerViewAdapter().getItem(event.adapterPosition).selected = event.checked;
    }

    @Override
    public void onPositiveButton() {
        List<Integer> selectedCirclesIDs = new ArrayList<>();
        for (Object circle : getRecyclerViewAdapter().getItems()) {
            if (((Circle)circle).selected)
                selectedCirclesIDs.add(((Circle)circle).id);
        }
        PoinilaNetService.changeFriendCircle(selectedCirclesIDs, friend.id);
        BusProvider.getBus().post(new FriendCirclesUpdated(selectedCirclesIDs, friend));
        super.onPositiveButton();
    }

/*    @Subscribe public void onMemberReceived(MemberReceivedEvent event){
        this.friend = event.member;
        fill();
    }*/

    private void fill(){
        List<Circle> allCircles = DBFacade.getMyCircles();
        selectFriendCurrentCircles(allCircles, friend.circle_ids);
        getRecyclerViewAdapter().resetData(allCircles);
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {
        super.loadStateFromBundle(savedInstanceState);
        friend = Parcels.unwrap(savedInstanceState.getParcelable(KEY_FRIEND));
    }

    @Override
    protected void saveStateToBundle(Bundle outState) {
        super.saveStateToBundle(outState);
        outState.putParcelable(KEY_FRIEND, Parcels.wrap(friend));
    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(R.string.select_circle, RESOURCE_NONE, R.string.finish, RESOURCE_NONE, RESOURCE_NONE);
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
    protected RecyclerView.OnScrollListener getRecyclerViewListener() {
        return RecyclerViewProvider.linearListEndDetectorListener(getRecyclerViewAdapter(), this);
    }

    @Override
    public void requestForMoreData() {

    }

    @Override
    public RecyclerViewAdapter<Circle, CheckedCircleViewHolder> createAndReturnRVAdapter() {
        return new RecyclerViewAdapter<Circle, CheckedCircleViewHolder>(getActivity(), R.layout.checked_text) {
            @Override
            protected CheckedCircleViewHolder getProperViewHolder(View v, int viewType) {
                return new CheckedCircleViewHolder(v);
            }
        };
    }
}
