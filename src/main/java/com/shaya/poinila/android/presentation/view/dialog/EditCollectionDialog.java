package com.shaya.poinila.android.presentation.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.CirclesSelectedUIEvent;
import com.shaya.poinila.android.presentation.uievent.PermissionEvent;
import com.shaya.poinila.android.presentation.uievent.SelectImageEvent;
import com.shaya.poinila.android.presentation.view.activity.BaseActivity;
import com.shaya.poinila.android.presentation.view.costom_view.EditCollectionImagePickerView;
import com.shaya.poinila.android.util.Logger;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;
import data.PoinilaNetService;
import data.event.CollectionReceivedEvent;
import data.event.CollectionUpdatedEvent;
import data.event.PostReceivedEvent;
import data.event.TopicsReceivedEvent;
import data.model.Circle;
import data.model.Collection;
import data.model.Image;
import data.model.ImageUrls;
import data.model.PrivacyType;
import data.model.Topic;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;

/**
 * Created by AlirezaF on 7/19/2015.
 */
public class EditCollectionDialog extends NewCollectionDialog{
    public static final String KEY_COVER_URL = "cover url";
    private static String tempCoverUrl;
    private Collection collection;

    @Bind(R.id.pickerView) EditCollectionImagePickerView pickerView;

    public static EditCollectionDialog newInstance(Collection collection) {
        // TODO: make collection parcelable
        Bundle args = new Bundle();
        EditCollectionDialog fragment = new EditCollectionDialog();
        fragment.collection = collection;
        fragment.saveStateToBundle(args);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void saveStateToBundle(Bundle outState) {
        super.saveStateToBundle(outState);
        // TODO: store createCollectionFromFields in bundle
        outState.putString(KEY_COVER_URL, tempCoverUrl);
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {
        super.loadStateFromBundle(savedInstanceState);
        // TODO load colletion from pardel
        tempCoverUrl = savedInstanceState.getString(KEY_COVER_URL, null);
    }

    @Override
    protected void initUI(Context context) {
        super.initUI(context);
        pickerView.setOnPickCoverFromPostsListener(new EditCollectionImagePickerView.OnPickCoverFromPostsListener() {
            @Override
            public void onPickCoverFromPosts() {
                DialogLauncher.launchPickCoverFromPosts(getFragmentManager(), collection.getId());
            }
        });
        setDefaultValues(collection);
    }


    private void setDefaultValues(Collection collection) {
        if (collectionHasCover(collection) && !pickerView.hasImage() && collection.coverImageUrls != null) {
            Image image = collection.coverImageUrls.properCollectionImage(ImageUrls.ImageSize.BIG);
            if(image != null)
                pickerView.setImage(image.url);
        }

        setText(nameField, collection.name);
        setText(descriptionField, collection.description);
        if (collection.privacy == PrivacyType.PUBLIC) {
            privacyContainer.setVisibility(View.GONE);
            selectCircleBtn.setVisibility(View.GONE);
        } else{
            privateCollectionCheckbox.setChecked(true);
            findCheckedCircles(mCheckedCircles, collection.circleIDs, mCircles);
            setText(selectCircleBtn, createSelectedCirclesText(mCheckedCircles));
        }
    }

    private boolean collectionHasCover(Collection collection) {
        return collection.coverImageUrls != null;
    }

    private void findCheckedCircles(boolean[] checkedCircles,
                                    List<Integer> circleIDs, List<Circle> circles) {
        for (Integer circleID : circleIDs) {
            for (int i = 0; i < circles.size(); i++){
                if (circleID == circles.get(i).id)
                    checkedCircles[i] = true;
            }
        }
    }

    @Subscribe public void onTopicReceived(TopicsReceivedEvent event){
        super.onTopicReceived(event);
        int index = findTopicIndex(mSpinnerAdapter, collection.topic.id);
        topicSpinner.setSelection(index);
    }

    @Subscribe public void onCirclesSelectedEvent(CirclesSelectedUIEvent event){
        super.onCirclesSelectedEvent(event);
    }

    @Subscribe public void onPostImageReceived(PostReceivedEvent event){
        tempCoverUrl = event.post.imagesUrls.properPostImage(ImageUrls.ImageSize.BIG).url;
        pickerView.setImage(tempCoverUrl);
    }

    private int findTopicIndex(MySpinnerAdapter mSpinnerAdapter, int topicID) {
        for (int i = 0; i < mSpinnerAdapter.getCount(); i++) {
            if (((Topic) mSpinnerAdapter.getItem(i)).id == topicID)
                return i;
        }
        return -1;
    }

    @Override
    public void onPositiveButton() {
        if (!validate())
            return;

        showProgressDialog();

        Logger.log("Image = " + pickerView.getImage(), Logger.LEVEL_INFO);

        PoinilaNetService.updateCollection(collection.getId(),
                createCollectionFromFields(), pickerView.getImage());


    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(R.string.edit_collection, RESOURCE_NONE, R.string.edit, R.string.cancel, RESOURCE_NONE);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.dialog_edit_collection;
    }

    @Subscribe
    public void selectImageEvent(SelectImageEvent event) {
        super.selectImageEvent(event);
    }

    // I think it's cleaner to ask for permission directly in pickerView
    @Subscribe public void askForPermissionEvent(PermissionEvent event){
        super.askForPermissionEvent(event);
    }

    @Subscribe
    public void onCollectionUpdated(CollectionUpdatedEvent event) {
        dismissProgressDialog();
        onNegativeButton();
    }

    @Subscribe
    public void onCollectionReceived(CollectionReceivedEvent event){
        dismissProgressDialog();
        onNegativeButton();
    }
}
