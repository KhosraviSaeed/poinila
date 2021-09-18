package com.shaya.poinila.android.presentation.viewholder;

import android.view.View;
import com.shaya.poinila.android.util.TimeUtils;

import data.event.BaseEvent;
import data.model.Collection;
import manager.DataRepository;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;

/**
 * Created by iran on 2015-08-05.
 */
public class MyFollowedCollectionViewHolder extends CollectionViewHolder {
    public MyFollowedCollectionViewHolder(View view, BaseEvent.ReceiverName receiverTag) {
        super(view, receiverTag);
    }

    @Override
    public void fill(Collection collection) {
        super.fill(collection);
        topTag.setVisibility(View.GONE);
        setText(bottomTag, TimeUtils.getTimeString(collection.lastPostCreationTime,
                DataRepository.getInstance().getServerTimeDifference()));
        /*setText(bottomTag, StringUtils.getStringWithPersianNumber(
                ResourceUtils.getString(R.string.new_posts_formatted), collection.unseenPostsCount));*/
    }
}
