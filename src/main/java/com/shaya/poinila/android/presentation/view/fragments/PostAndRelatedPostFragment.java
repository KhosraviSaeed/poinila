package com.shaya.poinila.android.presentation.view.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.uievent.CollectionClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.ExploreTagEvent;
import com.shaya.poinila.android.presentation.uievent.MemberClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.NewWebsitePostEvent;
import com.shaya.poinila.android.presentation.uievent.PostClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.PostComponentClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.RemovePostUIEvent;
import com.shaya.poinila.android.presentation.uievent.sync.PostActionSyncEvent;
import com.shaya.poinila.android.presentation.view.ViewInflater;
import com.shaya.poinila.android.presentation.view.dialog.DialogLauncher;
import com.shaya.poinila.android.presentation.view.dialog.PoinilaAlertDialog;
import com.shaya.poinila.android.presentation.view.help.Help;
import com.shaya.poinila.android.presentation.view.video.PonilaVideoView;
import com.shaya.poinila.android.presentation.viewholder.BaseViewHolder;
import com.shaya.poinila.android.presentation.viewholder.DashboardPostViewHolder;
import com.shaya.poinila.android.presentation.viewholder.PostDetailViewHolder;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.util.PoinilaPreferences;
import com.shaya.poinila.android.util.RandomUtils;
import com.shaya.poinila.android.util.StringUtils;
import com.shaya.poinila.android.util.TimeUtils;
import com.squareup.otto.Subscribe;

import org.apmem.tools.layouts.FlowLayout;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import data.PoinilaNetService;
import data.event.BaseEvent;
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
import data.model.PostType;
import data.model.PrivacyType;
import data.model.Tag;
import manager.DataRepository;
import manager.RequestSource;
import manager.dowload.NotificationDLManager;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;
import static com.shaya.poinila.android.presentation.uievent.PostComponentClickedUIEvent.Type.Comments;
import static com.shaya.poinila.android.presentation.uievent.PostComponentClickedUIEvent.Type.Fave;
import static com.shaya.poinila.android.presentation.uievent.PostComponentClickedUIEvent.Type.FaversList;
import static com.shaya.poinila.android.presentation.uievent.PostComponentClickedUIEvent.Type.FullImage;
import static com.shaya.poinila.android.presentation.uievent.PostComponentClickedUIEvent.Type.OriginalCollection;
import static com.shaya.poinila.android.presentation.uievent.PostComponentClickedUIEvent.Type.Poster;
import static com.shaya.poinila.android.presentation.uievent.PostComponentClickedUIEvent.Type.Reference;
import static com.shaya.poinila.android.presentation.uievent.PostComponentClickedUIEvent.Type.Repost;
import static com.shaya.poinila.android.presentation.uievent.PostComponentClickedUIEvent.Type.RepostersList;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setImage;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_ENTITY;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_REQUEST_ID;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_SECOND_ENTITY_ID;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_COLLECTION_POSTS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_POST_RELATED_POSTS;

/**
 * Created by iran on 8/14/2016.
 */
public class PostAndRelatedPostFragment extends ListBusFragment
        implements PonilaVideoView.OnFullScreenListener{


    private static final String VIDEO_POSITION = "video position";
    private static final String VIDEO_IS_PLAYING = "video state";


    protected Collection collection;
    private int requestType;
    private String mainEntityId;
    private String secondEntityId;

    private boolean showedHelp = false;

    // must be moved to bus fragment
    private Set<Integer> activeRequests;

    @Bind(R.id.post_video_view)
    PonilaVideoView videoView;

    Post mainPost;

    @Bind(R.id.post_title)
    ViewGroup postTitle;
    /*    TextView postName;
        ImageView faveIcon;
        TextView websiteName;
        TextView creationTime;*/
    @Bind(R.id.post_image)
    ImageView postImage;

    @Bind(R.id.content)
    TextView postContent;

    @Bind(R.id.website) TextView website;
    @Bind(R.id.reference_container) ViewGroup postReferenceContainer;

    @Bind(R.id.collection_info) View collectionInfo;
    @Bind(R.id.author_info) View authorInfo;

    @Bind(R.id.tags_divider) View tagsDivider;
    @Bind(R.id.tags_container)
    FlowLayout tagsContainer;

    @Bind(R.id.comment_container) ViewGroup commentsContainer;

    @Bind(R.id.stats) ViewGroup postStats;
    ImageButton commentBtn, repostBtn, faveBtn;
    TextView faveCount, commentCount, repostCount;

    @Bind(R.id.original_collection) ViewGroup originalCollection;

    @Bind(R.id.zoom_btn)
    ImageView zoomBtn;

//
    @Bind(R.id.main_post)
    LinearLayout mainPostContainer;

    @Bind(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;


    private final static int PERMISSION_WRITE_EXTERNAL_STORAGE = 10;


    public static PostAndRelatedPostFragment newInstance(String mainEntityId, int requestID) {
        return newInstance(mainEntityId, null, requestID);
    }

    public static PostAndRelatedPostFragment newInstance(String mainEntityId, String secondEntityId, int requestID) {
        PostAndRelatedPostFragment f = new PostAndRelatedPostFragment();
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

        getActivity().setTitle(getString(R.string.post));
        initUIMainPost();
        mainPost = DataRepository.getInstance().getTempModel(Post.class);
        if(mainPost != null)
            fill(mainPost);

        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if(!PoinilaPreferences.getHelpStatus(PostAndRelatedPostFragment.this.getClass().getName() + ".PostPage")){
                    Help.getInstance().showPostRelatedPostsHelp(getActivity(), postImage);
                    PoinilaPreferences.putHelpStatus(PostAndRelatedPostFragment.this.getClass().getName()+ ".PostPage", true);

                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mRecyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void computeVideoSize(){
        int width = getActivity().getResources().getDisplayMetrics().widthPixels;
        int height = (mainPost.imagesUrls.x736.height * width) / mainPost.imagesUrls.x736.width;
        videoView.setLayoutParams(
                new LinearLayout.LayoutParams(width, height));
    }

    private void initUIMainPost(){

        faveCount = ButterKnife.findById(postStats, R.id.fave_num);
        faveBtn = ButterKnife.findById(postStats, R.id.fave_icon);
        faveCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getBus().post(new PostComponentClickedUIEvent(FaversList));
            }
        });

        faveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getBus().post(new PostComponentClickedUIEvent(Fave));
            }
        });

        commentCount = ButterKnife.findById(postStats, R.id.comment_num);
        commentBtn = ButterKnife.findById(postStats, R.id.comment_icon);
        commentCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getBus().post(new PostComponentClickedUIEvent(Comments));
            }
        });

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getBus().post(new PostComponentClickedUIEvent(Comments));
            }
        });

        repostCount = ButterKnife.findById(postStats, R.id.repost_num);
        repostBtn = ButterKnife.findById(postStats, R.id.repost_icon);
        repostCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getBus().post(new PostComponentClickedUIEvent(RepostersList));
            }
        });
        repostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getBus().post(new PostComponentClickedUIEvent(Repost));
            }
        });

        authorInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getBus().post(new PostComponentClickedUIEvent(Poster));
            }
        });
        collectionInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getBus().post(new PostComponentClickedUIEvent(PostComponentClickedUIEvent.Type.Collection));
            }
        });
        originalCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getBus().post(new PostComponentClickedUIEvent(OriginalCollection));
            }
        });
    }

    public void fill(final Post post) {

        /*------actual fill--------*/


        ((TextView)postTitle.findViewById(R.id.title)).setText(
                post != null && post.name != null ? post.name : "");
        //((TextView)postTitle.findViewById(R.id.subtitle)).setText(post.author.urlName);
        //((TextView)postTitle).findViewById(R.actorID.image))
        ((TextView)postTitle.findViewById(R.id.date_created)).
                setText(TimeUtils.getTimeString(post.creationTime, DataRepository.getInstance().getServerTimeDifference()));

        if (post.type == PostType.IMAGE) {
            postImage.setVisibility(View.VISIBLE);
            setImage(postImage, post.imagesUrls, ImageUrls.ImageType.POST, ImageUrls.ImageSize.BIG);
            setText(postContent, post.summary);

            postImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(post.originalWebpage))
                        BusProvider.getBus().post(new PostComponentClickedUIEvent(FullImage));
                    else
                        BusProvider.getBus().post(new PostComponentClickedUIEvent(Reference));
                }
            });
        } else{
            //DataRepository.getInstance().getPostContent(post.contentUrl.url, postContent);
            postImage.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(post.contentUrl)){
                if (TextUtils.isEmpty(post.content))
                    DataRepository.getInstance().getPostContent(post.contentUrl, post.id);
                else
                    setText(postContent, Html.fromHtml(post.content));
            }
        }

        setImage((ImageView) authorInfo.findViewById(R.id.image),
                post.author.imageUrls, ImageUrls.ImageType.MEMBER, ImageUrls.ImageSize.AVATAR);
        ((TextView)authorInfo.findViewById(R.id.title)).setText(post.author.fullName);

        setImage((ImageView) collectionInfo.findViewById(R.id.image),
                post.collection.coverImageUrls, ImageUrls.ImageType.COLLECTION, ImageUrls.ImageSize.AVATAR);
        ((TextView)collectionInfo.findViewById(R.id.title)).setText(post.collection.name);

        if (TextUtils.isEmpty(post.originalWebpage))
            postReferenceContainer.setVisibility(View.GONE);
        else {
            setText(website, post.originalWebpage);
            postReferenceContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BusProvider.getBus().post(new PostComponentClickedUIEvent(Reference));
                }
            });
        }

        if (post.tags == null || post.tags.isEmpty()){
            tagsContainer.setVisibility(View.GONE);
            tagsDivider.setVisibility(View.GONE);
        }
        else{
            tagsContainer.removeAllViews();
            for (Tag tag : post.tags){
                //tagsContainer.addView(ViewInflater.inflateNormalTag(tag, getActivity()));
                // TODO: difference between tag in post and interest in member may rise some issues
                ViewInflater.addTagToContainer(tagsContainer, tag);
            }
        }

        /*---Comments----*/
        if (post.comments == null || post.comments.isEmpty()){
            commentsContainer.setVisibility(View.GONE);
            //??? findviewbyid
            rootView.findViewById(R.id.comment_container_divider).setVisibility(View.GONE);
        }else{
            commentsContainer.removeAllViews();
            for (int i = 0; i < 3 && i < post.comments.size(); i++){
                commentsContainer.addView(ViewInflater.inflateComment(post.comments.get(i), rootView.getContext())); // ???getActivity
            }
        }

        /*----stats----*/
        if (post.privacy == PrivacyType.PRIVATE){
            repostBtn.setVisibility(View.INVISIBLE);
            repostCount.setVisibility(View.INVISIBLE);
        }else {
            setText(repostCount, post.repostCount);
        }
        setText(faveCount, post.faveCount);
        faveBtn.setSelected(post.favedByMe);
        setText(commentCount, post.commentCount);

        if (post.originalCollection != null) {
            setImage((ImageView) originalCollection.findViewById(R.id.image), post.originalCollection.coverImageUrls,
                    ImageUrls.ImageType.COLLECTION, ImageUrls.ImageSize.AVATAR);
            ((TextView) originalCollection.findViewById(R.id.subtitle)).
                    setText(String.valueOf(post.originalCollection.name));
            ((TextView) originalCollection.findViewById(R.id.fave_num)).
                    setText(String.valueOf(post.originalCollection.totalLikeCount));
            ((TextView) originalCollection.findViewById(R.id.comment_num)).
                    setText(String.valueOf(post.originalCollection.totalCommentCount));
            ((TextView) originalCollection.findViewById(R.id.repost_num)).
                    setText(String.valueOf(post.originalCollection.totalRepostCount));
        }else{
            originalCollection.setVisibility(View.GONE);
        }

        if(!videoView.isPlaying() && mainPost.type.equals(PostType.VIDEO)){
            videoView.setOnFullScreenListener(this);
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoPreview(mainPost.imagesUrls);
            videoView.setVideoPath(mainPost.videoUrl);
        }

        if(mainPost.type.equals(PostType.IMAGE))
            zoomBtn.setVisibility(View.VISIBLE);
        else
            zoomBtn.setVisibility(View.GONE);

        if(mainPost.type.equals(PostType.VIDEO))
            computeVideoSize();
    }


    @Override
    public void onStart() {
        super.onStart();

        BusProvider.getSyncUIBus().register(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_post, menu);

        if(mainPost != null && !mainPost.type.equals(PostType.TEXT))
            menu.findItem(R.id.menu_item_download).setVisible(true);
        else
            menu.findItem(R.id.menu_item_download).setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.menu_item_download:
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_WRITE_EXTERNAL_STORAGE);

                }else {
                    download();
                }

                return true;
            case R.id.menu_item_share:
                // Handle this selection
                if(mainPost != null)
                    launchShareMenu(mainPost);
                return true;
            case R.id.menu_item_report:
                // Handle this selection
                if ( mainPost != null && getRecyclerViewAdapter().getItemCount() >= 1)
                    DialogLauncher.launchReportDialog(
                            getFragmentManager(),
                            R.string.report_post,
                            mainPost.id);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void download(){
        String url = "";
        switch (mainPost.type){
            case IMAGE:
                url = mainPost.imagesUrls.x736.url;
                break;
            case VIDEO:
                url = mainPost.videoUrl;
                break;
        }
        NotificationDLManager.getInstance().download(url, mainPost.name, "");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(mainPost != null)
                        download();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
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
                                mainPost.getId()) + "\n" +
                        getString(R.string.ponila_world_of_interest);
                break;
            default:
                return;
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, extra);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_dialog_title)));
    }

    @Subscribe
    public void onNewUrlImagePostEvent(NewWebsitePostEvent event){
        //DialogLauncher.launchNewPost(getChildFragmentManager(), event.suggestedPost);
        PageChanger.goToNewPost(getFragmentManager(), event.suggestedPost);
    }

    @Subscribe
    public void onPostsReceived(PostsReceivedEvent event) {
        populateIfNecessary(event); // was necessary earlier because response came from server lacked info about posts collection
        onGettingInitDataResponse(event);
        onGettingListDataResponse(event, event.bookmark);
    }

    @Subscribe
    public void onContentReceivedEvent(final ContentReceivedEvent event) {
        mainPost.content = StringUtils.removeHtmlDirAttribute(event.content);
        fill(mainPost);
    }

    @Subscribe
    public void onPostDetailsComponentClickEvent(PostComponentClickedUIEvent event) {
        if (DataRepository.isUserAnonymous() && PostComponentClickedUIEvent.Type.guestCantPerformActions.contains(event.type)) {
            Logger.toastError(R.string.error_guest_action);
            return;
        }

        switch (event.type) {
            case FaversList:
                PageChanger.goToLikersList(getActivity(), mainPost.faveCount, mainPost.getId());
                break;
            case Fave:
                favePost();
                getRecyclerViewAdapter().notifyItemChanged(0);
                break;
            case Comments:
                PageChanger.goToCommentList(getActivity(), mainPost.commentCount, mainPost.getId());
                getRecyclerViewAdapter().notifyItemChanged(0);
                break;
            case RepostersList:
                PageChanger.goToRepostList(getActivity(), mainPost.repostCount, mainPost.getId());
                break;
            case Repost:
                DialogLauncher.launchRepostDialog(getFragmentManager(), mainPost);
                getRecyclerViewAdapter().notifyItemChanged(0);
                break;
            case Poster:
                Member member = mainPost.author;
                PageChanger.goToProfile(getActivity(), member);
                break;
            case Collection:
                Collection collection = mainPost.collection;
                PageChanger.goToCollection(getActivity(), collection);
                break;
            case OriginalCollection:
                collection = mainPost.originalCollection;
                PageChanger.goToCollection(getActivity(), collection);
                break;
            case Reference:
                PageChanger.goToInlineBrowser(getActivity(), mainPost.originalWebpage.toLowerCase(), mainPost.getId(), mainPost.name);
                break;
            case FullImage:
                PageChanger.goToFullImage(getActivity(), mainPost.imagesUrls.properPostImage(ImageUrls.ImageSize.FULL_SIZE).url);
                break;
        }
    }

    private void favePost() {

        if (!mainPost.favedByMe) {
            PoinilaNetService.favePost(mainPost.getId());
            mainPost.faveCount++;
            //setText(faveCount, ++post.faveCount);
            //faveBtn.setSelected(true);
        } else {
            PoinilaNetService.unfavePost(mainPost.getId());
            mainPost.faveCount--;
//            setText(faveCount, --post.faveCount);
//            faveBtn.setSelected(false);*/
        }

        mainPost.favedByMe = !mainPost.favedByMe;

        faveBtn.setSelected(mainPost.favedByMe);
        setText(faveCount, mainPost.faveCount);

//        mainPost.favedByMe ^= true;

        BusProvider.getSyncUIBus().post(new PostActionSyncEvent(mainPost));

    }

    @Subscribe
    public void onUndofaveEvent(UndoFavePostEvent event) {
        mainPost.favedByMe = false;
        mainPost.faveCount--;
        faveBtn.setSelected(mainPost.favedByMe);
        setText(faveCount, mainPost.faveCount);
        // TODO: ???
        //faveBtn.setSelected(false);

        BusProvider.getSyncUIBus().post(new PostActionSyncEvent(mainPost));
    }

    @Subscribe
    public void onUndoUnfaveEvent(UndoUnfavePostEvent event) {
        mainPost.favedByMe = true;
        mainPost.faveCount++;
        faveBtn.setSelected(mainPost.favedByMe);
        setText(faveCount, mainPost.faveCount);
        // TODO: ???
        //faveBtn.setSelected(true);

        BusProvider.getSyncUIBus().post(new PostActionSyncEvent(mainPost));
    }

    @Subscribe
    public void onExploreTag(ExploreTagEvent event) {
        PageChanger.goToExplore(getActivity(), event.text);
    }


    @OnClick(R.id.zoom_btn)
    public void zoomBtnOnCLick(){
        PageChanger.goToFullImage(getActivity(), mainPost.imagesUrls.x736.url);
    }

    public static int findPostInAdapter(List<Post> adapter, int postID) {
        for (int i = 0; i < adapter.size(); i++) {
            if (adapter.get(i) instanceof Post && adapter.get(i).id == postID) // take account of ask rating item
                return i;
        }
        return -1;
    }

    public void populateIfNecessary(PostsReceivedEvent event) {
        if (collection != null) {
            for (Post post : event.posts) {
                post.collection = collection;
                //post.author = collection.owner;
            }
        }
    }

    @Subscribe
    public void onPostReceived(PostReceivedEvent event) {
        onGettingInitDataResponse(event);
    }

    @Override
    public void onSuccessfulInitData(BaseEvent baseEvent) {
        super.onSuccessfulInitData(baseEvent);

        if(baseEvent instanceof PostReceivedEvent){
            mainPost = ((PostReceivedEvent)baseEvent).post;
            fill(mainPost);
            getActivity().invalidateOptionsMenu();
        }

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
        if(getRecyclerViewAdapter().getItem(event.adapterPosition) instanceof Post){ // sometimes getItem return Loading Model
            Post post = (Post)getRecyclerViewAdapter().getItem(event.adapterPosition);
            PageChanger.goToPost(getActivity(), post);
        }
    }

    @Subscribe
    public void onCollectionClicked(CollectionClickedUIEvent event) {
        Collection collection = ((Post)getRecyclerViewAdapter().getItem(event.adapterPosition)).collection;
        PageChanger.goToCollection(getActivity(), collection);
    }

    @Override
    protected RecyclerView.OnScrollListener getRecyclerViewListener() {
        return RecyclerViewProvider.staggeredListEndDetectorListener(getRecyclerViewAdapter(), this);
    }

    @Override
    public void requestForMoreData() {
        int requestId = RandomUtils.getRandomInt();
        PoinilaNetService.getRelatedPosts(mainEntityId, bookmark, requestId);
    }

    @Override
    public RecyclerViewAdapter createAndReturnRVAdapter() {
        return new PostAndRelatedPostAdapter(getActivity());
    }

    @Override
    protected boolean sendsRequestAutomatically() {
        return true;
    }

    @Override
    protected void requestInitialData() {
        int requestId = RandomUtils.getRandomInt();
        activeRequests.add(requestId);
        DataRepository.getInstance().getPost(mainEntityId, RequestSource.FORCE_ONLINE, requestId);
        setLoading(new Loading());
        requestForMoreData();
    }

    @Override
    public boolean mustShowProgressView() {
        return false;
    }

    @Override
    public int getLayoutID() {
        return R.layout.post_related_posts;
    }

    @Override
    public void onSuccessfulListData(BaseEvent baseEvent, String newBookmark) {
        super.onSuccessfulListData(baseEvent, newBookmark);
        List<Post> posts = ((PostsReceivedEvent) baseEvent).posts;

        getRecyclerViewAdapter().addItems(posts);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        int currentOrientation = getResources().getConfiguration().orientation;

        if (isLandScape())
            setFullScreenStateVideo();
        else
            setDefaultStateVideo();

//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

    }

    private boolean isLandScape(){
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public void stateChanged() {
        if (isLandScape())
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void setDefaultStateVideo(){
        videoView.setFullScreenMode(false);
        int postCountChild = mainPostContainer.getChildCount();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();

        mRecyclerView.setVisibility(View.VISIBLE);

        computeVideoSize();

        for(int i=0 ; i < postCountChild ; i++){
            if(mainPostContainer.getChildAt(i).getId() != R.id.post_video_view){
                mainPostContainer.getChildAt(i).setVisibility(View.VISIBLE);
            }
        }

        fill(mainPost);
    }

    private void setFullScreenStateVideo(){
        videoView.setFullScreenMode(true);
        int postCountChild = mainPostContainer.getChildCount();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        mRecyclerView.setVisibility(View.GONE);

        videoView.getLayoutParams().width = getActivity().getResources().getDisplayMetrics().widthPixels;

        TypedValue tv = new TypedValue();
        getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        int actionBarHeight = getResources().getDimensionPixelSize(tv.resourceId);
        videoView.getLayoutParams().height =
                getActivity().getResources().getDisplayMetrics().heightPixels
                        + getStatusBarHeight(getActivity().getWindow())
                        - actionBarHeight;

        for(int i=0 ; i < postCountChild ; i++){
            if(mainPostContainer.getChildAt(i).getId() != R.id.post_video_view){
                mainPostContainer.getChildAt(i).setVisibility(View.GONE);
            }
        }
    }

    private int getStatusBarHeight(Window window){
        Rect rectangle = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
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
            } else
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

            return VIEW_TYPE_POST_ITEM;
        }
    }
}
