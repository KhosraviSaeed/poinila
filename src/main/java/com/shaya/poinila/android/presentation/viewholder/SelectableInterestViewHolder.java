package com.shaya.poinila.android.presentation.viewholder;

import android.view.View;
import android.widget.ImageView;

import com.makeramen.roundedimageview.Corner;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.util.ResourceUtils;
import com.squareup.picasso.Transformation;

import butterknife.Bind;
import data.model.ImageTag;
import data.model.ImageUrls;
import data.model.Tag;

/**
 * Created by iran on 2015-11-03.
 */
public class SelectableInterestViewHolder extends CheckedTextViewHolder<ImageTag>{
    @Bind(R.id.image) public ImageView mImageView;

    public SelectableInterestViewHolder(View view) {
        super(view);
    }

    @Override
    public void fill(Tag interest) {
        super.fill(interest);
        ImageTag imageInterest = ((ImageTag) interest);
//        textView.setTextColor(ResourceUtils.getColor(invalidImage(imageInterest) ? R.color.black : R.color.white));
        mImageView.setVisibility(invalidImage(imageInterest) ? View.INVISIBLE : View.VISIBLE);

        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(Corner.TOP_LEFT, ResourceUtils.getDimen(R.dimen.cardview_compat_inset_shadow))
                .cornerRadiusDp(Corner.TOP_RIGHT, ResourceUtils.getDimen(R.dimen.cardview_compat_inset_shadow))
                .oval(false)
                .build();

        ViewUtils.setImage(mImageView, ((ImageTag) interest).imageUrls, ImageUrls.ImageType.INTEREST, null, transformation);
    }

    private boolean invalidImage(ImageTag imageInterest){
        return imageInterest.imageUrls == null || imageInterest.imageUrls.interest == null;
    }

}
