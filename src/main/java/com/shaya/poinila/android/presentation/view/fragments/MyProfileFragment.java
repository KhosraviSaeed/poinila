package com.shaya.poinila.android.presentation.view.fragments;


import android.Manifest;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.widget.Space;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.ExploreTagEvent;
import com.shaya.poinila.android.presentation.uievent.FABMenuCollapseUIEvent;
import com.shaya.poinila.android.presentation.uievent.FABMenuExpandUIEvent;
import com.shaya.poinila.android.presentation.uievent.HelpMyProfileFragment;
import com.shaya.poinila.android.presentation.uievent.NewWebsitePostEvent;
import com.shaya.poinila.android.presentation.uievent.ShowVerifySnackbarEvent;
import com.shaya.poinila.android.presentation.view.ViewInflater;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.presentation.view.activity.BaseActivity;
import com.shaya.poinila.android.presentation.view.activity.CollectionListActivity;
import com.shaya.poinila.android.presentation.view.activity.CropImageActivity;
import com.shaya.poinila.android.presentation.view.activity.EditInterestsActivity;
import com.shaya.poinila.android.presentation.view.activity.MemberListActivity;
import com.shaya.poinila.android.presentation.view.activity.PostListActivity;
import com.shaya.poinila.android.presentation.view.activity.SettingActivity;
import com.shaya.poinila.android.presentation.view.costom_view.ActivityResultPermissionDelegate.ImagePickerResultPermissionDelegate;
import com.shaya.poinila.android.presentation.view.dialog.DialogLauncher;
import com.shaya.poinila.android.presentation.view.dialog.NewCollectionDialog;
import com.shaya.poinila.android.presentation.view.dialog.PoinilaInviteDialog;
import com.shaya.poinila.android.presentation.view.help.Help;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.NavigationUtils;
import com.shaya.poinila.android.util.PoinilaPreferences;
import com.shaya.poinila.android.util.ResourceUtils;
import com.shaya.poinila.android.util.StorageUtils;
import com.shaya.poinila.android.util.StringUtils;
import com.squareup.otto.Subscribe;


import org.apmem.tools.layouts.FlowLayout;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import data.event.BaseEvent;
import data.event.InviteUsedEvent;
import data.event.MemberReceivedEvent;
import data.event.ProfileDirtyEvent;
import data.model.Collection;
import data.model.ImageUrls;
import data.model.Member;
import manager.DBFacade;
import manager.DataRepository;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setFont;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setImage;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_ENTITY;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_IMAGE_ADDRESS;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_REQUEST_ID;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_MEMBER_COLLECTIONS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_MEMBER_FAVED_POSTS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_MEMBER_FOLLOWED_COLLECTIONS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_MEMBER_FOLLOWERS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_MEMBER_FRIENDS;

/**
 * A simple {@link android.app.Fragment} subclass.
 * Use the {@link MyProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProfileFragment extends BusFragment implements View.OnClickListener, FragmentCompat.OnRequestPermissionsResultCallback{

    @Bind(R.id.fab_menu)
    FloatingActionsMenu fabMenu;
    @Bind(R.id.fab_add_post)
    FloatingActionButton addPost;
    @Bind(R.id.fab_add_collection) FloatingActionButton addCollection;
    @Bind(R.id.fab_add_post_from_site) FloatingActionButton addFromUrl;
    @Bind(R.id.fab_invite) FloatingActionButton inviteToPoinila;

    /*-------Related to general member actions-------*/
    @Bind(R.id.followers) ViewGroup followersStatViewGroup;
    @Bind(R.id.favorited) ViewGroup favoritedStatViewGroup;
    @Bind(R.id.posts) ViewGroup postsStatViewGroup;
    @Bind(R.id.friends) ViewGroup friendsStatViewGroup;
    @Bind(R.id.owning_collections_container) View owningCollections;
    @Bind(R.id.following_collections_container) View followingCollections;
    @Bind(R.id.profile_general_info) View profileGeneralInfo;
    @Bind(R.id.interest_container) View interestsContainer;
    @Bind(R.id.blog_info) View blogInfo;
    @Bind(R.id.about_me) TextView aboutMe;

    private ImageView settingIcon;
    private Member member;
    private ImageView mAvatarImageView;
    private ImagePickerResultPermissionDelegate resultHandlerIMPL;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MyProfileFragment.
     */
    public static MyProfileFragment newInstance() {
        MyProfileFragment fragment = new MyProfileFragment();
        return fragment;
    }

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @OnClick(R.id.followers) public void onShowFollowers(){
        NavigationUtils.goToActivity(MemberListActivity.class, getActivity(),
                KEY_ENTITY, member.getId(), KEY_REQUEST_ID, REQUEST_MEMBER_FOLLOWERS);
    }

    @OnClick(R.id.friends) public void onShowFriends(){
        NavigationUtils.goToActivity(MemberListActivity.class, getActivity(),
                KEY_ENTITY, member.getId(), KEY_REQUEST_ID, REQUEST_MEMBER_FRIENDS);
    }

    @OnClick(R.id.favorited) public void onShowFavorited(){
        NavigationUtils.goToActivity(PostListActivity.class, getActivity(),
                KEY_ENTITY, member.getId(), KEY_REQUEST_ID, REQUEST_MEMBER_FAVED_POSTS);
    }

    @OnClick(R.id.posts) public void onShowPosts(){
        PageChanger.goToMemberPosts(getActivity(), member.getId(), member.fullName);
    }

    @OnClick(R.id.owning_collections_container) public void onShowOwniningCollections(){
        NavigationUtils.goToActivity(CollectionListActivity.class, getActivity(),
                KEY_ENTITY, member.getId(), KEY_REQUEST_ID, REQUEST_MEMBER_COLLECTIONS);
    }

    @OnClick(R.id.following_collections_container) public void onShowFollowingCollections(){
        NavigationUtils.goToActivity(CollectionListActivity.class, getActivity(),
                KEY_ENTITY, member.getId(), KEY_REQUEST_ID, REQUEST_MEMBER_FOLLOWED_COLLECTIONS);
    }

    // for my profile
    @OnClick(R.id.interest_container) public void onShowInterests(){
        NavigationUtils.goToActivity(EditInterestsActivity.class, getActivity(),
                KEY_ENTITY, member.getId());
    }


    @OnClick(R.id.fab_add_post) public void onAddPost(){
        fabMenu.collapse();
        //DialogLauncher.launchNewPost(getChildFragmentManager(), null);
        PageChanger.goToNewPost(getFragmentManager(), null);
    }

    @OnClick(R.id.fab_add_post_from_site) public void onAddPostFromUrl(){
        // todo: dialog, its layout and how to get a url imagesUrls;
        fabMenu.collapse();
//        DialogLauncher.launchNewWebsitePost(getFragmentManager());
        PageChanger.goToNewWebSitePost(getActivity(), null);
    }

    @Subscribe public void onNewUrlImagePostEvent(NewWebsitePostEvent event){
        //DialogLauncher.launchNewPost(getChildFragmentManager(), event.suggestedPost);
        PageChanger.goToNewPost(getFragmentManager(), event.suggestedPost);
    }

    @OnClick(R.id.fab_add_collection) public void onAddCollection(){
        fabMenu.collapse();
        new NewCollectionDialog().show(getFragmentManager(), null);
        //new NewCollectionDialog().show(getChildFragmentManager(), TAG_NEW_COLLECTION_DIALOG);
    }

    @OnClick(R.id.fab_invite) public void onInviteToPoinila(){
        fabMenu.collapse();
        new PoinilaInviteDialog().show(getFragmentManager(), null);
    }

    @Subscribe
    public void onInviteUsedEvent(InviteUsedEvent event){
        updateInviteFAB();
    }

    private void updateInviteFAB() {
        if (DataRepository.getInstance().getRemainedInvites() <= 0)
            inviteToPoinila.setVisibility(View.GONE);
        else
            inviteToPoinila.setTitle(getString(R.string.invite_to_poinila_formatted,
                    DataRepository.getInstance().getRemainedInvites()));
    }

    @Override
    protected void initUI() {
        //TODO: what the?
        resultHandlerIMPL = new ImagePickerResultPermissionDelegate(){
            @Override
            public void handleValidResults(int requestCode, Intent data) {
                super.handleValidResults(requestCode, data);

                if (TextUtils.isEmpty(this.imageAddress))
                    return;
                Intent intent = NavigationUtils.makeNavigationIntent(CropImageActivity.class, getActivity());
                intent.putExtra(KEY_IMAGE_ADDRESS, this.imageAddress);
                startActivity(intent);
            }

            @Override
            public void handlePermissionGranted() {
                startForResult(MyProfileFragment.this,
                        StorageUtils.dispatchCapturePhotoIntent(),
                        ConstantsUtils.REQUEST_CODE_TAKE_PHOTO);
            }
        };

        settingIcon = ButterKnife.findById(profileGeneralInfo, R.id.icon);
        settingIcon.setImageResource(R.drawable.action_settings);
        settingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSetting(v);
            }
        });
        mAvatarImageView = (ImageView) profileGeneralInfo.findViewById(R.id.image);

        //setRemainedInvites(new RemainedInvitesEvent(DataRepository.getInstance().getRemainedInvites()));
        updateInviteFAB();
        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                BusProvider.getBus().post(new FABMenuExpandUIEvent(BaseEvent.ReceiverName.MyProfileFragment));
            }

            @Override
            public void onMenuCollapsed() {
                BusProvider.getBus().post(new FABMenuCollapseUIEvent(BaseEvent.ReceiverName.MyProfileFragment));
            }
        });
        generalProfileInit();


    }

    public void onSetting(View v){
        NavigationUtils.goToActivity(SettingActivity.class, getActivity());
    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_profile;
    }

    @Override
    public void onStart() {
        // TODO: use better approaches later
        initDataResponseReceived = false; // this causes parent initData to be called.
        super.onStart();

        if (member == null)
            member = DBFacade.getCachedMyInfo();
        if (member != null) { // due to crash (LG Optimus 1.1.9.2) https://play.google.com/apps/publish/?dev_acc=18170414618191752575#ErrorClusterDetailsPlace:p=com.shaya.poinila&et=CRASH&lr=LAST_7_DAYS&ecn=java.lang.NullPointerException&tf=MyProfileFragment.java&tc=com.shaya.poinila.android.presentation.view.fragments.MyProfileFragment&tm=fill&nid&an&c&s=new_status_desc
            // however I dont know why this could happen
            fill(member);
        }
    }

    /*-------Related to general member actions-------*/

    @Subscribe public void onProfileReceived(MemberReceivedEvent event) {
        onGettingInitDataResponse(event);
    }

    @Override
    public void onSuccessfulInitData(BaseEvent baseEvent) {
        super.onSuccessfulInitData(baseEvent);
        this.member = ((MemberReceivedEvent)baseEvent).member;
        fill(member);
    }

    private void fill(final Member member) {
        // TODO: fill page with actual posts
        setImage(mAvatarImageView, member.imageUrls,
                ImageUrls.ImageType.MEMBER, ImageUrls.ImageSize.BIG);
        mAvatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogLauncher.launchSelectImage(getActivity().getSupportFragmentManager(), member, MyProfileFragment.this);
//                inflateAvatarMenu(v);
            }
        });

        setText((TextView) profileGeneralInfo.findViewById(R.id.title), member.fullName);
        setText((TextView) profileGeneralInfo.findViewById(R.id.subtitle), member.uniqueName);

        setFont((TextView) profileGeneralInfo.findViewById(R.id.title), getString(R.string.default_bold_font_path));
        setFont((TextView) profileGeneralInfo.findViewById(R.id.subtitle), getString(R.string.default_font_path));

        setText(aboutMe, member.aboutMe);
        setFont(aboutMe, getString(R.string.default_font_path));

        if (TextUtils.isEmpty(member.url))
            blogInfo.setVisibility(View.GONE);
        else {
            blogInfo.setVisibility(View.VISIBLE);
//            setText((TextView) blogInfo.findViewById(R.id.title), member.urlName);
            setText((TextView) blogInfo.findViewById(R.id.url),member.url);
            setFont((TextView) blogInfo.findViewById(R.id.url), getString(R.string.default_bold_font_path));
            setFont((TextView) blogInfo.findViewById(R.id.url_label), getString(R.string.default_bold_font_path));
            blogInfo.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View v) {
                    String url = member.url.toLowerCase().startsWith("http://") ?
                            member.url.toLowerCase() : "http://" + member.url.toLowerCase();
                    PageChanger.goToInlineBrowser(getActivity(), url, null, null);
                }
            });
        }

        setText((TextView)followersStatViewGroup.findViewById(R.id.top_text) ,member.followerCount);
        setText((TextView) favoritedStatViewGroup.findViewById(R.id.top_text), member.likesCount);
        setText((TextView) postsStatViewGroup.findViewById(R.id.top_text), member.postsCount);
        setText((TextView) friendsStatViewGroup.findViewById(R.id.top_text), member.friendsCount);

        setFont((TextView) followersStatViewGroup.findViewById(R.id.top_text), getString(R.string.default_bold_font_path));
        setFont((TextView) favoritedStatViewGroup.findViewById(R.id.top_text), getString(R.string.default_bold_font_path));
        setFont((TextView) postsStatViewGroup.findViewById(R.id.top_text), getString(R.string.default_bold_font_path));
        setFont((TextView) friendsStatViewGroup.findViewById(R.id.top_text), getString(R.string.default_bold_font_path));

        setFont((TextView) followersStatViewGroup.findViewById(R.id.bottom_text), getString(R.string.default_bold_font_path));
        setFont((TextView) favoritedStatViewGroup.findViewById(R.id.bottom_text), getString(R.string.default_bold_font_path));
        setFont((TextView) postsStatViewGroup.findViewById(R.id.bottom_text), getString(R.string.default_bold_font_path));
        setFont((TextView) friendsStatViewGroup.findViewById(R.id.bottom_text), getString(R.string.default_bold_font_path));

        setText((TextView) owningCollections.findViewById(R.id.card_title),
                getString(R.string.member_collections_formatted, member.fullName));

        setFont((TextView) owningCollections.findViewById(R.id.card_title),
                getString(R.string.default_bold_font_path));

        /*-----OWNING COLLECTIONS-------*/
        fillCollectionsSummery(owningCollections, member.owningCollections, member.owningCollectionsCount);
        /*----FOLLOWING COLLECTIONS-----*/
        fillCollectionsSummery(followingCollections, member.followingCollections, member.followingCollectionsCount);
        /*-------Interests--------*/
        if (member.interests != null && !member.interests.isEmpty()) {
            FlowLayout flowLayout = ButterKnife.findById(interestsContainer, R.id.tags_container);
            flowLayout.removeAllViews();
            for (int i = 0; i < 5 && i < member.interests.size(); i++) {
                ViewInflater.addTagToContainer(flowLayout, member.interests.get(i));
            }
        }
    }

    private void fillCollectionsSummery(View collectionsViewContainer,
                                        List<Collection> collections, int itemCount) {
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
            CardView card = (CardView)ViewInflater.inflateImageCaption(ll, collection.name, collection.coverImageUrls);
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
        ViewUtils.setFont(itemCountView, getString(R.string.default_bold_font_path));
        ViewUtils.setText(itemCountView, StringUtils.getStringWithPersianNumber("(%d)", itemCount));

    }

    private void onCollectionClicked(final Collection collection) {
        PageChanger.goToCollection(getActivity(), collection);
    }

    private void generalProfileInit() {
        ((TextView)followersStatViewGroup.findViewById(R.id.bottom_text))
                .setText(getString(R.string.follower));
        ((TextView)favoritedStatViewGroup.findViewById(R.id.bottom_text))
                .setText(getString(R.string.favorited));
        ((TextView)postsStatViewGroup.findViewById(R.id.bottom_text))
                .setText(getString(R.string.post));
        ((TextView)friendsStatViewGroup.findViewById(R.id.bottom_text))
                .setText(getString(R.string.friend));


        ((TextView)followingCollections.findViewById(R.id.card_title)).
                setText(getString(R.string.follows));

        ((TextView)interestsContainer.findViewById(R.id.card_title)).
                setText(getString(R.string.interest));

        setFont((TextView) followingCollections.findViewById(R.id.card_title), getString(R.string.default_bold_font_path));
        setFont((TextView)interestsContainer.findViewById(R.id.card_title), getString(R.string.default_bold_font_path));
        // TODO: member ro chejuri avval set konim?
    }

//    private void inflateAvatarMenu(View v) {
//        popupMenu = new PopupMenu(getActivity(), v);
//        popupMenu.setOnMenuItemClickListener(MyProfileFragment.this);
//        MenuInflater inflater = popupMenu.getMenuInflater();
//        inflater.inflate(R.menu.menu_popup_profile_picture, popupMenu.getMenu());
//        if (member.imageUrls == null || !member.imageUrls.isNotEmpty())
//            popupMenu.getMenu().removeItem(R.id.view_photo);
//        popupMenu.show();
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        resultHandlerIMPL.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        resultHandlerIMPL.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Subscribe public void onProfileChanged(ProfileDirtyEvent event) {
        DataRepository.getInstance().getMyProfile();
    }

    // If root layout be LinearLayout, below approach is useless.
    @Subscribe
    public void onFABMenuExpanded(FABMenuExpandUIEvent event){
        View maskView = LayoutInflater.from(getActivity()).inflate(R.layout.white_transarent_mask, rootView, false);
        maskView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                fabMenu.collapse();
                return true;
            }
        });
        // adding to index 1, after profile layout and before FAB menu
        rootView.addView(maskView, 1);
    }

    @Subscribe public void onFABMenuCollapsed(FABMenuCollapseUIEvent event){
        rootView.removeView(rootView.findViewById(R.id.white_transparent_mask));
    }


    @Subscribe public void onExploreTag(ExploreTagEvent event){
        PageChanger.goToExplore(getActivity(), event.text);
    }
    /*------------------------*/


    @Subscribe
    public void answerAvailable(HelpMyProfileFragment event) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(!PoinilaPreferences.getHelpStatus(getClass().getName())){
                    showHelp();
                    PoinilaPreferences.putHelpStatus(getClass().getName(), true);
                }

            }
        }, 500);



    }

    @Override
    protected boolean sendsRequestAutomatically() {
        return true;
    }

    @Override
    protected void requestInitialData() {
        DataRepository.getInstance().getMyProfile(); //ConnectionUitls.isNetworkOnline()
    }

    @Override
    public ViewGroup getLoadableView() {
        return null;
    }

    @Override
    public boolean mustShowProgressView() {
        return false;
    }

    public void showHelp() {
        if(this.isVisible()){
            Help.getInstance().showProfileHelp(getActivity(), fabMenu);
            viewedHelp = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.show_profile_image:
                PageChanger.goToFullImage(getActivity(), member.imageUrls.properMemberImage(ImageUrls.ImageSize.FULL_SIZE).url);
                break;
            case R.id.select_image_camera:
                resultHandlerIMPL.askForPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        BaseActivity.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                break;
            case  R.id.select_image_gallery:
                resultHandlerIMPL.startForResult(this, StorageUtils.dispatchSelectImageIntent(), ConstantsUtils.REQUEST_CODE_PICK_IMAGE);
                break;

        }
    }
    /*-----------------------------*/
}
