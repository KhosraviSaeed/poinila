package com.shaya.poinila.android.presentation.viewholder;

import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.shaya.poinila.android.presentation.PoinilaApplication;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.CollectionClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.MemberClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.PostClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.RemovePostUIEvent;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.Logger;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import data.event.BaseEvent;
import data.model.ImageUrls;
import data.model.Post;
import data.model.PostType;
import data.model.PrivacyType;
import manager.DataRepository;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setFont;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setImage;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;
import static com.shaya.poinila.android.util.ResourceUtils.getString;

/**
 * Created by iran on 2015-06-22.
 */
public class PostViewHolder extends BaseViewHolder<Post>{
    public final BaseEvent.ReceiverName receiverTag;

    /*@Bind(R.actorID.post_title)
    public ViewGroup postTitle;*/

    @Bind(R.id.post_image)
    public ImageView postImage;

    @Bind(R.id.post_content)
    public TextView postSummary;

    @Bind(R.id.post_stats)
    public ViewGroup postStats;

    @Bind(R.id.post_title)
    public TextView postName;
    //public TextView postWebsiteName;

    public ImageView favoriteBtn;
    public TextView favoriteCount;
    public ImageView commentBtn;
    public TextView commentCount;
    public ImageView repostBtn;
    public TextView repostCount;


    @Nullable @Bind(R.id.post_collection)
    public ViewGroup postCollection;
    public ImageView collectionImage;
    public TextView collectionStatusView;
    public TextView collectionName;

    @Nullable @Bind(R.id.post_author)
    public ViewGroup postAuthor;
    public ImageView avatar;
    public TextView authorName;
    public TextView createdByTextView;


    public PostViewHolder(View view, final BaseEvent.ReceiverName receiverTag) {
        super(view);
        this.receiverTag = receiverTag;

        favoriteBtn = ButterKnife.findById(postStats, R.id.fave_icon);
        favoriteCount = ButterKnife.findById(postStats, R.id.fave_num);
        commentBtn = ButterKnife.findById(postStats, R.id.comment_icon);
        commentCount = ButterKnife.findById(postStats, R.id.comment_num);
        repostBtn = ButterKnife.findById(postStats, R.id.repost_icon);
        repostCount = ButterKnife.findById(postStats, R.id.repost_num);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getBus().post(new PostClickedUIEvent(getAdapterPosition(), receiverTag));
            }
        });
    }

    public void fill(Post post) {
        switch (post.type){
            case TEXT:
                postSummary.setVisibility(View.VISIBLE);
                postImage.setVisibility(View.GONE);
                postName.setVisibility(View.VISIBLE);
                break;
            case IMAGE:
                postSummary.setVisibility(TextUtils.isEmpty(post.summary) ? View.GONE : View.VISIBLE);
                postImage.setVisibility(View.VISIBLE);
                postName.setVisibility(!TextUtils.isEmpty(post.name) ? View.VISIBLE : View.GONE);
                break;
            case VIDEO:
                postSummary.setVisibility(TextUtils.isEmpty(post.summary) ? View.GONE : View.VISIBLE);
                postImage.setVisibility(View.VISIBLE);
                postName.setVisibility(!TextUtils.isEmpty(post.name) ? View.VISIBLE : View.GONE);
                break;
        }

        if (post.type == PostType.IMAGE) {

            /*if (TextUtils.isEmpty(post.summary))
                postContent.setVisibility(View.GONE);*/
        }else{ //post.type == PostType.TEXT

        }

        favoriteBtn.setSelected(post.favedByMe);

        // name and site
        setText(postName, post.name);
        //setText(postWebsiteName, post.originalWebpage);

        // set Bold Font
        setFont(postName, getString(R.string.default_bold_font_path));



        float radius = PoinilaApplication.getAppContext().getResources().getDimension(R.dimen.cardview_compat_inset_shadow);
        // image
        setImage(postImage, post.imagesUrls, ImageUrls.ImageType.POST, ImageUrls.ImageSize.MEDIUM , new RoundedTransformationBuilder().cornerRadiusDp(radius).build());

        // content
        if (post.type == PostType.TEXT){
            if (!TextUtils.isEmpty(post.summary))
                setText(postSummary, post.summary);
            else if (!TextUtils.isEmpty(post.contentUrl)){
                if (TextUtils.isEmpty(post.content))
                    DataRepository.getInstance().getPostContent(post.contentUrl, post.id);
                else
                    setText(postSummary, Html.fromHtml(post.content));
            }
        }else {
            setText(postSummary, post.summary);
        }
        // stats
        if (post.privacy == PrivacyType.PRIVATE){
            repostBtn.setVisibility(View.INVISIBLE);
            repostCount.setVisibility(View.INVISIBLE);
        }else {
            setText(repostCount, post.repostCount);
        }
        setText(favoriteCount, post.faveCount);
        setText(commentCount, post.commentCount);

        // owner
        setImage(avatar, post.author.imageUrls, ImageUrls.ImageType.MEMBER, ImageUrls.ImageSize.AVATAR);
        String createdBy = post.isRepost ? getString(R.string.repost_by) : getString(R.string.created_by);
        setText(createdByTextView, createdBy);
        setText(authorName, post.author.fullName);

        // collection
        setImage(collectionImage, post.collection.coverImageUrls, ImageUrls.ImageType.COLLECTION, ImageUrls.ImageSize.AVATAR);
        setText(collectionStatusView, getString(R.string.collected_in));
        setText(collectionName, post.collection.name);

        if (postAuthor != null ){//&& DataRepository.getInstance().isMe(post.author.id)) {
            postAuthor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BusProvider.getBus().post(new MemberClickedUIEvent(getAdapterPosition(), receiverTag));
                }
            });
        }
        //else postAuthor.setOnClickListener(null);
    }

    @OnClick({R.id.post_image, R.id.post_content}) protected void onGoingToPost(){
        BusProvider.getBus().post(new PostClickedUIEvent(getAdapterPosition(), receiverTag));
    }

    @Nullable @OnClick(R.id.post_collection) protected void onGoingToCollection(){
        BusProvider.getBus().post(new CollectionClickedUIEvent(getAdapterPosition(), receiverTag));
    }


    /*@Nullable @OnClick (R.actorID.post_author) protected void onGoingToProfile(){
        BusProvider.getBus().post(new MemberClickedUIEvent(getAdapterPosition(), receiverTag));
    }*/

    @Nullable @OnClick(R.id.bottom_bar_remove) public void onRemovePost(){
        BusProvider.getBus().post(new RemovePostUIEvent(getAdapterPosition()));
    }
}

