package com.shaya.poinila.android.presentation.view.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.presentation.view.costom_view.AspectRatioImageView;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.utils.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;

import data.model.ImageUrls;

/**
 * Created by iran on 8/11/2016.
 */
public class PonilaVideoView extends LinearLayout
        implements
        PonilaMediaController.MediaPlayerControl,
        SurfaceHolder.Callback,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,
        View.OnClickListener{

    // all possible internal states
    private static final int STATE_ERROR              = -1;
    private static final int STATE_IDLE               = 0;
    private static final int STATE_PREPARING          = 1;
    private static final int STATE_PREPARED           = 2;
    private static final int STATE_PLAYING            = 3;
    private static final int STATE_PAUSED             = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    private int mCurrentState = STATE_IDLE;
    private int mTargetState  = STATE_IDLE;

    private MediaPlayer mMediaPlayer;
    private PonilaMediaController mMediaController;

    private SurfaceView videoSurface;
    private SurfaceHolder mVideoHolder;
    private Uri videoUri;
    private boolean fullScreen = false;
    private OnFullScreenListener onFullScreenListener;
    private AspectRatioImageView previewView;
    private ImageButton largePlayBtn;
    private View loading;
    private ViewGroup videoOverlay;
    private int mCurrentBufferPercentage;

    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
            mCurrentBufferPercentage = percent;
            if(mCurrentBufferPercentage > mMediaController.getSecondaryProgress())
                mMediaController.setSecondaryProgress(mCurrentBufferPercentage);
        }
    };

    public PonilaVideoView(Context context) {
        super(context);
        init();
    }

    public PonilaVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PonilaVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

        inflate(getContext(), R.layout.ponila_video_view, this);

        videoSurface = (SurfaceView) findViewById(R.id.videoSurface);

        previewView = (AspectRatioImageView)findViewById(R.id.video_preview);
        largePlayBtn = (ImageButton)findViewById(R.id.video_play_btn);
        loading = findViewById(R.id.progress_view);
        videoOverlay = (ViewGroup) findViewById(R.id.video_overlay);
        loading.setVisibility(GONE);

        largePlayBtn.setOnClickListener(this);

        videoSurface.getHolder().addCallback(this);
        videoSurface.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        videoSurface.setZOrderOnTop(true);

        mMediaController = new PonilaMediaController(getContext());

        mCurrentState = STATE_IDLE;
        mTargetState  = STATE_IDLE;
    }

    private void openVideo(){

        if(videoUri == null || mVideoHolder == null)return;

        release(false);
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(getContext(), videoUri);
            mMediaPlayer.setDisplay(mVideoHolder);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mCurrentBufferPercentage = 0;
            mMediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;
            attachMediaController();
        } catch (IOException e) {
            e.printStackTrace();
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
        } catch (IllegalStateException e){
            e.printStackTrace();
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
        }

    }

    private void attachMediaController() {
        if (mMediaPlayer != null && mMediaController != null) {
            mMediaController.setMediaPlayer(this);
            mMediaController.setAnchorView((FrameLayout)findViewById(R.id.videoSurfaceContainer));
//            mMediaController.setEnabled(isInPlaybackState());
        }
    }

    public void setVideoPreview(ImageUrls imageUrls){
        ViewUtils.setImage(
                previewView,
                imageUrls,
                ImageUrls.ImageType.POST,
                ImageUrls.ImageSize.BIG);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isInPlaybackState()){
            mMediaController.show();
        }
        return false;
    }

    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(mMediaPlayer != null && mTargetState == STATE_PLAYING)
            start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mVideoHolder = holder;
//        openVideo();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mVideoHolder = null;
//        if (mMediaController != null) mMediaController.hide();
        release(true);
    }

    // End SurfaceHolder.Callback

    // Implement MediaPlayer.OnPreparedListener

    public void onPrepared(MediaPlayer mp) {
        mCurrentState = STATE_PREPARED;
        if (mTargetState == STATE_PLAYING) {
//            videoSurface.setZOrderOnTop(false);
            start();
            loading.setVisibility(GONE);
            previewView.setVisibility(GONE);
        }
    }
    // End MediaPlayer.OnPreparedListener

    // Implement VideoMediaController.MediaPlayerControl
    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public boolean isFullScreen() {
        return fullScreen;
    }

    @Override
    public void toggleFullScreen() {
        if(!Utils.isEnabledAutoRotate()){
            Logger.toastError(R.string.change_orientation_error);
            return;
        }
        if(onFullScreenListener != null){
            fullScreen = !fullScreen;
            onFullScreenListener.stateChanged();
        }
    }

    @Override
    public int getBufferPercentage() {
        if(mMediaPlayer != null){
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;
    }

    public void suspend(){
        release(false);
    }

    public void resume(){
        openVideo();
    }

    @Override
    public void seekTo(int i) {
        mMediaPlayer.seekTo(i);
    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
    }

    private boolean isInPlaybackState() {
        return
//                mMediaPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING;
    }

    public void setVideoPath(String path){
        videoUri = Uri.parse(path);
        requestLayout();
        invalidate();
    }

    private void release(boolean cleartargetstate) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
//            mPendingSubtitleTracks.clear();
            mCurrentState = STATE_IDLE;
            if (cleartargetstate) {
                mTargetState  = STATE_IDLE;
            }
        }
    }

    public void setFullScreenMode(boolean status){
        fullScreen = status;
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int framework_err, int impl_err) {
        mCurrentState = STATE_ERROR;
        mTargetState = STATE_ERROR;
        if(mMediaController != null)
            mMediaController.hide();
        Logger.toastError(R.string.play_video_error);

        largePlayBtn.setVisibility(VISIBLE);
        loading.setVisibility(GONE);
        videoOverlay.setVisibility(VISIBLE);

        return false;
    }

    public void setOnFullScreenListener(OnFullScreenListener onFullScreenListener) {
        this.onFullScreenListener = onFullScreenListener;
    }

    @Override
    public void onClick(View view) {
        openVideo();
        view.setVisibility(GONE);
        loading.setVisibility(VISIBLE);
        videoOverlay.setVisibility(GONE);
        start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        mCurrentState = STATE_PLAYBACK_COMPLETED;
        mTargetState = STATE_PLAYBACK_COMPLETED;

        if(mMediaController != null)
            mMediaController.hide();
        previewView.setVisibility(VISIBLE);
        largePlayBtn.setVisibility(VISIBLE);
        loading.setVisibility(GONE);
        videoOverlay.setVisibility(VISIBLE);

    }

    public interface OnFullScreenListener{
        void stateChanged();
    }
}
