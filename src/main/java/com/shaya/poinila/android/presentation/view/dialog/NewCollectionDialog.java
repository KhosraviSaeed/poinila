package com.shaya.poinila.android.presentation.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v13.app.FragmentCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.CirclesSelectedUIEvent;
import com.shaya.poinila.android.presentation.uievent.PermissionEvent;
import com.shaya.poinila.android.presentation.uievent.SelectImageEvent;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.presentation.view.activity.BaseActivity;
import com.shaya.poinila.android.presentation.view.costom_view.ActivityResultPermissionDelegate.ImagePickerResultPermissionDelegate;
import com.shaya.poinila.android.presentation.view.costom_view.GalleryCameraImagePickerView;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.util.PoinilaPreferences;
import com.shaya.poinila.android.util.ResourceUtils;
import com.shaya.poinila.android.util.StorageUtils;
import com.shaya.poinila.android.util.StringUtils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import data.PoinilaNetService;
import data.event.BaseEvent;
import data.event.TopicsReceivedEvent;
import data.model.Circle;
import data.model.Collection;
import data.model.PrivacyType;
import data.model.Topic;
import manager.DBFacade;
import manager.DataRepository;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;
import static com.shaya.poinila.android.util.ConstantsUtils.max_length_collection_description;
import static com.shaya.poinila.android.util.ConstantsUtils.max_length_collection_name;
import static com.shaya.poinila.android.util.ConstantsUtils.min_length_collection_name;

/**
 * Created by iran on 2015-07-15.
 */
public class NewCollectionDialog extends BusDialogFragment implements FragmentCompat.OnRequestPermissionsResultCallback{

    protected static final String ABSOLUTE_PATH = "absolute path";
    protected static final String MEDIA_PATH = "media path";

    private static final String TAG_CIRCLE_DIALOG = "circle dialog";
    private static final java.lang.String KEY_EXPANDED = "is expanded";
    @Bind(R.id.caption_field)
    TextInputEditText nameField;
    @Bind(R.id.checkbox)
    CheckBox privateCollectionCheckbox;
    @Bind(R.id.privacy_container)
    ViewGroup privacyContainer;
    @Bind(R.id.select_circle)
    Button selectCircleBtn;
    @Bind(R.id.select_topic)
    Spinner topicSpinner;

    @Bind(R.id.expand)
    ImageButton expandButton;
    @Bind(R.id.description_field)
    TextInputEditText descriptionField;
    @Bind(R.id.description_input_layout)
    TextInputLayout descriptionInputlayout;
    @Bind(R.id.pickerView)
    GalleryCameraImagePickerView pickerView;

    private ImagePickerResultPermissionDelegate resultPermissionDelegateIMPL;

    protected boolean topicsReceived;
    List<Circle> mCircles;
    CharSequence[] mCircleNames;
    boolean[] mCheckedCircles;
    protected MySpinnerAdapter mSpinnerAdapter;
    private boolean expanded = false;

    @Override
    public int getLayoutResId() {
        return R.layout.dialog_new_collection;
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {
        expanded = savedInstanceState.getBoolean(KEY_EXPANDED, false);
    }

    @Override
    protected void saveStateToBundle(Bundle outState) {

    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(R.string.new_collection, RESOURCE_NONE, R.string.create, R.string.cancel, RESOURCE_NONE);
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
                startForResult(NewCollectionDialog.this,
                        StorageUtils.dispatchCapturePhotoIntent(),
                        ConstantsUtils.REQUEST_CODE_TAKE_PHOTO);
            }
        };
        pickerView.policy = GalleryCameraImagePickerView.Policy.FullFeatures;

        mCircles = DBFacade.getMyCircles();
        mCircleNames = new CharSequence[mCircles.size()];
        mCheckedCircles = new boolean[mCircles.size()];
        for (int i = 0; i < mCircleNames.length; i++) {
            mCircleNames[i] = mCircles.get(i).name;
        }

        privateCollectionCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectCircleBtn.setEnabled(isChecked);
                setText(selectCircleBtn,
                        isChecked ? createSelectedCirclesText(mCheckedCircles) :
                                createSelectedCirclesText(null));
            }
        });
        setText(selectCircleBtn, createSelectedCirclesText(null));

        expandOrCollapseArbitraryFields(expanded);
    }

    @Override
    public void onPositiveButton() {
        // TODO: request to server for creating new collectionSpinner
        if (!validate())
            return;

        Bitmap bitmap = null;
        if (pickerView.showMode == GalleryCameraImagePickerView.ShowMode.Cropping){
            Logger.toast(R.string.warning_complete_crop);
            return;
        }

        bitmap = pickerView.getImage();

        PoinilaNetService.createCollection(PoinilaPreferences.getMyId(),
                createCollectionFromFields(), bitmap);
        super.onPositiveButton();
    }

    @OnClick(R.id.expand)
    public void onExpand() {
        expanded ^= true;
        expandOrCollapseArbitraryFields(expanded);
    }

    private void expandOrCollapseArbitraryFields(boolean expanded) {
        View[] toChangeViews = new View[]{descriptionInputlayout, pickerView};
        if (expanded) {
            ViewUtils.setViewsVisibilityToVisible(toChangeViews);
        } else {
            ViewUtils.setViewsVisibilityToGone(toChangeViews);
        }
        expandButton.setImageResource(expanded ? R.drawable.arrow_up_white_24dp : R.drawable.arrow_down_white_24dp);
    }


    @Override
    protected boolean sendsRequestAutomatically() {
        return true;
    }

    @Override
    protected void requestInitialData() {
        DataRepository.getInstance().getTopics();
    }

    @Override
    public ViewGroup getLoadableView() {
        return null;
    }

    @Override
    public boolean mustShowProgressView() {
        return false;
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
    //---------------------------

    protected boolean validate() {
        if (!topicsReceived) {
            Logger.toastError(R.string.error_select_topic);
            return false;
        }

        if (pickerView.hasImage()) {
            Bitmap bitmap = pickerView.getImageBitmap();
            if (bitmap.getWidth() < ConstantsUtils.MIN_LENGTH_COLLECTION_COVER_DIMENSION ||
                    bitmap.getHeight() < ConstantsUtils.MIN_LENGTH_COLLECTION_COVER_DIMENSION) {
                Logger.toastError(R.string.error_small_image);
                return false;
            }
        }

        /*return ViewUtils.validateEntityName(nameField) &&
                ViewUtils.validateEdittexts(
                        new EditText[]{nameField}, new int[]{min_length_collection_name},
                        new EditText[]{nameField, descriptionField}, new int[]{max_length_collection_name, max_length_collection_description});*/

        return ViewUtils.validateInputs(
                        new EditText[]{nameField}, new int[]{min_length_collection_name},
                        new EditText[]{nameField, descriptionField}, new int[]{max_length_collection_name, max_length_collection_description})
                && ViewUtils.validateEntityName(nameField);
    }

    @Subscribe
    public void onTopicReceived(TopicsReceivedEvent event) {
        onGettingInitDataResponse(event);
    }

    @Override
    public void onSuccessfulInitData(BaseEvent baseEvent) {
        super.onSuccessfulInitData(baseEvent);
        mSpinnerAdapter = new MySpinnerAdapter(getActivity(), ((TopicsReceivedEvent) baseEvent).data);
        topicSpinner.setAdapter(mSpinnerAdapter);
        topicSpinner.setEnabled(true);
        topicsReceived = true;
    }

    protected Collection createCollectionFromFields() {
        Collection collection = new Collection();
        collection.name = nameField.getText().toString();
        collection.description = descriptionField.getText().toString();
        collection.topic = ((Topic) topicSpinner.getSelectedItem());
        collection.privacy = (privateCollectionCheckbox.isChecked()) ? PrivacyType.PRIVATE : PrivacyType.PUBLIC;
        List<Integer> circleIDs = new ArrayList<>(mCircles.size());
        for (int i = 0; i < mCheckedCircles.length; i++)
            if (mCheckedCircles[i])
                circleIDs.add(mCircles.get(i).id);
        collection.circleIDs = circleIDs;
        return collection;
    }

    @OnClick(R.id.select_circle)
    public void showCirclesDialog() {
        CirclesDialog.newInstance(mCircleNames, mCheckedCircles).
                show(getFragmentManager(), TAG_CIRCLE_DIALOG);
    }

    @Subscribe
    public void onCirclesSelectedEvent(CirclesSelectedUIEvent event) {
        mCheckedCircles = event.selectedCircles;
        setText(selectCircleBtn, createSelectedCirclesText(mCheckedCircles));
    }

    protected String createSelectedCirclesText(boolean[] checkedCircles) {
        if (checkedCircles == null) return ResourceUtils.getString(R.string.visible_to_everyone);
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < checkedCircles.length; i++) {
            if (checkedCircles[i])
                strings.add(mCircleNames[i].toString());
        }
        if (strings.isEmpty())
            return ResourceUtils.getString(R.string.private_for_all);
        return StringUtils.join(strings, ", ");
    }

    class MySpinnerAdapter extends BaseAdapter {

        List<Topic> items;
        private Context context;

        public MySpinnerAdapter(Context context, List<Topic> topics) {
            this.context = context;
            items = topics;
        }

        public void addItems(List<Topic> topics) {
            items.addAll(topics);
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
            View spinView;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                spinView = inflater.inflate(R.layout.spinner_item, parent, false);
            } else {
                spinView = convertView;
            }
            ((TextView) spinView.findViewById(R.id.spinner_row_title)).setText(((Topic) getItem(position)).name);
            return spinView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View spinView;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                spinView = inflater.inflate(R.layout.spinner_item, parent, false);
            } else {
                spinView = convertView;
            }
            ((TextView) spinView.findViewById(R.id.spinner_row_title)).setText(((Topic) getItem(position)).name);
            return spinView;
        }
    }


    public static class CirclesDialog extends android.support.v4.app.DialogFragment {
        private static final String KEY_CIRCLE_NAMES = "circles";
        private static final String KEY_CHECKED_ITEMS = "checked";

        public static CirclesDialog newInstance(CharSequence[] circleNames, boolean[] checkedItems
        ) {
            CirclesDialog df = new CirclesDialog();
            Bundle b = new Bundle();
            b.putCharSequenceArray(KEY_CIRCLE_NAMES, circleNames);
            b.putBooleanArray(KEY_CHECKED_ITEMS, checkedItems);
            df.setArguments(b);
            return df;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Bundle b = getArguments();
            CharSequence[] circleNames = b.getCharSequenceArray(KEY_CIRCLE_NAMES);
            final boolean[] checkedItems = b.getBooleanArray(KEY_CHECKED_ITEMS);


            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Set the dialog title
            builder.setTitle(R.string.select_circle)
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    .setMultiChoiceItems(circleNames, checkedItems,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which,
                                                    boolean isChecked) {
                                    if (isChecked) {
                                        // If the user checked the item, add it to the selected items
                                        checkedItems[which] = true;
                                    } else if (checkedItems[which]) {
                                        // Else, if the item is already in the array, remove it
                                        checkedItems[which] = false;
                                    }
                                }
                            })
                            // Set the action buttons
                    .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            BusProvider.getBus().post(new CirclesSelectedUIEvent(checkedItems, -1));
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            CirclesDialog.this.dismiss();
                        }
                    });
            return builder.create();
        }
    }
}
