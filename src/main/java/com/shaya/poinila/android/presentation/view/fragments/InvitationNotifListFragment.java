package com.shaya.poinila.android.presentation.view.fragments;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.uievent.AnswerFriendshipUIEvent;
import com.shaya.poinila.android.presentation.uievent.NotifActorClickedUIEvent;
import com.shaya.poinila.android.presentation.viewholder.InviteNotifViewHolder;
import com.squareup.otto.Subscribe;

import data.PoinilaNetService;
import data.event.AnswerFriendRequestResponse;
import data.event.BaseEvent;
import data.event.MyFriendshipRequestsEvent;
import data.model.InvitationNotif;
import data.model.Member;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by iran on 2015-09-29.
 */
public class InvitationNotifListFragment extends ListBusFragment<InvitationNotif> {

    @Override
    protected RecyclerView.OnScrollListener getRecyclerViewListener() {
        return RecyclerViewProvider.linearListEndDetectorListener(getRecyclerViewAdapter(), this);
    }

    @Override
    public int getLayoutID() {
        return R.layout.recycler_view_full;
    }

    @Override
    protected void initUI() {
        super.initUI();
        mRecyclerView = new RecyclerViewProvider(mRecyclerView).
                setAdapter(getRecyclerViewAdapter()).
                setLinearLayoutManager(VERTICAL).bindViewToAdapter();
    }

    public static android.support.v4.app.Fragment newInstance() {
        return new InvitationNotifListFragment();
    }

    @Subscribe public void onNotifsReceived(MyFriendshipRequestsEvent event){
        onGettingInitDataResponse(event);
        onGettingListDataResponse(event, event.bookmark);
    }

    @Override
    public void onSuccessfulListData(BaseEvent baseEvent, String newBookmark) {
        super.onSuccessfulListData(baseEvent, newBookmark);
        getRecyclerViewAdapter().addItems(((MyFriendshipRequestsEvent) baseEvent).data);
    }

    @Override
    public void onEndListData() {

    }

    @Subscribe public void onAnswerFriendRequest(AnswerFriendshipUIEvent event){
        PoinilaNetService.answerFriendRequest(
                getRecyclerViewAdapter().getItem(event.adapterPosition).member.id, event.accept, -1);
        //ViewUtils.removeView(mInvitationsNotificationsContainer, event.adapterPosition);
        clickedItemPosition = event.adapterPosition;
    }

    @Subscribe public void onAnswerFriendRequestResponse(AnswerFriendRequestResponse event){
        if (event.succeed){
            getRecyclerViewAdapter().removeItem(clickedItemPosition);
            // TODO: on accepting as a friend, chanage item view to "you and felani are now friends"
        }
    }

    @Subscribe public void onMainActorClicked(NotifActorClickedUIEvent event){
        Member member = getRecyclerViewAdapter().getItem(event.adapterPosition).member;
        if (member == null) return; // invitations accepted friendship has got no main actor.
        PageChanger.goToProfile(getActivity(), member.getId());
    }

//---------------
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
        PoinilaNetService.getMyFriendshipRequests(bookmark);
    }

    @Override
    public RecyclerViewAdapter<InvitationNotif, InviteNotifViewHolder> createAndReturnRVAdapter() {
        return new RecyclerViewAdapter<InvitationNotif, InviteNotifViewHolder>(getActivity(), R.layout.notif_requested_tobe_your_friend) {
            @Override
            protected InviteNotifViewHolder getProperViewHolder(View v, int viewType) {
                return new InviteNotifViewHolder(v);
            }
        };
    }
}
