package com.shaya.poinila.android.presentation.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.OnFollowUnfollowCollectionUIEvent;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.PoinilaPreferences;
import com.shaya.poinila.android.util.ResourceUtils;

import butterknife.Bind;
import data.event.BaseEvent;
import data.model.Collection;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;
import static com.shaya.poinila.android.util.ResourceUtils.getColor;
import static com.shaya.poinila.android.util.ResourceUtils.getString;

/**
 * Created by iran on 2015-08-05.
 */
public class FollowableCollectionViewHolder extends CollectionViewHolder {
    @Bind(R.id.collection_follow_text)
    TextView followText;

    @Bind(R.id.collection_follow_icon)
    ImageView followIcon;

    @Bind(R.id.follow_collection_bar) View followBar;

    public FollowableCollectionViewHolder(View view, BaseEvent.ReceiverName receiverTag) {
        super(view, receiverTag);
    }

    @Override
    public void fill(final Collection collection) {
        super.fill(collection);

        topTag.setVisibility(View.GONE);
        setText(bottomTag, ResourceUtils.getStringFormatted(R.string.posts_formatted, collection.postCount));

        followBar.setVisibility(
                collection.owner.id == Integer.parseInt(PoinilaPreferences.getMyId())
                        ? View.GONE : View.VISIBLE);

        // Important note: just filling views. In fact, we are in a not known state here.
        updateFollowIcon(collection.followedByMe);

        followBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getBus().post(new OnFollowUnfollowCollectionUIEvent(getAdapterPosition(), !collection.followedByMe));
            }
        });
    }

    /*@OnClick(R.actorID.follow_collection_bar) public void onGoingToProfile(){
        BusProvider.getBus().post(new OnFollowUnfollowCollectionUIEvent(getAdapterPosition(), ));
    }*/

    public void updateFollowIcon(boolean isFollowed){ // action appearance is opposite to collection state
        followIcon.setSelected(isFollowed);
        followText.setTextColor(getColor(isFollowed ? R.color.sea_buckthorn : R.color.tundora));
        setText(followText, getString(isFollowed ? R.string.unfollow_item : R.string.follow_item));
    }
}
