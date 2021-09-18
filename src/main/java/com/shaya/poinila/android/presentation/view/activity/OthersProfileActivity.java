package com.shaya.poinila.android.presentation.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.Space;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.FriendCirclesUpdated;
import com.shaya.poinila.android.presentation.uievent.NeutralDialogButtonClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.PositiveButtonClickedUIEvent;
import com.shaya.poinila.android.presentation.view.ViewInflater;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.presentation.view.dialog.DialogLauncher;
import com.shaya.poinila.android.presentation.view.dialog.PoinilaAlertDialog;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.util.NavigationUtils;
import com.shaya.poinila.android.util.ResourceUtils;
import com.shaya.poinila.android.util.StringUtils;
import com.squareup.otto.Subscribe;

import org.apmem.tools.layouts.FlowLayout;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import data.PoinilaNetService;
import data.event.MemberReceivedEvent;
import data.model.Collection;
import data.model.FriendRequestAnswer;
import data.model.FriendshipStatus;
import data.model.ImageUrls;
import data.model.Member;
import manager.DBFacade;
import manager.DataRepository;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setImage;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_CONTENT_URI;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_ENTITY;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_MEMBER_ID;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_REQUEST_ID;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_MEMBER_COLLECTIONS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_MEMBER_FAVED_POSTS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_MEMBER_FOLLOWED_COLLECTIONS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_MEMBER_FOLLOWERS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_MEMBER_FRIENDS;

public class OthersProfileActivity extends ToolbarActivity {

    @Bind(R.id.profile_general_info)
    ViewGroup profileGeneralInfo;
    @Bind(R.id.followers)
    ViewGroup followersStatViewGroup;
    @Bind(R.id.favorited)
    ViewGroup favoritedStatViewGroup;
    @Bind(R.id.posts)
    ViewGroup postsStatViewGroup;
    @Bind(R.id.friends)
    ViewGroup friendsStatViewGroup;
    @Bind(R.id.owning_collections_container)
    View owningCollections;
    @Bind(R.id.following_collections_container)
    View followingCollections;
    @Bind(R.id.interest_container)
    View interestsContainer;
    @Bind(R.id.blog_info)
    View blogInfo;
    @Bind(R.id.about_me)
    TextView aboutMe;

    // TODO: get member from cache if exist else request the server
    Member member;

    private ImageView friendIcon;
    private String memberID;

    @Override
    protected void handleIntentExtras() {
        memberID = getIntent().getStringExtra(KEY_MEMBER_ID);
        // TODO: wtf?
        /*titleParameter = "";
        //Uri shareUri = getIntent().getData();*/

    }

    @Override
    public void onStart() {
        super.onStart();
        if (requestOnFirstTime)
            DataRepository.getInstance().getProfile(memberID);
    }

    @Override
    protected void initUI() {
        friendIcon = ButterKnife.findById(profileGeneralInfo, R.id.icon);
        //friendIcon.setImageResource(R.drawable.add_friend);
        //friendIcon.setVisibility(View.INVISIBLE);
        interestsContainer.setVisibility(View.GONE);

        generalProfileInit();

        getActivity().setTitle(titleParameter);

        member = DataRepository.getInstance().getTempModel(Member.class);
        if (member != null)
            fill(member);
    }


    @Subscribe
    public void onProfileReceived(MemberReceivedEvent event) {
        this.member = event.member;
        requestOnFirstTime = false;

        fill(member);
    }

    private void showAddingAsFriendDialog() {
        // TODO: if is friend launch remove friend dialog.
        // if not launch add as friend dialog
        new PoinilaAlertDialog.Builder().
                setTitle(getString(R.string.friend_request)).
                setMessage(getString(R.string.approve_send_friend_request)).
                setPositiveBtnText(getString(R.string.yes)).
                setNegativeBtnText(getString(R.string.no)).
                build().show(getSupportFragmentManager(), null);
    }

    private void showEditFriendDialog() {

        new PoinilaAlertDialog.Builder().
                setTitle(R.string.edit_friendship).
                setPositiveBtnText(R.string.remove_friend).
                setNegativeBtnText(R.string.cancel).
                setNeutralBtnText(R.string.edit_circle).
                build().show(getSupportFragmentManager(), ConstantsUtils.TAG_EDIT_FRIENDSHIP);
    }

    @Subscribe
    public void onPositiveDialogButton(PositiveButtonClickedUIEvent event) {
        switch (member.friendshipStatus) {
            case NotFriend:
                PoinilaNetService.friendRequest(member.getId(), DBFacade.getDefaultCircle().id);
                member.friendshipStatus = FriendshipStatus.Pending;
                friendIcon.setImageResource(R.drawable.pending_friendship_request);
                break;
            case WaitingForAction: // sending request
                PoinilaNetService.answerFriendRequest(member.id, (FriendRequestAnswer)event.getData(), DBFacade.getDefaultCircle().id);
                member.friendshipStatus = FriendshipStatus.IsFriend;
                friendIcon.setImageResource(R.drawable.friends);
                break;
            case IsFriend: // removing friend
                PoinilaNetService.removeFriend(member.getId());
                member.friendshipStatus = FriendshipStatus.NotFriend;
                friendIcon.setImageResource(R.drawable.add_friend_selector);
                break;
            case Pending: // no action yet
                break;
        }
        //friendIcon.setImageResource(member.isFriend ? R.drawable.friends : R.drawable.add_friend_selector);
    }


    @Subscribe
    public void onNeutralDialogButton(NeutralDialogButtonClickedUIEvent event) {
        DialogLauncher.launchChangeFriendCircle(getSupportFragmentManager(), member);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_others_profile;
    }

    @OnClick(R.id.followers)
    public void onShowFollowers() {
        NavigationUtils.goToActivity(MemberListActivity.class, getActivity(),
                KEY_ENTITY, member.getId(), KEY_REQUEST_ID, REQUEST_MEMBER_FOLLOWERS);
    }

    @OnClick(R.id.friends)
    public void onShowFriends() {
        NavigationUtils.goToActivity(MemberListActivity.class, getActivity(),
                KEY_ENTITY, member.getId(), KEY_REQUEST_ID, REQUEST_MEMBER_FRIENDS);
    }

    @OnClick(R.id.favorited)
    public void onShowFavorited() {
        NavigationUtils.goToActivity(PostListActivity.class, getActivity(),
                KEY_ENTITY, member.getId(), KEY_REQUEST_ID, REQUEST_MEMBER_FAVED_POSTS);
    }

    @OnClick(R.id.posts)
    public void onShowPosts() {
        PageChanger.goToMemberPosts(getActivity(), member.getId(), member.fullName);
    }


    @OnClick(R.id.owning_collections_container)
    public void onShowOwniningCollections() {
        NavigationUtils.goToActivity(CollectionListActivity.class, getActivity(),
                KEY_ENTITY, member.getId(), KEY_REQUEST_ID, REQUEST_MEMBER_COLLECTIONS);
    }

    @OnClick(R.id.following_collections_container)
    public void onShowFollowingCollections() {
        if(member == null)return;
        NavigationUtils.goToActivity(CollectionListActivity.class, getActivity(),
                KEY_ENTITY, member.getId(), KEY_REQUEST_ID, REQUEST_MEMBER_FOLLOWED_COLLECTIONS);
    }


    private void fill(final Member member) {
        if (member == null) return;

        titleParameter = member.uniqueName;
        getActivity().setTitle(titleParameter);

        // TODO: fill page with actual posts
        friendIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataRepository.isUserAnonymous()){
                    Logger.toastError(R.string.error_guest_action);
                    return;
                }
                DialogLauncher.launchFriendshipDialog(member, getSupportFragmentManager());
            }
        });

        setImage((ImageView) profileGeneralInfo.findViewById(R.id.image), member.imageUrls,
                ImageUrls.ImageType.MEMBER, ImageUrls.ImageSize.BIG);
        if (member.imageUrls != null && member.imageUrls.isNotEmpty()) {
            profileGeneralInfo.findViewById(R.id.image).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = NavigationUtils.makeNavigationIntent(FullImageActivity.class, getActivity());
                    intent.putExtra(KEY_CONTENT_URI, member.imageUrls.properMemberImage(ImageUrls.ImageSize.FULL_SIZE).url);
                    startActivity(intent);
                }
            });
        }
        setText((TextView) profileGeneralInfo.findViewById(R.id.title), member.fullName);
        setText((TextView) profileGeneralInfo.findViewById(R.id.subtitle), member.uniqueName);

        //friendIcon.setBackgroundResource(0); // to clear old background
        if (DataRepository.getInstance().isMe(member.id)) {
            // TODO: this is terrible! must use the profile fragment instead.
            friendIcon.setVisibility(View.GONE);
        } else if (member.friendshipStatus == null) {
            friendIcon.setVisibility(View.INVISIBLE);
        } else {
            friendIcon.setVisibility(View.VISIBLE);
            switch (member.friendshipStatus) {
                case NotFriend:
                    friendIcon.setImageResource(R.drawable.add_friend_selector);
                    break;
                case WaitingForAction:
                    friendIcon.setImageResource(R.drawable.pending_friendship_request);
                    break;
                case IsFriend:
                    friendIcon.setImageResource(R.drawable.friends);
                    break;
                case Pending:
                    friendIcon.setImageResource(R.drawable.pending_friendship_request);
                    break;
            }
        }

        setText(aboutMe, member.aboutMe);

        if (TextUtils.isEmpty(member.url))
            blogInfo.setVisibility(View.GONE);
        else {
            setText((TextView) blogInfo.findViewById(R.id.title), member.urlName);
            setText((TextView) blogInfo.findViewById(R.id.url), member.url);
            blogInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavigationUtils.goToUrl(getActivity(), member.url.toLowerCase(), null);
                }
            });
        }

        setText((TextView) followersStatViewGroup.findViewById(R.id.top_text), member.followerCount);
        setText((TextView) favoritedStatViewGroup.findViewById(R.id.top_text), member.likesCount);
        setText((TextView) postsStatViewGroup.findViewById(R.id.top_text), member.postsCount);
        setText((TextView) friendsStatViewGroup.findViewById(R.id.top_text), member.friendsCount);

        setText((TextView) owningCollections.findViewById(R.id.card_title),
                getString(R.string.member_collections_formatted, member.fullName));

        /*-----OWNING COLLECTIONS-------*/
        fillCollectionsSummery(owningCollections, member.owningCollections, member.owningCollectionsCount);
        /*----FOLLOWING COLLECTIONS-----*/
        fillCollectionsSummery(followingCollections, member.followingCollections, member.followingCollectionsCount);

        /*-------Interests--------*/
        if (member.interests != null && !member.interests.isEmpty()) {
            FlowLayout flowLayout = ButterKnife.findById(interestsContainer, R.id.tags_container);
            for (int i = 0; i < 5 && i < member.interests.size(); i++) {
                ViewInflater.addTagToContainer(flowLayout, member.interests.get(i));
            }
        }
    }

    private void fillCollectionsSummery(View collectionsViewContainer, List<Collection> collections, int itemCount) {
        LinearLayout ll = (LinearLayout) collectionsViewContainer.findViewById(R.id.cards_container);
        if (collections == null || collections.isEmpty()) {
            ll.setVisibility(View.GONE);
            return;
        }
        ll.removeAllViews();
        ll.setVisibility(View.VISIBLE);
        int collectionsCount = getResources().getInteger(R.integer.profile_page_collection_summary_count);
        for (int i = 0; i < collectionsCount && i < collections.size(); i++) {
            final Collection collection = collections.get(i);
            View card = ViewInflater.inflateImageCaption(ll, collection.name, collection.coverImageUrls);
            card.findViewById(R.id.image).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCollectionClicked(collection);
                }
            });
            ll.addView(card, new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1));
            if (i != collectionsCount - 1) {
                Space margin = new Space(getActivity());
                ll.addView(margin, new LinearLayout.LayoutParams(
                        (int) ResourceUtils.getDimen(R.dimen.margin_lvl1), 1));
            }
        }
        TextView itemCountView = ButterKnife.findById(collectionsViewContainer, R.id.item_count);
        itemCountView.setVisibility(View.VISIBLE);
        ViewUtils.setText(itemCountView, StringUtils.getStringWithPersianNumber("(%d)", itemCount));
    }

    private void onCollectionClicked(final Collection collection) {
        PageChanger.goToCollection(getActivity(), collection);
    }

    private void generalProfileInit() {
        ((TextView) followersStatViewGroup.findViewById(R.id.bottom_text))
                .setText(getString(R.string.follower));
        ((TextView) favoritedStatViewGroup.findViewById(R.id.bottom_text))
                .setText(getString(R.string.favorited));
        ((TextView) postsStatViewGroup.findViewById(R.id.bottom_text))
                .setText(getString(R.string.post));
        ((TextView) friendsStatViewGroup.findViewById(R.id.bottom_text))
                .setText(getString(R.string.friend));


        ((TextView) followingCollections.findViewById(R.id.card_title)).
                setText(getString(R.string.follows));

        ((TextView) interestsContainer.findViewById(R.id.card_title)).
                setText(getString(R.string.interest));
        // TODO: member ro chejuri avval set konim?
    }

    @Subscribe
    public void onFriendCirclesUpdated(FriendCirclesUpdated event) {
        if (member == null) return;
        if (event.member.id == member.id)
            member.circle_ids = event.selectedCirclesIDs;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu); // TODO: create a new menu xml resource
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                // Handle this selection
                launchShareMenu();
                return true;
            case R.id.menu_item_report:
                // Handle this selection
                DialogLauncher.launchReportDialog(getSupportFragmentManager(), R.string.report_user, member.id);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void launchShareMenu() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String extra = getString(R.string.checkout_this_member) + "\n" +
                getString(R.string.member_share_url,
                        ConstantsUtils.POINILA_ORIGIN_ADDRESS,
                        Uri.encode(member.uniqueName)) + "\n" +
                getString(R.string.ponila_world_of_interest);
        shareIntent.putExtra(Intent.EXTRA_TEXT, extra);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_dialog_title)));
    }
}
