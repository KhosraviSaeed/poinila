package com.shaya.poinila.android.presentation.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.RemoveTagEvent;
import com.shaya.poinila.android.presentation.uievent.UpdateUiRepostEvent;
import com.shaya.poinila.android.util.BusProvider;
import com.squareup.otto.Subscribe;

import org.parceler.Parcels;

import data.PoinilaNetService;
import data.model.Collection;
import data.model.Post;
import data.model.PostType;

import static com.shaya.poinila.android.presentation.view.ViewUtils.validateInputs;
import static com.shaya.poinila.android.util.ConstantsUtils.NO_RESOURCE;
import static com.shaya.poinila.android.util.ConstantsUtils.max_length_image_post_summary;
import static com.shaya.poinila.android.util.ConstantsUtils.max_length_text_post_summary;

/**
 * Created by iran on 2015-10-07.
 */
public class RepostDialog extends NewPostDialog{

    private static final String KEY_POST = "initial post";
    private Post post;

    public static RepostDialog newInstance(Post post) {
        Bundle args = new Bundle();
        RepostDialog fragment = new RepostDialog();
        args.putParcelable(KEY_POST, Parcels.wrap(post));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void loadStateFromBundle(Bundle savedStateBundle) {
        super.loadStateFromBundle(savedStateBundle);
        post = Parcels.unwrap(savedStateBundle.getParcelable(KEY_POST));
        expanded = savedStateBundle.getBoolean(KEY_IS_EXPANDED);
    }

    @Override
    public void saveStateToBundle(Bundle outState) {
        outState.putParcelable(KEY_POST, Parcels.wrap(createPostFromFields()));
        outState.putBoolean(KEY_IS_EXPANDED, expanded);
    }



    /*private void setDefaultValues(Post post) {
        if (post.type == PostType.IMAGE){
            isImaged = true;
            if (TextUtils.isEmpty(post.name)){
                name.setVisibility(View.GONE);
            }else {
                setAndDisableName();
            }
            pickerView.setVisibility(View.VISIBLE);
            if (!pickerView.hasImage()) pickerView.setImage(post.imagesUrls.properPostImage(BIG).url);

            content.setVisibility(View.GONE);
        }else { // Text Post
            isImaged = false;
            setAndDisableName();
            setAndDisableContent();
            // baraye etiminan!
            pickerView.setVisibility(View.GONE);
        }

        summary.setText(post.summary);

        for (Tag tag : post.tags) {
            ViewInflater.addRemovableTagToContainer(tagsFlowLayout, tag.name);
            tags.add(tag);
        }
    }*/

    /*private void setAndDisableContent() {
        content.setVisibility(View.VISIBLE);
        content.setEnabled(false);
        content.setText(post.content);
    }

    private void setAndDisableName() {
        name.setVisibility(View.VISIBLE);
        name.setEnabled(false);
        name.setText(post.name);
    }*/

    @Override
    protected void initUI(Context context) {
        //super.initUI(context);
        /*new Handler().postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                if (rootView != null)
                    rootView.removeView(postTypeContainer);
            }
        });*/

        sharedInitUIBetweenRepostAndNewPost();
        if (post.type == PostType.TEXT)
            onTextType();
        else
            onImageType();
        postTypeContainer.setVisibility(View.GONE);
        fillViewsWithValues(post, true);
    }



    @Override
    public void onPositiveButton() {
        if (!validate()) return;
        PoinilaNetService.repost(((Collection) collectionSpinner.getSelectedItem()).getId(),
                post.id, summary.getText().toString(), tags);
        BusProvider.getSyncUIBus().post(new UpdateUiRepostEvent(post.id, true));
        onNegativeButton();
    }

    @Override
    protected boolean validate() {
        if (collectionSpinner.getSelectedItem() == null)
            return false;

        EditText[] requiredItems = new EditText[]{};
        EditText[] limitedItems = new EditText[]{summary};
        int[] minLengths = new int[]{};
        int[] maxLengths;
        if (isImaged){
            maxLengths = new int[]{max_length_image_post_summary};
        }else{
            maxLengths = new int[]{max_length_text_post_summary};
        }
        return validateInputs(requiredItems, minLengths, limitedItems, maxLengths);
    }

    @Subscribe
    public void onRemoveTag(final RemoveTagEvent event){
        super.removeTag(event);
    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(R.string.repost_post, NO_RESOURCE, R.string.create, R.string.cancel, NO_RESOURCE);
    }
}
