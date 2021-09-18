package com.shaya.poinila.android.presentation.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.shaya.poinila.android.presentation.AndroidUtilities;
import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
//import com.shaya.poinila.android.presentation.presenter.HelpProvider;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.uievent.CollectionClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.MemberClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.PostClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.RatePonilaEvent;
import com.shaya.poinila.android.presentation.uievent.UpdateUICommentEvent;
import com.shaya.poinila.android.presentation.uievent.UpdateUiRepostEvent;
import com.shaya.poinila.android.presentation.uievent.sync.PostActionSyncEvent;
import com.shaya.poinila.android.presentation.view.help.Help;
import com.shaya.poinila.android.presentation.view.dialog.DialogLauncher;
import com.shaya.poinila.android.presentation.view.fragments.DashboardFragment.DashboardRecyclerViewAdapter.AskIfUserLikesPonila;
import com.shaya.poinila.android.presentation.viewholder.AskUserLikesPonilaViewHolder;
import com.shaya.poinila.android.presentation.viewholder.BaseViewHolder;
import com.shaya.poinila.android.presentation.viewholder.DashboardPostViewHolder;
import com.shaya.poinila.android.presentation.viewholder.RatePonilaViewHolder;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.ConnectionUitls;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.util.PoinilaPreferences;
import com.shaya.poinila.android.util.StringUtils;
import com.shaya.poinila.android.utils.uisynchronize.UISynchronizeBus;
import com.squareup.otto.Subscribe;

import java.io.Serializable;

import data.event.BaseEvent;
import data.event.ContentReceivedEvent;
import data.event.DashboardEvent;
import data.model.Collection;
import data.model.Loading;
import data.model.Member;
import data.model.Post;
import manager.DataRepository;

import static com.shaya.poinila.android.presentation.view.fragments.PostListFragment.findPostInAdapter;

/**
 * <b>Important Documentation</b>
 * <p>On Creating this fragment, we request the server and cache both. we control reading
 * from cache by {@link #loadFromCache} (firstly true) and reading from server by
 * {@link ConnectionUitls#isNetworkOnline()} method.</p><br/>
 * If response is from the cache, we sets the received items count as offset for next db query.
 * (working in offline mode)<br/>
 * If response is from the server (which comes after response from cache logically!), loadFromCache
 * is set to false so we doesn't read db items anymore and items array (populating with cached items)
 * is cleared. In this case response bookmark is kept and
 * would be send with next request. <br/>
 * When refreshing (meaningful in online mode) items are added to list's head.
 */
public class DashboardFragment extends BusRefreshableListFragment {

    boolean loadFromCache;
    // offset in sql query when scrolling in offline mode
    private int cachedItems;
    protected boolean showedHelp = false;
//    private HelpProvider mHelpProvider;



//    @Bind(R.id.fab_menu)
//    FloatingActionsMenu fabMenu;
//
//    @Bind(R.id.fab_add_post_from_site)
//    FloatingActionButton addFromUrl;
//
//    @Bind(R.id.fab_add_post)
//    FloatingActionButton addPost;

    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadFromCache = true;

    }

    @Override
    public int getLayoutID() {
        return R.layout.recycler_view_full;
    }

    @Override
    protected void initUI() {
        super.initUI();
        mRecyclerView = new RecyclerViewProvider(mRecyclerView).
                setStaggeredLayoutManager(StaggeredGridLayoutManager.VERTICAL,
                        getResources().getInteger(R.integer.column_count)).
                setAdapter(getRecyclerViewAdapter()).
                bindViewToAdapter();
    }

    @Override
    public void onStart() {
        super.onStart();

        BusProvider.getSyncUIBus().register(this);
    }

    // TODO: candidate of refactoring. Can use static method from `RecyclerViewProvider` instead.
    @Override
    protected RecyclerView.OnScrollListener getRecyclerViewListener() {
        return RecyclerViewProvider.staggeredListEndDetectorListener(getRecyclerViewAdapter(), this);
    }

    @Override
    public void requestForMoreData() {
        DataRepository.getInstance().getSuggestions(ConnectionUitls.isNetworkOnline(),
                loadFromCache, bookmark, cachedItems);
    }

    @Subscribe
    public void onPostActionSyncEvent(PostActionSyncEvent event){

        int position = ((DashboardRecyclerViewAdapter)getRecyclerViewAdapter())
                .getItemPositionByPostId(event.post.id);

        if(position == -1) return;

        Post post = (Post)getRecyclerViewAdapter().getItem(position);
        post.faveCount += event.post.favedByMe ? 1: -1;
        post.favedByMe = event.post.favedByMe;
        getRecyclerViewAdapter().setItem(post, position);
        getRecyclerViewAdapter().notifyItemChanged(position);
    }

    @Subscribe
    public void onUpdateUiRepostEvent(UpdateUiRepostEvent event){

//        int position = ((DashboardRecyclerViewAdapter)getRecyclerViewAdapter())
//                .getItemPositionByPostId(event.postId);
//
//        if(position == -1) return;
//
//        Post post = (Post)getRecyclerViewAdapter().getItem(position);
//        post.repostCount += event.isSuccess ? 1: -1;
//        getRecyclerViewAdapter().setItem(post, position);
//        getRecyclerViewAdapter().notifyItemChanged(position);
    }

    @Subscribe
    public void onUpdateUICommentEvent(UpdateUICommentEvent event){

        int position = ((DashboardRecyclerViewAdapter)getRecyclerViewAdapter())
                .getItemPositionByPostId(Integer.parseInt(event.postId));

        if(position == -1) return;

        if(event.action == UpdateUICommentEvent.INCREMENT_COMMENTS)
            ((Post)getRecyclerViewAdapter().getItem(position)).commentCount++;
        else
            ((Post)getRecyclerViewAdapter().getItem(position)).commentCount--;

        getRecyclerViewAdapter().notifyItemChanged(position);
    }

    @Override
    protected void requestInitialData() {
        refresh();
    }

//    @OnClick(R.id.fab_add_post) public void onAddPost(){
//        fabMenu.collapse();
//        //DialogLauncher.launchNewPost(getChildFragmentManager(), null);
//        PageChanger.goToNewPost(getFragmentManager(), null);
//    }
//
//    @OnClick(R.id.fab_add_post_from_site) public void onAddPostFromUrl(){
//        // todo: dialog, its layout and how to get a url imagesUrls;
//        fabMenu.collapse();
//        DialogLauncher.launchNewWebsitePost(getFragmentManager());
//    }

    @Subscribe
    public void onPostClickedEvent(PostClickedUIEvent event) {
        if (event.receiverName != BaseEvent.ReceiverName.DashboardFragment) return;
        PageChanger.goToPost(getActivity(), (Post) getRecyclerViewAdapter().getItem(event.adapterPosition));
    }

    @Subscribe
    public void onProfilePicClickedEvent(MemberClickedUIEvent event) {
        if (event.receiverName != BaseEvent.ReceiverName.DashboardFragment) return;
        Member member =((Post)getRecyclerViewAdapter().getItem(event.adapterPosition)).author;
        PageChanger.goToProfile(getActivity(), member);
    }

    @Subscribe
    public void onPostCollectionClickedEvent(CollectionClickedUIEvent event) {
        if (event.receiverName != BaseEvent.ReceiverName.DashboardFragment) return;
        Collection collection = ((Post)getRecyclerViewAdapter().getItem(event.adapterPosition)).collection;
        PageChanger.goToCollection(getActivity(), collection);
    }

    @Subscribe
    public void onSuggestedPostsReceived(DashboardEvent event) {
        super.onGettingInitDataResponse(event);
        super.onGettingListDataResponse(event, event.bookmark);
    }

    @Override
    protected boolean isListDataResponseValid(BaseEvent baseEvent, String responseBookmark) {
        DashboardEvent event = ((DashboardEvent) baseEvent);
        return event.getData() != null && event.getData().size() != 0 && super.isListDataResponseValid(event, responseBookmark);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RecyclerViewAdapter createAndReturnRVAdapter() {
        return new DashboardRecyclerViewAdapter(getActivity(), -1, getFragmentManager());
    }


    @Override
    public void onSuccessfulListData(BaseEvent baseEvent, String newBookmark) {
        super.onSuccessfulListData(baseEvent, newBookmark);
        DashboardEvent dashboardEvent = ((DashboardEvent) baseEvent);
        if (dashboardEvent.isFromCache) {  // from cache/db
            setLoading(new Loading());
            getRecyclerViewAdapter().addItems(dashboardEvent.getData());
            cachedItems = cachedItems + dashboardEvent.getData().size();
        } else {                  // from server
            loadFromCache = false;
            // hiding swipe refresh layout and adding items to head
            if (swipeRefreshLayout.isRefreshing()) {
                onRefreshFinished();


                //mAdapter.addItemsToListHead(event.getData());
                getRecyclerViewAdapter().resetData(dashboardEvent.getData());
                // TODO: needs better approach. why we should clear on every refresh?

                setLoading(new Loading());

                // checking if it's the time to show "ask rating"
                if (DataRepository.shouldAskForRating()) {
                    //noinspection unchecked
                    getRecyclerViewAdapter().getUngenericedItems().add(0, new AskIfUserLikesPonila());
                    getRecyclerViewAdapter().notifyItemChanged(0);
                }
            } else {
                getRecyclerViewAdapter().addItems(dashboardEvent.getData());
            }


            DataRepository.getInstance().saveSuggestions(dashboardEvent.getData());
        }

        if(!showedHelp && !PoinilaPreferences.getHelpStatus(getClass().getName())){
            this.showedHelp = true;
            PoinilaPreferences.putHelpStatus(getClass().getName(), true);
            showHelp();
        }

    }

    @Override
    protected boolean sendsRequestAutomatically() {
        return true;
    }

    @Override
    public void refresh() {
        bookmark = null; // TODO: is it necessary?!
        swipeRefreshLayout.setRefreshing(true);
        requestForMoreData();
        if (!ConnectionUitls.isNetworkOnline())
            Logger.toast(R.string.warning_connect_to_network);
        //refreshRequest = true;
    }

    @Subscribe
    public void onContentReceivedEvent(final ContentReceivedEvent event) {
        final int postIndex = findPostInAdapter(getRecyclerViewAdapter().getItems(), event.postID);
        if (postIndex != -1) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ((Post) getRecyclerViewAdapter().getItem(postIndex)).content = StringUtils.removeHtmlDirAttribute(event.content);
                    getRecyclerViewAdapter().notifyItemChanged(postIndex);
                }
            });
        }
    }




    @Override
    public boolean mustShowProgressView() {
        return false;
    }

    // no need implement since it doesn't show progressBar. Bad design :)
    @Override
    public ViewGroup getLoadableView() {
        return null;
    }

    @Subscribe
    public void onRateApplication(RatePonilaEvent event) {
        // TODO: read market destination from server response
        AndroidUtilities.rateApplication(getActivity(), DataRepository.getDestinationMarket());
    }

    public void showHelp() {

        if(getRecyclerViewAdapter().getItem(0) instanceof Post){
            mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    Help.getInstance().showDashboardHelp(getActivity(), mRecyclerView.getLayoutManager().findViewByPosition(0));
                    viewedHelp = true;

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        mRecyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });

        }
    }

    @Override
    public UISynchronizeBus.UI_SYNCHRONIZE_ACTION getSynchronizeAction() {
        return UISynchronizeBus.UI_SYNCHRONIZE_ACTION.UPDATE_DASHBOARD_POST;
    }

    @Override
    public void loadDataForSynchronize(Serializable data, UISynchronizeBus.UI_SYNCHRONIZE_ACTION action) {
        super.loadDataForSynchronize(data, action);
        Log.i(getClass().getName(), "loadDataForSynchronize");

    }

    public static class DashboardRecyclerViewAdapter extends RecyclerViewAdapter {
        public static final int VIEW_TYPE_POST = 1;
        public static final int VIEW_TYPE_RATE_APP = 2;
        private static final int VIEW_TYPE_LIKES_APP = 3;
        private final android.support.v4.app.FragmentManager fragmentManager;

        public DashboardRecyclerViewAdapter(Context context, @LayoutRes int itemLayoutID, android.support.v4.app.FragmentManager fragmentManager) {
            super(context, itemLayoutID);
            this.fragmentManager = fragmentManager;
        }

        @Override
        protected BaseViewHolder getProperViewHolder(View v, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == VIEW_TYPE_RATE_APP || getItemViewType(position) == VIEW_TYPE_LIKES_APP || getItemViewType(position) == VIEW_TYPE_LOAD_PROGRESS) {
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
                layoutParams.setFullSpan(true);
            } else {
                ((DashboardPostViewHolder) holder).fill(((Post) getItem(position)));
            }
        }

        public int getItemPositionByPostId(int id){
            int length = getItems().size();
            for( int i = 0 ; i < length ; i++){
                if(getItem(i) instanceof Post){
                    Post post = (Post)getItem(i);
                    if(post.id == id) return i;
                }
            }
            return -1;
        }

        @Override
        protected boolean isStaggeredGridLayoutManager() {
            return true;
        }

        // TODO: can be written cleaner
        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_LIKES_APP) {
                final AskUserLikesPonilaViewHolder holder = new AskUserLikesPonilaViewHolder(mLayoutInflater.inflate(R.layout.ask_if_user_likes_ponila, parent, false));
                holder.dontKnowButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeItem(holder.getAdapterPosition());
                        // TODO increase show rate dialog ask interval
                        DataRepository.updateAskRatingThreshold(false);
                    }
                });
                holder.positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setItem(new AskIfUserRatesPonila(), 0);
                    }
                });
                holder.negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeItem(holder.getAdapterPosition());
                        DialogLauncher.launchContactUsDialog(fragmentManager);
                        // TODO disable asking (later we want to ask in every update)
                        DataRepository.updateAskRatingThreshold(true);
                    }
                });
                return holder;
            } else if (viewType == VIEW_TYPE_RATE_APP) {
                final RatePonilaViewHolder holder = new RatePonilaViewHolder(mLayoutInflater.inflate(R.layout.ask_if_user_rates_ponila, parent, false));
                holder.notNowButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeItem(holder.getAdapterPosition());
                        DataRepository.updateAskRatingThreshold(false);
                    }
                });
                holder.positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeItem(holder.getAdapterPosition());
                        DataRepository.updateAskRatingThreshold(true);
                        BusProvider.getBus().post(new RatePonilaEvent());
                    }
                });
                holder.negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeItem(holder.getAdapterPosition());
                        DataRepository.updateAskRatingThreshold(true);
                    }
                });
                return holder;
            }else if(viewType == VIEW_TYPE_LOAD_PROGRESS){
                return new BaseViewHolder.EmptyViewHolder(mLayoutInflater.inflate(R.layout.progress, parent, false));
            }else
                return new DashboardPostViewHolder(mLayoutInflater.inflate(R.layout.post_dashboard, parent, false), BaseEvent.ReceiverName.DashboardFragment);

        }



        @Override
        public int getItemViewType(int position) {
            int type = super.getItemViewType(position);
            if(type == VIEW_TYPE_LOAD_PROGRESS){
                return super.getItemViewType(position);
            }
            if (getItem(position) instanceof Post)
                return VIEW_TYPE_POST;
            else if (getItem(position) instanceof AskIfUserLikesPonila)
                return VIEW_TYPE_LIKES_APP;
            else
                return VIEW_TYPE_RATE_APP;

        }

        /* do not delete */
        public static class AskIfUserLikesPonila {
        }

        public static class AskIfUserRatesPonila {
        }
    }

}
