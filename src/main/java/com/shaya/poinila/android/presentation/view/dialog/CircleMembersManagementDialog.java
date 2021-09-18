package com.shaya.poinila.android.presentation.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.uievent.MemberCircleToggledEvent;
import com.shaya.poinila.android.presentation.viewholder.CircleMemberViewHolder;
import com.squareup.otto.Subscribe;

import java.util.List;

import data.PoinilaNetService;
import data.event.BaseEvent;
import data.event.MembersReceivedEvent;
import data.model.Member;
import manager.DataRepository;

/**
 * Created by iran on 2015-07-26.
 */
public class CircleMembersManagementDialog extends ListBusDialogFragment<Member>{

    private static final String KEY_CIRCLE_ID = "circle id";
    private String circleID;

    public static CircleMembersManagementDialog newInstance(String circleID) {
        Bundle args = new Bundle();
        CircleMembersManagementDialog fragment = new CircleMembersManagementDialog();
        fragment.circleID = circleID;
        fragment.saveStateToBundle(args);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {
        super.loadStateFromBundle(savedInstanceState);
        circleID = savedInstanceState.getString(KEY_CIRCLE_ID, null);
    }

    @Override
    protected void saveStateToBundle(Bundle outState) {
        super.saveStateToBundle(outState);
        outState.putString(KEY_CIRCLE_ID, circleID);
    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(R.string.circle_members_management, RESOURCE_NONE, R.string.finish, RESOURCE_NONE, RESOURCE_NONE);
    }

    @Subscribe
    public void onToggle(MemberCircleToggledEvent event){
        Member member = getRecyclerViewAdapter().getItem(event.adapterPosition);
        member.selected ^= true;
        if (member.selected) {
            PoinilaNetService.addFriendToCircle(circleID, member.getId());
            // TODO: request for removing this collectionSpinner from frame
        }else {
            PoinilaNetService.removeFriendFromCircle(circleID, member.getId());
        }
        getRecyclerViewAdapter().notifyItemChanged(event.adapterPosition);
    }

    @Override
    protected void initUI(Context context) {
        super.initUI(context);
        mRecyclerView = new RecyclerViewProvider(mRecyclerView).
                setLinearLayoutManager(LinearLayoutManager.VERTICAL).
                setAdapter(getRecyclerViewAdapter()).
                bindViewToAdapter();
    }

    @Override
    protected RecyclerView.OnScrollListener getRecyclerViewListener() {
        return RecyclerViewProvider.linearListEndDetectorListener(getRecyclerViewAdapter(), this);
    }

    @Override
    public RecyclerViewAdapter<Member, CircleMemberViewHolder> createAndReturnRVAdapter() {
        return new RecyclerViewAdapter<Member, CircleMemberViewHolder>(getActivity(), R.layout.member_inlist) {
            @Override
            protected CircleMemberViewHolder getProperViewHolder(View v, int viewType) {
                return new CircleMemberViewHolder(v);
            }
        };
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
        DataRepository.getInstance().getMemberFriends(DataRepository.getInstance().getMyId(), bookmark);
    }

    @Subscribe public void OnFriendsReceived(MembersReceivedEvent event){
        onGettingInitDataResponse(event);
        onGettingListDataResponse(event, event.bookmark);
    }

    @Override
    public void onSuccessfulListData(BaseEvent baseEvent, String newBookmark) {
        super.onSuccessfulListData(baseEvent, newBookmark);
        List<Member> members = ((MembersReceivedEvent) baseEvent).members;
        for (Member member : members) {
            member.selected = member.circle_ids != null && member.circle_ids.contains(Integer.parseInt(circleID));
        }
        getRecyclerViewAdapter().addItems(members);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.recycler_view_weighted_full;
    }
}
