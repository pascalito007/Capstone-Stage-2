package capstone.nanodegree.udacity.com.mypodcast.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.fragment.EpisodeListFragment;
import capstone.nanodegree.udacity.com.mypodcast.model.Episode;
import capstone.nanodegree.udacity.com.mypodcast.service.AppWidgetPlayerIntentService;
import capstone.nanodegree.udacity.com.mypodcast.service.PlayMediaService;
import capstone.nanodegree.udacity.com.mypodcast.utils.Constant;

public class PlayMediaActivity extends AppCompatActivity implements EpisodeListFragment.PlayListClickListener{
    @BindView(R.id.playerView)
    SimpleExoPlayerView mPlayerView;
    //SimpleExoPlayer mExoPlayer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    Episode episode;
    String img;
    @BindView(R.id.tv_description)
    TextView description;
    //private static MediaSessionCompat mMediaSession;
    //private PlaybackStateCompat.Builder mStateBuilder;
    //private NotificationManager mNotificationManager;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar pbLoadingIndicator;

    @BindView(R.id.adView)
    AdView mAdView;
    SharedPreferences sharedPreferences;
    PlayMediaService service;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_media);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            img = intent.getStringExtra("img");
            episode = (Episode) intent.getExtras().getSerializable("episode_extra");
        }
        toolbar.setTitle(episode.getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        service = new PlayMediaService();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            description.setText(Html.fromHtml(episode.getFullDescription(), Html.FROM_HTML_MODE_COMPACT));
        } else
            description.setText(Html.fromHtml(episode.getFullDescription()));

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("media_title", episode.getTitle());
        editor.apply();
        //initializeMediaSession();
        Log.d("serviceplaying:", isMyServiceRunning(PlayMediaService.class) + "" + "|img" + img);
        if (isMyServiceRunning(PlayMediaService.class)) {
            Intent intent1 = new Intent(this, PlayMediaService.class);
            stopService(intent1);
        }
        Intent intent2 = new Intent(this, PlayMediaService.class);
        intent2.putExtra("mp3_url", episode.getMp3FileUrl());
        startService(intent2);


        Glide.with(this).asBitmap().load(img).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                mPlayerView.setDefaultArtwork(resource);
            }
        });
        if (findViewById(R.id.container) != null) {
            Log.d("episodeidpass:", episode + "");

            EpisodeListFragment episodeListFragment = new EpisodeListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("podcast_id_extra", episode.getPodcastId());
            episodeListFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.container, episodeListFragment).commit();
        }

        //Update App widget to play that music
        AppWidgetPlayerIntentService.startActionUpdateMediaTitle(this);

        //Set up for pre-fetching interstitial ad request
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");


        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("FA9F653AECE1C006CA7AB09735E30CD9")
                .build();
        mAdView.loadAd(adRequest);

    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    //This is to initialize player with the media source

    private void initializePlayer(SimpleExoPlayer exoPlayer) {
        Log.d("playerfromservice:", exoPlayer + "");
        mPlayerView.setPlayer(exoPlayer);
        mPlayerView.setControllerShowTimeoutMs(0);
        mPlayerView.setControllerHideOnTouch(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showPlayer(SimpleExoPlayer exoPlayer) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("playerservicetag:", exoPlayer + "");
                initializePlayer(exoPlayer);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //releasePlayer();
        //mMediaSession.setActive(false);
        setBottomValues();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setBottomValues();
    }

    public void setBottomValues() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constant.bottom_play_pause_icon, R.drawable.exo_controls_pause);
        editor.putString(Constant.bottom_img_cover, img);
        editor.putString(Constant.bottom_title, episode.getTitle());
        editor.putString(Constant.bottom_sub_title, episode.getDescription());
        editor.putString(Constant.bottom_mp3_url, episode.getMp3FileUrl());
        editor.apply();
    }


    @Override
    public void onPlayItemClick(Episode episode) {
        Toast.makeText(this, "episode clicked:" + episode.getTitle(), Toast.LENGTH_LONG).show();
        this.episode = episode;
        setTitle(episode.getTitle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            description.setText(Html.fromHtml(episode.getFullDescription(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            description.setText(Html.fromHtml(episode.getFullDescription()));
        }


    }


    public static class MediaReceiver extends BroadcastReceiver {
        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            PlayMediaService service = new PlayMediaService();
            MediaButtonReceiver.handleIntent(service.getMediaSession(), intent);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("podcast_id", episode.getPodcastId());
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }


}
