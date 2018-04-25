package com.ldh.android.videodemo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.util.Formatter;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class VideoPreviewActivity extends AppCompatActivity {


    @BindView(R.id.vv_player)
    VideoView videoPlayer;
    @BindView(R.id.iv_playControl)
    ImageView ivPlayControl;
    @BindView(R.id.tv_currentTime)
    TextView tvCurrentTime;
    @BindView(R.id.sb_play)
    SeekBar sbPlay;
    @BindView(R.id.tv_totalTime)
    TextView tvTotalTime;
    @BindView(R.id.btn_rephotograph)
    TextView btnRephotograph;
    @BindView(R.id.btn_affirm)
    TextView btnAffirm;
    @BindView(R.id.ll_control)
    LinearLayout llControl;
    @BindView(R.id.content_main2)
    RelativeLayout contentMain2;
    @BindView(R.id.iv_preview)
    ImageView ivPreview;

    private String path;
    private File cacheFile;

    StringBuilder mFormatBuilder;
    Formatter mFormatter;
    public static final int UPDATE_TIME = 0x100;
    private static final String KEY_VIDEO_TOTAL_TIME = "videoTotalTime";
    private static final String KEY_VIDEO_URL = "video_url";

    private int currentPosition;

    private final UIHandler uiHandler = new UIHandler();

    /**
     * 手势识别
     */
    private GestureDetector detector;

    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int currentTime = videoPlayer.getCurrentPosition();
            sbPlay.setProgress(currentTime);
            updateTimeFormat(tvCurrentTime, currentTime);
            sendEmptyMessageDelayed(UPDATE_TIME, 500);
        }
    }

    public static void start(Activity activity, String path, String videoTotalTime) {
        Intent intent = new Intent(activity, VideoPreviewActivity.class);
        intent.putExtra(KEY_VIDEO_URL, path);
        intent.putExtra(KEY_VIDEO_TOTAL_TIME, videoTotalTime);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        ButterKnife.bind(this);
        initViewPlayer();
        initEvents();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (videoPlayer.canPause()) {
            setPauseStatus();
            videoPlayer.pause();
            currentPosition = videoPlayer.getCurrentPosition();
        }
        if (uiHandler.hasMessages(UPDATE_TIME)) {
            uiHandler.removeMessages(UPDATE_TIME);
        }
//        LocationManager.cancelRequest(locationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoPlayer.canSeekForward()) {
            videoPlayer.seekTo(currentPosition);
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setSystemUiHide();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setSystemUiHide();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setSystemUiVisible();
        }
    }

    private void setSystemUiHide() {
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void setSystemUiVisible() {
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        Log.d("VideoPreviewActivity", "onSonNewIntent");
//        sbPlay.setProgress(0);
//        super.onNewIntent(intent);
//    }

    @Override
    protected void onStop() {
        Log.d("VideoPreviewActivity", "onStop");
        super.onStop();
        if (videoPlayer.canPause()) {
            setPauseStatus();
            videoPlayer.pause();
        }
        if (uiHandler.hasMessages(UPDATE_TIME)) {
            uiHandler.removeMessages(UPDATE_TIME);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d("VideoPreviewActivity", "onDestroy");
        super.onDestroy();
        if (videoPlayer.canPause()) {
            videoPlayer.pause();
        }
        if (uiHandler.hasMessages(UPDATE_TIME)) {
            uiHandler.removeMessages(UPDATE_TIME);
        }
    }

    private void initViewPlayer() {
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        path = getIntent().getStringExtra(KEY_VIDEO_URL);

        cacheFile = new File(path);
        if (!cacheFile.exists()) {
            finish();
            return;
        }


        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        Bitmap bitmap = mmr.getFrameAtTime();//获取第一帧图片
        ivPreview.setImageBitmap(bitmap);
        mmr.release();//释放资源

        videoPlayer.setVideoPath(path);
        videoPlayer.requestFocus();
        tvTotalTime.setText(getIntent().getStringExtra(KEY_VIDEO_TOTAL_TIME));
        detector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                //手指点击的时候隐藏或展示视频控制台
//                if (llControl.getVisibility() == View.VISIBLE) {
//                    llControl.setVisibility(View.INVISIBLE);
//                } else {
//                    llControl.setVisibility(View.VISIBLE);
//                }
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
    }

    private void initEvents() {
        ivPlayControl.setOnClickListener(v -> {
            if (videoPlayer.isPlaying()) {
                setPauseStatus();
                videoPlayer.pause();
                uiHandler.removeMessages(UPDATE_TIME);
            } else {
                setPlayStatus();
                videoPlayer.start();
                uiHandler.sendEmptyMessage(UPDATE_TIME);
            }
        });
        sbPlay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoPlayer.seekTo(progress);
                    updateTimeFormat(tvCurrentTime, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                uiHandler.removeMessages(UPDATE_TIME);
                if (!videoPlayer.isPlaying()) {
                    //如果手指拖动进度条，开始播放
                    setPlayStatus();
                    videoPlayer.start();
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                uiHandler.sendEmptyMessage(UPDATE_TIME);
            }
        });

        videoPlayer.setOnCompletionListener(mp -> {
            ivPlayControl.setImageResource(R.mipmap.comm_sp_button_play);
            videoPlayer.seekTo(0);
            sbPlay.setProgress(0);
            updateTimeFormat(tvCurrentTime, 0);
            videoPlayer.pause();
            uiHandler.removeMessages(UPDATE_TIME);
        });
        btnRephotograph.setOnClickListener(v -> {
            deleteFile(cacheFile);
            gotoActivity(ShootVideoActivity.class);
        });
        btnAffirm.setOnClickListener(v -> {
            addVideo2MediaStore(cacheFile);
            gotoActivity(ScrollingActivity.class);

        });

        videoPlayer.setOnTouchListener((v, event) -> detector.onTouchEvent(event));
    }


    protected void gotoActivity(Class<? extends Activity> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
    }

    private void setPlayStatus() {
        ivPreview.setVisibility(View.GONE);
        ivPlayControl.setImageResource(R.mipmap.comm_sp_button_pause);
        sbPlay.setMax(videoPlayer.getDuration());
        updateTimeFormat(tvTotalTime, videoPlayer.getDuration());
    }


    private void setPauseStatus() {
        ivPlayControl.setImageResource(R.mipmap.comm_sp_button_play);
    }


    private void updateTimeFormat(TextView tv, int millisecond) {
        int totalSeconds = millisecond / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        String time;
        if (hours > 0) {
            time = mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            time = mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
        tv.setText(time);
    }

    private void deleteFile(File videoFile) {
        if (videoFile.exists()) {
            videoFile.delete();
        }
    }

    private void addVideo2MediaStore(File videoFile) {
        Flowable.create(new FlowableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(FlowableEmitter<Boolean> e) throws Exception {
                try {
                    // 将视频加入到媒体库
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.Video.Media.DATA, videoFile.getAbsolutePath());
                    contentValues.put(MediaStore.Video.Media.TITLE, videoFile.getName());
                    contentValues.put(MediaStore.Video.Media.DISPLAY_NAME, videoFile.getName());
                    Uri uri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
                    Log.d("VideoPreviewActivity", "addVideo2MediaStore() videoUri: " + uri.toString());
                    e.onNext(true);
                    e.onComplete();
                } catch (Exception exception) {
                    e.onNext(false);
                    e.onComplete();
                }
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Exception {
                        Log.d("VideoPreviewActivity", "addVideo2MediaStore() : " + (result ? "插入成功!" : "插入失败!"));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("VideoPreviewActivity", "addVideo2MediaStore() : " + "插入失败!");
                    }
                });
    }


//    @Override
//    public void onBackPressed() {
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        } else {
//            super.onBackPressed();
//        }
//    }


}
