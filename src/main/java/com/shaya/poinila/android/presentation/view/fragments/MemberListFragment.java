package com.shaya.poinila.android.presentation.view.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.uievent.FriendCirclesUpdated;
import com.shaya.poinila.android.presentation.uievent.FriendshipClickEvent;
import com.shaya.poinila.android.presentation.uievent.MemberClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.PositiveButtonClickedUIEvent;
import com.shaya.poinila.android.presentation.view.dialog.DialogLauncher;
import com.shaya.poinila.android.presentation.viewholder.MemberViewHolder;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.Logger;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import data.PoinilaNetService;
import data.event.BaseEvent;
import data.event.MembersReceivedEvent;
import data.model.FriendshipStatus;
import data.model.Member;
import manager.DBFacade;
import manager.DataRepository;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_ENTITY;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_ITEM_COUNT;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_REQUEST_ID;

public class MemberListFragment extends ListBusFragment<Member> {

    private int requestID;
    private String mainActorID;
    @Nullable @Bind(R.id.item_count) TextView mItemCountView;

    public MemberListFragment() {
        // Required empty public constructor
    }


    @Override
    public int getLayoutID() {
        if (requestID == ConstantsUtils.REQUEST_POST_LIKERS)
            return R.layout.fragment_favorites;
        return R.layout.recycler_view_full;
    }


    @Override
    protected void initUI() {
        super.initUI();
        mRecyclerView = new RecyclerViewProvider(mRecyclerView).
                setAdapter(getRecyclerViewAdapter()).
                setLinearLayoutManager(VERTICAL).
                bindViewToAdapter();
        //mRecyclerView.setHasFixedSize(true);

        switch (requestID){
            case ConstantsUtils.REQUEST_MEMBER_FOLLOWERS:
                getActivity().setTitle(R.string.title_activity_member_followers);
                break;
            case ConstantsUtils.REQUEST_MEMBER_FRIENDS:
                getActivity().setTitle(R.string.title_activity_member_friends);
                break;
            case ConstantsUtils.REQUEST_POST_LIKERS:
                setText(mItemCountView, getString(R.string.favorites_formatted,
                        getActivity().getIntent().getIntExtra(KEY_ITEM_COUNT, 0)));
                getActivity().setTitle(R.string.title_activity_post_likers);
                break;
        }
    }

    @Override
    protected RecyclerView.OnScrollListener getRecyclerViewListener() {
        return RecyclerViewProvider.linearListEndDetectorListener(getRecyclerViewAdapter(), this);
    }

    @Subscribe public void onGotoProfile(MemberClickedUIEvent event){
        Member member = getRecyclerViewAdapter().getItem(event.adapterPosition);
        PageChanger.goToProfile(getActivity(), member);
    }

    @Subscribe public void onShowFriendShipDialog(FriendshipClickEvent event){
        if (DataRepository.isUserAnonymous()){
            Logger.toastError(R.string.error_guest_action);
            return;
        }

        Member member = getRecyclerViewAdapter().getItem(event.adapterPosition);
        DialogLauncher.launchFriendshipDialog(member, getFragmentManager());
        clickedItemPosition = event.adapterPosition;
    }

    @Subscribe public void onMembersReceived(MembersReceivedEvent event){
        onGettingInitDataResponse(event);
        onGettingListDataResponse(event, event.bookmark);
    }

    @Subscribe
    public void onPositiveDialogButton(PositiveButtonClickedUIEvent event) {
        Member member = getRecyclerViewAdapter().getItem(clickedItemPosition);
        switch (member.friendshipStatus){
            case NotFriend:
            case WaitingForAction: // sending request
                PoinilaNetService.friendRequest(member.getId(), DBFacade.getDefaultCircle().id);
                member.friendshipStatus = FriendshipStatus.Pending;
                break;
            // we handle this case in EditFriendshipDialog
            /*case IsFriend: // removing friend
                PoinilaNetService.removeFriend(member.getId());
                member.friendshipStatus = FriendshipStatus.NotFriend;
                break;*/
            case Pending: // no action yet
                break;
        }
        getRecyclerViewAdapter().notifyItemChanged(clickedItemPosition);
    }

    /*@Subscribe public void onNeutralDialogButton(NeutralDialogButtonClickedUIEvent event){
        DialogLauncher.launchChangeFriendCircle(
                getChildFragmentManager(),
                getRecyclerViewAdapter().getItem(clickedItemPosition));
    }*/

    public static android.support.v4.app.Fragment newInstance(String id, int requestID) {
        MemberListFragment f = new MemberListFragment();
        Bundle b = new Bundle();
        b.putString(KEY_ENTITY, id);
        b.putInt(KEY_REQUEST_ID, requestID);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActorID = getArguments().getString(KEY_ENTITY);
        requestID = getArguments().getInt(KEY_REQUEST_ID);
    }

    @Subscribe public void onFriendCirclesUpdated(FriendCirclesUpdated event){
        int index = getRecyclerViewAdapter().getItems().indexOf(event.member);
        if (index < 0) return;
        getRecyclerViewAdapter().getItem(index).circle_ids = event.selectedCirclesIDs;
    }
/*---------------*/
    @Override
    public void requestForMoreData() {
        switch (requestID){
            case ConstantsUtils.REQUEST_MEMBER_FOLLOWERS:
                PoinilaNetService.getMemberFollowers(mainActorID, bookmark);
                break;
            case ConstantsUtils.REQUEST_MEMBER_FRIENDS:
                PoinilaNetService.getMemberFriends(mainActorID, bookmark);
                break;
            case ConstantsUtils.REQUEST_POST_LIKERS:
                PoinilaNetService.getPostLikers(mainActorID, bookmark);
                break;
        }
    }

    @Override
    public RecyclerViewAdapter<Member, MemberViewHolder> createAndReturnRVAdapter() {
        return new RecyclerViewAdapter<Member, MemberViewHolder>(getActivity(), R.layout.member_inlist) {
            @Override
            protected MemberViewHolder getProperViewHolder(View v, int viewType) {
                return new MemberViewHolder(v, BaseEvent.ReceiverName.MemberListFragment);
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
    public void onSuccessfulListData(BaseEvent baseEvent, String newBookmark) {
        super.onSuccessfulListData(baseEvent, newBookmark);
        getRecyclerViewAdapter().addItems(((MembersReceivedEvent) baseEvent).members);
    }

}
