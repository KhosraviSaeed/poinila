package com.shaya.poinila.android.presentation.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.Corner;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.ExploreTagEvent;
import com.shaya.poinila.android.presentation.uievent.RemoveTagEvent;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.ResourceUtils;
import com.shaya.poinila.android.util.TimeUtils;
import com.squareup.picasso.Transformation;

import org.apmem.tools.layouts.FlowLayout;

import butterknife.ButterKnife;
import data.model.Comment;
import data.model.ImageUrls;
import data.model.Tag;
import manager.DataRepository;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by AlirezaF on 7/22/2015.
 */
public class ViewInflater {
    public static View inflateRemovableTag(Tag tag, Context context) {
        final View tagView = LayoutInflater.from(context).inflate(R.layout.tag_removable, null);
        ButterKnife.findById(tagView, R.id.removeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getBus().post(new RemoveTagEvent(tagView));
            }
        });
        ((TextView)ButterKnife.findById(tagView, R.id.tag)).setText(tag.name);
        return tagView;
    }

    public static void addTagToSearchBar(Tag tag, Context context, RelativeLayout rlContainer) {
        final View tagView = LayoutInflater.from(context).inflate(R.layout.tag_removable, rlContainer, false);
        tagView.setId(rlContainer.getChildCount());
        ButterKnife.findById(tagView, R.id.removeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getBus().post(new RemoveTagEvent(tagView));
            }
        });
        ((TextView) ButterKnife.findById(tagView, R.id.tag)).setText(tag.name);
        View lastChild = rlContainer.getChildAt(rlContainer.getChildCount() - 1);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lp.addRule(RelativeLayout.LEFT_OF, lastChild.getId());
        lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        rlContainer.addView(tagView, rlContainer.getChildCount(), lp);
    }

  /*  public static View inflateNormalTag(Tag tag, Context context) {
        TextView tagView = (TextView) LayoutInflater.from(context).
                inflate(R.layout.tag_text_view, null);
        tagView.setText(tag.name);
        return tagView;
    }*/

    public static View inflateNormalTag(String tag, Context context) {
        TextView tagView = (TextView) LayoutInflater.from(context).
                inflate(R.layout.tag_text_view, null);
        tagView.setText(tag);
        return tagView;
    }

    public static View inflateComment(Comment comment, Context context){
        View commentView = LayoutInflater.from(context).inflate(R.layout.comment, null);
        ViewUtils.setImage((ImageView) commentView.findViewById(R.id.image),
                comment.commenter.imageUrls, ImageUrls.ImageType.MEMBER, ImageUrls.ImageSize.AVATAR);
        ((TextView)commentView.findViewById(R.id.name)).setText(comment.commenter.fullName);
        ((TextView)commentView.findViewById(R.id.date_created)).
                setText(TimeUtils.getTimeString(comment.creationDate, DataRepository.getInstance().getServerTimeDifference()));
        ((TextView)commentView.findViewById(R.id.comment_content)).
                setText(comment.content);

        return commentView;
    }

    public static void addTagToContainer(FlowLayout tagsContainer, Tag tag){//Tag tag) {
        final TextView tagView = (TextView) LayoutInflater.from(tagsContainer.getContext()).
                inflate(R.layout.tag_text_view, tagsContainer, false);
        tagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getBus().post(new ExploreTagEvent(tagView.getText().toString()));
            }
        });
        tagView.setText(tag.name);//.name)
        ViewUtils.setFont(tagView, tagView.getContext().getString(R.string.default_bold_font_path));
// ;
        tagsContainer.addView(tagView);
    }

    public static void addRemovableTagToContainer(final FlowLayout tagsContainer, String tagText) {
        final LinearLayout tagView = (LinearLayout) LayoutInflater.from(tagsContainer.getContext()).
                inflate(R.layout.tag_removable, tagsContainer, false);

        TextView tag = ButterKnife.findById(tagView, R.id.tag);
        tag.setText(tagText);//.name);
        tagView.findViewById(R.id.removeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getBus().post(new RemoveTagEvent(tagsContainer.indexOfChild(tagView)));
            }
        });

        tagsContainer.addView(tagView);
    }

    public static View inflateImageCaption(LinearLayout ll, String name, ImageUrls coverImageUrl) {
        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(Corner.TOP_LEFT, ResourceUtils.getDimen(R.dimen.corner_lvlhalf))
                .cornerRadiusDp(Corner.TOP_RIGHT, ResourceUtils.getDimen(R.dimen.corner_lvlhalf))
                .oval(false)
                .build();
        CardView card = (CardView) LayoutInflater.from(ll.getContext()).inflate(R.layout.image_caption, ll, false);
        ViewUtils.setText(((TextView) card.findViewById(R.id.caption)), name);
        ViewUtils.setFont(((TextView) card.findViewById(R.id.caption)), ll.getContext().getString(R.string.default_font_path));
        ViewUtils.setImage((ImageView) card.findViewById(R.id.image), coverImageUrl, ImageUrls.ImageType.COLLECTION, ImageUrls.ImageSize.BIG, transformation);
        return card;
    }


}
