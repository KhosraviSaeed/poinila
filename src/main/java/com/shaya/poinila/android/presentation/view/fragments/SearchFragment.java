package com.shaya.poinila.android.presentation.view.fragments;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.uievent.CollectionClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.FriendCirclesUpdated;
import com.shaya.poinila.android.presentation.uievent.FriendshipClickEvent;
import com.shaya.poinila.android.presentation.uievent.MemberClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.NeutralDialogButtonClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.OnFollowUnfollowCollectionUIEvent;
import com.shaya.poinila.android.presentation.uievent.PositiveButtonClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.PostClickedUIEvent;
import com.shaya.poinila.android.presentation.view.dialog.DialogLauncher;
import com.shaya.poinila.android.presentation.viewholder.BaseViewHolder;
import com.shaya.poinila.android.presentation.viewholder.DashboardPostViewHolder;
import com.shaya.poinila.android.presentation.viewholder.FollowableCollectionViewHolder;
import com.shaya.poinila.android.presentation.viewholder.MemberViewHolder;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.util.ResourceUtils;
import com.squareup.otto.Subscribe;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import butterknife.Bind;
import butterknife.OnClick;
import data.PoinilaNetService;
import data.event.BaseEvent;
import data.event.CollectionsReceivedEvent;
import data.event.MembersReceivedEvent;
import data.event.PostsReceivedEvent;
import data.model.Collection;
import data.model.FriendRequestAnswer;
import data.model.FriendshipStatus;
import data.model.Loading;
import data.model.Member;
import data.model.Post;
import manager.DBFacade;
import manager.DataRepository;

import static android.support.v7.widget.StaggeredGridLayoutManager.VERTICAL;
import static java.util.Collections.singletonList;


// fragmenta ro joda nakardam be khatere bahse moshkelate inheritance tu subscrib haye otto.
// avval recyclerview ro GONE mizaram o adapteresh nulle chon set kardane listener ina be bag mikhore.


public class SearchFragment extends BusFragment {
    private static final String POST = "post";
    private static final String COLLECTION = "collection";
    private static final String MEMBER = "member";
    private static final String TAG = "search fragment";

    @Bind(R.id.recycler_view)
    protected RecyclerView mRecyclerView;

    private static final long POST_ANIMATION_DURATION = 400; //800; // milliseconds
    private static final long COLLECTION_ANIMATION_DURATION = 400; //600; // milliseconds
    private static final long MEMBER_ANIMATION_DURATION = 400;// 400; // milliseconds

    @Bind(R.id.search_btn)
    ImageButton searchButton;

    @Bind(R.id.search_field)
    EditText searchField;

    @Bind(R.id.post_tag)
    TextView post;

    @Bind(R.id.collection_tag)
    TextView collection;

    @Bind(R.id.member_tag)
    TextView member;

    @Bind(R.id.search_container)
    ViewGroup searchBox;

    private RecyclerViewAdapter mAdapter;
    private ViewPropertyAnimator first_animation;
    private ViewPropertyAnimator second_animation;
    private ViewPropertyAnimator third_animation;
    private String selectedCategory = "";
    private float search_container_right;
    private int tag_size;
    private float search_field_right;
    private Handler mHandler;
    private int MEMBER_DIST;
    private int COLLECTION_DIST;
    private int POST_DIST;
    private int TAG_WIDTH;
    private int VELOCITY;
    private String bookmark;
    private boolean requestingIsLocked = false;
    private String query;
    private RecyclerView.OnScrollListener recyclerViewListener;

    private boolean hasLoading = false;

    private RecyclerView.ItemDecoration mItemDecoration;


    public SearchFragment() {
        // Required empty public constructor

    }

    public static SearchFragment newInstance(){
        SearchFragment fragment = new SearchFragment();

        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        TAG_WIDTH = ResourceUtils.covertDpToPixel(R.dimen.search_tag_width);
        VELOCITY = (int) (TAG_WIDTH / (0.4)); // in dp/seconds

        runJustBeforeBeingDrawn(container, findSizesRunnable);

        mItemDecoration = new HorizontalDividerItemDecoration.Builder(getActivity())
                .sizeResId(R.dimen.divider_thickness)
                .marginResId(R.dimen.border_thin)
                .build();
        return v;
    }


    @Override
    public int getLayoutID() {
        return R.layout.fragment_search;
    }

    @Override
    protected void initUI() {
        mRecyclerView.setVisibility(View.INVISIBLE);

        searchField.setPadding(searchField.getPaddingLeft(), searchField.getPaddingTop(),
                searchField.getPaddingRight() +
                        //(int)ResourceUtils.getDimen(R.dimen.search_tag_width) +
                        (int) ResourceUtils.getDimen(R.dimen.padding_lvl2),
                searchField.getPaddingBottom());
        searchButton.setEnabled(false);

        searchField.setEnabled(false);
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchButton.performClick();
                }
                return false; // ??
            }
        });
    }

    @OnClick(R.id.search_btn)
    public void onSearchClicked() {
        loadMore = false;
        bookmark = null;
        if (mAdapter != null) {
            mAdapter.clear();
            mAdapter = null;
        }
        mRecyclerView.setVisibility(View.GONE);
        query = getQueryFromInputField();
        if (query == null) {
            Logger.toast(R.string.error_short_query);
            return;
        }
        requestingIsLocked = true;

        if(selectedCategory.equals(MEMBER)){
            mRecyclerView.addItemDecoration(mItemDecoration);
        }else {
            mRecyclerView.removeItemDecoration(mItemDecoration);
        }

        initData();
    }

    public void onLoadMore() {
        if (!requestingIsLocked) {
            requestingIsLocked = true;
            requestInitialData();
        }
    }

    @Override
    protected void requestInitialData() {
        switch (selectedCategory) {
            case POST:
                PoinilaNetService.searchPostWithQuery(singletonList(query), bookmark);
                break;
            case COLLECTION:
                PoinilaNetService.searchCollectionsWithQuery(singletonList(query), bookmark);
                break;
            case MEMBER:
                PoinilaNetService.searchMembersWithQuery(singletonList(query), bookmark);
                break;
        }
    }

    @Override
    protected boolean isInitDataResponseValid(BaseEvent baseEvent) {
        return super.isInitDataResponseValid(baseEvent) && !loadMore;
    }

    protected boolean isListDataResponseValid(BaseEvent baseEvent, String bookmark) {
        return !bookmark.equals(this.bookmark);
    }

    @Override
    public void onSuccessfulInitData(BaseEvent baseEvent) {
        requestingIsLocked = false;
        switch (selectedCategory) {
            case POST:
                if (((PostsReceivedEvent) baseEvent).posts.isEmpty())
                    Logger.toast(R.string.error_nothing_found);
                mRecyclerView = new RecyclerViewProvider(mRecyclerView).
                        setAdapter(getRecyclerViewAdapter()).
                        setStaggeredLayoutManager(VERTICAL, getResources().getInteger(R.integer.column_count)).
                        bindViewToAdapter();
                getRecyclerViewAdapter().resetData(((PostsReceivedEvent) baseEvent).posts);

                if(((PostsReceivedEvent) baseEvent).posts.size() >= 25){
                    setLoading(new Loading());
                }


                break;
            case COLLECTION:
                if (((CollectionsReceivedEvent) baseEvent).collections.isEmpty())
                    Logger.toast(R.string.error_nothing_found);
                mRecyclerView = new RecyclerViewProvider(mRecyclerView).
                        // TODO: changed from gridview to this because of follow bar on some collections
                                setAdapter(getRecyclerViewAdapter()).
                        setStaggeredLayoutManager(VERTICAL, getResources().getInteger(R.integer.column_count)).
                        bindViewToAdapter();
                getRecyclerViewAdapter().resetData(((CollectionsReceivedEvent) baseEvent).collections);

                if(((CollectionsReceivedEvent) baseEvent).collections.size() >= 25){
                    setLoading(new Loading());
                }

                break;
            case MEMBER:
                if (((MembersReceivedEvent) baseEvent).members.isEmpty())
                    Logger.toast(R.string.error_nothing_found);
                mRecyclerView = new RecyclerViewProvider(mRecyclerView).
                        setAdapter(getRecyclerViewAdapter()).
                        setLinearLayoutManager(VERTICAL).
                        bindViewToAdapter();
                getRecyclerViewAdapter().resetData(((MembersReceivedEvent) baseEvent).members);

                if(((MembersReceivedEvent) baseEvent).members.size() >= 25){
                    setLoading(new Loading());
                }

                break;
        }
        mRecyclerView.removeOnScrollListener(lastUsedListener);
        lastUsedListener = getRecyclerViewListener();
        mRecyclerView.addOnScrollListener(lastUsedListener);

        mRecyclerView.setVisibility(View.VISIBLE);
        super.onSuccessfulInitData(baseEvent);
    }

    public void onSuccessfulListData(BaseEvent baseEvent, String newBookmark) {
        requestingIsLocked = false;
        bookmark = newBookmark;
        switch (selectedCategory) {
            case POST:
                getRecyclerViewAdapter().addItems(((PostsReceivedEvent) baseEvent).posts);
                break;
            case COLLECTION:
                getRecyclerViewAdapter().addItems(((CollectionsReceivedEvent) baseEvent).collections);
                break;
            case MEMBER:
                getRecyclerViewAdapter().addItems(((MembersReceivedEvent) baseEvent).members);
                break;
        }
    }

    private void onGettingResult(BaseEvent event, String bookmark) {
        if (isInitDataResponseValid(event)) {
            this.bookmark = bookmark;
            onSuccessfulInitData(event);
            return;
        }

        if (isListDataResponseValid(event, bookmark)) {
            onSuccessfulListData(event, bookmark);
        }else{
            onEndListData();
        }
    }

    @Subscribe
    public void onSearchPostResults(PostsReceivedEvent event) {
        if (event.receiverName != BaseEvent.ReceiverName.SearchFragment ||
                !selectedCategory.equals(POST))
            return;
        onGettingResult(event, event.bookmark);
    }

    @Subscribe
    public void onSearchCollectionResults(CollectionsReceivedEvent event) {
        if (event.receiverName != BaseEvent.ReceiverName.SearchFragment ||
                !selectedCategory.equals(COLLECTION))
            return;
        onGettingResult(event, event.bookmark);
    }

    @Subscribe
    public void onSearchPeopleResults(MembersReceivedEvent event) {
        if (!selectedCategory.equals(MEMBER))
            return;
        onGettingResult(event, event.bookmark);
    }

    public void setLoading(Loading loading){
        hasLoading = true;
        getRecyclerViewAdapter().setLoading(loading);
    }

    public void removeLoading(){
        if(hasLoading) {
            hasLoading = false;
            getRecyclerViewAdapter().removeLoading();
        }
    }

    private void onEndListData(){
        removeLoading();
    }

    private RecyclerViewAdapter getRecyclerViewAdapter() {
        if (mAdapter == null) {
            final BaseEvent.ReceiverName receiverName = BaseEvent.ReceiverName.SearchFragment;
            switch (selectedCategory) {
                case POST:
                    mAdapter = new RecyclerViewAdapter(getContext(), R.layout.post_dashboard) {
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
                    break;
                case COLLECTION:
                    mAdapter = new RecyclerViewAdapter(getContext(), R.layout.collection_followable) {
                        @Override
                        protected BaseViewHolder getProperViewHolder(View v, int viewType) {

                            if(viewType == VIEW_TYPE_LOAD_PROGRESS){
                                return new BaseViewHolder.EmptyViewHolder(v);
                            }

                            return new FollowableCollectionViewHolder(v, receiverName);
                        }
                    };
                    break;
                case MEMBER:
                    mAdapter = new RecyclerViewAdapter(getContext(), R.layout.member_inlist) {
                        @Override
                        protected BaseViewHolder getProperViewHolder(View v, int viewType) {

                            if(viewType == VIEW_TYPE_LOAD_PROGRESS){
                                return new BaseViewHolder.EmptyViewHolder(v);
                            }

                            return new MemberViewHolder(v, receiverName);
                        }


                    };
                    break;
            }
        }
        return mAdapter;
    }

    @Override
    public boolean mustShowProgressView() {
        return true;
    }


    public ViewGroup getLoadableView() {
        return mRecyclerView;
    }

    @Override
    protected boolean sendsRequestAutomatically() {
        return false;
    }


    private String getQueryFromInputField() {
        String query = searchField.getText().toString();
        //return query;
        return (query.trim().length() <= 1) ? null : query;
    }

    @OnClick({R.id.post_tag, R.id.collection_tag, R.id.member_tag})
    public void onTagClick(View v) {
        if (selectedCategory.isEmpty()) {
            switch (v.getId()) {
                case R.id.post_tag:
                    selectedCategory = POST;
                    post.setBackgroundResource(R.drawable.search_tag_active);
                    animate(MEMBER_DIST, COLLECTION_DIST, POST_DIST - TAG_WIDTH);
                    break;
                case R.id.collection_tag:
                    selectedCategory = COLLECTION;
                    collection.setBackgroundResource(R.drawable.search_tag_active);
                    animate(MEMBER_DIST, COLLECTION_DIST - TAG_WIDTH, POST_DIST);
                    break;
                case R.id.member_tag:
                    selectedCategory = MEMBER;
                    member.setBackgroundResource(R.drawable.search_tag_active);
                    animate(MEMBER_DIST - TAG_WIDTH, COLLECTION_DIST, POST_DIST);
                    break;
            }
            searchField.setEnabled(true);
            searchButton.setEnabled(true);
        } else {
            animateBack();
            switch (v.getId()) {
                case R.id.post_tag:
                    post.setBackgroundResource(R.drawable.search_tag_inactive);
                    break;
                case R.id.collection_tag:
                    collection.setBackgroundResource(R.drawable.search_tag_inactive);
                    break;
                case R.id.member_tag:
                    member.setBackgroundResource(R.drawable.search_tag_inactive);
                    break;
            }
            searchField.setEnabled(false);
            searchButton.setEnabled(false);
            selectedCategory = "";
            searchField.setText("");
        }
    }

    public void animate(int memberDist, int collectionDist, int postDist) {
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(
                member, "x", member.getLeft() + memberDist).setDuration(MEMBER_ANIMATION_DURATION);//(memberDist * 1000/VELOCITY);
        ObjectAnimator oa2 = ObjectAnimator.ofFloat(
                collection, "x", collection.getLeft() + collectionDist).setDuration(COLLECTION_ANIMATION_DURATION);//(collectionDist * 1000/VELOCITY);
        ObjectAnimator oa3 = ObjectAnimator.ofFloat(
                post, "x", post.getLeft() + postDist).setDuration(POST_ANIMATION_DURATION);//(postDist * 1000/VELOCITY);
        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new AccelerateInterpolator());
        set.playTogether(oa1, oa2, oa3);
        set.start();
    }

    public void animateBack() {
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(
                member, "x", member.getLeft()).setDuration(MEMBER_ANIMATION_DURATION);//((long)Math.abs(member.getX() - member.getLeft()) * 1000/VELOCITY);
        ObjectAnimator oa2 = ObjectAnimator.ofFloat(
                collection, "x", collection.getLeft()).setDuration(COLLECTION_ANIMATION_DURATION);//((long)Math.abs(collection.getX() - collection.getLeft()) * 1000/VELOCITY);
        ObjectAnimator oa3 = ObjectAnimator.ofFloat(
                post, "x", post.getLeft()).setDuration(POST_ANIMATION_DURATION);//((long)Math.abs(post.getX() - post.getLeft()) * 1000/VELOCITY);
        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new AccelerateInterpolator());
        set.playTogether(oa1, oa2, oa3);
        set.start();
    }

    private static void runJustBeforeBeingDrawn(final View view, final Runnable runnable) {
        final ViewTreeObserver vto = view.getViewTreeObserver();
        final ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                //Log.d(App.APPLICATION_TAG, CLASS_TAG + "onpredraw");
                runnable.run();
                final ViewTreeObserver vto = view.getViewTreeObserver();
                vto.removeOnPreDrawListener(this);
                return true;
            }
        };
        vto.addOnPreDrawListener(preDrawListener);
    }

    Runnable findSizesRunnable = new Runnable() {
        @Override
        public void run() {
            MEMBER_DIST = post.getRight() - member.getLeft();
            COLLECTION_DIST = post.getRight() - collection.getLeft();
            POST_DIST = post.getRight() - post.getLeft();
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        searchField.setText("");
    }

    private boolean loadMore = false;

    public RecyclerView.OnScrollListener getRecyclerViewListener() {
        switch (selectedCategory) {
            case POST:
                return getPostListListener();
            case COLLECTION:
                return getCollectionListListener();
            case MEMBER:
                return getMemberScrollListener();
        }
        // must never happen
        return new RecyclerView.OnScrollListener() {
        };
    }

    private RecyclerView.OnScrollListener getPostListListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int[] lastVisibleItems = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).
                        findLastVisibleItemPositions(null);
                int itemCount = getRecyclerViewAdapter().getItemCount() - 1;
                if ((lastVisibleItems[0] == itemCount ||
                        lastVisibleItems[1] == itemCount) && dy != 0) {
                    loadMore = true;
                    onLoadMore();
                }
            }
        };
    }

    private RecyclerView.OnScrollListener getCollectionListListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int[] lastVisibleItems = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).
                        findLastVisibleItemPositions(null);
                int itemCount = getRecyclerViewAdapter().getItemCount() - 1;
                if ((lastVisibleItems[0] == itemCount ||
                        lastVisibleItems[1] == itemCount) && dy != 0) {
                    loadMore = true;
                    onLoadMore();
                }
            }
        };
    }

    private RecyclerView.OnScrollListener getMemberScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() ==
                        getRecyclerViewAdapter().getItemCount() - 1 && dy != 0) {
                    loadMore = true;
                    onLoadMore();
                }
            }
        };
    }

    RecyclerView.OnScrollListener lastUsedListener;


    
    /*----------------*/
    // TODO; I think its better we replace fragments with each other for taking use of
    // event handling on ListFragments like MemberListFragment

    /*--------Post----------*/

    @Subscribe
    public void onProfilePicClickedEvent(MemberClickedUIEvent event) {
        if (event.receiverName != BaseEvent.ReceiverName.SearchFragment)
            return;

        Object item = getRecyclerViewAdapter().getItem(event.adapterPosition);

        Member member = null;
        if (item instanceof Post) {
            member = ((Post) item).author;
        } else if (item instanceof Collection) {
            member = ((Collection) item).owner;
        } else if (item instanceof Member) {
            member = ((Member) item);
        }
        if (member == null) return;
        PageChanger.goToProfile(getActivity(), member);
    }

    @Subscribe
    public void onPostClicked(PostClickedUIEvent event) {
        if (event.receiverName != BaseEvent.ReceiverName.SearchFragment)
            return;
        Post post = ((Post) getRecyclerViewAdapter().getItem(event.adapterPosition));
        PageChanger.goToPost(getActivity(), post);
    }
    
    
    /*-----Collection------*/


    private int clickedItemPosition;

    @Subscribe
    public void onPositiveDialogButton(PositiveButtonClickedUIEvent event) {
        Member member = ((Member) getRecyclerViewAdapter().getItem(clickedItemPosition));
        switch (member.friendshipStatus) {
            case NotFriend:
                PoinilaNetService.friendRequest(member.getId(), DBFacade.getDefaultCircle().id);
                member.friendshipStatus = FriendshipStatus.Pending;
                break;
            case WaitingForAction: // sending request
                PoinilaNetService.answerFriendRequest(member.id, (FriendRequestAnswer)event.getData(), DBFacade.getDefaultCircle().id);
                member.friendshipStatus = FriendshipStatus.IsFriend;
                break;
            case IsFriend: // removing friend
                PoinilaNetService.removeFriend(member.getId());
                member.friendshipStatus = FriendshipStatus.NotFriend;
                break;
            case Pending: // no action yet
                break;
        }
        getRecyclerViewAdapter().notifyItemChanged(clickedItemPosition);
    }

    @Subscribe
    public void onCollectionClicked(CollectionClickedUIEvent event) {
        if (event.receiverName != BaseEvent.ReceiverName.SearchFragment)
            return;

        Object item = getRecyclerViewAdapter().getItem(event.adapterPosition);
        Collection collection = null;
        if (item instanceof Post) {
            collection = ((Post) item).collection;
        } else if (item instanceof Collection) {
            collection = ((Collection) item);
        }
        if (collection == null) return;
        PageChanger.goToCollection(getActivity(), collection);

    }

    /*-------Member------*/
    @Subscribe
    public void onNeutralDialogButton(NeutralDialogButtonClickedUIEvent event) {
        Member clickedMember = ((Member) getRecyclerViewAdapter().getItem(clickedItemPosition));
        DialogLauncher.launchChangeFriendCircle(getFragmentManager(), clickedMember);
    }

    @Subscribe
    public void onShowFriendShipDialog(FriendshipClickEvent event) {
        // handling anonymous
        if (DataRepository.isUserAnonymous()) {
            Logger.toastError(R.string.error_guest_action);
            return;
        }

        Member member = (Member) getRecyclerViewAdapter().getItem(event.adapterPosition);
        DialogLauncher.launchFriendshipDialog(member, getFragmentManager());
        clickedItemPosition = event.adapterPosition;
    }

    @Subscribe
    public void onFollowCollection(OnFollowUnfollowCollectionUIEvent event) {
        // handling anonymous
        if (DataRepository.isUserAnonymous()) {
            Logger.toastError(R.string.error_guest_action);
            return;
        }

        Collection collection = (Collection) getRecyclerViewAdapter().getItem(event.adapterPosition);
        if (event.follow) {
            PoinilaNetService.followCollection(collection.getId());
        } else {
            PoinilaNetService.unfollowCollection(collection.getId());
        }
        collection.followedByMe ^= true; // toggle boolean by xor ing with "True".
        getRecyclerViewAdapter().notifyItemChanged(event.adapterPosition);
    }

    @Subscribe
    public void onFriendCirclesUpdated(FriendCirclesUpdated event) {
        if (selectedCategory.equals(MEMBER)) {
            int index = getRecyclerViewAdapter().getItems().indexOf(event.member);
            if (index < 0) return;

            ((Member) getRecyclerViewAdapter().getItem(index)).circle_ids = event.selectedCirclesIDs;
        }
    }

}

