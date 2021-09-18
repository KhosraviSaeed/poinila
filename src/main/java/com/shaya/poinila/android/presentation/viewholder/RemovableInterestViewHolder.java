package com.shaya.poinila.android.presentation.viewholder;

import android.view.View;
import android.widget.ImageView;

import com.makeramen.roundedimageview.Corner;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.util.ResourceUtils;
import com.squareup.picasso.Transformation;

import butterknife.Bind;
import data.model.ImageTag;
import data.model.ImageUrls;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setImage;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;

/**
 * Created by iran on 2015-11-04.
 */
public class RemovableInterestViewHolder extends RemovableTagViewHolder<ImageTag>{

    @Bind(R.id.image) public ImageView mImageView;

    public RemovableInterestViewHolder(View view) {
        super(view);
    }

    public void fill(ImageTag interest) {
        setText(textView, interest.name);
//        textView.setTextColor(ResourceUtils.getColor(invalidImage(interest) ? R.color.black : R.color.white));
        mImageView.setVisibility(invalidImage(interest) ? View.INVISIBLE : View.VISIBLE);
//        removeBtn.setImageResource(invalidImage(interest) ? R.drawable.remove_boulder_36dp : R.drawable.remove_white_36dp);

        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(Corner.TOP_LEFT, ResourceUtils.getDimen(R.dimen.cardview_compat_inset_shadow))
                .cornerRadiusDp(Corner.TOP_RIGHT, ResourceUtils.getDimen(R.dimen.cardview_compat_inset_shadow))
                .oval(false)
                .build();


        setImage(mImageView, interest.imageUrls, ImageUrls.ImageType.INTEREST, null, transformation);
    }

    private boolean invalidImage(ImageTag imageInterest){
        return imageInterest.imageUrls == null || imageInterest.imageUrls.interest == null;
    }
}
