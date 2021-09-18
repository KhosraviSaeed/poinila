package com.shaya.poinila.android.presentation.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shaya.poinila.android.presentation.PoinilaApplication;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.CollectionClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.MemberClickedUIEvent;
import com.shaya.poinila.android.util.BusProvider;

import butterknife.Bind;
import butterknife.OnClick;
import data.event.BaseEvent;
import data.model.Collection;
import data.model.ImageUrls;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setFont;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setImage;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;

/**
 * Created by iran on 2015-06-22.
 */
public abstract class CollectionViewHolder extends BaseViewHolder<Collection>{

    public final BaseEvent.ReceiverName receiverTag;

    public CollectionViewHolder(View view, BaseEvent.ReceiverName receiverTag) {
        super(view);
        this.receiverTag = receiverTag;
    }


    @Bind(R.id.collection_author)
    protected ViewGroup collectionAuthorView;

    protected @Bind(R.id.text_top) TextView topTag;
    protected @Bind(R.id.text_bottom) TextView bottomTag;
    protected @Bind(R.id.image_big) ImageView cover;
    protected @Bind(R.id.image_small_top) ImageView post1Image;
    protected @Bind(R.id.image_small_middle) ImageView post2Image;
    protected @Bind(R.id.image_small_bottom) ImageView post3Image;

    /**
     * Always call super.fill when overriding
     * @param collection
     */
    public void fill(Collection collection) {
        setImage((ImageView) collectionAuthorView.findViewById(R.id.image),
                collection.owner.imageUrls, ImageUrls.ImageType.MEMBER, ImageUrls.ImageSize.AVATAR);
        setText((TextView) collectionAuthorView.findViewById(R.id.title), collection.name);
        setFont((TextView) collectionAuthorView.findViewById(R.id.title),
                PoinilaApplication.getAppContext().getString(R.string.default_bold_font_path));
        setText((TextView) collectionAuthorView.findViewById(R.id.subtitle), collection.owner.fullName);

        /*Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(ResourceUtils.getDimen(R.dimen.corner_columned_lvlhalf))
                .oval(false)
                .build();*/

        setImage(cover, collection.coverImageUrls, ImageUrls.ImageType.COLLECTION, ImageUrls.ImageSize.BIG);
        setImage(post1Image, collection.image1Url, ImageUrls.ImageType.COLLECTION, ImageUrls.ImageSize.AVATAR);
        setImage(post2Image, collection.image2Url, ImageUrls.ImageType.COLLECTION, ImageUrls.ImageSize.AVATAR);
        setImage(post3Image, collection.image3Url, ImageUrls.ImageType.COLLECTION, ImageUrls.ImageSize.AVATAR);
    }

    @OnClick(R.id.collection_author) protected void onGoingToProfile(){
        BusProvider.getBus().post(new MemberClickedUIEvent(getAdapterPosition(), receiverTag));
    }

    @OnClick(R.id.collection) protected void onGoingToCollection(){
        BusProvider.getBus().post(new CollectionClickedUIEvent(getAdapterPosition(), receiverTag));
    }
}
