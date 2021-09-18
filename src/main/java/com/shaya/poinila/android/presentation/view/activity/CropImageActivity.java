package com.shaya.poinila.android.presentation.view.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.isseiaoki.simplecropview.CropImageView;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.presentation.view.costom_view.GalleryCameraImagePickerView;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.ImageUtils;
import com.shaya.poinila.android.util.Logger;

import butterknife.Bind;
import butterknife.OnClick;
import data.PoinilaNetService;

import static com.shaya.poinila.android.util.ConstantsUtils.KEY_IMAGE_ADDRESS;
import static com.shaya.poinila.android.util.ConstantsUtils.PROFILE_PIC_MIN_DIMENSION;

public class CropImageActivity extends ToolbarActivity {

    @Bind(R.id.crop_image_view)
    GalleryCameraImagePickerView pickerView;

    @Bind(R.id.submit) Button submitBtn;
    @Bind(R.id.cancel) Button cancelBtn;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_crop_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.doneButton){
            if (pickerView.showMode == GalleryCameraImagePickerView.ShowMode.Cropping){
                item.setIcon(R.drawable.crop_24dp);
                pickerView.submitCrop();
                cancelBtn.setVisibility(View.VISIBLE);
                submitBtn.setVisibility(View.VISIBLE);
            }else if (pickerView.showMode == GalleryCameraImagePickerView.ShowMode.Showing){
                item.setIcon(R.drawable.done_flamingo_24dp);
                pickerView.goToCropMode();
                cancelBtn.setVisibility(View.GONE);
                submitBtn.setVisibility(View.GONE);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.submit) public void onSubmit(){
        // TODO Refactoring: get REQUEST_ID from intent extras or pass the cropped bitmap and let
        // the activity starter pager handle the case.
        Bitmap cropAvatar = pickerView.getImage();
        if (ViewUtils.validateImage(cropAvatar, PROFILE_PIC_MIN_DIMENSION)) {
            PoinilaNetService.uploadProfilePicture(cropAvatar);
            onCancel();
        }
    }

    @OnClick(R.id.cancel) public void onCancel(){
        finish();
    }

    @Override
    protected void initUI() {
        pickerView.policy = GalleryCameraImagePickerView.Policy.CropFullScreen;
        pickerView.setImage(getIntent().getStringExtra(KEY_IMAGE_ADDRESS));
        pickerView.goToCropMode();
    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_crop_image;
    }

    @Override
    protected View getActivityView() {
        ViewGroup vp = (ViewGroup) getLayoutInflater().inflate(getLayoutResourceId(), null);
        toolbar = (Toolbar) getLayoutInflater().inflate(R.layout.toolbar_black, vp, false);
        handleToolbar();
        vp.addView(toolbar, 0);
        return vp;
    }

}
