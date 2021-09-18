package com.shaya.poinila.android.presentation.viewholder;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.PostComponentClickedUIEvent;
import com.shaya.poinila.android.presentation.view.ViewInflater;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.TimeUtils;

import org.apmem.tools.layouts.FlowLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import data.model.ImageUrls;
import data.model.Post;
import data.model.PostType;
import data.model.PrivacyType;
import data.model.Tag;
import manager.DataRepository;

import static com.shaya.poinila.android.presentation.uievent.PostComponentClickedUIEvent.Type.Collection;
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

/**
 * Created by iran on 2015-11-18.
 */
public class PostDetailViewHolder extends BaseViewHolder<Post>{
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


    public PostDetailViewHolder(View view) {
        super(view);
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
        commentBtn= ButterKnife.findById(postStats, R.id.comment_icon);
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
                BusProvider.getBus().post(new PostComponentClickedUIEvent(Collection));
            }
        });
        originalCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getBus().post(new PostComponentClickedUIEvent(OriginalCollection));
            }
        });

    }


    @Override
    public void fill(final Post post) {

        /*------actual fill--------*/

        ((TextView)postTitle.findViewById(R.id.title)).setText(post.name);
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
    }
}
