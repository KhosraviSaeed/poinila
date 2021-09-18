package com.shaya.poinila.android.presentation.viewholder;

/**
 * Created by iran on 2015-09-05.
 */

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.CollectionFrameToggledEvent;
import com.shaya.poinila.android.util.BusProvider;

import butterknife.Bind;
import butterknife.OnClick;
import data.model.Collection;
import data.model.ImageUrls;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setImage;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;


/**
 * Created by iran on 2015-07-26.
 */
public class FrameCollectionViewHolder extends BaseViewHolder<Collection>{
    @Bind(R.id.image) public ImageView avatarImage;
    @Bind(R.id.title) public TextView nameView;
    @Bind(R.id.subtitle) public TextView subTitleView;
    @Bind(R.id.icon) public ImageButton addRemoveIcon;
    @Bind(R.id.icon_caption) public TextView iconCaptionView;

    public FrameCollectionViewHolder(View view) {
        super(view);
        iconCaptionView.setVisibility(View.GONE);
    }

    @OnClick(R.id.icon) public void onAddOrRemove(){
        BusProvider.getBus().post(new CollectionFrameToggledEvent(getAdapterPosition()));
    }

    public void fill(Collection collection){
        setImage(avatarImage, collection.coverImageUrls, ImageUrls.ImageType.COLLECTION, ImageUrls.ImageSize.AVATAR);
        setText(nameView, collection.name);
        addRemoveIcon.setBackgroundResource(R.drawable.add_remove_checkbox_selector);
        addRemoveIcon.setSelected(collection.selected);
    }
}
