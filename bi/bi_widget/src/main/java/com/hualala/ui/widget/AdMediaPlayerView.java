package com.hualala.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.hualala.libcommonui.RatioFrameLayout;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.PLOnPreparedListener;
import com.pili.pldroid.player.PLOnVideoSizeChangedListener;

public class AdMediaPlayerView extends RatioFrameLayout {

    public static final String SDCARD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String DEFAULT_CACHE_DIR = SDCARD_DIR + "/PLDroidPlayer";

    private static final String TAG = AdMediaPlayerView.class.getSimpleName();

    private SurfaceView mSurfaceView;
    private PLMediaPlayer mMediaPlayer;
    private AVOptions mAVOptions;

    private int mSurfaceWidth = 0;
    private int mSurfaceHeight = 0;

    private String mVideoPath = null;
    private boolean mIsStopped = false;


    private boolean mDisableLog = false;

    public AdMediaPlayerView(Context context) {
        super(context);
        mSurfaceView = new SurfaceView(getContext());
        addView(mSurfaceView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public AdMediaPlayerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mSurfaceView = new SurfaceView(getContext());
        addView(mSurfaceView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public AdMediaPlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSurfaceView = new SurfaceView(getContext());
        addView(mSurfaceView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }


    private Intent mIntent;

    public void onCreate(Intent intent) {
        mIntent = intent;

        mVideoPath = mIntent.getStringExtra("videoPath");

        Log.d(TAG, "视频播放地址：" + mVideoPath);

        boolean isLiveStreaming = mIntent.getIntExtra("liveStreaming", 1) == 1;

        mSurfaceView.getHolder().addCallback(mCallback);

        mSurfaceWidth = getResources().getDisplayMetrics().widthPixels;
        mSurfaceHeight = getResources().getDisplayMetrics().heightPixels;

        mAVOptions = new AVOptions();
        mAVOptions.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        // 1 -> hw codec enable, 0 -> disable [recommended]
        int codec = mIntent.getIntExtra("mediaCodec", AVOptions.MEDIA_CODEC_AUTO);
        mAVOptions.setInteger(AVOptions.KEY_MEDIACODEC, codec);
        mAVOptions.setInteger(AVOptions.KEY_LIVE_STREAMING, isLiveStreaming ? 1 : 0);
        boolean cache = mIntent.getBooleanExtra("cache", false);
        if (!isLiveStreaming && cache) {
            mAVOptions.setString(AVOptions.KEY_CACHE_DIR, DEFAULT_CACHE_DIR);
        }
        mDisableLog = mIntent.getBooleanExtra("disable-log", false);
        mAVOptions.setInteger(AVOptions.KEY_LOG_LEVEL, mDisableLog ? 5 : 0);
        if (!isLiveStreaming) {
            int startPos = mIntent.getIntExtra("start-pos", 0);
            mAVOptions.setInteger(AVOptions.KEY_START_POSITION, startPos * 1000);
        }


        AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (mMediaPlayer != null) {
            prepare();
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onDestroy();
    }

    protected void onDestroy() {
        release();
        AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(null);
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mSurfaceView != null) {
            mSurfaceView.getHolder().removeCallback(mCallback);
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            Log.d(TAG, "广告播放停止");
            mMediaPlayer.stop();
        }
    }

    private void prepare() {
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new PLMediaPlayer(getContext(), mAVOptions);
                mMediaPlayer.setLooping(getIntent().getBooleanExtra("loop", false));
                mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
                mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
                mMediaPlayer.setOnCompletionListener(null);
                mMediaPlayer.setOnErrorListener(null);
                mMediaPlayer.setOnInfoListener(null);
                mMediaPlayer.setOnBufferingUpdateListener(null);
                mMediaPlayer.setWakeMode(getContext().getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            }
            mMediaPlayer.setDataSource(mVideoPath);
            mMediaPlayer.setDisplay(mSurfaceView.getHolder());
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "广告播放异常");
        }
    }

    private Intent getIntent() {
        return mIntent;
    }

    private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            prepare();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            release();
        }
    };

    private PLOnVideoSizeChangedListener mOnVideoSizeChangedListener = new PLOnVideoSizeChangedListener() {
        public void onVideoSizeChanged(int width, int height) {
            Log.d(TAG, "onVideoSizeChanged: width = " + width + ", height = " + height);
            if (width != 0 && height != 0) {
                float ratioW = (float) width / (float) mSurfaceWidth;
                float ratioH = (float) height / (float) mSurfaceHeight;
                float ratio = Math.max(ratioW, ratioH);
                width = (int) Math.ceil((float) width / ratio);
                height = (int) Math.ceil((float) height / ratio);
                FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(width, height);
                layout.gravity = Gravity.CENTER;
                mSurfaceView.setLayoutParams(layout);

                //视频是横屏 旋转方向
                if ((width > height) && isScreenAutoRotate(getContext())) {
                    ((Activity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
        }
    };

    private PLOnPreparedListener mOnPreparedListener = new PLOnPreparedListener() {
        @Override
        public void onPrepared(int preparedTime) {
            Log.d(TAG, "On Prepared ! prepared time = " + preparedTime + " ms");
            mMediaPlayer.start();
            mIsStopped = false;
        }
    };

    /**
     * 判断是否开启了 “屏幕自动旋转”
     */
    public static boolean isScreenAutoRotate(Context context) {
        int gravity = 0;
        try {
            gravity = Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return gravity == 1;
    }

}
