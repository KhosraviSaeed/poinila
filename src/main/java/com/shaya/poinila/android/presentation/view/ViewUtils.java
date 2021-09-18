package com.shaya.poinila.android.presentation.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.text.BidiFormatter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.mobsandgeeks.saripaar.ValidationError;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.NotifParticipantClickedUIEvent;
import com.shaya.poinila.android.presentation.view.costom_view.AspectRatioImageView;
import com.shaya.poinila.android.presentation.view.costom_view.SvgMaskedImageView;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.ContextHolder;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.util.ResourceUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import data.model.Image;
import data.model.ImageUrls;
import data.model.Notification;
import data.model.Participant;
import de.hdodenhof.circleimageview.CircleImageView;
import static com.shaya.poinila.android.util.ResourceUtils.getDimen;
import static com.shaya.poinila.android.util.ResourceUtils.getString;
import static com.shaya.poinila.android.util.ResourceUtils.getStringFormatted;
import static com.shaya.poinila.android.util.StringUtils.persianNumber;

/**
 * checks field for being null avoiding NullPointerException. In case of imagesUrls set placeholder
 * also.
 */
public class ViewUtils {

    public static void setText(TextView textView, CharSequence text) {
        if (textView != null && text != null) {
            /*if (textView.getVisibility() == View.GONE)
                textView.setVisibility(View.VISIBLE);*/
            textView.setText(text);
        }
        /*else if (textView != null)
            textView.setVisibility(View.GONE);*/
    }

    public static void setFont(TextView textView, String path){
        Typeface typeface = Typeface.createFromAsset(textView.getContext().getAssets(), path);
        textView.setTypeface(typeface);
    }

    public static void setText(TextView textView, int number) {
        setText(textView, persianNumber(number));
    }

    public static void setImage(ImageView imageView, ImageUrls urls,
                                ImageUrls.ImageType imageType, ImageUrls.ImageSize imageSize) {
        setImage(imageView, urls, imageType, imageSize, null);
    }

    public static void setImage(ImageView imageView, ImageUrls urls,
                                ImageUrls.ImageType imageType, ImageUrls.ImageSize imageSize, @NonNull Transformation transformation) {
        if (imageView == null)
            return;
        Drawable placeHolderDrawable = properPlaceHolder(urls, imageType, imageSize);
        Image properImage = null;
        if (urls != null && urls.isNotEmpty()) {
            switch (imageType) {
                case POST:
                    properImage = urls.properPostImage(imageSize);
                    if (imageSize != ImageUrls.ImageSize.AVATAR) { // maintain ratio
                        //imageView.getLayoutParams().height = imageView.getLayoutParams().width * properImage.height / properImage.width;
                        ((AspectRatioImageView) imageView).setAspectRatio(properImage.height * 1f / properImage.width);
                        imageView.requestLayout();
                    }
                    break;
                case COLLECTION:
                    properImage = urls.properCollectionImage(imageSize);
                    break;
                case MEMBER:
                    properImage = urls.properMemberImage(imageSize);
                    break;
                case INTEREST:
                    properImage = urls.interest;
                    break;
            }
        }
        handleImage(imageView, properImage, placeHolderDrawable, transformation);
    }

    private static Drawable properPlaceHolder(ImageUrls urls, ImageUrls.ImageType imageType, ImageUrls.ImageSize imageSize) {
        if (urls != null && !TextUtils.isEmpty(urls.dominantColor)) {
            if (!urls.dominantColor.contains("#"))
                urls.dominantColor = "#" + urls.dominantColor;
            return new ColorDrawable(Color.parseColor(urls.dominantColor));
        }

        int defaultResId = 0;
        switch (imageType) {
            case POST:
                if (imageSize == ImageUrls.ImageSize.AVATAR)// maintain ratio
                    defaultResId = R.drawable.post_no_image;
                break;
            case COLLECTION:
                defaultResId = R.drawable.collection_no_image;
                break;
            case MEMBER:
                if (imageSize == ImageUrls.ImageSize.BIG)
                    defaultResId = R.drawable.user_no_image_big;
                else if (imageSize == ImageUrls.ImageSize.AVATAR)
                    defaultResId = R.drawable.user_no_image;
                break;
        }
        return defaultResId == 0 ? null : ContextCompat.getDrawable(ContextHolder.getContext(), defaultResId);
    }

    private static void handleImage(ImageView imageView, Image properImage, Drawable placeHolderDrawable,
                                    Transformation transformation) {
        if (properImage != null) {
            RequestCreator creator = Picasso.with(imageView.getContext()).load(properImage.url);
            if (placeHolderDrawable != null)
                creator.placeholder(placeHolderDrawable).into(imageView);
            if (transformation != null)
                creator.transform(transformation);
            creator.into(imageView);
        } else {
            imageView.setImageDrawable(placeHolderDrawable);
        }
    }

    public static void setNotificationTitle(TextView titleView, Notification notification) {
        List<Participant> participants = notification.participants;
        switch (notification.type) {
            case FRIENDS_FOLLOWED_COLLECTIONS:
            case FRIENDS_LIKED_POSTS:
            case FRIENDS_CREATED_COLLECTIONS:
                setText(titleView, notification.mainActor.userName);
                return;
        }

        String Space = " ";
        String AND = ResourceUtils.getString(R.string.and);
        String OTHER_PEOPLE = ResourceUtils.getString(R.string.other_people);

        StringBuilder title = new StringBuilder();
        title.append("%s");
        if (participants.size() > 1) {
            String participantsCount = persianNumber(participants.size() - 1);
            title.append(Space).append(AND).append(Space).append(participantsCount).append(Space).append(OTHER_PEOPLE);
        }
        /*int participantsLimit = ResourceUtils.getInteger(R.integer.participants_show_limit);
        if (participants.size() > participantsLimit){
            for (int i = 0; i < participantsLimit; i++) {
                title.append(participants.get(i).uniqueName);
                if (i != participantsLimit)
            }
            title.append(AND).append(participants.size() - participantsLimit).append(" ").append(OTHER_PEOPLE);
        }
        else if (participants.size() == participantsLimit){
            title.append(participants.get(0).uniqueName).append(COMMA).
                    append(participants.get(1).uniqueName).append(AND).append(participants.get(2).uniqueName);
        }
        else if (participants.size() == 2){
            title.append(participants.get(0).uniqueName).append(AND).append(participants.get(1).uniqueName);
        }
        else{ // == 1
            title.append(participants.get(0).uniqueName);
        }*/
        /*BidiFormatter.getInstance(true).unicodeWrap(title.toString())*/
        String wrappedUniqueName = BidiFormatter.getInstance(true /* rtlContext */).unicodeWrap(participants.get(0).userName);
        String formattedText = String.format(title.toString(), wrappedUniqueName);
        setText(titleView, formattedText);
    }

    public static void setNotificationImages(ViewGroup imageContainer,
                                             List<Participant> participants,
                                             ImageUrls.ImageType participantImageType) {
        int participantsLimit = ResourceUtils.getInteger(R.integer.participants_show_limit);
        Context context = imageContainer.getContext();
        imageContainer.removeAllViews();
        int participantsSize = (int) getDimen(R.dimen.participant_size);

        for (int i = 0; i < participantsLimit && i < participants.size(); i++) {
            ImageView imageView = null;
            ImageUrls urls = participants.get(i).imageUrls;
            switch (participantImageType) {
                case MEMBER:
                    imageView = new CircleImageView(context);
                    break;
                case POST:
                    imageView = new SvgMaskedImageView(context);
                    break;
                case COLLECTION:
                    imageView = new RoundedImageView(context);
                    int corner = ((int) getDimen(R.dimen.corner_lvlhalf));
                    ((RoundedImageView) imageView).setCornerRadius(corner);
                    break;
            }

            imageContainer.addView(imageView, 0);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    participantsSize, participantsSize);
            params.setMargins(0, 0, ((int) getDimen(R.dimen.margin_lvlhalf)), 0);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            setParticipantClickListener(imageView, participants.get(i), participantImageType);
            if (urls == null) {
                Picasso.with(imageView.getContext()).load(getDefaultNotifImage(participantImageType)).into(imageView);
            } else {
                setImage(imageView, urls, participantImageType, ImageUrls.ImageSize.AVATAR);
            }
        }
        if (participants.size() > participantsLimit) {
            TextView moreParticipant = new TextView(context);
            moreParticipant.setLayoutParams(new ViewGroup.LayoutParams(participantsSize, participantsSize));
            moreParticipant.setGravity(Gravity.CENTER);

            imageContainer.addView(moreParticipant);
            setText(moreParticipant, "+" + persianNumber(participants.size() - participantsLimit));
        }
    }

    private static void setParticipantClickListener(
            ImageView imageView, final Participant participant, final ImageUrls.ImageType participantImageType) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getBus().post(
                        new NotifParticipantClickedUIEvent(participant, participantImageType));
            }
        });
    }

    public static void enableLayoutChildes(ViewGroup viewGroup, boolean enable) {
        viewGroup.setEnabled(enable);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup)
                enableLayoutChildes(((ViewGroup) child), enable);
        }
    }

    public static int getDefaultNotifImage(ImageUrls.ImageType type) {
        switch (type) {
            case COLLECTION:
                return R.drawable.collection_no_image;
            case POST:
                return R.drawable.post_no_image;
            case MEMBER:
                return R.drawable.user_no_image;
        }
        return -1;
    }

    public static void removeView(final ViewGroup parentView, final int position) {
        new Handler().postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                parentView.removeViewAt(position); //ButterKnife.findById(container, R.actorID.dialog_content)
            }
        });
    }

    public static void removeView(final ViewGroup parentView, final View removeView) {
        new Handler().postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                parentView.removeView(removeView); //ButterKnife.findById(container, R.actorID.dialog_content)
            }
        });
    }


    public static boolean validateInputs(EditText[] requiredItems, int[] minLengths, EditText[] limitedItems, int[] maxLengths) {
        for (int i = 0; i < requiredItems.length; i++) {
            validateInputMinLength(requiredItems[i], minLengths[i]);
        }
        for (int i = 0; i < limitedItems.length; i++) {
            validateInputMaxLength(limitedItems[i], maxLengths[i]);
        }
        return true;
    }

    public static boolean validatePasswordInput(EditText passwordInput){
        return validateInputMinLength(passwordInput, ConstantsUtils.min_length_password);
    }

    public static boolean validateInputMinLength(EditText requiredInput, int minLength){
        String text = requiredInput.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            setInputError(requiredInput, getString(R.string.error_required_field));
            return false;
        } else if (text.length() < minLength) {
            setInputError(requiredInput, getStringFormatted(R.string.error_min_formatted, minLength));
            return false;
        }
        return true;
    }

    public static boolean validateInputMaxLength(EditText editText, int maxLength) {
        String text = editText.getText().toString().trim();
        if (text.length() > maxLength) {
            setInputError(editText, getStringFormatted(R.string.error_max_formatted, maxLength));
            return false;
        }
        return true;
    }

    public static boolean validateEdittextsByRegex(EditText[] regexItems, Pattern[] regexPatterns, @StringRes int[] regexErrors) {
        for (int i = 0; i < regexItems.length; i++) {
            String text = regexItems[i].getText().toString().trim();
            if (!regexPatterns[i].matcher(text).matches()) {
                setInputError(regexItems[i], getString(regexErrors[i]));
                return false;
            }
        }
        return true;
    }

    public static boolean validateEntityName(EditText editText) {
        return validateEdittextsByRegex(new EditText[]{editText}, new Pattern[]{ConstantsUtils.entitiesNamePattern}, new int[]{R.string.error_entity_name});
    }

    public static void temporaryError(final EditText editText, String error) {
        editText.setError(error);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                editText.setError(null);
            }
        }, 2000);
    }

    public static void setInputError(final EditText editText, @StringRes int errorResId){
        setInputError(editText, getString(errorResId));
    }

    public static void setInputError(final EditText editText, String error) {
        if (editText.getParent() instanceof TextInputLayout) {
            final TextInputLayout inputLayout = (TextInputLayout) editText.getParent();
            inputLayout.setErrorEnabled(true);
            inputLayout.setError(error);
            TextWatcher inputLayoutTextWatcher = null;
            final TextWatcher finalInputLayoutTextWatcher = inputLayoutTextWatcher;
            inputLayoutTextWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    inputLayout.setErrorEnabled(false);
                    inputLayout.setError(null);
                    editText.removeTextChangedListener(finalInputLayoutTextWatcher);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };
            editText.addTextChangedListener(inputLayoutTextWatcher);
        } else {
            temporaryError(editText, error);
        }
    }

    public static boolean validateImage(Bitmap cropAvatar, int minDimensionSize) {
        if(cropAvatar == null ){
            Logger.toastError(R.string.error_no_image_found);
            return false;
        }
        if (cropAvatar.getWidth() < minDimensionSize || cropAvatar.getHeight() < minDimensionSize) {
            Logger.toastError(R.string.error_small_image);
            return false;
        }
        return true;
    }

    public static void setViewsVisibilityToVisible(View... views) {
        setViewsVisibilityToVisible(Arrays.asList(views));
    }

    public static void setViewsVisibilityToGone(View... views) {
        setViewsVisibilityToGone(Arrays.asList(views));
    }

    public static void setViewsVisibilityToVisible(List<View> views) {
        ButterKnife.apply(views, VISIBILITY, View.VISIBLE);
    }

    public static void setViewsVisibilityToGone(List<View> views) {
        ButterKnife.apply(views, VISIBILITY, View.GONE);
    }

    static final ButterKnife.Setter<View, Integer> VISIBILITY = new ButterKnife.Setter<View, Integer>() {
        @Override
        public void set(View view, Integer value, int index) {
            view.setVisibility(value);
        }
    };

    public static void setValueAndDisableTextView(TextView textView, String value, boolean disableField) {
        textView.setVisibility(View.VISIBLE);
        textView.setEnabled(!disableField);
        textView.setText(value);
    }

    public static void setValueAndDisableInputLayout(TextInputLayout inputLayout, String value, boolean disableField) {
        if (disableField) {
            inputLayout.setCounterEnabled(false);
            inputLayout.setErrorEnabled(false);
            inputLayout.setError(null);
        }
        setValueAndDisableTextView(inputLayout.getEditText(), value, disableField);
    }

    public static void handleSaripaarErrors(List<ValidationError> errors, Context context) {
        for (ValidationError error : errors) {
            if (error.getView() instanceof TextView) {
                setInputError(((EditText) error.getView()),
                        error.getCollatedErrorMessage(context));
            }
        }
    }

    public static boolean validateUrl(EditText urlInput) {
        return validateEdittextsByRegex(new EditText[]{urlInput},
                new Pattern[]{Patterns.WEB_URL}, new int[]{R.string.error_invalid_url});
    }
}
