package com.shaya.poinila.android.presentation.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.uievent.CollectionClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.ExploreTagEvent;
import com.shaya.poinila.android.presentation.uievent.MemberClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.NewWebsitePostEvent;
import com.shaya.poinila.android.presentation.uievent.PositiveButtonClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.PostClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.PostComponentClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.RemovePostUIEvent;
import com.shaya.poinila.android.presentation.uievent.UpdateUICommentEvent;
import com.shaya.poinila.android.presentation.uievent.UpdateUiRepostEvent;
import com.shaya.poinila.android.presentation.uievent.sync.PostActionSyncEvent;
import com.shaya.poinila.android.presentation.view.dialog.DialogLauncher;
import com.shaya.poinila.android.presentation.view.dialog.PoinilaAlertDialog;
import com.shaya.poinila.android.presentation.view.help.Help;
import com.shaya.poinila.android.presentation.viewholder.BaseViewHolder;
import com.shaya.poinila.android.presentation.viewholder.DashboardPostViewHolder;
import com.shaya.poinila.android.presentation.viewholder.PostDetailViewHolder;
import com.shaya.poinila.android.presentation.viewholder.PostsOfCollectionViewHolder;
import com.shaya.poinila.android.presentation.viewholder.RemovablePostViewHolder;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.util.PoinilaPreferences;
import com.shaya.poinila.android.util.RandomUtils;
import com.shaya.poinila.android.util.StringUtils;
import com.squareup.otto.Subscribe;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.OnClick;
import data.PoinilaNetService;
import data.event.BaseEvent;
import data.event.CollectionReceivedEvent;
import data.event.ContentReceivedEvent;
import data.event.PostReceivedEvent;
import data.event.PostsReceivedEvent;
import data.event.UndoFavePostEvent;
import data.event.UndoUnfavePostEvent;
import data.model.Collection;
import data.model.ImageUrls;
import data.model.Loading;
import data.model.Member;
import data.model.Post;
import manager.DataRepository;
import manager.RequestSource;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setFont;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_ENTITY;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_REQUEST_ID;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_SECOND_ENTITY_ID;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_COLLECTION_POSTS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_EXPLORE;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_MEMBER_FAVED_POSTS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_MEMBER_POSTS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_POST_RELATED_POSTS;
import static com.shaya.poinila.android.util.ResourceUtils.getStringFormatted;

/**
 * Created by iran on 2015-08-09.
 */
public class PostListFragment extends ListBusFragment {

    protected Collection collection;
    private int requestType;
    private String mainEntityId;
    private String secondEntityId;

    private boolean showedHelp = false;

    // must be moved to bus fragment
    private Set<Integer> activeRequests;

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


    FloatingActionsMenu fabMenu;
    FloatingActionButton addFromUrl;
    FloatingActionButton addPost;


//    @Nullable
//    @Bind(R.id.collection_description_container)
//    ViewGroup descriptionContainer;

    @Nullable
    @Bind(R.id.explored_tag)
    TextView exploredTagView;

    Post mainPost;

    @Override
    protected RecyclerView.OnScrollListener getRecyclerViewListener() {
        return RecyclerViewProvider.staggeredListEndDetectorListener(getRecyclerViewAdapter(), this);
    }

    private void fillCollection(Collection collection) {
        setText(itemCountView, getStringFormatted(R.string.posts_formatted, collection.postCount));

        if (!TextUtils.isEmpty(collection.description)) {
            setText(collectionDescription, collection.description);
            setFont(collectionDescription, getString(R.string.default_bold_font_path));
            collectionDescription.setVisibility(View.VISIBLE);
        }else {
            collectionDescription.setVisibility(View.GONE);
        }

        if (!DataRepository.isMyCollection(collection)) {
            followButton.setVisibility(View.VISIBLE);
            followButtonImg.setVisibility(View.VISIBLE);
            updateFollowButton();
        } else {
            editButton.setVisibility(View.VISIBLE);
            removeButton.setVisibility(View.VISIBLE);
        }
        //getActivity().setTitle(getString(R.string.title_activity_collection, mainEntityId));
    }

    public static PostListFragment newInstance(String mainEntityId, int requestID) {
        return newInstance(mainEntityId, null, requestID);
    }

    public static PostListFragment newInstance(String mainEntityId, String secondEntityId, int requestID) {
        PostListFragment f = new PostListFragment();
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

        // when opening new post, in some situations we already have post data. We show that to
        // user avoiding loading icons and at the same time request for post with updated data.
        /*if (requestID == ConstantsUtils.REQUEST_POST_RELATED_POSTS &&
                DataRepository.getInstance().getTempModel(Post.class) != null){

        }*/
    }

    @Override
    public int getLayoutID() {
        switch (requestType) {
            case REQUEST_COLLECTION_POSTS:
                return R.layout.fragment_collection_detail_owned;
            case REQUEST_EXPLORE:
                return R.layout.fragment_explore;
            case REQUEST_MEMBER_POSTS:
                return R.layout.fragment_member_posts;
            default:
                return R.layout.recycler_view_full;
        }
    }

    @Override
    protected void initUI() {
        super.initUI();
        mRecyclerView = new RecyclerViewProvider(mRecyclerView).setAdapter(getRecyclerViewAdapter()).
                setStaggeredLayoutManager(VERTICAL, getResources().getInteger(R.integer.column_count)).
                bindViewToAdapter();
        mRecyclerView.getItemAnimator().setChangeDuration(0);


        switch (requestType) {
            case REQUEST_MEMBER_POSTS:
                getActivity().setTitle(getString(R.string.title_activity_member_posts, secondEntityId));
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

                break;
            case REQUEST_MEMBER_FAVED_POSTS:
                getActivity().setTitle(R.string.title_activity_member_faved_posts);
                break;
            case ConstantsUtils.REQUEST_POST_RELATED_POSTS:
                getActivity().setTitle(getString(R.string.post));
                // adding main post as first adapter item
                mainPost = DataRepository.getInstance().getTempModel(Post.class);
                if(mainPost != null){
                    getRecyclerViewAdapter().addItem(mainPost);
                    getRecyclerViewAdapter().notifyDataSetChanged();
                }

                mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        View view = ((ViewGroup) mRecyclerView.getLayoutManager().findViewByPosition(0)).getChildAt(1);
                        if(!PoinilaPreferences.getHelpStatus(PostListFragment.this.getClass().getName() + ".PostPage")){
                            Help.getInstance().showPostRelatedPostsHelp(getActivity(), view);
                            PoinilaPreferences.putHelpStatus(PostListFragment.this.getClass().getName()+ ".PostPage", true);

                        }

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            mRecyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }

                    }
                });

                break;
            case ConstantsUtils.REQUEST_COLLECTION_POSTS:
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

                getActivity().setTitle(byDeepLink(mainEntityId) ? mainEntityId : secondEntityId);
                collection = DataRepository.getInstance().getTempModel(Collection.class);
                if (collection == null)
                    return;
                fillCollection(collection);
                setLoading(new Loading());

                break;
            case REQUEST_EXPLORE:
                getActivity().setTitle(R.string.title_activity_explore);
                exploredTagView.setText(mainEntityId);
                break;
        }
    }

    @Subscribe
    public void onNewUrlImagePostEvent(NewWebsitePostEvent event){
        //DialogLauncher.launchNewPost(getChildFragmentManager(), event.suggestedPost);
        PageChanger.goToNewPost(getFragmentManager(), event.suggestedPost);
    }

    private void updateFollowButton() {
        setText(followButton, collection.followedByMe
                ? getString(R.string.unfollow_item)
                : getString(R.string.follow_item));

        followButtonImg.setSelected(collection.followedByMe);
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

    @Subscribe
    public void onPostReceived(PostReceivedEvent event) {
        onGettingInitDataResponse(event);
    }

    @Subscribe
    public void onRemovePost(RemovePostUIEvent event) {
        clickedItemPosition = event.adapterPosition;
        new PoinilaAlertDialog.Builder().setMessage(R.string.confirm_delete_post).
                setPositiveBtnText(R.string.yes).setNegativeBtnText(R.string.no).
                build().show(getFragmentManager(), null);
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

    @Nullable
    @OnClick(R.id.follow_button)
    public void onFollowCollection(Button followButton) {
        if (DataRepository.isUserAnonymous()) {
            Logger.toastError(R.string.error_guest_action);
            return;
        }

        if (collection.followedByMe) {
            PoinilaNetService.unfollowCollection(collection.getId());
            collection.followedByMe = false;
        } else {
            PoinilaNetService.followCollection(collection.getId());
            collection.followedByMe = true;
        }
        updateFollowButton();
    }

   /* @Nullable @OnClick(R.id.edit_button) public void onEditCollection(){
        DataRepository.getInstance().putTempModel(collection);
        new PoinilaDialog.Builder().setTitle(R.string.edit_collection).
                setPositiveText(R.string.submit).
                setNegativeText(R.string.cancel).
                setBody(new EditCollectionDialog(collection)).
                build().show(getChildFragmentManager(), null);
    }*/

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
    public void onPositiveDialogButtonClicked(PositiveButtonClickedUIEvent event) {
        switch (requestType) {
            case REQUEST_MEMBER_POSTS:
                PoinilaNetService.deletePost(((Post)getRecyclerViewAdapter().getItem(clickedItemPosition)).getId());
                getRecyclerViewAdapter().removeItem(clickedItemPosition);
                //setText(itemCountView, --collection.postCount);
                break;
            case REQUEST_COLLECTION_POSTS:
                PoinilaNetService.deleteCollection(collection);
                getActivity().finish();
                break;
        }
    }

    public void populateIfNecessary(PostsReceivedEvent event) {
        if (collection != null) {
            for (Post post : event.posts) {
                post.collection = collection;
                //post.author = collection.owner;
            }
        }
    }

    // response for getting collection info OR editing it.
    @Subscribe
    public void onCollectionInfoReceived(CollectionReceivedEvent event) {
        onGettingInitDataResponse(event);
    }

    @Subscribe
    public void onContentReceivedEvent(final ContentReceivedEvent event) {
        final int postIndex = findPostInAdapter(getRecyclerViewAdapter().getItems(), event.postID);
        if (postIndex != -1) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ((Post)getRecyclerViewAdapter().getItem(postIndex)).content = StringUtils.removeHtmlDirAttribute(event.content);
                    getRecyclerViewAdapter().notifyItemChanged(postIndex);
                }
            });
        }
    }

    public static int findPostInAdapter(List<Post> adapter, int postID) {
        for (int i = 0; i < adapter.size(); i++) {
            if (adapter.get(i) instanceof Post && adapter.get(i).id == postID) // take account of ask rating item
                return i;
        }
        return -1;
    }

    /*---------------*/

    @Override
    protected boolean sendsRequestAutomatically() {
        return true;
    }

    @Override
    protected void requestInitialData() {
        // TODO: is necessary to invoke listData requests or it gets called by listener?
        if (requestType == REQUEST_COLLECTION_POSTS) {
            // deep links are unique with two parameters -> user name, collection name
            Log.i(getClass().getName(), "collection unige name : " + (byDeepLink(mainEntityId) ? secondEntityId : null));
            DataRepository.getInstance().getCollection(
                    mainEntityId,
                    byDeepLink(mainEntityId) ? secondEntityId : null,
                    RequestSource.FORCE_ONLINE);
        } else if (requestType == REQUEST_POST_RELATED_POSTS) {
            int requestId = RandomUtils.getRandomInt();
            activeRequests.add(requestId);
            DataRepository.getInstance().getPost(mainEntityId, RequestSource.FORCE_ONLINE, requestId);
            setLoading(new Loading());
        }
        requestForMoreData();
    }

    @Override
    public boolean mustShowProgressView() {
        // always show except on related post when main post is present
        return !(requestType == REQUEST_POST_RELATED_POSTS && !getRecyclerViewAdapter().isEmpty() && getRecyclerViewAdapter().getItem(0) != null);
    }

    @Override
    public void requestForMoreData() {
        int requestId = RandomUtils.getRandomInt();
        activeRequests.add(requestId);
        switch (requestType) {
            case REQUEST_MEMBER_FAVED_POSTS:
                PoinilaNetService.getFavedPostByMember(mainEntityId, bookmark);
                break;
            case REQUEST_MEMBER_POSTS:
                PoinilaNetService.getMemberPosts(mainEntityId, bookmark);
                break;
            case REQUEST_COLLECTION_POSTS:
                DataRepository.getCollectionPosts(
                        mainEntityId,
                        byDeepLink(mainEntityId) ? secondEntityId : null,
                        bookmark,
                        BaseEvent.ReceiverName.PostListFragment);
                break;
            case REQUEST_POST_RELATED_POSTS:
                // experimental
                PoinilaNetService.getRelatedPosts(mainEntityId, bookmark, requestId);
                break;
            case REQUEST_EXPLORE:
                PoinilaNetService.explore(mainEntityId, bookmark);
                break;
        }
    }

    @Override
    public RecyclerViewAdapter createAndReturnRVAdapter() {
        final BaseEvent.ReceiverName receiverName = (requestType == REQUEST_POST_RELATED_POSTS)
                ? BaseEvent.ReceiverName.PostRelatedPosts : BaseEvent.ReceiverName.PostListFragment;
        switch (requestType) {
            case REQUEST_POST_RELATED_POSTS:
                return new PostAndRelatedPostAdapter(getActivity());
            case REQUEST_EXPLORE:
            case REQUEST_MEMBER_FAVED_POSTS:
                return new RecyclerViewAdapter(getActivity(), R.layout.post_dashboard) {
                    @Override
                    protected BaseViewHolder getProperViewHolder(View v, int viewType) {

                        if(viewType == VIEW_TYPE_LOAD_PROGRESS){
                            return new BaseViewHolder.EmptyViewHolder(v);
                        }

                        return new DashboardPostViewHolder(v, receiverName);
                    }

                    @Override
                    public void onBindViewHolder(BaseViewHolder holder, int position) {
                        if (getItemViewType(position) == VIEW_TYPE_LOAD_PROGRESS) {
                            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
                            layoutParams.setFullSpan(true);
                        } else {
                            super.onBindViewHolder(holder, position);
                        }
                    }
                };
            case REQUEST_MEMBER_POSTS:
                if (mainEntityId.equals(DataRepository.getInstance().getMyId())) { // user posts
                    return new RecyclerViewAdapter(getActivity(), R.layout.post_item_removable) {
                        @Override
                        protected BaseViewHolder getProperViewHolder(View v, int viewType) {

                            if(viewType == VIEW_TYPE_LOAD_PROGRESS){
                                return new BaseViewHolder.EmptyViewHolder(v);
                            }

                            return new RemovablePostViewHolder(v, receiverName);
                        }

                        @Override
                        protected boolean isStaggeredGridLayoutManager() {
                            return true;
                        }
                    };
                } else {
                    return new RecyclerViewAdapter(getActivity(), R.layout.post_dashboard) {
                        @Override
                        protected BaseViewHolder getProperViewHolder(View v, int viewType) {

                            if(viewType == VIEW_TYPE_LOAD_PROGRESS){
                                return new BaseViewHolder.EmptyViewHolder(v);
                            }

                            return new DashboardPostViewHolder(v, receiverName);
                        }

                        @Override
                        protected boolean isStaggeredGridLayoutManager() {
                            return true;
                        }
                    };
                }
            case ConstantsUtils.REQUEST_COLLECTION_POSTS:
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
        return null;
    }

    @Override
    public void onSuccessfulListData(BaseEvent baseEvent, String newBookmark) {
        super.onSuccessfulListData(baseEvent, newBookmark);
        List<Post> posts = ((PostsReceivedEvent) baseEvent).posts;

        getRecyclerViewAdapter().addItems(posts);

        if(!showedHelp && !PoinilaPreferences.getHelpStatus(getClass().getName()+ ".CollectionPage") && requestType == REQUEST_COLLECTION_POSTS){
            showHelp();
            showedHelp = true;
            PoinilaPreferences.putHelpStatus(getClass().getName() + ".CollectionPage", true);
        }

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

    @Override
    protected boolean isInitDataResponseValid(BaseEvent baseEvent) {
        boolean res = true;
        if (baseEvent instanceof CollectionReceivedEvent) {
            res = requestType == REQUEST_COLLECTION_POSTS;
        } else if (baseEvent instanceof PostReceivedEvent) {
            res = (requestType == REQUEST_POST_RELATED_POSTS &&
                    activeRequests.contains(((PostReceivedEvent) baseEvent).requestId));
        }
        return res && super.isInitDataResponseValid(baseEvent);
    }

    @Override
    protected boolean isListDataResponseValid(BaseEvent baseEvent, String responseBookmark) {
        boolean res;
        PostsReceivedEvent event = (PostsReceivedEvent) baseEvent;
        if (requestType == REQUEST_POST_RELATED_POSTS)
            res = (event.receiverName == BaseEvent.ReceiverName.PostRelatedPosts
                    && activeRequests.contains(event.requestId));
        else if (requestType == REQUEST_EXPLORE)
            res = event.receiverName == BaseEvent.ReceiverName.ExploredTagPosts;
        else
            res = event.receiverName == BaseEvent.ReceiverName.PostListFragment;
        /*switch (requestID) {
            case ((PostsReceivedEvent) baseEvent).receiverName
        }*/
        return res && super.isListDataResponseValid(baseEvent, responseBookmark);

    }

    /*----Related post stuff------*/
    // all event belong to first post in adapter which we show in full detail


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
            case Fave:
                favePost(post);
                getRecyclerViewAdapter().notifyItemChanged(0);
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

    private void favePost(Post post) {
        if (!post.favedByMe) {
            PoinilaNetService.favePost(post.getId());
            post.faveCount++;
            //post.favedByMe = true;
            //setText(faveCount, ++post.faveCount);
            //faveBtn.setSelected(true);
        } else {
            PoinilaNetService.unfavePost(post.getId());
            post.faveCount--;
            /*post.favedByMe = false;
            setText(faveCount, --post.faveCount);
            faveBtn.setSelected(false);*/
        }

        post.favedByMe ^= true;

        BusProvider.getSyncUIBus().post(new PostActionSyncEvent(post));

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        int currentOrientation = getResources().getConfiguration().orientation;



    }

    @Subscribe
    public void onUndofaveEvent(UndoFavePostEvent event) {
        ((Post)getRecyclerViewAdapter().getItem(0)).favedByMe = false;
        getRecyclerViewAdapter().notifyItemChanged(0);
        // TODO: ???
        //faveBtn.setSelected(false);
    }

    @Subscribe
    public void onUndoUnfaveEvent(UndoUnfavePostEvent event) {
        ((Post)getRecyclerViewAdapter().getItem(0)).favedByMe = true;
        getRecyclerViewAdapter().notifyItemChanged(0);
        // TODO: ???
        //faveBtn.setSelected(true);
    }

    @Subscribe
    public void onUpdateUiRepostEvent(UpdateUiRepostEvent event){

//        Logger.log(getClass().getName() + " isSuccess : " + event.isSuccess, Logger.LEVEL_INFO);
//
//        if(event.isSuccess)
//            ((Post)getRecyclerViewAdapter().getItem(0)).repostCount++;
//        else
//            ((Post)getRecyclerViewAdapter().getItem(0)).repostCount--;
//
//        getRecyclerViewAdapter().notifyItemChanged(0);
    }

    @Subscribe
    public void onUpdateUICommentEvent(UpdateUICommentEvent event){

        if(getRecyclerViewAdapter().isEmpty())return;

        if(event.action == UpdateUICommentEvent.INCREMENT_COMMENTS)
            ((Post)getRecyclerViewAdapter().getItem(0)).commentCount++;
        else
            ((Post)getRecyclerViewAdapter().getItem(0)).commentCount--;

        getRecyclerViewAdapter().notifyItemChanged(0);
    }

    private class PostAndRelatedPostAdapter extends RecyclerViewAdapter<Post, BaseViewHolder<Post>> {
        public static final int VIEW_TYPE_POST_FULL = 1;
        public static final int VIEW_TYPE_POST_ITEM = 2;

        public PostAndRelatedPostAdapter(Context context) {
            super(context, -1);
        }

        @Override
        protected PostDetailViewHolder getProperViewHolder(View v, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(BaseViewHolder<Post> holder, int position) {
            if (getItemViewType(position) == VIEW_TYPE_POST_FULL) {
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
                layoutParams.setFullSpan(true);
            }
            super.onBindViewHolder(holder, position);
        }

        @Override
        public BaseViewHolder<Post> onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == VIEW_TYPE_LOAD_PROGRESS){
                return new BaseViewHolder.EmptyViewHolder(mLayoutInflater.inflate(R.layout.progress, parent, false));
            }else if (viewType == VIEW_TYPE_POST_FULL)
                return new PostDetailViewHolder(mLayoutInflater.inflate(R.layout.post_full_detail, parent, false));
            else
                return new DashboardPostViewHolder(mLayoutInflater.inflate(R.layout.post_dashboard, parent, false), BaseEvent.ReceiverName.PostRelatedPosts);

        }

        @Override
        protected boolean isStaggeredGridLayoutManager() {
            return true;
        }

        @Override
        public int getItemViewType(int position) {
            int type = super.getItemViewType(position);

            if(type == VIEW_TYPE_LOAD_PROGRESS){
                return type;
            }

            return (position == 0) ? VIEW_TYPE_POST_FULL : VIEW_TYPE_POST_ITEM;
        }
    }

    private boolean byDeepLink(String collectionIdOrName) {
        return !StringUtils.isInteger(collectionIdOrName);
    }

    /*share collection and post*/
    //private ShareActionProvider mShareActionProvider;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        if (Arrays.asList(REQUEST_COLLECTION_POSTS, REQUEST_POST_RELATED_POSTS).contains(requestType)){

            // Inflate menu resource file.
            inflater.inflate(R.menu.menu_post, menu);

            // Hidden Report Item
            if(requestType != ConstantsUtils.REQUEST_POST_RELATED_POSTS)
                menu.findItem(R.id.menu_item_report).setVisible(false);

            /*// Locate MenuItem with ShareActionProvider
            MenuItem item = menu.findItem(R.id.menu_item_share);
            // Fetch reference to the share action provider
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);*/

        } else
            super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStart() {
        super.onStart();

        BusProvider.getSyncUIBus().register(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // handle item selection
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                // Handle this selection

                launchShareMenu(mainPost);
                return true;
            case R.id.menu_item_report:
                // Handle this selection
                if(getRecyclerViewAdapter().getItemCount() >= 1)
                    DialogLauncher.launchReportDialog(
                            getFragmentManager(),
                            R.string.report_post,
                            mainPost.id);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void launchShareMenu(Post post) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String extra = null;
        switch (requestType) {
            case REQUEST_COLLECTION_POSTS:

                extra = getString(R.string.checkout_this_collection) + "\n" +
                        getString(R.string.collection_share_url,
                                ConstantsUtils.POINILA_ORIGIN_ADDRESS,
                                Uri.encode(collection.owner.uniqueName),
                                Uri.encode(collection.name)) + "\n" +
                        getString(R.string.ponila_world_of_interest);

                break;
            case REQUEST_POST_RELATED_POSTS:
                extra = post.name + "\n\n" +
                        post.summary + "\n" +
                        getString(R.string.checkout_this_post) + "\n" +
                        getString(R.string.post_share_url, ConstantsUtils.POINILA_ORIGIN_ADDRESS,
                                ((Post)getRecyclerViewAdapter().getItem(0)).getId()) + "\n" +
                        getString(R.string.ponila_world_of_interest);
                break;
            default:
                return;
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, extra);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_dialog_title)));
    }


    @Subscribe
    public void onExploreTag(ExploreTagEvent event) {
        PageChanger.goToExplore(getActivity(), event.text);
    }

    public void showHelp(){
        Help.getInstance().showPostsOfCollectionHelp(getActivity(), followButton);
    }
}
