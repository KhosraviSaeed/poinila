package com.shaya.poinila.android.presentation.view.dialog;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.presentation.view.activity.BaseActivity;
import com.shaya.poinila.android.presentation.view.costom_view.ActivityResultPermissionDelegate;
import com.shaya.poinila.android.presentation.viewholder.BaseViewHolder;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.StorageUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import data.model.ImageUrls;
import data.model.Member;

/**
 * Created by iran on 6/6/2016.
 */
public class SelectImageDialog extends BaseDialogFragment{

    private Member member;

    private View.OnClickListener onItemClickListener;

    @Bind(R.id.show_profile_image)
    Button showProfileImage;

    @Bind(R.id.select_image_camera)
    Button selectImageCamera;

    @Bind(R.id.select_image_gallery)
    Button selectImageGallery;

    public static SelectImageDialog newInstance(Member member, View.OnClickListener onItemClickListener){
        Bundle args = new Bundle();
        SelectImageDialog fragment = new SelectImageDialog();
        fragment.member = member;
        fragment.onItemClickListener = onItemClickListener;
        fragment.saveStateToBundle(args);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public int getLayoutResId() {
        return R.layout.select_image_dialog;
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {

    }

    @Override
    protected void saveStateToBundle(Bundle outState) {

    }

    @Override
    protected void initUI(Context context) {

        showProfileImage.setOnClickListener(onItemClickListener);
        selectImageCamera.setOnClickListener(onItemClickListener);
        selectImageGallery.setOnClickListener(onItemClickListener);

        ViewUtils.setFont(showProfileImage, getString(R.string.default_bold_font_path));
        ViewUtils.setFont(selectImageCamera, getString(R.string.default_bold_font_path));
        ViewUtils.setFont(selectImageGallery, getString(R.string.default_bold_font_path));


        if(member != null && (member.imageUrls == null || !member.imageUrls.isNotEmpty())){
            showProfileImage.setVisibility(View.GONE);
        }

    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(null, null, null, null, null);
    }



}
