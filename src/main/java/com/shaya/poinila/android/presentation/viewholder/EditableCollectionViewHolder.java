package com.shaya.poinila.android.presentation.viewholder;

import android.view.View;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.EditItemUIEvent;
import com.shaya.poinila.android.presentation.uievent.RemoveItemUIEvent;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.ResourceUtils;

import butterknife.OnClick;
import data.event.BaseEvent;
import data.model.Collection;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;

/**
 * Created by iran on 2015-08-17.
 */
public class EditableCollectionViewHolder extends CollectionViewHolder{


    public final BaseEvent.ReceiverName receiverTag;

    public EditableCollectionViewHolder(View view, BaseEvent.ReceiverName receiverTag) {
        super(view, receiverTag);
        this.receiverTag = receiverTag;
    }

    @Override
    public void fill(Collection collection) {
        super.fill(collection);
        topTag.setVisibility(View.GONE);
        setText(bottomTag, ResourceUtils.getStringFormatted(R.string.posts_formatted, collection.postCount));
    }

    @OnClick(R.id.remove_collection) public void onRemoveCollection(){
        BusProvider.getBus().post(new RemoveItemUIEvent(getAdapterPosition()));
    }

    @OnClick(R.id.edit_collection) public void onEditCollection(){
        BusProvider.getBus().post(new EditItemUIEvent(getAdapterPosition()));
    }
}
