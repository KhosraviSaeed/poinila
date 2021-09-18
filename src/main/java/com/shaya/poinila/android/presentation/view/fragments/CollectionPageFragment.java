package com.shaya.poinila.android.presentation.view.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.makeramen.roundedimageview.RoundedImageView;
import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.uievent.CollectionClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.MemberClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.PositiveButtonClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.PostClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.PostComponentClickedUIEvent;
import com.shaya.poinila.android.presentation.view.dialog.DialogLauncher;
import com.shaya.poinila.android.presentation.view.help.Help;
import com.shaya.poinila.android.presentation.viewholder.BaseViewHolder;
import com.shaya.poinila.android.presentation.viewholder.PostsOfCollectionViewHolder;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.util.PoinilaPreferences;
import com.shaya.poinila.android.util.RandomUtils;
import com.shaya.poinila.android.util.StringUtils;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.OnClick;
import data.PoinilaNetService;
import data.event.BaseEvent;
import data.event.CollectionReceivedEvent;
import data.event.PostReceivedEvent;
import data.event.PostsReceivedEvent;
import data.model.Collection;
import data.model.ImageUrls;
import data.model.Loading;
import data.model.Member;
import data.model.Post;
import manager.DataRepository;
import manager.RequestSource;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setFont;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setImage;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_ENTITY;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_REQUEST_ID;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_SECOND_ENTITY_ID;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_COLLECTION_POSTS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_EXPLORE;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_MEMBER_POSTS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_POST_RELATED_POSTS;
import static com.shaya.poinila.android.util.ResourceUtils.getStringFormatted;

/**
 * Created by hossein on 8/30/16.
 */
public class CollectionPageFragment extends ListBusFragment {

    @Nullable
    @Bind(R.id.collection_name)
    TextView collectionName;

    @Nullable
    @Bind(R.id.image_big)
    RoundedImageView collectionImage;

    @Nullable
    @Bind(R.id.follow_button)
    Button followButton;
    @Nullable
    @Bind(R.id.follow_button_img)
    ImageView followButtonImg;
    @Nullable
    @Bind(R.id.edit_button)
    Button editButton;
    @Nullable
    @Bind(R.id.remove_button)
    Button removeButton;

    @Nullable
    @Bind(R.id.collection_description)
    TextView collectionDescription;

//    @Nullable
//    @Bind(R.id.collection_info_bar)
//    View collectionInfoView;

    @Nullable
    @Bind(R.id.item_count)
    TextView itemCountView;

    @Bind(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;

    protected Collection collection;

    private int requestType;
    private String mainEntityId;
    private String secondEntityId;
    private Set<Integer> activeRequests;

    FloatingActionsMenu fabMenu;
    FloatingActionButton addFromUrl;
    FloatingActionButton addPost;

    private boolean showedHelp = false;

    public static CollectionPageFragment newInstance(String mainEntityId, int requestID) {
        return newInstance(mainEntityId, null, requestID);
    }


    public static CollectionPageFragment newInstance(String mainEntityId, String secondEntityId, int requestID) {
        CollectionPageFragment f = new CollectionPageFragment();
        Bundle arguments = new Bundle();
        arguments.putString(KEY_ENTITY, mainEntityId);
        arguments.putString(KEY_SECOND_ENTITY_ID, secondEntityId);
        arguments.putInt(KEY_REQUEST_ID, requestID);
        //arguments.putBoolean(ConstantsUtils.KEY_IS_USER_COLLECTION, isCollectionOwnedByUser);
        f.setArguments(arguments);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainEntityId = getArguments().getString(KEY_ENTITY);
        requestType = getArguments().getInt(KEY_REQUEST_ID);
        secondEntityId = getArguments().getString(KEY_SECOND_ENTITY_ID);

        activeRequests = new HashSet<>(2);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        // Inflate menu resource file.
        inflater.inflate(R.menu.menu_post, menu);
        menu.findItem(R.id.menu_item_report).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                // Handle this selection
                launchShareMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void launchShareMenu() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String extra = null;
        extra = getString(R.string.checkout_this_collection) + "\n" +
                getString(R.string.collection_share_url,
                        ConstantsUtils.POINILA_ORIGIN_ADDRESS,
                        Uri.encode(collection.owner.uniqueName),
                        Uri.encode(collection.name)) + "\n" +
                getString(R.string.ponila_world_of_interest);
        shareIntent.putExtra(Intent.EXTRA_TEXT, extra);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_dialog_title)));
    }

    @Override
    protected void initUI() {
        super.initUI();

        mRecyclerView = new RecyclerViewProvider(mRecyclerView).setAdapter(getRecyclerViewAdapter()).
                setStaggeredLayoutManager(VERTICAL, getResources().getInteger(R.integer.column_count)).
                bindViewToAdapter();
        mRecyclerView.getItemAnimator().setChangeDuration(0);
        mRecyclerView.setNestedScrollingEnabled(false);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    onLoadMore();
                }
            }
        });

        fabMenu = (FloatingActionsMenu)rootView.findViewById(R.id.fab_menu);
        addFromUrl = (FloatingActionButton)rootView.findViewById(R.id.fab_add_post_from_site);
        addPost = (FloatingActionButton)rootView.findViewById(R.id.fab_add_post);

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenu.collapse();
                //DialogLauncher.launchNewPost(getChildFragmentManager(), null);
                PageChanger.goToNewPost(getFragmentManager(), null);
            }
        });

        addFromUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenu.collapse();
                DialogLauncher.launchNewWebsitePost(getFragmentManager());
            }
        });

        getActivity().setTitle(R.string.collection);
        collection = DataRepository.getInstance().getTempModel(Collection.class);
        if (collection == null)
            return;
        fillCollection(collection);
        setLoading(new Loading());
    }

    private boolean byDeepLink(String collectionIdOrName) {
        return !StringUtils.isInteger(collectionIdOrName);
    }

    @Override
    protected RecyclerView.OnScrollListener getRecyclerViewListener() {
        return RecyclerViewProvider.staggeredListEndDetectorListener(getRecyclerViewAdapter(), this);
    }

    private void fillCollection(Collection collection) {
        setText(itemCountView, getStringFormatted(R.string.posts_formatted, collection.postCount));

        if (!TextUtils.isEmpty(collection.description)) {
            setText(collectionDescription, collection.description);
            setFont(collectionDescription, getString(R.string.default_bold_font_path));
        }

        if(!TextUtils.isEmpty(collection.name)){
            setText(collectionName, collection.name);
            setFont(collectionName, getString(R.string.default_font_path));
        }

        if (!DataRepository.isMyCollection(collection)) {
            followButton.setVisibility(View.VISIBLE);
            followButtonImg.setVisibility(View.VISIBLE);
            updateFollowButton();
        } else {
            editButton.setVisibility(View.VISIBLE);
            removeButton.setVisibility(View.VISIBLE);
        }

        setImage(collectionImage, collection.coverImageUrls, ImageUrls.ImageType.COLLECTION, ImageUrls.ImageSize.AVATAR);
        //getActivity().setTitle(getString(R.string.title_activity_collection, mainEntityId));
    }

    private void updateFollowButton() {
        setText(followButton, collection.followedByMe
                ? getString(R.string.unfollow_item)
                : getString(R.string.follow_item));

        followButtonImg.setSelected(collection.followedByMe);
        followButton.setSelected(collection.followedByMe);
    }

    @Override
    public void requestForMoreData() {

        int requestId = RandomUtils.getRandomInt();
        activeRequests.add(requestId);

        DataRepository.getCollectionPosts(
                mainEntityId,
                byDeepLink(mainEntityId) ? secondEntityId : null,
                bookmark,
                BaseEvent.ReceiverName.CollectionPageFragment);
    }

    @Override
    public RecyclerViewAdapter createAndReturnRVAdapter() {
        final BaseEvent.ReceiverName receiverName = BaseEvent.ReceiverName.CollectionPageFragment;
        return new RecyclerViewAdapter(getActivity(), R.layout.post_in_collection) {
            @Override
            protected BaseViewHolder getProperViewHolder(View v, int viewType) {
                if(viewType == VIEW_TYPE_LOAD_PROGRESS){
                    return new BaseViewHolder.EmptyViewHolder(v);
                }
                return new PostsOfCollectionViewHolder(v, receiverName);
            }

            @Override
            protected boolean isStaggeredGridLayoutManager() {
                return true;
            }
        };
    }

    @Override
    protected boolean sendsRequestAutomatically() {
        return true;
    }

    @Override
    protected void requestInitialData() {
        // deep links are unique with two parameters -> user name, collection name
//        Log.i(getClass().getName(), "collection unige name : " + (byDeepLink(mainEntityId) ? secondEntityId : null));
        DataRepository.getInstance().getCollection(
                mainEntityId,
                byDeepLink(mainEntityId) ? secondEntityId : null,
                RequestSource.FORCE_ONLINE);

        requestForMoreData();
    }

    @Override
    public boolean mustShowProgressView() {
        return false;
    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_collection_detail_owned;
    }


    @Override
    public void onSuccessfulInitData(BaseEvent baseEvent) {
        super.onSuccessfulInitData(baseEvent);

        if (baseEvent instanceof CollectionReceivedEvent) {
            collection = ((CollectionReceivedEvent) baseEvent).collection;
            fillCollection(collection);
        } else if (baseEvent instanceof PostReceivedEvent) {
            if (getRecyclerViewAdapter().isEmpty()) { // click on notification participant
                getRecyclerViewAdapter().addItem(((PostReceivedEvent) baseEvent).post, 0);
            } else {
                //TODO: replace nemikone ba avvalin item related post ha?
                getRecyclerViewAdapter().setItem(((PostReceivedEvent) baseEvent).post, 0);
            }
        }

        /*collection.description = event.collection.description;
        collection.circleIDs = event.collection.circleIDs;
        collection.name = event.collection.name;
        collection.topic = event.collection.topic;
        collection.coverImageUrls = event.collection.coverImageUrls;*/

    }

    @Subscribe
    public void onCollectionReceived(CollectionReceivedEvent event){
        onGettingInitDataResponse(event);
    }


    @Subscribe
    public void onPostsReceived(PostsReceivedEvent event) {

        if(event.posts.size() >= 25 && mRecyclerView.getAdapter().getItemCount() == 0){
            setLoading(new Loading());
        }

        populateIfNecessary(event); // was necessary earlier because response came from server lacked info about posts collection
        onGettingInitDataResponse(event);
        onGettingListDataResponse(event, event.bookmark);

    }

    public void populateIfNecessary(PostsReceivedEvent event) {
        if (collection != null) {
            for (Post post : event.posts) {
                post.collection = collection;
                //post.author = collection.owner;
            }
        }
    }

    @Override
    public void onSuccessfulListData(BaseEvent baseEvent, String newBookmark) {
        super.onSuccessfulListData(baseEvent, newBookmark);
        List<Post> posts = ((PostsReceivedEvent) baseEvent).posts;

        getRecyclerViewAdapter().addItems(posts);

        if(!showedHelp && !PoinilaPreferences.getHelpStatus(getClass().getName()) && requestType == REQUEST_COLLECTION_POSTS){
            showHelp();
            showedHelp = true;
            PoinilaPreferences.putHelpStatus(getClass().getName(), true);
        }

    }

    @Nullable
    @OnClick(R.id.remove_button)
    public void onRemoveCollection() {
        // must not happen but anyway... :)
        if (DataRepository.isUserAnonymous()) {
            Logger.toastError(R.string.error_guest_action);
            return;
        }

        DialogLauncher.launchDeleteCollection(getFragmentManager());
    }

    @Nullable
    @OnClick(R.id.follow_button)
    public void onFollowCollection(Button followButton) {
        if (DataRepository.isUserAnonymous()) {
            Logger.toastError(R.string.error_guest_action);
            return;
        }

        if (collection.followedByMe)
            PoinilaNetService.unfollowCollection(collection.getId());
        else
            PoinilaNetService.followCollection(collection.getId());

        collection.followedByMe = !collection.followedByMe;

        updateFollowButton();
    }


    @Nullable
    @OnClick(R.id.edit_button)
    public void onEditCollection() {
        // must not happen but anyway... :)
        if (DataRepository.isUserAnonymous()) {
            Logger.toastError(R.string.error_guest_action);
            return;
        }

        DialogLauncher.launchEditCollectionDialog(getFragmentManager(), collection);
    }

    @Subscribe
    public void onProfilePicClickedEvent(MemberClickedUIEvent event) {
        Member member = ((Post)getRecyclerViewAdapter().getItem(event.adapterPosition)).author;
        PageChanger.goToProfile(getActivity(), member);
    }

    @Subscribe
    public void onPostClicked(PostClickedUIEvent event) {
        Post post = (Post)getRecyclerViewAdapter().getItem(event.adapterPosition);
        PageChanger.goToPost(getActivity(), post);
    }

    @Subscribe
    public void onCollectionClicked(CollectionClickedUIEvent event) {
        Collection collection = ((Post)getRecyclerViewAdapter().getItem(event.adapterPosition)).collection;
        PageChanger.goToCollection(getActivity(), collection);
    }

    @Subscribe
    public void onPositiveDialogButtonClicked(PositiveButtonClickedUIEvent event) {
        PoinilaNetService.deleteCollection(collection);
        getActivity().finish();
    }

    @Subscribe
    public void onPostDetailsComponentClickEvent(PostComponentClickedUIEvent event) {
        if (DataRepository.isUserAnonymous() && PostComponentClickedUIEvent.Type.guestCantPerformActions.contains(event.type)) {
            Logger.toastError(R.string.error_guest_action);
            return;
        }


        Post post = (Post)getRecyclerViewAdapter().getItem(0);
        switch (event.type) {
            case FaversList:
                PageChanger.goToLikersList(getActivity(), post.faveCount, post.getId());
                break;
            case Comments:
                PageChanger.goToCommentList(getActivity(), post.commentCount, post.getId());
                getRecyclerViewAdapter().notifyItemChanged(0);
                break;
            case RepostersList:
                PageChanger.goToRepostList(getActivity(), post.repostCount, post.getId());
                break;
            case Repost:
                DialogLauncher.launchRepostDialog(getFragmentManager(), post);
                getRecyclerViewAdapter().notifyItemChanged(0);
                break;
            case Poster:
                Member member = post.author;
                PageChanger.goToProfile(getActivity(), member);
                break;
            case Collection:
                Collection collection = post.collection;
                PageChanger.goToCollection(getActivity(), collection);
                break;
            case OriginalCollection:
                collection = post.originalCollection;
                PageChanger.goToCollection(getActivity(), collection);
                break;
            case Reference:
                PageChanger.goToInlineBrowser(getActivity(), post.originalWebpage.toLowerCase(), post.getId(), post.name);
                break;
            case FullImage:
                PageChanger.goToFullImage(getActivity(), post.imagesUrls.properPostImage(ImageUrls.ImageSize.FULL_SIZE).url);
                break;
        }
    }


    @Override
    protected boolean isInitDataResponseValid(BaseEvent baseEvent) {
        boolean res = requestType == REQUEST_COLLECTION_POSTS;
        return res && super.isInitDataResponseValid(baseEvent);
    }

    @Override
    protected boolean isListDataResponseValid(BaseEvent baseEvent, String responseBookmark) {
        boolean res;
        PostsReceivedEvent event = (PostsReceivedEvent) baseEvent;
        res = event.receiverName == BaseEvent.ReceiverName.CollectionPageFragment;

        /*switch (requestID) {
            case ((PostsReceivedEvent) baseEvent).receiverName
        }*/
        return res && super.isListDataResponseValid(baseEvent, responseBookmark);

    }



    public void showHelp(){
        Help.getInstance().showPostsOfCollectionHelp(getActivity(), followButton);
    }


}
