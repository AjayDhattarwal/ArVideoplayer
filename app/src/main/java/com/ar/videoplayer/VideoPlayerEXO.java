package com.ar.videoplayer;

import static com.google.android.exoplayer2.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON;

import android.app.Dialog;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.RemoteAction;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.MediaCodecAudioRenderer;
import com.google.android.exoplayer2.mediacodec.DefaultMediaCodecAdapterFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecInfo;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.tabs.TabLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class VideoPlayerEXO extends AppCompatActivity {

    private static final int ACTION_MANAGE_WRITE_SETTINGS_CODE = 110;
    private MovieInfo movieInfo;
    Dialog dialog;
    private int device_width, brightness;
    boolean start = false,left ,right;
    private float baseX, baseY;
    private long diffX,diffY;
    public static final int MINIMUM_DISTANCE = 100;
    boolean success = false, singleTap = false, swipe_move = false;
    private TextView vol_text, brt_text;
    private ProgressBar vol_progress, brt_progress;
    ProgressBar loadingProgressBar;
    private LinearLayout vol_progress_container, vol_text_container, brt_progress_container, brt_text_container;
    private ImageView vol_icon,brt_icon;
    private float scaleFactor = 1.0f, maxScaleFactor = 3.0f;
    private boolean isZoomedIn = false;
    private AudioManager audioManager;
    private ContentResolver contentResolver;
    private Window window;
    private Uri videoUri;
    private SimpleExoPlayer player;
    private PlayerView playerView;
    private int currentVideoQualityIndex = 0;
    List<String> qualityOptions = new ArrayList<>();
    private ImageButton btnPlayPause;
    ImageButton forward_btn,backward_btn;
    private int forwardOffset = 0,backwardOffset = 0;
    TextView forwardsectext, backwardsectext;
    ImageButton btnFullScreen;
    private int currentMode = AspectRatioFrameLayout.RESIZE_MODE_FIT;
    private SeekBar seekBar;
    private boolean isSeeking = false,isFullScreen = false;
    private TextView tvTotalDuration, tvCurrDuration ;
    private long currentPosition,duration;
    private Handler handler;
    boolean isInPiPMode = false,isPlayingInPiP = false;
    private LinearLayout settingsButton;
    private ViewPager viewPager;
    private DefaultTrackSelector trackSelector;
    int currentAudioTrack = 1;
    LinearLayout customButtonLayout,customButtonLayout0,customButtonLayout2;
    private String[] audioTrackNames;
    private List<ImageView> imageViews = new ArrayList<>();
    private List<TextView> textViews = new ArrayList<>();
    private String[] subtitleTrackNames;
    private int currentSubtitleTrack = -1;
    private List<ImageView> subtitleImageViews = new ArrayList<>();
    private List<TextView> subtitleTextViews = new ArrayList<>();
    private ImageButton exitPlayerActivity;
    private String artist,date,videoFileName,storylines,releaseDate,thumbnailUrl,size,resolution,sub_title_pot;
    private List<String> cast;
    private String[] genre;
    private Float rating = 4.5f;
    private TextView video_title, title_portrait,sub_title_portrait,storyLine_portrait;
    private RatingBar ratingBar;
    private RelativeLayout wishlistToggleButton,share_video_Button;
    private Animation slideLeftAnimation,slideRightAnimation;
    private int flagOrientation;
    private static final float GESTURE_THRESHOLD = 50.0f;
    long totalDuration,tillWatched;
    private DatabaseHelper databaseHelper;
    private LottieAnimationView animationView;
    boolean isInWatchList = false;
    ImageButton btnQuality, btnAudio, btnSubtitle;
    TemplateView template;
    AdLoader adLoader;
    boolean isLongPressing = false;
    TextView two_x_speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player_exo);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        MobileAds.initialize(this);
        adLoader = new AdLoader.Builder(this, "ca-app-pub-9633578355774891/8489722313")
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        NativeTemplateStyle styles = new
                                NativeTemplateStyle.Builder().build();
                        template = findViewById(R.id.video_activity_native_ads);
                        template.setVisibility(View.VISIBLE);
                        template.setStyles(styles);
                        template.setNativeAd(nativeAd);
                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
        AdView mAdView = findViewById(R.id.banner_video_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Intent intent = getIntent();
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW)) {
            videoUri = intent.getData();
        }
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_VIEW.equals(action) && type != null) {
            if (type.startsWith("video/")) {
                String filePath = getFilePathFromContentUri(intent.getData());
                if (filePath != null) {
                    // Now you have the original file path, you can play the video from there
                    videoUri = Uri.parse(filePath);
                } else {
                    Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
                    videoUri = intent.getData();
                }
                String s= String.valueOf(videoUri);
                Log.d("videoPathWhileOpenWith", s );
            } else {
                // Unsupported file type
                Toast.makeText(this, "Unsupported file type", Toast.LENGTH_SHORT).show();
            }
        }

        WindowManager.LayoutParams params = getWindow().getAttributes();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        getWindow().setAttributes(params);

        initView();
        if( getIntent().getSerializableExtra("movie_info") != null){
            movieInfo = (MovieInfo) getIntent().getSerializableExtra("movie_info");
            videoUri = Uri.parse(movieInfo.getVideoPath());
            if(movieInfo.getVideoPath().startsWith("http")){
                videoFileName = movieInfo.getTitle();
            }
            storylines = movieInfo.getStoryLines();
            artist = movieInfo.getArtists();
            genre = movieInfo.getGenre();
            if(videoFileName == null){
                videoFileName = movieInfo.getDisplayName();
            }
            sub_title_pot = movieInfo.getSubText();
            if(movieInfo.getRating() != null){
                rating = movieInfo.getRating();
            }
            releaseDate = movieInfo.getReleaseDate();
            cast = movieInfo.getCast();
            thumbnailUrl = movieInfo.getImageUrl();
            size = movieInfo.getSize();
            resolution = movieInfo.getResolution();
        }
        setMethods();


        trackSelector = new DefaultTrackSelector(this);

        if (videoUri != null) {
            initializePlayer(videoUri);
        }
        TableClass.WatchlistItem watchlistItemExists = databaseHelper.hisWatchDao().getWatchListItemByPath(String.valueOf(videoUri));
        if(watchlistItemExists != null){
            isInWatchList = true;
            animationView.setMinAndMaxProgress(0.5f,1.0f);
        }
        else{
            isInWatchList = false;
        }


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        device_width = displayMetrics.widthPixels;


        playerView.setOnTouchListener(new GesturesOfVideos(this, new GesturesOfVideos.ScaleListener() {
            @Override
            public void onScale(ScaleGestureDetector detector) {
                scaleFactor *= detector.getScaleFactor();
                scaleFactor = Math.max(1.0f, Math.min(scaleFactor, maxScaleFactor));

        //                // Apply scaling only to the PlayerView
        //                playerView.getVideoSurfaceView().setScaleX(scaleFactor);
        //                playerView.getVideoSurfaceView().setScaleY(scaleFactor);

                if (scaleFactor > 1.0f && !isZoomedIn) {
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                isZoomedIn = true;
                } else if (scaleFactor <= 1.0f && isZoomedIn) {
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                isZoomedIn = false;
                }
            }

        }){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int pointerCount = motionEvent.getPointerCount();
                if(pointerCount == 2){
                    GesturesOfVideos.scaleGestureDetector.onTouchEvent(motionEvent);
                }
                else {
                    if (isFullScreen) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                start = true;
                                if (motionEvent.getX() < (device_width / 2)) {
                                    left = true;
                                    right = false;
                                } else {
                                    right = true;
                                    left = false;
                                }
                                baseX = motionEvent.getX();
                                baseY = motionEvent.getY();
                                break;

                            case MotionEvent.ACTION_MOVE:
                                swipe_move = true;
                                diffX = (long) Math.ceil(motionEvent.getX() - baseX);
                                diffY = (long) Math.ceil(motionEvent.getY() - baseY);
                                double brightnessSpeed = 0.01;
                                playerView.showController();
                                playerView.findViewById(R.id.controlsLayout).setVisibility(View.GONE);
                                playerView.findViewById(R.id.gesture_inclued_layout).setVisibility(View.VISIBLE);
                                singleTap = true;

                                if (Math.abs(diffY) > MINIMUM_DISTANCE) {
                                    start = true;
                                    if (Math.abs(diffY) > Math.abs(diffX)) {
                                        boolean value;
                                        value = Settings.System.canWrite(getApplicationContext());
                                        if (value) {
                                            if (left) {
                                                brt_progress_container.setVisibility(View.VISIBLE);
                                                brt_text_container.setVisibility(View.VISIBLE);
                                                contentResolver = getContentResolver();
                                                window = getWindow();
                                                try {
                                                    brightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS);

                                                } catch (Settings.SettingNotFoundException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                int new_brightness = (brightness - (int) (brightnessSpeed + diffY * brightnessSpeed));
                                                if (new_brightness < 1) {
                                                    new_brightness = 1;
                                                } else if (new_brightness > 255) {
                                                    new_brightness = 255;
                                                }
                                                double brt_percentage = Math.ceil(((double) new_brightness / (double) 255) * 100);

                                                brt_progress.setProgress((int) brt_percentage);
                                                if (brt_percentage < 30) {
                                                    brt_icon.setImageResource(R.drawable.ic_brightness_low);
                                                } else {
                                                    brt_icon.setImageResource(R.drawable.ic_brightness);
                                                }
                                                brt_text.setText("" + (int) brt_percentage + "%");
                                                Settings.System.putInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, (new_brightness));
                                                WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                                                layoutParams.screenBrightness = (float) new_brightness / 255.0f;
                                                getWindow().setAttributes(layoutParams);
                                            } else if (right) {
                                                vol_text_container.setVisibility(View.VISIBLE);
                                                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

                                                // Calculate the change in Y position
                                                float deltaY = motionEvent.getY() - baseY;
                                                // Check if the change in Y position exceeds the threshold
                                                if (Math.abs(deltaY) > GESTURE_THRESHOLD) {
                                                    if (deltaY > 0) {
                                                        // Swipe down: Decrease volume
                                                        adjustVolume(-1, maxVolume);
                                                    } else {
                                                        // Swipe up: Increase volume
                                                        adjustVolume(1, maxVolume);
                                                    }
                                                    // Reset the starting position for the next calculation
                                                    baseY = motionEvent.getY();
                                                }

                                            }
                                            success = true;

                                        } else {
                                            Toast.makeText(getApplicationContext(), "Allow write settings", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                            intent.setData(Uri.parse("package:" + getPackageName()));
                                            startActivityForResult(intent, ACTION_MANAGE_WRITE_SETTINGS_CODE);
                                        }
                                    }
                                }

                                break;
                            case MotionEvent.ACTION_UP:
                                swipe_move = false;
                                start = false;
                                vol_progress_container.setVisibility(View.GONE);
                                vol_text_container.setVisibility(View.GONE);
                                brt_progress_container.setVisibility(View.GONE);
                                brt_text_container.setVisibility(View.GONE);

                                if (isLongPressing) {
                                    setPlaybackSpeed(1.0f); // Reset to normal speed
                                    isLongPressing = false;

                                    two_x_speed.setVisibility(View.INVISIBLE);
                                }

                                break;
                        }
                    }
                }
                return super.onTouch(view, motionEvent);
            }

            @Override
            public void onDoubleTouch(MotionEvent motionEvent) {
                playerView.findViewById(R.id.controlsLayout).setVisibility(View.INVISIBLE);
                singleTap = true;
                super.onDoubleTouch(motionEvent);
                    float screenWidth = getResources().getDisplayMetrics().widthPixels;
                    if (motionEvent.getX() < screenWidth / 2) {
                        seekBackward();
                    } else {
                        seekForward();
                    }
            }

            @Override
            public void onSingleTouch() {
                super.onSingleTouch();
                Log.d("doubleTap","singletaped");
                if(singleTap){
                    playerView.findViewById(R.id.controlsLayout).setVisibility(View.VISIBLE);
                    playerView.showController();
                    singleTap = false;
                }else{
                    playerView.hideController();
                    singleTap = true;
                }
            }

            @Override
            public void onLongPressTouch() {
                super.onLongPressTouch();
                if(isFullScreen){
                    setPlaybackSpeed(2.0f);
                    isLongPressing = true;
                    two_x_speed.setVisibility(View.VISIBLE);
                }
            }
        });

        new ParseM3U8Task().execute(videoUri);


        ////////controls

        wishlistToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInWatchList) {
                    animationView.setMinAndMaxProgress(0.5f,1.0f);
                    animationView.playAnimation();
                    databaseHelper.hisWatchDao().deleteWatchlistItem(databaseHelper.hisWatchDao().getWatchListItemByPath(String.valueOf(videoUri)));
                    isInWatchList = false;
                } else {
                    animationView.setMinAndMaxProgress(0.0f,0.5f);
                    animationView.playAnimation();
                    isInWatchList = true;
                    databaseHelper.hisWatchDao().insertWatchlistItem(new TableClass.WatchlistItem(String.valueOf(videoUri),videoFileName,formatTime(totalDuration),currentPosition,size,resolution,sub_title_pot,rating));
                }
            }
        });
        share_video_Button.setOnClickListener(view -> {
            if(movieInfo.getVideoPath().startsWith("http")){
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,movieInfo.getVideoPath());
                startActivity(Intent.createChooser(shareIntent,"Share Video via"));
            }else{
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("video/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM,videoUri);
                startActivity(Intent.createChooser(shareIntent,"Share Video via"));

            }
        });


        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });


        // Set the click listener for the forward button
        forward_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forward_btn.startAnimation(slideRightAnimation);
                if (player != null && player.getCurrentPosition() >= player.getDuration()) {
                    // If the video has completed, prevent seeking beyond the end of the video
                    player.seekTo(player.getDuration());
                }
                else{
                    currentPosition += 10000;
                    forwardOffset+=10;
                    if(currentPosition > player.getDuration()){
                        player.seekTo(player.getDuration());
                        currentPosition = player.getDuration();
                    }
                    else {
                        player.seekTo(currentPosition);

                    }
                }
                forwardsectext.setText(String.valueOf(forwardOffset));
                forwardsectext.setVisibility(View.VISIBLE);
                backwardsectext.setVisibility(View.INVISIBLE);

             }

        });


        backward_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backward_btn.startAnimation(slideLeftAnimation);
                if(player != null && player.getDuration() <= 10000){
                    player.seekTo(0);
                }
                else{
                    currentPosition -= 10000;
                    backwardOffset +=10;
                    if(currentPosition <= 0){
                        player.seekTo(0);
                        currentPosition = 0;
                    }
                    else{
                        player.seekTo(currentPosition);
                    }
                }
                backwardsectext.setText(String.valueOf(backwardOffset));
                backwardsectext.setVisibility(View.VISIBLE);
                forwardsectext.setVisibility(View.INVISIBLE);
            }
        });

        playerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                if (visibility == View.GONE || visibility == View.INVISIBLE) {
                    backwardsectext.setText("10");
                    backwardsectext.setVisibility(View.INVISIBLE);
                    backwardOffset = 0;
                    forwardsectext.setText("10");
                    forwardsectext.setVisibility(View.INVISIBLE);
                    forwardOffset = 0;
                }
            }
        });


        playerView.findViewById(R.id.settings_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(2);
            }
        });



        btnFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toggleFullScreen();
            }
        });


        ImageButton btnEnterPiP = playerView.findViewById(R.id.btnpip);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.reward.videoplayer.TOGGLE_PLAY_PAUSEs");
        registerReceiver(new Receiver(), intentFilter);


        btnEnterPiP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    enterpipmode();
                }

                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    enterFullScreenMode();
                }

            }
        });



        // Set the seek bar change listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the video playback position based on SeekBar progress
                if (player != null && fromUser) {
                    long newPosition = duration * progress / 100;

                    player.seekTo(newPosition);
                    tvCurrDuration.setText(formatTime(newPosition));

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Pause video playback while seeking
                if (player != null) {
                    isSeeking = true;

                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Resume video playback after seeking
                if (player != null) {
                    isSeeking = false;
                }
            }
        });


        handler = new Handler();

        // Start updating the seek bar and current time
        updateSeekBar();
        // Register the player event listener
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY) {
                    // Video is ready to play, update the total duration and other UI elements
                    totalDuration = player.getDuration();
                    tvTotalDuration.setText(formatTime(totalDuration));

                }
            }
            // Other overridden methods
        });


         btnQuality.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 showPopup(0);
             }
         });
        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(1);
            }
        });
        btnSubtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(2);
            }
        });

        exitPlayerActivity = playerView.findViewById(R.id.exit_player_activity);
        //exit button from activity
        exitPlayerActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFullScreen){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    exitFullScreenMode();
                    undefinedOrientation();
                }else{
                    onBackPressed();
                }
            }
        });


    }

    private String getFilePathFromContentUri(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(contentUri, projection, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }

        return null;
    }

    private void setMethods() {
        if(videoFileName == null){
            title_portrait.setText("Unknown");
            video_title.setText("Unknown");
        }else{
            title_portrait.setText(videoFileName);
            video_title.setText(videoFileName);
        }
        storyLine_portrait.setText(storylines);
        sub_title_portrait.setText(sub_title_pot);
        ratingBar.setRating(rating);
    }




    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();


        if(isFullScreen){
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

                // Handle volume key event
                if (action == KeyEvent.ACTION_DOWN) {
                    // Adjust the volume using AudioManager
                    int direction = (keyCode == KeyEvent.KEYCODE_VOLUME_UP) ? AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER;
                    Log.d("direction", "direction:  "+direction);
                    int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);;
                    adjustVolume(direction, maxVolume);
                    playerView.showController();
                    playerView.findViewById(R.id.controlsLayout).setVisibility(View.GONE);
                    playerView.findViewById(R.id.gesture_inclued_layout).setVisibility(View.VISIBLE);
                    singleTap = true;
                }
                // Return true to consume the event
                return true;
            }
        }

        // Let the system handle other key events
        return super.dispatchKeyEvent(event);
    }


    private void adjustVolume(int direction, int maxVolume) {
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int newVolume = currentVolume + direction;

        // Ensure the new volume is within bounds (0 to maxVolume)
        newVolume = Math.max(0, Math.min(maxVolume, newVolume));

        // Calculate the volume percentage
        double volumePercentage = (newVolume / (double) maxVolume) * 100;

        // Set the new volume
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0);

        // Update the TextView with the volume percentage
        vol_text.setText(String.format("%.0f%%", volumePercentage));

        // Update your UI based on the volume percentage if needed
        if (volumePercentage < 1) {
            vol_icon.setImageResource(R.drawable.ic_volume_off);
        } else {
            vol_icon.setImageResource(R.drawable.ic_volume);
        }
        vol_text.setVisibility(View.VISIBLE);
        vol_text_container.setVisibility(View.VISIBLE);
        vol_progress_container.setVisibility(View.VISIBLE);
        vol_progress.setProgress((int) volumePercentage);
    }

    private void initView() {
        playerView = findViewById(R.id.playerView);
        loadingProgressBar = playerView.findViewById(R.id.loadingProgressBar);
        video_title = playerView.findViewById(R.id.video_Title);
        btnPlayPause = playerView.findViewById(R.id.btnPlayPause);
        backwardsectext  = playerView.findViewById(R.id.backwardsectext);
        forwardsectext  = playerView.findViewById(R.id.forwardsectext);
        forward_btn = playerView.findViewById(R.id.forwardsecicon);
        backward_btn = playerView.findViewById(R.id.backwardsecicon);
        slideRightAnimation = AnimationUtils.loadAnimation(this, R.anim.forward_anim);
        slideLeftAnimation = AnimationUtils.loadAnimation(this, R.anim.backward_anim);
        settingsButton = playerView.findViewById(R.id.settings_button);
        btnFullScreen = playerView.findViewById(R.id.btnFullScreen);
        tvTotalDuration = playerView.findViewById(R.id.tvTotalTime);
        tvCurrDuration = playerView.findViewById(R.id.tvcurrentTime);
        seekBar = playerView.findViewById(R.id.seekBar);
        btnQuality = playerView.findViewById(R.id.btnQuality);
        btnAudio = playerView.findViewById(R.id.btnAudioTrack);
        btnSubtitle = playerView.findViewById(R.id.btnSubtitle);
        vol_icon = playerView.findViewById(R.id.vol_icon);
        brt_icon = playerView.findViewById(R.id.brt_icon);
        vol_text = playerView.findViewById(R.id.vol_text);
        brt_text = playerView.findViewById(R.id.brt_text);
        vol_text_container = playerView.findViewById(R.id.vol_text_container);
        brt_text_container = playerView.findViewById(R.id.brt_text_container);
        vol_progress_container = playerView.findViewById(R.id.volume_progress_container);
        vol_progress = playerView.findViewById(R.id.vol_progress);
        brt_progress = playerView.findViewById(R.id.brt_progress);
        brt_progress_container = playerView.findViewById(R.id.bright_progress_container);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        animationView = findViewById(R.id.lottieAnimationView);
        wishlistToggleButton = findViewById(R.id.wishlistToggleButton);
        share_video_Button = findViewById(R.id.share_video_Button);
        title_portrait = findViewById(R.id.title_portrait);
        sub_title_portrait = findViewById(R.id.sub_title_portrait);
        ratingBar = findViewById(R.id.ratingBar);
        storyLine_portrait = findViewById(R.id.storyLine_portrait);
        two_x_speed = findViewById(R.id.two_x_speed);

    }

    private void seekForward() {
        currentPosition += 10000;
        forwardOffset += 10;
        if (currentPosition > player.getDuration()) {
            player.seekTo(player.getDuration());
            currentPosition = player.getDuration();
        } else {
            player.seekTo(currentPosition);
        }
        forwardsectext.setText(String.valueOf(forwardOffset));
        forwardsectext.setVisibility(View.VISIBLE);
        backwardsectext.setVisibility(View.INVISIBLE);
    }

    private void seekBackward() {
        currentPosition -= 10000;
        backwardOffset += 10;
        if (currentPosition < 0) {
            player.seekTo(0);
            currentPosition = 0;
        } else {
            player.seekTo(currentPosition);
        }
        backwardsectext.setText(String.valueOf(backwardOffset));
        backwardsectext.setVisibility(View.VISIBLE);
        forwardsectext.setVisibility(View.INVISIBLE);
    }





/////////////////////////////////////////////////////////////----------------------------------------------------------
    private void setPlaybackSpeed(float speed) {
        PlaybackParameters playbackParameters = new PlaybackParameters(speed);
        player.setPlaybackParameters(playbackParameters);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        TableClass.HistoryItem existingItem = databaseHelper.hisWatchDao().getHistoryItemByPath(String.valueOf(videoUri));
        if (existingItem != null) {
            databaseHelper.hisWatchDao().deleteHistoryItem(databaseHelper.hisWatchDao().getHistoryItemByPath(String.valueOf(videoUri)));
        }
        databaseHelper.hisWatchDao().insertHistoryItem(new TableClass.HistoryItem(String.valueOf(videoUri),videoFileName,formatTime(totalDuration),currentPosition,size,resolution,sub_title_pot,rating));
        videoUri = null;
        if (player != null) {
            player.release();
            player = null;
        }

        if( intent.getSerializableExtra("movie_info") != null){
            movieInfo = (MovieInfo) intent.getSerializableExtra("movie_info");
            videoUri = Uri.parse(movieInfo.getVideoPath());
            if(movieInfo.getVideoPath().startsWith("http")){
                videoFileName = movieInfo.getTitle();
            }
            storylines = movieInfo.getStoryLines();
            artist = movieInfo.getArtists();
            genre = movieInfo.getGenre();
            if(videoFileName == null){
                videoFileName = movieInfo.getDisplayName();
            }
            sub_title_pot = movieInfo.getSubText();
            if(movieInfo.getRating() != null){
                rating = movieInfo.getRating();
            }
            releaseDate = movieInfo.getReleaseDate();
            cast = movieInfo.getCast();
            thumbnailUrl = movieInfo.getImageUrl();
            size = movieInfo.getSize();
            resolution = movieInfo.getResolution();
        }
        setMethods();

        if (videoUri != null) {
            initializePlayer(videoUri);
        }

    }

    private void initializePlayer(Uri videoUri) {
        if (player == null) {
            tillWatched = 0;
            databaseHelper = DatabaseHelper.getDB(this);
            TableClass.HistoryItem existingItem = databaseHelper.hisWatchDao().getHistoryItemByPath(String.valueOf(videoUri));
            if (existingItem != null) {
                tillWatched = existingItem.getVideoWatchedTime();
                databaseHelper.hisWatchDao().deleteHistoryItem(databaseHelper.hisWatchDao().getHistoryItemByPath(String.valueOf(videoUri)));
            }
            DefaultLoadControl loadControl = new DefaultLoadControl();

            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this)
                    .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);
            



            player = new SimpleExoPlayer.Builder(this, renderersFactory)
                    .setTrackSelector(trackSelector)
                    .setLoadControl(loadControl)
                    .build();

            playerView.setPlayer(player);
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                    this,
                    Util.getUserAgent(this, getApplicationInfo().name)
            );

            playerView.setResizeMode(currentMode);
            player.seekTo(C.TIME_UNSET);

            player.setHandleAudioBecomingNoisy(true);

            playerView.setKeepScreenOn(true);


            // Hide controls
            hideControls();

            MediaItem mediaItem = MediaItem.fromUri(videoUri);
            HlsMediaSource hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem);

            // Set the MediaItem to the player
            player.setMediaItem(mediaItem);

            // Set up a listener to show/hide the loading ProgressBar
            player.addListener(new Player.Listener() {
                @Override
                public void onLoadingChanged(boolean isLoading) {
                    loadingProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                }
            });


            playerError();

            flagOrientation = getVideoOrientation(this, videoUri);
            // Prepare the player
            player.prepare();
            player.seekTo(tillWatched);

            // Start video playback
            player.setPlayWhenReady(true);
            if(size==null || videoFileName ==null || resolution == null){
                Log.e("nullvalue", "nullvalue: "+ (size +videoFileName + resolution +videoFileName));
                getMediaData(videoUri);
            }
        } else {
            // Release the existing player to play a new video

            player.release();
            player = null;
            initializePlayer(videoUri);
        }
    }

    private void updatePlayPauseButtonIcon(boolean isPlaying) {
        if (isPlaying) {
            // Update your play/pause button to show the pause icon
            btnPlayPause.setImageResource(R.drawable.ic_pause);
        } else {
            // Update your play/pause button to show the play icon
            btnPlayPause.setImageResource(R.drawable.ic_play);
        }
    }

    private void playerError() {
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                Toast.makeText(getApplicationContext(), "Source down or URL error", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    // Media playback ended
                } else if (playbackState == Player.STATE_READY && playWhenReady) {
                    // Media playback started or resumed
                    updatePlayPauseButtonIcon(true); // Update UI to show pause icon
                } else if (playbackState == Player.STATE_READY) {
                    // Media playback paused
                    updatePlayPauseButtonIcon(false); // Update UI to show play icon
                } else if (playbackState == Player.STATE_BUFFERING) {
                    // Media buffering
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        if(isFullScreen){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            exitFullScreenMode();
            undefinedOrientation();
        }else{
            super.onBackPressed();
        }

    }

    private class RetrieveMetadataTask extends AsyncTask<Void, Void, Void> {
    private Uri videoUri;

    public RetrieveMetadataTask(Uri videoUri) {
        this.videoUri = videoUri;
    }

        @Override
        protected Void doInBackground(Void... voids) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();

            try {
                String videoUrl = videoUri.toString();
                if(videoFileName == null){
                    videoFileName = getFileNameFromUri(videoUri);
                }

                if (videoUrl.startsWith("http") || videoUrl.startsWith("https")) {
                    // Online video, perform network operation
                    Request request = new Request.Builder().url(videoUrl).build();
                    OkHttpClient client = new OkHttpClient();

                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        File videoFile = new File(getFilesDir(), videoFileName);
                        FileOutputStream fos = new FileOutputStream(videoFile);
                        fos.write(response.body().bytes());
                        fos.close();
                        String videoFilePath = videoFile.getAbsolutePath();

                        retriever.setDataSource(videoFilePath);
                        if(resolution == null){
                            resolution = (retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                                    +"x"+
                                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                        }
                        if(size == null){
                            size = formatFileSize(videoFile.length());
                        }

                        // Rest of your existing metadata extraction code
                    } else {
                        // Handle unsuccessful network response
                    }
                } else {
                    retriever.setDataSource(VideoPlayerEXO.this, videoUri);

                    // Rest of your existing metadata extraction code
                }

                retriever.release();

                // Update UI elements on the UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        video_title.setText(videoFileName);
                        title_portrait.setText(videoFileName);
                    }
                });
            } catch (Exception e) {
                // Handle exceptions here
                e.printStackTrace(); // Print the stack trace for debugging

                // Show a Toast message on the UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(VideoPlayerEXO.this, "Error during metadata retrieval", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return null;
        }

    }

    // Call this method to start the background task
    private void getMediaData(Uri videoUri) {
        new RetrieveMetadataTask(videoUri).execute();
    }

    public static String formatFileSize(long fileSizeInBytes) {
        if (fileSizeInBytes <= 0) {
            return "0 B"; // If the file size is zero or negative, return "0 B"
        }

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(fileSizeInBytes) / Math.log10(1024));

        return String.format("%.2f %s", fileSizeInBytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    public static String getFileNameFromUri(Uri uri) {
        File file = new File(uri.getPath());
        return file.getName();
    }

    private void togglePlayPause() {
        if (player != null) {
            if (player.isPlaying()) {
                // If video is playing, pause it
                player.pause();
                btnPlayPause.setImageResource(R.drawable.ic_play); // Change button icon to play
            } else {
                // If video is paused or stopped, start playback
                player.play();
                btnPlayPause.setImageResource(R.drawable.ic_pause); // Change button icon to pause
            }
        }


    }
    private void toggleFullScreen() {
        if(isFullScreen)
        {
            if(flagOrientation == 0){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            exitFullScreenMode();
            undefinedOrientation();
        }
        else{
            if(isAutoRotateEnabled(this)){
                undefinedOrientation();
            }

            if(flagOrientation == 0){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            }
            enterFullScreenMode();


        }


    }

    private void undefinedOrientation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

                }
            },2000);
        }
    }
    public static boolean isAutoRotateEnabled(Context context) {
        try {
            // Get the current system setting for auto-rotate
            int accelerometerRotation = Settings.System.getInt(
                    context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);

            // Return true if auto-rotate is enabled, false if it's disabled
            return accelerometerRotation == 1;
        } catch (Settings.SettingNotFoundException e) {
            // Handle exceptions here
            e.printStackTrace();
            return false; // Return false by default if an exception occurs
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientation = newConfig.orientation;


        if (orientation == Configuration.ORIENTATION_PORTRAIT && flagOrientation == 0) {

            exitFullScreenMode();

        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            enterFullScreenMode();
        }

    }
    private void enterFullScreenMode() {
        // Hide the status bar and navigation bar for full-screen mode
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );



        // Update the layout parameters of the RelativeLayout for full-screen mode
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        playerView.setLayoutParams(layoutParams);
        // Update the full-screen button icon
        btnFullScreen.setImageResource(R.drawable.ic_exit_full_screen);

        isFullScreen = true;
        hideControls();
    }


    private void exitFullScreenMode() {
        // Show the status bar and navigation bar for normal mode
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);


        // Update the full-screen button icon
        btnFullScreen.setImageResource(R.drawable.ic_full_screen);
        // Update the layout parameters of the RelativeLayout for normal mode
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
        layoutParams.height = getResources().getDimensionPixelSize(R.dimen.player_height);
        playerView.setLayoutParams(layoutParams);
        isFullScreen = false;
        hideControls();
    }
    public void hideControls(){
        if(isFullScreen){
            playerView.findViewById(R.id.portraitcontrol).setVisibility(View.GONE);
            playerView.findViewById(R.id.landscapcontrol).setVisibility(View.VISIBLE);
            playerView.findViewById(R.id.video_Title).setVisibility(View.VISIBLE);
        }
        else{
            playerView.findViewById(R.id.landscapcontrol).setVisibility(View.GONE);
            playerView.findViewById(R.id.portraitcontrol).setVisibility(View.VISIBLE);
            playerView.findViewById(R.id.video_Title).setVisibility(View.INVISIBLE);
        }
    }


    // Helper method to format time in hh:mm:ss format
    private String formatTime(long millis) {
        if (millis < 0) {
            millis = 0; // Handle negative durations, set to zero or whatever default value you prefer
        }
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        String timeString = String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
        return timeString;
    }

    // Function to update the seek bar and current time
    private void updateSeekBar() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isSeeking && player != null) {
                    currentPosition = player.getCurrentPosition();
                    duration = player.getDuration();

                    // Update the seek bar progress
                    int progress = (int) (100 * currentPosition / duration);
                    seekBar.setProgress(progress);

                    // Update the current time text view
                    tvCurrDuration.setText(formatTime(currentPosition));

                }
                handler.postDelayed(this, 1000);
            }
        }, 1000); // Start the task after a short delay (e.g., 1000 ms)
    }


    //pip mode

    @Override
    protected void onUserLeaveHint() {

        if(isFullScreen)
            enterpipmode();
        super.onUserLeaveHint();
    }

    public void enterpipmode() {
        PictureInPictureParams pipp = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pipp = new PictureInPictureParams.Builder()
                    .setSeamlessResizeEnabled(true)
                    .setAutoEnterEnabled(true)
                    .setActions(Collections.singletonList(createPlayPauseAction()))
                    .build();
        }
        updatePictureInPictureActions();
        enterPictureInPictureMode(pipp);
        isInPiPMode = true;
        isPlayingInPiP = player.isPlaying(); // Set the variable based on the current video playback state

    }

    private void updatePictureInPictureActions() {
        List<RemoteAction> actions = new ArrayList<>();
        actions.add(createPlayPauseAction());
        // Add more actions if needed

        PictureInPictureParams.Builder pipBuilder = new PictureInPictureParams.Builder();
        pipBuilder.setActions(actions);

        setPictureInPictureParams(pipBuilder.build());
    }

    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.equals(intent.getAction(), "com.reward.videoplayer.TOGGLE_PLAY_PAUSEs")) {
                togglePlayPausepip();
            }
        }
    }

    private RemoteAction createPlayPauseAction() {
        Intent intent = new Intent("com.reward.videoplayer.TOGGLE_PLAY_PAUSEs");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                1,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        int iconResourceId = isPlayingInPiP ? R.drawable.ic_pip_pause : R.drawable.ic_pip_play;
        String title = isPlayingInPiP ? "Pause" : "Play";

        return new RemoteAction(
                Icon.createWithResource(this, iconResourceId),
                title,
                "Play/Pause",
                pendingIntent
        );
    }




    private void togglePlayPausepip() {
        if (player != null) {
            if (player.isPlaying()) {
                // If video is playing, pause it
                player.pause();
                isPlayingInPiP = false;
            } else {
                // If video is paused or stopped, start playback
                player.play();
                isPlayingInPiP = true;
            }

            // Update the play/pause action for PiP mode
            updatePictureInPictureActions();
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode,Configuration configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode,configuration);
        if(isInPictureInPictureMode){
            playerView.findViewById(R.id.controlsLayout).setVisibility(View.GONE);
        }
        else {
            playerView.findViewById(R.id.controlsLayout).setVisibility(View.VISIBLE);
            player.pause();
            isPlayingInPiP = false;
        }
    }




    private void showPopup(int settab) {

        if(player.isPlaying()){
            togglePlayPause();
        }
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_settings);

        viewPager = dialog.findViewById(R.id.view_pager_settings);
        TabLayout tabLayout = dialog.findViewById(R.id.tab_layout_settings);
        ImageButton Dialog_close_btn;
        Dialog_close_btn = dialog.findViewById(R.id.Dialog_close_btn);
        // Set up ViewPager with a custom adapter
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(2);


        // Attach the TabLayout to the ViewPager AFTER setting the adapter
        tabLayout.setupWithViewPager(viewPager);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                if (isFullScreen) {
                    viewPager.setCurrentItem(settab);
                    Dialog_close_btn.setVisibility(View.VISIBLE);
                }
                View layoutView3 = viewPager.getChildAt(2);
                View layoutView1 = viewPager.getChildAt(0);
                View layoutView2 = viewPager.getChildAt(1);

                if(layoutView1 != null) {
                    setVideoQualityCustomButtonLayout(dialog.getContext(), R.layout.layout_quality, layoutView1);
                }


                if (layoutView2 != null) {
                    set_Audio_CustomButtonLayout(layoutView2.getContext(), R.layout.layout_audio_language, layoutView2);
                }

                if (layoutView3 != null) {
                    setSubtitleCustomButtonLayout(layoutView3.getContext(), R.layout.layout_subtitle, layoutView3);
                }


                Dialog_close_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

            }
        });
        // Set up dismiss listener
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                togglePlayPause();
                Dialog_close_btn.setVisibility(View.GONE);

            }
        });
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if(isFullScreen){
            // Add margins to the existing tab views
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null) {
                    View tabView = tab.view;

                    // Apply margins to the tab view
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
                    params.setMargins(16, 0, 16, 0); // Set your desired margins here

                    // Update the tab view's layout parameters
                    tabView.setLayoutParams(params);
                }
            }
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        else{
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }



        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
        imageViews.clear();
        textViews.clear();
    }





    private int findAudioRendererIndex(MappingTrackSelector.MappedTrackInfo mappedTrackInfo) {
        for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
            int trackType = mappedTrackInfo.getRendererType(i);
            if (trackType == C.TRACK_TYPE_AUDIO) {
                return i; // Return the index of the audio renderer
            }
        }
        return -1; // Return a value indicating that no audio renderer was found
    }
    private String[] getAudioTrackNames() {
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();

        if (mappedTrackInfo != null) {
            int rendererIndexAudio = findAudioRendererIndex(mappedTrackInfo);
            TrackGroupArray audioTracks = mappedTrackInfo.getTrackGroups(rendererIndexAudio);

            if (audioTracks != null && audioTracks.length > 0) {
                String[] audioTrackNames = new String[audioTracks.length];

                for (int i = 0; i < audioTracks.length; i++) {
                    Format format = audioTracks.get(i).getFormat(0); // Assuming single track
                    audioTrackNames[i] = format.language; // Use appropriate field based on your video format
                }

                return audioTrackNames;
            }
        }

        return new String[0]; // Return an empty array if no audio tracks are available
    }
    public void set_Audio_CustomButtonLayout(Context context,int layoutId,View layoutView ) {

        if (layoutId == R.layout.layout_audio_language) {
            LinearLayout buttonContainer = layoutView.findViewById(R.id.button_container_audio);

            audioTrackNames = getAudioTrackNames();

            for (int i = 0; i < audioTrackNames.length; i++) {
                // Create a custom button layout
                customButtonLayout = new LinearLayout(context);
                customButtonLayout.setOrientation(LinearLayout.HORIZONTAL);
                customButtonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));

                // Create and customize the ImageView (checkmark icon)
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.baseline_check_24);
                imageView.setVisibility(i == currentAudioTrack - 1 ? View.VISIBLE : View.INVISIBLE);
                customButtonLayout.addView(imageView);

                // Add the ImageView to the list
                imageViews.add(imageView);


                // Create and customize the TextView (audio track name)
                TextView textView = new TextView(context);
                textView.setId(View.generateViewId());
                textView.setText(new Locale(audioTrackNames[i]).getDisplayLanguage());
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                customButtonLayout.addView(textView);

                // Add the TextView to the list
                textViews.add(textView);




                // Set click listener for the custom button
                final int trackIndex = i + 1;
                customButtonLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setAudioTrack(trackIndex);

                    }
                });

                // Set initial text color based on selection
                if (i == currentAudioTrack - 1) {
                    textView.setTextColor(Color.WHITE); // Selected language
                } else {
                    textView.setTextColor(Color.GRAY); // Other languages
                }

                // Add the custom button layout to the button container
                buttonContainer.addView(customButtonLayout);

                // Add layout margin to each button
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) customButtonLayout.getLayoutParams();
                layoutParams.setMargins(0, 0, 0, 20); // Set margins (left, top, right, bottom)
                customButtonLayout.setLayoutParams(layoutParams);
            }


        }
    }

    public void setAudioTrack(int track) {


        System.out.println("setAudioTrack: " + track);

        // Get the currently available track information
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = Assertions.checkNotNull(trackSelector.getCurrentMappedTrackInfo());

        // Get the current parameters of the track selector
        DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();

        // Create a builder to modify the current parameters
        DefaultTrackSelector.Parameters.Builder builder = parameters.buildUpon();

        // Loop through each renderer to select the desired audio track
        for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
            int trackType = mappedTrackInfo.getRendererType(rendererIndex);

            // Check if the track is an audio track
            if (trackType == C.TRACK_TYPE_AUDIO ) {
                // Enable the renderer and clear any previous track selections
                builder.clearSelectionOverrides(rendererIndex).setRendererDisabled(rendererIndex, false);

                // Calculate the group index and track index based on the given track parameter
                int groupIndex = track - 1;  // Assuming the parameter 'track' is one-based index
                int[] tracks = {0};  // Select the first track by default

                // Create a selection override for the audio track
                DefaultTrackSelector.SelectionOverride override = new DefaultTrackSelector.SelectionOverride(groupIndex, tracks);

                // Apply the selection override to the renderer
                builder.setSelectionOverride(rendererIndex, mappedTrackInfo.getTrackGroups(rendererIndex), override);
            }
        }

        // Set the modified parameters to the track selector
        trackSelector.setParameters(parameters); // Corrected to builder.build()


        // Update the currently selected audio track
        currentAudioTrack = track;
        // Manually update the color and image visibility for all language buttons
        for (int i = 0; i < audioTrackNames.length; i++) {
            boolean isSelected = (i == currentAudioTrack - 1);
            ImageView imageView = imageViews.get(i);
            TextView textView = textViews.get(i);

            if (isSelected) {
                imageView.setVisibility(View.VISIBLE);
                textView.setTextColor(Color.WHITE);
            } else {
                imageView.setVisibility(View.INVISIBLE);
                textView.setTextColor(Color.GRAY);
            }
        }
    }



    // Parse the master.m3u8 playlist to get available video quality options
    private class ParseM3U8Task extends AsyncTask<Uri, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Uri... uris) {
            qualityOptions.add("Auto (Recommended)");

            try {
                URL url = new URL(uris[0].toString());
                InputStream inputStream = url.openStream();

                if (inputStream != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        Log.d("M3U8Line", line); // Log the line content for debugging

                        if (line.startsWith("#EXT-X-STREAM-INF:")) {
                            // Extract the RESOLUTION attribute from the line
                            String resolution = getAttributeValue(line, "RESOLUTION");

                            // Add the resolution to the quality options list if it is not already present
                            if (!qualityOptions.contains(resolution)) {
                                qualityOptions.add(resolution);
                            }
                        }
                    }

                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return qualityOptions;
        }

        @Override
        protected void onPostExecute(List<String> qualityOptions) {
            super.onPostExecute(qualityOptions);

            // Handle the result here (e.g., update UI)
            if (qualityOptions.isEmpty()) {
//                resultTextView.setText("No quality options found.");
            } else {
                StringBuilder sb = new StringBuilder("Quality Options:\n");
                for (String option : qualityOptions) {
                    sb.append(option).append("\n");
                }
//                resultTextView.setText(sb.toString());
            }
        }
    }

    private String getAttributeValue(String line, String attribute) {
        String pattern = attribute + "=(.*?)(,|$)";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(line);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }



    // Create and customize UI buttons for video quality options
    public void setVideoQualityCustomButtonLayout(Context context, int layoutId, View layoutView) {
        if (layoutId == R.layout.layout_quality) {
            LinearLayout buttonContainer = layoutView.findViewById(R.id.button_container_video);



            for (int i = 0; i < qualityOptions.size(); i++) {
                customButtonLayout0 = new LinearLayout(context);
                customButtonLayout0.setOrientation(LinearLayout.HORIZONTAL);
                customButtonLayout0.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));

                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.baseline_check_24);
                imageView.setVisibility(i == currentVideoQualityIndex ? View.VISIBLE : View.INVISIBLE);
                customButtonLayout0.addView(imageView);

                TextView textView = new TextView(context);
                textView.setId(View.generateViewId());
                textView.setText(qualityOptions.get(i));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                customButtonLayout0.addView(textView);

                int qualityIndex = i;
                int finalI = i;
                customButtonLayout0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setVideoQuality(qualityIndex);
                        currentVideoQualityIndex = qualityIndex;
                        // Update the checkmark icon visibility based on selection
                        for (int j = 0; j < buttonContainer.getChildCount(); j++) {
                            View child = buttonContainer.getChildAt(j);
                            if (child instanceof LinearLayout) {
                                TextView qualityTextView = (TextView) ((LinearLayout) child).getChildAt(1);
                                qualityTextView.setTextColor(j == qualityIndex ? Color.WHITE : Color.GRAY);
                            }
                            if (child instanceof LinearLayout) {
                                ImageView checkmarkIcon = (ImageView) ((LinearLayout) child).getChildAt(0);
                                checkmarkIcon.setVisibility(j == qualityIndex ? View.VISIBLE : View.INVISIBLE);

                            }
                        }
                    }
                });
                // Set the initial text color based on the currentVideoQualityIndex
                textView.setTextColor(i == currentVideoQualityIndex ? Color.WHITE : Color.GRAY);


                buttonContainer.addView(customButtonLayout0);

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) customButtonLayout0.getLayoutParams();
                layoutParams.setMargins(0, 0, 0, 20);
                customButtonLayout0.setLayoutParams(layoutParams);
            }
        }
    }

    // Set the selected video quality
    public void setVideoQuality(int qualityIndex) {
        // Get the currently available track information
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = Assertions.checkNotNull(trackSelector.getCurrentMappedTrackInfo());

        // Get the current parameters of the track selector
        DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();

        // Create a builder to modify the current parameters
        DefaultTrackSelector.Parameters.Builder builder = parameters.buildUpon();

        if (qualityIndex == 0) {
            // For "Auto" button (index 0), clear all selection overrides to enable automatic quality selection
            for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
                builder.clearSelectionOverrides(rendererIndex);
            }
        } else {
            // Loop through each renderer to select the desired video quality
            for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
                int trackType = mappedTrackInfo.getRendererType(rendererIndex);

                // Check if the track is a video track
                if (trackType == C.TRACK_TYPE_VIDEO) {
                    // Enable the renderer and clear any previous track selections
                    builder.clearSelectionOverrides(rendererIndex).setRendererDisabled(rendererIndex, false);

                    // Calculate the group index and track index based on the given qualityIndex parameter
                    int groupIndex = 0;  // Assuming video quality options are in the first group
                    int[] tracks = {qualityIndex - 1};  // Adjust index to exclude "Auto"

                    // Create a selection override for the video quality
                    DefaultTrackSelector.SelectionOverride override = new DefaultTrackSelector.SelectionOverride(groupIndex, tracks);

                    // Apply the selection override to the renderer
                    builder.setSelectionOverride(rendererIndex, mappedTrackInfo.getTrackGroups(rendererIndex), override);
                }
            }
        }

        // Set the modified parameters to the track selector
        trackSelector.setParameters(builder.build());

        // Update the currently selected video quality index
        currentVideoQualityIndex = qualityIndex;
    }



    public static int getVideoOrientation(Context context, Uri videoPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(context, videoPath);
            String rotationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            int width = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            int height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            if (rotationString != null) {
                int rotation = Integer.parseInt(rotationString);
                if (rotation == 0 || rotation == 180) {
                    if (width > height) {
                        return 0; // Landscape (based on width and height)
                    } else {
                        return 1; // Portrait (based on width and height)
                    }
                } else {
                    return 1; // Portrait
                }
            }
        } catch (Exception e) {
            // Handle any exceptions (e.g., invalid video path, missing metadata)
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // Return a default value if unable to determine orientation
        return -1; // Undefined
    }


    private int findSubtitleRendererIndex(MappingTrackSelector.MappedTrackInfo mappedTrackInfo) {
        for (int i = 0; i < mappedTrackInfo.getRendererCount(); i++) {
            int trackType = mappedTrackInfo.getRendererType(i);
            if (trackType == C.TRACK_TYPE_AUDIO) {
                return i; // Return the index of the subtitle renderer
            }
        }
        return -1; // Return a value indicating that no subtitle renderer was found
    }

    private String[] getSubtitleTrackNames() {
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();

        if (mappedTrackInfo != null) {
            int rendererIndexSubtitle = findSubtitleRendererIndex(mappedTrackInfo);
            TrackGroupArray subtitleTracks = mappedTrackInfo.getTrackGroups(rendererIndexSubtitle);

            if (subtitleTracks != null && subtitleTracks.length > 0) {
                String[] subtitleTrackNames = new String[subtitleTracks.length];

                for (int i = 0; i < subtitleTracks.length; i++) {
                    Format format = subtitleTracks.get(i).getFormat(0); // Assuming single track
                    subtitleTrackNames[i] = format.label; // Use appropriate field based on your video format
                    Log.d("sub", "subtitle :  " + subtitleTrackNames[i]); // Log each subtitle track name separately
                }

                return subtitleTrackNames;
            }
        }

        return new String[0]; // Return an empty array if no subtitle tracks are available
    }


    public void setSubtitleCustomButtonLayout(Context context, int layoutId, View layoutView) {
        if (layoutId == R.layout.layout_subtitle) { // Replace with your subtitle layout ID
            LinearLayout buttonContainer = layoutView.findViewById(R.id.button_container_subtitle);

            subtitleTrackNames = getSubtitleTrackNames();

            for (int i = 0; i < subtitleTrackNames.length; i++) {
                // Create a custom button layout
                customButtonLayout2 = new LinearLayout(context);
                customButtonLayout2.setOrientation(LinearLayout.HORIZONTAL);
                customButtonLayout2.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));

                // Create and customize the ImageView (checkmark icon)
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.baseline_check_24);
                imageView.setVisibility(i == currentSubtitleTrack ? View.VISIBLE : View.INVISIBLE);
                customButtonLayout2.addView(imageView);

                // Add the ImageView to the list
                subtitleImageViews.add(imageView);

                // Create and customize the TextView (subtitle track name)
                TextView textView = new TextView(context);
                textView.setId(View.generateViewId());
                textView.setText(subtitleTrackNames[i]);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                customButtonLayout2.addView(textView);

                // Add the TextView to the list
                subtitleTextViews.add(textView);

                // Set click listener for the custom button
                final int trackIndex = i;
                customButtonLayout2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(trackIndex == currentSubtitleTrack){
                            imageView.setVisibility(View.INVISIBLE);
                            textView.setTextColor(Color.GRAY);
                            setSubtitleTrack( -1 );
                        }
                        else{
                            setSubtitleTrack(trackIndex);
                            if (trackIndex == currentSubtitleTrack) {
                                imageView.setVisibility(View.VISIBLE);
                                textView.setTextColor(Color.WHITE); // Selected subtitle track
                            } else {
                                textView.setTextColor(Color.GRAY); // Other subtitle tracks
                                imageView.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                });

                // Set initial text color based on selection
                if (i == currentSubtitleTrack) {
                    textView.setTextColor(Color.WHITE); // Selected subtitle track
                } else {
                    textView.setTextColor(Color.GRAY); // Other subtitle tracks
                }

                // Add the custom button layout to the button container
                buttonContainer.addView(customButtonLayout2);

                // Add layout margin to each button
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) customButtonLayout2.getLayoutParams();
                layoutParams.setMargins(0, 0, 0, 20); // Set margins (left, top, right, bottom)
                customButtonLayout2.setLayoutParams(layoutParams);
            }
        }
    }
    public void setSubtitleTrack(int track) {

        if(track ==  -1 ){

            trackSelector.setParameters(new DefaultTrackSelector.ParametersBuilder()
                    .setRendererDisabled(C.TRACK_TYPE_TEXT, true)
                    .build()
            );
            currentSubtitleTrack = -1;
        }else{
            trackSelector.setParameters(new DefaultTrackSelector.ParametersBuilder()
                    .setRendererDisabled(C.TRACK_TYPE_TEXT, false)
                    .build()
            );
            // Get the currently available track information
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo = Assertions.checkNotNull(trackSelector.getCurrentMappedTrackInfo());

            // Get the current parameters of the track selector
            DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();

            // Create a builder to modify the current parameters
            DefaultTrackSelector.Parameters.Builder builder = parameters.buildUpon();

            // Loop through each renderer to select the desired subtitle track
            for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
                int trackType = mappedTrackInfo.getRendererType(rendererIndex);

                // Check if the track is a subtitle track
                if (trackType == C.TRACK_TYPE_TEXT) {
                    // Calculate the group index and track index based on the given track parameter
                    int groupIndex = (track == -1) ? C.INDEX_UNSET : track;
                    int[] tracks = {0}; // Select the first track by default

                    // Create a selection override for the subtitle track
                    DefaultTrackSelector.SelectionOverride override = new DefaultTrackSelector.SelectionOverride(groupIndex, tracks);

                    // Apply the selection override to the renderer
                    builder.setSelectionOverride(rendererIndex, mappedTrackInfo.getTrackGroups(rendererIndex), override);

                    // Enable or disable the renderer based on the track parameter
                    builder.setRendererDisabled(rendererIndex, track == -1);
                }
            }

            // Set the modified parameters to the track selector
            trackSelector.setParameters(builder.build());

            // Update the currently selected subtitle track
            currentSubtitleTrack = track;
        }



    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }

    }
    @Override
    protected void onStop() {
        super.onStop();
        if(isPlayingInPiP){
            Log.d("onDestroy","onDestroy function called ");
            TableClass.HistoryItem existingItem = databaseHelper.hisWatchDao().getHistoryItemByPath(String.valueOf(videoUri));
            if (existingItem != null) {
                databaseHelper.hisWatchDao().deleteHistoryItem(databaseHelper.hisWatchDao().getHistoryItemByPath(String.valueOf(videoUri)));
            }
            databaseHelper.hisWatchDao().insertHistoryItem(new TableClass.HistoryItem(String.valueOf(videoUri),videoFileName,formatTime(totalDuration),currentPosition,size,resolution,sub_title_pot,rating));
            if (player != null) {
                player.release();
                player = null;
            }
        }
    }

    // Function to pause video playback
    private void pauseVideo() {
        if (player != null && player.isPlaying()) {
            player.pause();
            btnPlayPause.setImageResource(R.drawable.ic_play); // Change button icon to play
        }
    }

    // Override onPause() to pause video playback when the activity is not visible
    @Override
    protected void onPause() {
        super.onPause();
        pauseVideo();
        TableClass.HistoryItem existingItem = databaseHelper.hisWatchDao().getHistoryItemByPath(String.valueOf(videoUri));
        if (existingItem != null) {
            databaseHelper.hisWatchDao().deleteHistoryItem(databaseHelper.hisWatchDao().getHistoryItemByPath(String.valueOf(videoUri)));
        }
        databaseHelper.hisWatchDao().insertHistoryItem(new TableClass.HistoryItem(String.valueOf(videoUri),videoFileName,formatTime(totalDuration),currentPosition,size,resolution,sub_title_pot,rating));
    }


    // Override onResume() to resume video playback when the activity is visible again
    @Override
    protected void onResume() {
        super.onResume();
        if (isFullScreen) {
            enterFullScreenMode();
        } else {
            exitFullScreenMode();
        }

        player.play();
        togglePlayPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ACTION_MANAGE_WRITE_SETTINGS_CODE){
            boolean value;
            value = Settings.System.canWrite(getApplicationContext());
            if(value){
                success = true;
            }
            else{
                Toast.makeText(this, "Not Granted", Toast.LENGTH_LONG).show();
            }
        }
    }
}