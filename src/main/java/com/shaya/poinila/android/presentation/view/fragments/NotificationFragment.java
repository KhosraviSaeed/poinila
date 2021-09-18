package com.shaya.poinila.android.presentation.view.fragments;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.NotificationAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.uievent.MemberClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.NotifActorClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.NotifParticipantClickedUIEvent;
import com.shaya.poinila.android.presentation.view.activity.InvitationNotifListActivity;
import com.shaya.poinila.android.util.NavigationUtils;
import com.squareup.otto.Subscribe;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindInt;
import butterknife.OnClick;
import data.PoinilaNetService;
import data.event.AbstractNotificationsReceivedEvent;
import data.event.AnswerFriendRequestResponse;
import data.event.BaseEvent;
import data.event.MyFriendshipRequestsEvent;
import data.model.FriendRequestAnswer;
import data.model.ImageUrls;
import data.model.InvitationNotif;
import data.model.Loading;
import data.model.Member;
import data.model.Notification;
import data.model.Participant;
import data.model.Post;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setFont;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setImage;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;

public class NotificationFragment extends ListBusFragment {

    @BindInt(R.integer.friendship_invitations_summary_limit)
    int INVITATION_LIMIT;
    private static final int INVITE_PER_REQUEST = 10;
    @Bind(R.id.my_notifs)
    TextView mMyNotificationsBtn;
    @Bind(R.id.others_notifs)
    TextView mOthersNotificationsBtn;
    @Bind(R.id.invitations_notifications)
    ViewGroup mInvitationsNotificationsContainer;
    @Bind(R.id.invitations_header)
    TextView mInvitationHeaderView;
    @Bind(R.id.container)
    ViewGroup rootView;

    private String myNotifsBookmark, othersNotifBookmark, acceptedFriendshipBookmark, myFriendshipRequessBookmark;
    List<Notification> myNotifs;
    List<Notification> othersNotifs;
    List<InvitationNotif> mInviteNotifs;


    private int state;
    private static final int STATE_NOTHING_SELECTED = 1;
    private static final int STATE_MY_NOTIFS = 2;
    private static final int STATE_OTHERS_NOTIF = 3;
    private int partsReceived;


    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance(){
        NotificationFragment fragment = new NotificationFragment();

        return fragment;
    }


    @Override
    public int getLayoutID() {
        return R.layout.fragment_notification;
    }

    @Override
    protected void initUI() {
        /*getChildFragmentManager().beginTransaction().add(R.actorID.inner_container,
                new MyNotificationsListFragment(),
                TAG_MY_NOTIFICATION_FRAGMENT).commit();*/
        mRecyclerView = new RecyclerViewProvider(mRecyclerView).
                setLinearLayoutManager(VERTICAL).
                setAdapter(getRecyclerViewAdapter()).
                bindViewToAdapter();
//        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
//                .marginResId(R.dimen.margin_lvl1)
//                .build());

        myNotifs = new ArrayList<>();
        othersNotifs = new ArrayList<>();
        mInviteNotifs = new ArrayList<>();

        switchToMyNotifications();
    }

    public void switchToMyNotifications() {
        mInvitationsNotificationsContainer.setVisibility(mInviteNotifs.isEmpty() ? View.GONE : View.VISIBLE);
        mInvitationHeaderView.setVisibility(mInviteNotifs.isEmpty() ? View.GONE : View.VISIBLE);

        setFont(mMyNotificationsBtn, getString(R.string.default_bold_font_path));
        setFont(mOthersNotificationsBtn, getString(R.string.default_font_path));

        mMyNotificationsBtn.setSelected(true);
        mOthersNotificationsBtn.setSelected(false);

        mMyNotificationsBtn.setTextColor(getResources().getColor(R.color.white));
        mOthersNotificationsBtn.setTextColor(getResources().getColor(R.color.poinila_dark_gray));

//        mMyNotificationsBtn.setTextSize(getResources().getDimension(R.dimen.fontsize_medium));
//        mOthersNotificationsBtn.setTextSize(getResources().getDimension(R.dimen.fontsize_small));

        getRecyclerViewAdapter().resetData(myNotifs);
        state = STATE_MY_NOTIFS;
    }

   /* private void updateAdapter(List list) {
        mAdapter.resetData(list);
    }

    private List sortMyNotifs(List<Notification> myNotifs) {
        return myNotifs;
    }*/

    public void switchToOthersNotifications() {
        mInvitationsNotificationsContainer.setVisibility(View.GONE);
        mInvitationHeaderView.setVisibility(View.GONE);

        setFont(mMyNotificationsBtn, getString(R.string.default_font_path));
        setFont(mOthersNotificationsBtn, getString(R.string.default_bold_font_path));

        mMyNotificationsBtn.setSelected(false);
        mOthersNotificationsBtn.setSelected(true);

        mMyNotificationsBtn.setTextColor(getResources().getColor(R.color.poinila_dark_gray));
        mOthersNotificationsBtn.setTextColor(getResources().getColor(R.color.white));

//        mMyNotificationsBtn.setTextSize(getResources().getDimension(R.dimen.fontsize_small));
//        mOthersNotificationsBtn.setTextSize(getResources().getDimension(R.dimen.fontsize_medium));

        getRecyclerViewAdapter().resetData(othersNotifs);
        //updateAdapter(sortNotifs(othersNotif));
        state = STATE_OTHERS_NOTIF;
    }

    private List sortNotifs(List<Notification> othersNotif) {
        // TODO:
        return othersNotif;
    }

    @OnClick(R.id.my_notifs)
    public void onMyNotifications() {
        switchToMyNotifications();
    }

    @OnClick(R.id.others_notifs)
    public void onOthersNotifications() {
        switchToOthersNotifications();
    }

    @Override
    protected RecyclerView.OnScrollListener getRecyclerViewListener() {
        return RecyclerViewProvider.linearListEndDetectorListener(getRecyclerViewAdapter(), this);
    }

    @Override
    public void onLoadMore() {
        if (!isResumed())
            return;
        super.onLoadMore();
    }

    @Override
    public void initData() {
        super.initData();
        myNotifs.clear();
    }

    @Override
    public RecyclerViewAdapter<Notification, ?> createAndReturnRVAdapter() {
        return new NotificationAdapter(getActivity());
    }

    @Override
    protected boolean sendsRequestAutomatically() {
        return true;
    }

    @Override
    protected void requestInitialData() {
        PoinilaNetService.getMyFriendshipRequests(null);
        PoinilaNetService.getMyNotifications(myNotifsBookmark);
        PoinilaNetService.getOthersNotification(othersNotifBookmark);
    }

    @Override
    public void onSuccessfulInitData(BaseEvent baseEvent) {
        super.onSuccessfulInitData(baseEvent);
        mInviteNotifs = ((MyFriendshipRequestsEvent) baseEvent).data;
        updateInviteNotifs();
    }

    @Override
    protected boolean isListDataResponseValid(BaseEvent baseEvent, String responseBookmark) {
        if (baseEvent instanceof AbstractNotificationsReceivedEvent.MyNotificationsReceivedEvent) {
            return checkBookMark(myNotifsBookmark, responseBookmark);
        } else if (baseEvent instanceof AbstractNotificationsReceivedEvent.OthersNotificationsReceivedEvent) {
            return checkBookMark(othersNotifBookmark, responseBookmark);
        }
        return false;
    }

    @Override
    public boolean mustShowProgressView() {
        return true;
    }

    @Override
    public void requestForMoreData() {
        switch (state) {
            case STATE_MY_NOTIFS:
                PoinilaNetService.getMyNotifications(myNotifsBookmark);
                break;
            case STATE_OTHERS_NOTIF:
                PoinilaNetService.getOthersNotification(othersNotifBookmark);
                break;
        }
    }

    @Override
    public void onSuccessfulListData(BaseEvent baseEvent, String newBookmark) {
        super.onSuccessfulListData(baseEvent, newBookmark); // bookmark is useless in this page.
        AbstractNotificationsReceivedEvent event = ((AbstractNotificationsReceivedEvent) baseEvent);

        if (baseEvent instanceof AbstractNotificationsReceivedEvent.MyNotificationsReceivedEvent) {
            myNotifs.addAll(event.data);
            myNotifsBookmark = event.bookmark;
            if (state == STATE_MY_NOTIFS) getRecyclerViewAdapter().addItems(event.data);
        } else if (baseEvent instanceof AbstractNotificationsReceivedEvent.OthersNotificationsReceivedEvent) {
            othersNotifs.addAll(event.data);
            othersNotifBookmark = event.bookmark;
            if (state == STATE_OTHERS_NOTIF) getRecyclerViewAdapter().addItems(event.data);
        }

        if(getRecyclerViewAdapter().getItemCount() >= 25){
            setLoading(new Loading());
        }


        // myNotifs/othersNotifs has already set as adapter's data source. so by adding to adapters item, we
        // update myNotifs as well
        //getRecyclerViewAdapter().notifyDataSetChanged();
    }

    @Subscribe
    public void onMyNotifsReceived(AbstractNotificationsReceivedEvent.MyNotificationsReceivedEvent event) {
        onGettingListDataResponse(event, event.bookmark);
    }

    @Subscribe
    public void onOthersNotifsReceived(AbstractNotificationsReceivedEvent.OthersNotificationsReceivedEvent event) {
        onGettingListDataResponse(event, event.bookmark);
    }

    @Subscribe
    public void onMyFriendshipRequestsReceived(MyFriendshipRequestsEvent event) {
        onGettingInitDataResponse(event);
//        partOfInitDataReceived();
    }

//    @Subscribe
//    public void onProfilePicClickedEvent(MemberClickedUIEvent event) {
//        if (event.receiverName != BaseEvent.ReceiverName.NotificationFragment) return;
//        Member member =((Notification)getRecyclerViewAdapter().getItem(event.adapterPosition)).mainActor.userName;
//        PageChanger.goToProfile(getActivity(), member);
//    }
    /*private void partOfInitDataReceived() {
        partsReceived++;
        if (partsReceived >= 3){
            onGettingInitDataResponse();
        }
    }*/

    private void updateInviteNotifs() {
        mInvitationsNotificationsContainer.removeAllViews();
        if (mInviteNotifs.isEmpty()) {
            mInvitationsNotificationsContainer.setVisibility(View.GONE);
            mInvitationHeaderView.setVisibility(View.GONE);
            return;
        }

        mInvitationsNotificationsContainer.setVisibility(View.VISIBLE);
        mInvitationHeaderView.setVisibility(View.VISIBLE);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        for (int i = 0; i < INVITATION_LIMIT && i < mInviteNotifs.size(); i++) {
            addInviteNotif(inflater, mInviteNotifs.get(i));
        }
        updateNotifCountText();
    }

    private void updateNotifCountText() {
        String res;
        int count = mInviteNotifs.size();
        if (count < INVITATION_LIMIT)
            res = getString(R.string.view_all_invitations);
        else if (count < INVITE_PER_REQUEST)
            res = getString(R.string.view_all_invitations).concat(String.format(" (%d)", count));
        else
            res = getString(R.string.view_all_invitations).concat(String.format(" (%d+)", INVITE_PER_REQUEST));
        mInvitationHeaderView.setText(res);
    }

    @OnClick(R.id.invitations_header)
    public void viewAllInvitations() {
        NavigationUtils.goToActivity(InvitationNotifListActivity.class, getActivity());
    }

    private void addInviteNotif(LayoutInflater inflater, final InvitationNotif notif) {
        final View inviteNotifView = inflater.inflate(R.layout.notif_requested_tobe_your_friend,
                mInvitationsNotificationsContainer, false);
        setImage((ImageView) inviteNotifView.findViewById(R.id.image), notif.member.imageUrls, ImageUrls.ImageType.MEMBER, ImageUrls.ImageSize.AVATAR);
        setText((TextView) inviteNotifView.findViewById(R.id.title), notif.member.fullName);
        setText((TextView) inviteNotifView.findViewById(R.id.subtitle), getString(R.string.requested_to_be_your_friend));

        mInvitationsNotificationsContainer.addView(inviteNotifView);

        /*--Accept--*/
        inviteNotifView.findViewById(R.id.agree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PoinilaNetService.answerFriendRequest(notif.member.id, FriendRequestAnswer.ACCEPT, -1);
                clickedNotif = notif;
            }
        });
        /*--Reject--*/
        inviteNotifView.findViewById(R.id.ignore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PoinilaNetService.answerFriendRequest(notif.member.id, FriendRequestAnswer.REJECT, -1);
                clickedNotif = notif;
            }
        });
        inviteNotifView.findViewById(R.id.image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageChanger.goToProfile(getActivity(), notif.member.getId());
            }
        });
    }

    private InvitationNotif clickedNotif;

    @Subscribe
    public void onAnswerFriendRequestResponse(AnswerFriendRequestResponse event) {
        if (event.succeed) {
            mInviteNotifs.remove(clickedNotif);
            // TODO: on accepting as a friend, chanage item view to "you and felani are now friends"
            updateInviteNotifs();
        }
    }

    @Subscribe
    public void onMainActorClicked(NotifActorClickedUIEvent event) {
        Notification notification = ((Notification)getRecyclerViewAdapter().getItem(event.adapterPosition));
        Participant participant = notification.mainActor;
        if (participant == null)
            return;
        goToPage(participant, participant.type);
    }

    @Subscribe
    public void onParticipantActorClicked(NotifParticipantClickedUIEvent event) {
        goToPage(event.participant, event.participantsType);
    }

    private void goToPage(Participant participant, ImageUrls.ImageType type) {
        switch (type) {
            case MEMBER:
                PageChanger.goToProfile(getActivity(), participant.getId());
                break;
            case COLLECTION:
                PageChanger.goToCollection(getActivity(), participant.getId(), participant.collectionName, null);
                break;
            case POST:
                PageChanger.goToPost(getActivity(), participant.getId());
                break;
        }
    }
}
