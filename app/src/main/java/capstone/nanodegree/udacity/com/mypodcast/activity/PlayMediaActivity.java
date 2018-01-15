package capstone.nanodegree.udacity.com.mypodcast.activity;

import android.app.Activity;
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
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.eventbus.MessageEvent;
import capstone.nanodegree.udacity.com.mypodcast.fragment.EpisodeListFragment;
import capstone.nanodegree.udacity.com.mypodcast.model.Episode;
import capstone.nanodegree.udacity.com.mypodcast.service.AppWidgetPlayerIntentService;
import capstone.nanodegree.udacity.com.mypodcast.service.PlayMediaService;
import capstone.nanodegree.udacity.com.mypodcast.utils.AppUtils;
import capstone.nanodegree.udacity.com.mypodcast.utils.Constant;

public class PlayMediaActivity extends AppCompatActivity implements EpisodeListFragment.PlayListClickListener {
    @BindView(R.id.playerView)
    SimpleExoPlayerView mPlayerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    public  Episode episode;
    public  String img;
    @BindView(R.id.tv_description)
    TextView description;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar pbLoadingIndicator;

    SharedPreferences sharedPreferences;
    PlayMediaService service;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_media);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            img = intent.getStringExtra(Constant.img);
            episode = Parcels.unwrap(intent.getParcelableExtra(Constant.episode_extra));
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
        editor.putString(Constant.media_title, episode.getTitle());
        editor.apply();
        Log.d("serviceplaying:", AppUtils.isMyServiceRunning(PlayMediaService.class,this) + "" + "|img" + img);
        setBottomValues();
        if (AppUtils.isMyServiceRunning(PlayMediaService.class,this)) {
            Intent intent1 = new Intent(this, PlayMediaService.class);
            stopService(intent1);
        }
        Intent intent2 = new Intent(this, PlayMediaService.class);
        intent2.setAction(Constant.ACTION_NEW_URL);
        intent2.putExtra(Constant.mp3_url, episode.getMp3FileUrl());
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
            bundle.putString(Constant.podcast_id_extra, episode.getPodcastId());
            bundle.putString(Constant.img,img);
            episodeListFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, episodeListFragment).commit();
        }

        //Update App widget to play that music
        AppWidgetPlayerIntentService.startActionUpdateMediaTitle(this);
    }




    //This is to initialize player with the media source

    private void initializePlayer(MessageEvent event) {
        Log.d("playerfromservice:", event.exoPlayer + "");
        mPlayerView.setPlayer(event.exoPlayer);
        mPlayerView.setControllerShowTimeoutMs(0);
        mPlayerView.setControllerHideOnTouch(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showPlayer(MessageEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("playerservicetag:", event.exoPlayer + "");
                initializePlayer(event);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
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

    @Override
    protected void onResume() {
        super.onResume();
        setBottomValues();
    }

    public void setBottomValues() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constant.bottom_play_pause_icon, R.drawable.exo_controls_pause);
        editor.putString(Constant.bottom_img_cover, img);
        editor.putString(Constant.bottom_title, episode.getTitle());
        editor.putString(Constant.bottom_sub_title, episode.getDescription());
        editor.putString(Constant.bottom_mp3_url, episode.getMp3FileUrl());
        ExoPlayer exoPlayer = this.mPlayerView.getPlayer();
        if (mPlayerView.getPlayer() != null) {
            editor.putInt(Constant.player_state, mPlayerView.getPlayer().getPlaybackState());
            editor.putLong(Constant.player_current_position, mPlayerView.getPlayer().getCurrentPosition());
        }
        editor.apply();
    }


    @Override
    public void onPlayItemClick(Episode episode) {
        Toast.makeText(this, getString(R.string.episode_clicked) + episode.getTitle(), Toast.LENGTH_LONG).show();
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
            returnIntent.putExtra(Constant.podcast_id, episode.getPodcastId());
            returnIntent.putExtra(Constant.img,img);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }


}
