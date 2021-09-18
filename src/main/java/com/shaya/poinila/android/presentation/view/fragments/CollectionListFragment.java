package com.shaya.poinila.android.presentation.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.uievent.CollectionClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.EditItemUIEvent;
import com.shaya.poinila.android.presentation.uievent.MemberClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.OnFollowUnfollowCollectionUIEvent;
import com.shaya.poinila.android.presentation.uievent.PositiveButtonClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.RemoveItemUIEvent;
import com.shaya.poinila.android.presentation.view.dialog.DialogLauncher;
import com.shaya.poinila.android.presentation.view.dialog.NewCollectionDialog;
import com.shaya.poinila.android.presentation.viewholder.BaseViewHolder;
import com.shaya.poinila.android.presentation.viewholder.CollectionViewHolder;
import com.shaya.poinila.android.presentation.viewholder.DashboardPostViewHolder;
import com.shaya.poinila.android.presentation.viewholder.EditableCollectionViewHolder;
import com.shaya.poinila.android.presentation.viewholder.FollowableCollectionViewHolder;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.Logger;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import data.PoinilaNetService;
import data.event.BaseEvent;
import data.event.CollectionReceivedEvent;
import data.event.CollectionUpdatedEvent;
import data.event.CollectionsReceivedEvent;
import data.model.Collection;
import data.model.Loading;
import data.model.Member;
import data.model.Post;
import manager.DataRepository;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_ENTITY;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_ITEM_COUNT;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_REQUEST_ID;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_MEMBER_COLLECTIONS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_MEMBER_FOLLOWED_COLLECTIONS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_POST_REPOSTING_COLLECTIONS;
import static com.shaya.poinila.android.util.ResourceUtils.getInteger;


/**
 * Created by iran on 2015-08-06.
 */
public class CollectionListFragment extends ListBusFragment {

    private String mainActorID;
    private int requestID;


    @Nullable
    @Bind(R.id.item_count)
    TextView mItemCountView;

    FloatingActionButton fabMenu;

    public static android.support.v4.app.Fragment newInstance(String id, int requestID) {
        CollectionListFragment f = new CollectionListFragment();
        Bundle b = new Bundle();
        b.putString(KEY_ENTITY, id);
        b.putInt(KEY_REQUEST_ID, requestID);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mainActorID = getArguments().getString(KEY_ENTITY);
            requestID = getArguments().getInt(KEY_REQUEST_ID);
        }
    }


    @Override
    protected RecyclerView.OnScrollListener getRecyclerViewListener() {
        switch (requestID) {
            case REQUEST_POST_REPOSTING_COLLECTIONS:
            case REQUEST_MEMBER_FOLLOWED_COLLECTIONS:
                return RecyclerViewProvider.staggeredListEndDetectorListener(getRecyclerViewAdapter(), this);
            case REQUEST_MEMBER_COLLECTIONS:
                return (areMyCollections() ?
                        RecyclerViewProvider.gridListEndDetectionListener(getRecyclerViewAdapter(), this) :
                        RecyclerViewProvider.staggeredListEndDetectorListener(getRecyclerViewAdapter(), this));
            default: // my followed collections
                return RecyclerViewProvider.gridListEndDetectionListener(getRecyclerViewAdapter(), this);
        }
    }


    @Subscribe
    public void onCollectionsReceived(CollectionsReceivedEvent event) {

        if(event.collections.size() >= 25 && mRecyclerView.getAdapter().getItemCount() == 0){
            setLoading(new Loading());
        }

        onGettingInitDataResponse(event);
        onGettingListDataResponse(event, event.bookmark);


    }

    @Override
    public void onSuccessfulListData(BaseEvent baseEvent, String newBookmark) {
        super.onSuccessfulListData(baseEvent, newBookmark);
        getRecyclerViewAdapter().addItems(((CollectionsReceivedEvent) baseEvent).collections);
    }

    @Override
    public void onEndListData() {
        removeLoading();
    }

    @Override
    public int getLayoutID() {
        if (requestID == REQUEST_POST_REPOSTING_COLLECTIONS)
            return R.layout.fragment_reposts;
        if (requestID == REQUEST_MEMBER_COLLECTIONS)
            return R.layout.fragment_member_collections;
        return R.layout.recycler_view_full;
    }

    @Override
    protected void initUI() {
        super.initUI();
        Bundle b = getArguments();
        requestID = b.getInt(KEY_REQUEST_ID);
        mainActorID = b.getString(KEY_ENTITY);
        switch (requestID) {
            case ConstantsUtils.REQUEST_MEMBER_COLLECTIONS:
                if (areMyCollections()) {
                    mRecyclerView = new RecyclerViewProvider(mRecyclerView).
                            setAdapter(getRecyclerViewAdapter()).
                            setGridLayoutManager(VERTICAL, getResources().getInteger(R.integer.column_count), new GridLayoutManager.SpanSizeLookup() {
                                @Override
                                public int getSpanSize(int position) {
                                    if(getRecyclerViewAdapter().getItemViewType(position) == RecyclerViewAdapter.VIEW_TYPE_LOAD_PROGRESS ){
                                        return getResources().getInteger(R.integer.column_count);
                                    }
                                    return 1;
                                }
                            }).
                            bindViewToAdapter();


                    fabMenu = (FloatingActionButton)rootView.findViewById(R.id.fab_add_collection);

                    fabMenu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new NewCollectionDialog().show(getFragmentManager(), null);
                        }
                    });
                    break;
                }
            case REQUEST_POST_REPOSTING_COLLECTIONS:
                setText(mItemCountView, getString(R.string.reposts_formatted,
                        getActivity().getIntent().getIntExtra(KEY_ITEM_COUNT, 0)));
            case REQUEST_MEMBER_FOLLOWED_COLLECTIONS:
                mRecyclerView = new RecyclerViewProvider(mRecyclerView).
                        setAdapter(getRecyclerViewAdapter()).
                        setStaggeredLayoutManager(VERTICAL, getInteger(R.integer.column_count)).
                        bindViewToAdapter();
                break;
        }
        mRecyclerView.getItemAnimator().setChangeDuration(0);


    }

    @Override
    public RecyclerViewAdapter createAndReturnRVAdapter() {
        switch (requestID) {
            case ConstantsUtils.REQUEST_MEMBER_COLLECTIONS:
                if (areMyCollections())
                    return new RecyclerViewAdapter(getActivity(), R.layout.collection_editable) {
                        @Override
                        protected BaseViewHolder getProperViewHolder(View v, int viewType) {
                            if(viewType == RecyclerViewAdapter.VIEW_TYPE_LOAD_PROGRESS ){
                                return new BaseViewHolder.EmptyViewHolder(v);
                            }
                            return new EditableCollectionViewHolder(v, BaseEvent.ReceiverName.CollectionListFragment);
                        }
                    };
            case REQUEST_POST_REPOSTING_COLLECTIONS:
            case REQUEST_MEMBER_FOLLOWED_COLLECTIONS:
                return new RecyclerViewAdapter(getActivity(), R.layout.collection_followable) {

                    @Override
                    protected BaseViewHolder getProperViewHolder(View v, int viewType) {
                        if(viewType == RecyclerViewAdapter.VIEW_TYPE_LOAD_PROGRESS ){
                            return new BaseViewHolder.EmptyViewHolder(v);
                        }
                        return new FollowableCollectionViewHolder(v, BaseEvent.ReceiverName.CollectionListFragment);
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
            default:
                return null;
        }
    }

    private boolean areMyCollections() {
        return mainActorID.equals(DataRepository.getInstance().getMyId());
    }


    @Subscribe
    public void onRemoveCollection(RemoveItemUIEvent event) {
        // must not happen but anyway... :)
        if (DataRepository.isUserAnonymous()){
            Logger.toastError(R.string.error_guest_action);
            return;
        }

        clickedItemPosition = event.adapterPosition;
        DialogLauncher.launchDeleteCollection(getFragmentManager());
    }

    @Subscribe
    public void onEditCollection(EditItemUIEvent event) {
        // must not happen but anyway... :)
        if (DataRepository.isUserAnonymous()){
            Logger.toastError(R.string.error_guest_action);
            return;
        }

        clickedItemPosition = event.adapterPosition;
        Collection collection = (Collection)getRecyclerViewAdapter().getItem(event.adapterPosition);
        DialogLauncher.launchEditCollectionDialog(getFragmentManager(), collection);
    }

    @Subscribe
    public void onPositiveDialogButtonClicked(PositiveButtonClickedUIEvent event) {
        PoinilaNetService.deleteCollection((Collection)getRecyclerViewAdapter().getItem(clickedItemPosition));
        getRecyclerViewAdapter().removeItem(clickedItemPosition);
    }

    @Subscribe
    public void onFollowCollection(OnFollowUnfollowCollectionUIEvent event) {
        if (DataRepository.isUserAnonymous()){
            Logger.toastError(R.string.error_guest_action);
            return;
        }

        if (event.follow) {
            PoinilaNetService.followCollection(((Collection)getRecyclerViewAdapter().getItem(event.adapterPosition)).getId());
        } else {
            PoinilaNetService.unfollowCollection(((Collection)getRecyclerViewAdapter().getItem(event.adapterPosition)).getId());
        }
        ((Collection)getRecyclerViewAdapter().getItem(event.adapterPosition)).followedByMe ^= true; // toggle boolean by xor ing with "True".
        getRecyclerViewAdapter().notifyItemChanged(event.adapterPosition);
    }

    @Subscribe
    public void onProfilePicClickedEvent(MemberClickedUIEvent event) {
        if (event.receiverName != BaseEvent.ReceiverName.CollectionListFragment)
            return;
        Member member = ((Collection)getRecyclerViewAdapter().getItem(event.adapterPosition)).owner;
        PageChanger.goToProfile(getActivity(), member);
    }

    @Subscribe
    public void onCollectionClicked(CollectionClickedUIEvent event) {
        if (event.receiverName != BaseEvent.ReceiverName.CollectionListFragment)
            return;
        Collection collection = (Collection)getRecyclerViewAdapter().getItem(event.adapterPosition);
        PageChanger.goToCollection(getActivity(), collection);
    }

    @Subscribe
    public void onCollectionUpdated(CollectionUpdatedEvent event) {
        int collectionPosition = findCollectionPositionInAdapter(event.collection);
        if (collectionPosition == -1) return;
        Collection collection = (Collection)getRecyclerViewAdapter().getItem(collectionPosition);
        collection.description = event.collection.description;
        collection.circleIDs = event.collection.circleIDs;
        collection.name = event.collection.name;
        collection.topic = event.collection.topic;
        Logger.log("coverImageUrls = " + event.collection.coverImageUrls, Logger.LEVEL_INFO);
        collection.coverImageUrls = event.collection.coverImageUrls;
        getRecyclerViewAdapter().setItem(collection, collectionPosition);
        getRecyclerViewAdapter().notifyItemChanged(collectionPosition);
    }

    @Subscribe
    public void onCollectionReceived(CollectionReceivedEvent event){
        int collectionPosition = findCollectionPositionInAdapter(event.collection);
        if (collectionPosition == -1) return;
        Collection collection = (Collection)getRecyclerViewAdapter().getItem(collectionPosition);
        collection.description = event.collection.description;
        collection.circleIDs = event.collection.circleIDs;
        collection.name = event.collection.name;
        collection.topic = event.collection.topic;
        collection.coverImageUrls = event.collection.coverImageUrls;
        getRecyclerViewAdapter().setItem(collection, collectionPosition);
        getRecyclerViewAdapter().notifyItemChanged(collectionPosition);
    }

    private int findCollectionPositionInAdapter(Collection collection) {
        return getRecyclerViewAdapter().getItems().indexOf(collection);
    }


    /*-----------*/


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
        switch (requestID) {
            case ConstantsUtils.REQUEST_MEMBER_COLLECTIONS:
                DataRepository.getInstance().getMemberCollections(mainActorID, bookmark);
                break;
            case REQUEST_MEMBER_FOLLOWED_COLLECTIONS:
                DataRepository.getInstance().getPeopleFollowingCollections(mainActorID, bookmark);
                break;
            case REQUEST_POST_REPOSTING_COLLECTIONS:
                //DataRepository.getInstance().get
                PoinilaNetService.getRepostCollections(mainActorID, bookmark);
                break;
        }
    }
}
