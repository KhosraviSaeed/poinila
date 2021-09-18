package com.shaya.poinila.android.presentation.viewholder;

import android.view.View;
import android.widget.ImageView;

import com.shaya.poinila.android.presentation.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import data.event.BaseEvent;
import data.model.Post;
import data.model.PostType;
import data.model.SuggestionReason;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;
import static com.shaya.poinila.android.util.ResourceUtils.getString;

/**
 * Created by iran on 2015-08-09.
 */
public class DashboardPostViewHolder extends PostViewHolder{

    @Bind(R.id.video_type_icon)
    ImageView videoType;

    public DashboardPostViewHolder(View view, BaseEvent.ReceiverName receiverTag) {
        super(view, receiverTag);

        avatar = ButterKnife.findById(postAuthor, R.id.image);
        createdByTextView = ButterKnife.findById(postAuthor, R.id.title);
        authorName = ButterKnife.findById(postAuthor, R.id.subtitle);


        collectionImage = ButterKnife.findById(postCollection, R.id.image);
        collectionStatusView = ButterKnife.findById(postCollection, R.id.title);
        collectionName = ButterKnife.findById(postCollection, R.id.subtitle);
    }

    @Override
    public void fill(Post post) {
        super.fill(post);

        if(post.type.equals(PostType.VIDEO))
            videoType.setVisibility(View.VISIBLE);
        else
            videoType.setVisibility(View.GONE);

        String reason = null;

        if (post.reason == SuggestionReason.PickedForYou)
            reason = getString(R.string.picked_for_you);
        else if (post.reason == SuggestionReason.FoundInInterest)
            reason = getString(R.string.found_in_interest);
        else if (post.reason == SuggestionReason.Following)
            reason = getString(R.string.found_in_followed_collections);
        else
            reason = getString(R.string.collected_in);
        setText(collectionStatusView, reason);
    }

   /* @OnClick @Nullable (R.actorID.post_collection) protected void onGoingToCollection(){
        BusProvider.getBus().post(new CollectionClickedUIEvent(getAdapterPosition(), receiverTag));
    }


    @OnClick @Nullable (R.actorID.post_author) protected void onGoingToProfile(){
        BusProvider.getBus().post(new MemberClickedUIEvent(getAdapterPosition(), receiverTag));
    }*/
}
