package com.shaya.poinila.android.presentation.view.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v13.app.FragmentCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.PermissionEvent;
import com.shaya.poinila.android.presentation.uievent.RemoveTagEvent;
import com.shaya.poinila.android.presentation.uievent.SelectImageEvent;
import com.shaya.poinila.android.presentation.uievent.UpdateNewPostDialogEvent;
import com.shaya.poinila.android.presentation.view.ViewInflater;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.presentation.view.activity.BaseActivity;
import com.shaya.poinila.android.presentation.view.costom_view.ActivityResultPermissionDelegate;
import com.shaya.poinila.android.presentation.view.costom_view.ActivityResultPermissionDelegate.ImagePickerResultPermissionDelegate;
import com.shaya.poinila.android.presentation.view.costom_view.GalleryCameraImagePickerView;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.util.StorageUtils;
import com.shaya.poinila.android.util.StringUtils;
import com.squareup.otto.Subscribe;

import org.apmem.tools.layouts.FlowLayout;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import data.PoinilaNetService;
import data.model.Collection;
import data.model.ImageUrls;
import data.model.Post;
import data.model.PostType;
import data.model.SuggestedWebPagePost;
import data.model.Tag;
import manager.DBFacade;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setValueAndDisableInputLayout;
import static com.shaya.poinila.android.presentation.view.ViewUtils.validateEntityName;
import static com.shaya.poinila.android.presentation.view.ViewUtils.validateInputs;
import static com.shaya.poinila.android.presentation.view.costom_view.GalleryCameraImagePickerView.Policy.NoFeature;
import static com.shaya.poinila.android.util.ConstantsUtils.NO_RESOURCE;
import static com.shaya.poinila.android.util.ConstantsUtils.max_length_image_post_summary;
import static com.shaya.poinila.android.util.ConstantsUtils.max_length_image_post_title;
import static com.shaya.poinila.android.util.ConstantsUtils.max_length_tag;
import static com.shaya.poinila.android.util.ConstantsUtils.max_length_text_post_content;
import static com.shaya.poinila.android.util.ConstantsUtils.max_length_text_post_summary;
import static com.shaya.poinila.android.util.ConstantsUtils.max_length_text_post_title;
import static com.shaya.poinila.android.util.ConstantsUtils.min_length_tag;
import static com.shaya.poinila.android.util.ConstantsUtils.min_length_text_post_content;
import static com.shaya.poinila.android.util.ConstantsUtils.min_length_text_post_title;

/**
 * Created by iran on 2015-08-26.
 */
public class NewPostDialog extends BusDialogFragment implements FragmentCompat.OnRequestPermissionsResultCallback{
    private static final String KEY_WEBSITE_POST = "website post";
    private static final java.lang.String KEY_IS_IMAGED_POST = "is imaged post";
    private static final String KEY_IS_FROM_WEB = "is from web";
    protected static final String KEY_IS_EXPANDED = "is expanded";
    //AbstractCreateDialogPost extends AbstractDialogContent {
    @Bind(R.id.post_type_container)
    RadioGroup postTypeContainer;
    @Bind(R.id.image_radio_btn)
    protected RadioButton image;
    @Bind(R.id.text_radio_btn)
    protected RadioButton text;

    @Bind(R.id.tag_field)
    protected EditText tagField;
    @Bind(R.id.tags_flowlayout)
    protected FlowLayout tagsFlowLayout;
    @Bind(R.id.tags_container)
    protected ViewGroup tagsContainer;

    @Bind(R.id.select_collection)
    protected AppCompatSpinner collectionSpinner;

    @Bind(R.id.expand)
    ImageButton expandButton;

    @Bind(R.id.caption_field)
    TextInputEditText name;
    @Bind(R.id.summary_field)
    TextInputEditText summary;
    @Bind(R.id.caption_input_layout)
    TextInputLayout titleInputLayout;
    @Bind(R.id.summary_input_layout)
    TextInputLayout summaryInputLayout;

    protected List<Tag> tags;

    /*-------image----*/
    @Bind(R.id.pickerView)
    GalleryCameraImagePickerView pickerView;

    /*-----text------*/
    @Bind(R.id.content_field)
    TextInputEditText contentInput;
    @Bind(R.id.content_input_layout)
    TextInputLayout contentInputLayout;

    // state variables
    private SuggestedWebPagePost suggestedPost;
    protected boolean isImaged = true;
    private boolean isFromWeb = false;
    protected boolean expanded;
    private ActivityResultPermissionDelegate.ImagePickerResultPermissionDelegate resultPermissionDelegateIMPL;

    MySpinnerAdapter mSpinnerAdapter;


    public static NewPostDialog newInstance(SuggestedWebPagePost suggestedPost) {
        // TODO: use Parceler library later but read https://guides.codepath.com/android/Must-Have-Libraries first
        NewPostDialog fragment = new NewPostDialog();
        Bundle args = new Bundle();

        // kasif o lus!
        fragment.suggestedPost = suggestedPost;

        fragment.saveStateToBundle(args);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void saveStateToBundle(Bundle outState) {
        outState.putParcelable(KEY_WEBSITE_POST, Parcels.wrap(suggestedPost));
        outState.putBoolean(KEY_IS_FROM_WEB, suggestedPost != null);

        outState.putBoolean(KEY_IS_IMAGED_POST, isImaged);
        outState.putBoolean(KEY_IS_EXPANDED, expanded);
    }

    @Override
    protected void loadStateFromBundle(Bundle savedStateBundle) {
        suggestedPost = Parcels.unwrap(savedStateBundle.getParcelable(KEY_WEBSITE_POST));
        isFromWeb = suggestedPost != null;

        isImaged = savedStateBundle.getBoolean(KEY_IS_IMAGED_POST);
        expanded = savedStateBundle.getBoolean(KEY_IS_EXPANDED);
    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(R.string.new_post, NO_RESOURCE, R.string.create, R.string.cancel, NO_RESOURCE);
    }

    @Override
    protected void initUI(Context context) {
        resultPermissionDelegateIMPL = new ImagePickerResultPermissionDelegate() {
            @Override
            public void handleValidResults(int requestCode, Intent data) {
                super.handleValidResults(requestCode, data);
                pickerView.setImage(resultPermissionDelegateIMPL.getImageAddress());
            }

            @Override
            public void handlePermissionGranted() {
                startForResult(NewPostDialog.this,
                        StorageUtils.dispatchCapturePhotoIntent(),
                        ConstantsUtils.REQUEST_CODE_TAKE_PHOTO);
            }
        };

        sharedInitUIBetweenRepostAndNewPost();

        postTypeContainer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.image_radio_btn:
                        onImageType();
                        break;
                    case R.id.text_radio_btn:
                        onTextType();
                        break;
                }
            }
        });

        if (isFromWeb) {
            postTypeContainer.setVisibility(View.GONE);
            if (TextUtils.isEmpty(suggestedPost.imageAddress))
                onTextType();
            else
                onImageType();
            fillViewsWithValues(suggestedPost);
        }

        expandOrCollapseArbitraryFields(expanded);
    }

    protected void sharedInitUIBetweenRepostAndNewPost() {
        tags = new ArrayList<>();
        mSpinnerAdapter = new MySpinnerAdapter(getActivity(), DBFacade.getMyCollections());
        collectionSpinner.setAdapter(mSpinnerAdapter);
        collectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position){
                    case 1:
                        new NewCollectionDialog().show(getFragmentManager(), null);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        pickerView.policy = NoFeature;
        if (tagField != null) {
            tagField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        addTagIfValid(tagField, true);
                        return true;
                    }
                    return false; // ??
                }
            });
        }
    }

    protected void fillViewsWithValues(Post post, boolean disableNonEditableFields) {
        if (post.type == PostType.IMAGE) {
            if (TextUtils.isEmpty(post.name) && !isFromWeb) {
                titleInputLayout.setVisibility(View.GONE);
            } else {
                setValueAndDisableInputLayout(titleInputLayout, post.name, disableNonEditableFields);
            }

            pickerView.setVisibility(View.VISIBLE);
            if (!pickerView.hasImage() && !isFromWeb) // will be set for web post in different way
                pickerView.setImage(post.imagesUrls.properPostImage(ImageUrls.ImageSize.BIG).url);

            contentInputLayout.setVisibility(View.GONE);
        } else { // Text Post
            setValueAndDisableInputLayout(titleInputLayout, post.name, disableNonEditableFields);
            setValueAndDisableInputLayout(contentInputLayout, post.content, disableNonEditableFields);
            // baraye etiminan!
            pickerView.setVisibility(View.GONE);
        }

        summary.setText(post.summary);

        for (Tag tag : post.tags) {
            ViewInflater.addRemovableTagToContainer(tagsFlowLayout, tag.name);
            tags.add(tag);
        }
    }

    private void fillViewsWithValues(SuggestedWebPagePost suggestedPost) {
        fillViewsWithValues(new Post(suggestedPost.name, suggestedPost.summary, suggestedPost.content, suggestedPost.tags), false);
        if (!TextUtils.isEmpty(suggestedPost.imageAddress))
            pickerView.setImage(suggestedPost.imageAddress);
    }

    private void addTagIfValid(EditText tagField, boolean makeToast) {
        EditText[] editTexts = new EditText[]{tagField};
        if (validateEntityName(tagField) &&
                validateInputs(editTexts, new int[]{min_length_tag}, editTexts, new int[]{max_length_tag})) {
            addTag(tagField.getText().toString());
        }
    }

    private void addTag(String tagString) {
        Tag newTag = new Tag(-1, tagString);
        addTagViewToContainer(newTag.name);
        tags.add(newTag);
        tagField.setText("");
    }

    public void addTagViewToContainer(String tagText) {
        ViewInflater.addRemovableTagToContainer(tagsFlowLayout, tagText);
    }

    @Subscribe
    public void removeTag(final RemoveTagEvent event) {
        tags.remove(event.adapterPosition);
        new Handler().postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                tagsFlowLayout.removeViewAt(event.adapterPosition); //ButterKnife.findById(container, R.actorID.dialog_content)
            }
        });
    }

    @Subscribe
    public void onUpdateCollectionSpinner(UpdateNewPostDialogEvent event){
        mSpinnerAdapter.addItem((Collection)event.model);
        collectionSpinner.setSelection(mSpinnerAdapter.getCount() - 1);
        mSpinnerAdapter.notifyDataSetChanged();
    }


    class MySpinnerAdapter extends BaseAdapter {

        private final int SPINNER_ITEM_TYPE = 0;
        private final int SPINNER_PROMPT_TYPE = 1;

        List<Collection> items;
        private Context context;

        public MySpinnerAdapter(Context context, List<Collection> collections) {
            this.context = context;
            items = new ArrayList<>();
            Collection defaultItem = new Collection();
            defaultItem.name = "---";
            Collection createNew = new Collection();
            createNew.name = getString(R.string.create_new_collection);
            items.add(defaultItem);
            items.add(createNew);
            items.addAll(collections);
        }

        public void addItems(List<Collection> Collections) {
            /*for (Collection Collection : mCircles)
                items.add(Collection);*/
            items.addAll(Collections);
        }

        public void addItem(Collection collection){
            items.add(collection);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getSpinnerView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            return getSpinnerView(position, convertView, parent);
        }

        private View getSpinnerView(int position, View convertView, ViewGroup parent){
            View spinView;
            spinView = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);
            ((TextView) spinView.findViewById(R.id.spinner_row_title)).setText(((Collection) getItem(position)).name);

            return spinView;
        }
    }

    /*--------------New form----------------*/

    protected Post createPostFromFields() {
        Post post = new Post();
        post.name = name.getText().toString();
        post.summary = summary.getText().toString().replaceAll("\\n", "\n");
        post.tags = tags;
        if (!isImaged) {
            // Edit text removes nextlines, tabs etc. we insert them explicitly.
            // Html.toHtml() creates a <p> tag with direction attribute which textView doesn't support!
            post.content = StringUtils.removeHtmlDirAttribute(
                    //Html.toHtml(Html.fromHtml(
                    contentInput.getText().toString().replaceAll("\\n", "\n"));
            //));
        }
        return post;
    }

    @Override
    public void onPositiveButton() {

        if (!validate())
            return;

        if(collectionSpinner == null
                || collectionSpinner.getCount() == 0
                || collectionSpinner.getSelectedItemPosition() <= 1){
            Logger.toastError(R.string.error_select_collection);
            return;
        }

        String collectionID = ((Collection) collectionSpinner.getSelectedItem()).getId();
        Post newPost = createPostFromFields();
        /*image*/
        if (isFromWeb) {
            PoinilaNetService.createReferencedPost(collectionID, newPost, suggestedPost.siteAddress, suggestedPost.imageAddress, suggestedPost.videoAddress);
            getActivity().finish();
        } else if (isImaged) {
            //TODO:
            PoinilaNetService.createImagePost(collectionID, pickerView.getImage(), newPost);
        } else { // normal text post
            PoinilaNetService.createTextPost(collectionID, newPost);
        }
//        onNegativeButton();
        dismiss();
    }

    @Override
    public void onNegativeButton() {
        super.onNegativeButton();
        if(collectionSpinner.getSelectedItemPosition() == 1)
            collectionSpinner.setSelection(0);
    }

    // image post views order: MANDATORY: image, summary, collection | ARBITRARY: tags, name | HIDE: content
    // text  post views order: MANDATORY: name, content, collection | ARBITRARY: tags, summary | HIDE: image
    // default order for union of text and image post:
    // title, content, image, collection, tags, summary
    public void onTextType() {
        isImaged = false;
        updateViews(PostType.TEXT);
        titleInputLayout.setCounterMaxLength(getResources().getInteger(R.integer.max_length_text_post_title));
        summaryInputLayout.setCounterMaxLength(getResources().getInteger(R.integer.max_length_text_post_summary));
    }

    public void onImageType() {
        isImaged = true;
        updateViews(PostType.IMAGE);
        titleInputLayout.setCounterMaxLength(getResources().getInteger(R.integer.max_length_image_post_title));
        titleInputLayout.setCounterMaxLength(getResources().getInteger(R.integer.max_length_image_post_summary));
    }


    private void updateViews(PostType postType) {
        expanded = false;
        expandOrCollapseArbitraryFields(expanded);
        final LinearLayout ll = getLLContainer();

        final List<View> toHideViews = (postType == PostType.TEXT) ?
                Arrays.<View>asList(tagsContainer, summaryInputLayout) :
                Arrays.<View>asList(tagsContainer, titleInputLayout);
        final List<View> toVisibleViews = (postType == PostType.TEXT) ?
                Arrays.<View>asList(titleInputLayout, contentInputLayout) :
                Arrays.<View>asList(pickerView, summaryInputLayout);

        // we add all views that must be shown every time from scratch.
        final List<View> toShowViews = (postType == PostType.TEXT) ?
                Arrays.<View>asList(postTypeContainer, titleInputLayout, contentInputLayout, collectionSpinner, expandButton, tagsContainer, summaryInputLayout) :
                Arrays.<View>asList(postTypeContainer, pickerView, summaryInputLayout, collectionSpinner, expandButton, tagsContainer, titleInputLayout);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ll.removeAllViews();
                ViewUtils.setViewsVisibilityToGone(toHideViews);
                ViewUtils.setViewsVisibilityToVisible(toVisibleViews);
                for (View child : toShowViews) {
                    ll.addView(child);
                }
            }
        });
    }

    private LinearLayout getLLContainer() {
        return ButterKnife.findById(rootView, R.id.dialog_content);
    }

    @OnClick(R.id.expand)
    public void onExpand() {
        expanded ^= true;
        expandOrCollapseArbitraryFields(expanded);
    }

    private void expandOrCollapseArbitraryFields(boolean expanded) {
        View[] toChangeViews = isImaged ? new View[]{tagsContainer, titleInputLayout} : /*text post */ new View[]{tagsContainer, summaryInputLayout};
        if (expanded) {
            ViewUtils.setViewsVisibilityToVisible(toChangeViews);
        } else {
            ViewUtils.setViewsVisibilityToGone(toChangeViews);
        }
        expandButton.setImageResource(expanded ? R.drawable.arrow_up_white_24dp : R.drawable.arrow_down_white_24dp);
    }

    /*---------image----------*/

    protected boolean validate() {
        EditText[] requiredItems;
        EditText[] limitedItems;
        int[] maxLengths, minLengths;
        if (isImaged) {
            requiredItems = new EditText[]{};
            limitedItems = new EditText[]{name, summary};
            minLengths = new int[]{};//min_length_image_post_summary};
            maxLengths = new int[]{max_length_image_post_title, max_length_image_post_summary};
        } else {
            requiredItems = new EditText[]{name, contentInput};
            limitedItems = new EditText[]{name, summary, contentInput};
            minLengths = new int[]{min_length_text_post_title, min_length_text_post_content};
            maxLengths = new int[]{max_length_text_post_title, max_length_text_post_summary, max_length_text_post_content};
        }

        if (isImaged) {
            if (!pickerView.hasImage()) {
                Logger.toast(R.string.error_imagepost_without_image);
                return false;
            } else {
                Bitmap bitmap = pickerView.getImageBitmap();
                if (bitmap.getWidth() < ConstantsUtils.MINIMUM_POST_IMAGE_WIDTH ||
                        bitmap.getHeight() < ConstantsUtils.MINIMUM_POST_IMAGE_HEIGHT) {
                    Logger.toastError(R.string.error_small_image);
                    return false;
                }
            }
        }
        return validateInputs(requiredItems, minLengths, limitedItems, maxLengths);
    }


    @Subscribe
    public void selectImageEvent(SelectImageEvent event) {
        resultPermissionDelegateIMPL.startForResult(this, event.intent, event.requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        resultPermissionDelegateIMPL.onActivityResult(requestCode, resultCode, data);
    }

    // I think it's cleaner to ask for permission directly in pickerView
    @Subscribe public void askForPermissionEvent(PermissionEvent event){
        resultPermissionDelegateIMPL.askForPermission(this, event.permissionString, BaseActivity.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        resultPermissionDelegateIMPL.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /*not important implemented methods*/
    @Override
    protected boolean sendsRequestAutomatically() {
        return false;
    }

    @Override
    protected void requestInitialData() {
    }

    @Override
    public ViewGroup getLoadableView() {
        return null;
    }

    @Override
    public boolean mustShowProgressView() {
        return false;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.dialog_new_post;
    }

}
