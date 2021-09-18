package com.shaya.poinila.android.presentation.view.costom_view;

import android.Manifest;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.isseiaoki.simplecropview.CropImageView;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.PermissionEvent;
import com.shaya.poinila.android.presentation.uievent.SelectImageEvent;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.DeviceInfoUtils;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.util.ResourceUtils;
import com.shaya.poinila.android.util.StorageUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setViewsVisibilityToGone;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setViewsVisibilityToVisible;

/**
 * Created by iran on 12/1/2015.
 */
public class GalleryCameraImagePickerView extends FrameLayout implements ImagePickerCropper {

    // related to crop
    @Bind(R.id.cropView)
    CropImageView mCropImageView;

    @Bind(R.id.doneButton)
    ImageButton mDoneButton;
    @Bind(R.id.removeButton)
    ImageButton mRemoveButton;
    @Bind(R.id.cropButton)
    ImageButton mCropButton;
    // related to rotate
    @Bind(R.id.rotateRightButton)
    ImageButton mRotateRightButton;
    @Bind(R.id.rotateLeftButton)
    ImageButton mRotateLeftButton;

    //TODO: ability to rotate image in factor of 90 degrees
    //@Bind(R.id.doneButton) ImageButton mRotateButton;

    // related to pick image
    @Bind(R.id.pick_image_container)
    LinearLayout mPickerLayout;
    @Bind(R.id.galleryButton)
    ImageButton mGalleryButton;
    @Bind(R.id.cameraButton)
    ImageButton mCameraButton;
    @Bind(R.id.image_container)
    ViewGroup mCropViewContainer;

    public ShowMode showMode = ShowMode.Selecting;
    public Policy policy = Policy.FullFeatures;
    private boolean waitingForPermissionGrant = false;
    protected Target picassoTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            setImage(bitmap);
        }
        @Override
        public void onBitmapFailed(Drawable errorDrawable) {}
        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {}
    };


    public Bitmap getImage() {
        switch (showMode) {
            case Showing:
                return getImageBitmap();
            case Cropping:
                return getCroppedImageBitmap();
        }
        return null;
    }

    public boolean hasImage() {
        return showMode != ShowMode.Selecting;
    }

    public boolean isInCropMode() {
        return showMode == ShowMode.Cropping;
    }

    public enum ShowMode {
        Cropping,
        Showing,
        Selecting
    }

    public enum Policy {
        FullFeatures,
        NoFeature,
        CropFullScreen,
    }

    public GalleryCameraImagePickerView(Context context) {
        this(context, null);
    }

    public GalleryCameraImagePickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GalleryCameraImagePickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater inflater = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        inflater.inflate(getLayoutId(), this, true);
        ButterKnife.bind(this, this);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.GalleryCameraImagePickerView, defStyle, 0);
        if (ta.getInt(R.styleable.GalleryCameraImagePickerView_crop_ratio, 1) == 2)
            mCropImageView.setCropMode(CropImageView.CropMode.RATIO_FREE);
        ta.recycle();

        init();
    }

    protected void init() {
        switch (showMode) {
            case Cropping:
                goToCropMode();
                break;
            case Showing:
                goToShowMode();
                break;
            case Selecting:
                goToSelectMode();
                break;
        }
    }

    private
    @LayoutRes
    int getLayoutId() {
        return R.layout.crop_image;
    }

    /*---------------ImagePickerCropper------------*/

    /**
     * Manually sets picker view image to passed bitmap. Usage is discouraged. Use {@link #setImage(String)} instead
     *
     * @param bitmap
     */
    @Override
    public void setImage(Bitmap bitmap) {
        mCropImageView.setImageBitmap(bitmap);
        onImageSet();
    }

    /**
     * Gives an address in form of absolute file address, content uri and url and renders it using
     * picasso api and set it as cropView image.
     *
     * @param address
     */
    @Override
    public void setImage(String address) {
        // Picasso can't handle addresses in form of "/mnt/sdcard0..." "/storage/..."

        address = StorageUtils.isLocalNonContentFile(address) ? StorageUtils.toSchemedFileAddress(address) : address;

        Picasso.with(getContext()).load(address).
                resize(ResourceUtils.getDisplayMetrics().widthPixels, ResourceUtils.getDisplayMetrics().heightPixels).
                centerInside().
                config(Bitmap.Config.ARGB_8888).
                into(mCropImageView);

        onImageSet();
    }

    // good place for declaring custom behaviors
    private void onImageSet() {
        mPickerLayout.setVisibility(View.GONE);
        switch (policy){
            case CropFullScreen:
            case FullFeatures:
                goToCropMode();
                break;
            case NoFeature:
                goToShowMode();
                break;
        }
    }

    /**
     * Changes the state of the view (updates the buttons) and replaces the bitmap with cropped bitmap
     */
    @OnClick(R.id.doneButton)
    public void submitCrop() {
        cropImage();
        goToShowMode();
    }

    public void goToShowMode() {
        mCropViewContainer.setVisibility(View.VISIBLE);
        if (policy == Policy.FullFeatures) {
            setViewsVisibilityToVisible(mRemoveButton, mCropButton);
            setViewsVisibilityToGone(mDoneButton, mRotateLeftButton, mRotateRightButton);
        } else if (policy == Policy.NoFeature || policy == Policy.CropFullScreen) {
            setViewsVisibilityToGone(new ArrayList<View>(Arrays.asList(
                    mDoneButton, mRemoveButton, mCropButton, mRotateLeftButton, mRotateRightButton)));
        }
        mCropImageView.setCropEnabled(false);
        showMode = ShowMode.Showing;
    }

    /**
     * Not used normally. If you just want to invoke crop process call {@link #submitCrop()} instead.
     */
    @OnClick(R.id.cropButton)
    public void goToCropMode() {
        mCropViewContainer.setVisibility(View.VISIBLE);

        if (policy == Policy.FullFeatures) {
            setViewsVisibilityToVisible(new ArrayList<View>(Arrays.asList(mRemoveButton, mDoneButton, mRotateLeftButton, mRotateRightButton)));
            setViewsVisibilityToGone(new ArrayList<View>(Collections.singletonList(mCropButton)));
        } else if (policy == Policy.CropFullScreen) {
            setViewsVisibilityToVisible(new ArrayList<View>(Arrays.asList(mRotateLeftButton, mRotateRightButton)));
            setViewsVisibilityToGone(new ArrayList<View>(Arrays.asList(mCropButton, mDoneButton, mRemoveButton)));
        }
        mCropImageView.setCropEnabled(true);
        showMode = ShowMode.Cropping;
    }

    @Override
    @OnClick(R.id.removeButton)
    public void removeImage() {
        goToSelectMode();
    }

    private void goToSelectMode() {
        mPickerLayout.setVisibility(View.VISIBLE);
        mCropViewContainer.setVisibility(View.GONE);
        showMode = ShowMode.Selecting;
    }

    /**
     * Not used normally. If you just want to invoke crop process call {@link #submitCrop()} instead. <br/>
     * Crops the image and set it as new image resource
     */
    @Override
    public void cropImage() {
        mCropImageView.setImageBitmap(mCropImageView.getCroppedBitmap());
    }

    /**
     * For getting bitmap considering picker view state call {@link #getImage()}
     *
     * @return picker view initial bitmap
     */
    @Override
    public Bitmap getImageBitmap() {
        return mCropImageView.getImageBitmap();
    }

    // TODO: use BitmapRegionDecoder for cropping original image

    /**
     * For getting bitmap considering picker view state call {@link #getImage()}
     *
     * @return bitmap in crop region
     */
    @Override
    public Bitmap getCroppedImageBitmap() {
        return mCropImageView.getCroppedBitmap();
    }


    //Define the list of accepted constants
    @IntDef({ROTATE_RIGHT_90, ROTATE_LEFT_90})
    //Tell the compiler not to store annotation data in the .class file
    @Retention(RetentionPolicy.SOURCE)
    //Declare the NavigationMode annotation
    public @interface RotateStep {
    }

    public static final int ROTATE_RIGHT_90 = 270;
    public static final int ROTATE_LEFT_90 = 90;

    @Override
    public void rotate(@RotateStep int degrees) {
        if (degrees == ROTATE_RIGHT_90) {
            mCropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
        } else if (degrees == ROTATE_LEFT_90) {
            mCropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_270D);
        } else {
            throw new IllegalArgumentException("use either ROTATE_RIGHT_90 or ROTATE_LEFT_90");
        }
    }

    @OnClick(R.id.rotateRightButton)
    void rotateRight90Degrees() {
        rotate(ROTATE_RIGHT_90);
    }

    @OnClick(R.id.rotateLeftButton)
    void rotateLeft90Degrees() {
        rotate(ROTATE_LEFT_90);
    }

    @OnClick(R.id.cameraButton)
    public void pickFromCamera() {
        BusProvider.getBus().post(new PermissionEvent(Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    @OnLongClick(R.id.cameraButton)
    public boolean showCameraHint() {
        Logger.toast(R.string.hint_take_picture);
        return true;
    }

    @OnClick(R.id.galleryButton)
    public void pickFromGallery() {
        BusProvider.getBus().post(new SelectImageEvent(
                StorageUtils.dispatchSelectImageIntent(), ConstantsUtils.REQUEST_CODE_PICK_IMAGE));
    }

    @OnLongClick(R.id.galleryButton)
    public boolean showGalleryHint() {
        Logger.toast(R.string.hint_pick_from_gallery);
        return true;
    }

    public void startPickingFromCamera() {

    }


    // Save/Restore support ////////////////////////////////////////////////////////////////////////

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.showMode = this.showMode;
        ss.policy = this.policy;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.showMode = ss.showMode;
        this.policy = ss.policy;
    }

    public static class SavedState extends BaseSavedState {
        ShowMode showMode;
        Policy policy;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            showMode = (ShowMode) in.readSerializable();
            policy = (Policy) in.readSerializable();
        }

        @Override
        public void writeToParcel(Parcel out, int flag) {
            super.writeToParcel(out, flag);
            out.writeSerializable(showMode);
            out.writeSerializable(policy);
        }

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
            public SavedState createFromParcel(final Parcel inParcel) {
                return new SavedState(inParcel);
            }

            public SavedState[] newArray(final int inSize) {
                return new SavedState[inSize];
            }
        };
    }

    /*@Subscribe public void onPermissionResult(PermissionEvent event){
        if (!waitingForPermissionGrant)
            return;

        if (event.requestCode == BaseActivity.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE){
            if (event.granted){
                BusProvider.getBus().post(new SelectImageEvent(
                        StorageUtils.dispatchCapturePhotoIntent(), ConstantsUtils.REQUEST_CODE_TAKE_PHOTO));
            } else
                Logger.longToast(getContext().getString(R.string.permission_reason_camera));
        }
    }*/
}
