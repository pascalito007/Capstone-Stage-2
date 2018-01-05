package capstone.nanodegree.udacity.com.mypodcast.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.fragment.EpisodeListFragment;
import capstone.nanodegree.udacity.com.mypodcast.fragment.EpisodeListFragment_;
import capstone.nanodegree.udacity.com.mypodcast.model.Episode;
import capstone.nanodegree.udacity.com.mypodcast.utils.AppUtils;

@EActivity(R.layout.activity_play_media)
public class PlayMediaActivity extends AppCompatActivity implements ExoPlayer.EventListener, EpisodeListFragment.PlayListClickListener {
    @ViewById(R.id.playerView)
    SimpleExoPlayerView mPlayerView;
    private SimpleExoPlayer mExoPlayer;
    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @Extra("episode_extra")
    @InstanceState
    Episode episode;
    @Extra("img")
    @InstanceState
    String img;
    @ViewById(R.id.tv_description)
    TextView description;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private NotificationManager mNotificationManager;
    @ViewById(R.id.pb_loading_indicator)
    ProgressBar pbLoadingIndicator;


    @AfterViews
    public void myOnCreate() {
        toolbar.setTitle(episode.getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            description.setText(Html.fromHtml(episode.getFullDescription(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            description.setText(Html.fromHtml(episode.getFullDescription()));
        }
        Glide.with(this).asBitmap().load(img).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                mPlayerView.setDefaultArtwork(resource);
            }
        });

        initializeMediaSession();


        String rootUrl = episode.getMp3FileUrl().substring(0, episode.getMp3FileUrl().indexOf("/", 7));
        String other = episode.getMp3FileUrl().substring(rootUrl.length());
        File file = AppUtils.getFileInInternalMemory(other.replaceAll("/", "_"));
        if (file != null) {
            Log.d("fileavailable:", file.getName());
            initializePlayer(Uri.fromFile(file));
        } else {
            initializePlayer(Uri.parse(episode.getMp3FileUrl()));
        }
        if (findViewById(R.id.container) != null) {
            Log.d("episodeidpass:", episode + "");
            EpisodeListFragment episodeListFragment = new EpisodeListFragment_().builder().arg("podcast_id_extra", episode.getPodcastId()).build();
            getSupportFragmentManager().beginTransaction().add(R.id.container, episodeListFragment).commit();
        }
    }


    //This is to initialize player with the media source
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            //mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);
            mPlayerView.setControllerShowTimeoutMs(0);
            mPlayerView.setControllerHideOnTouch(false);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(this, "PlayMediaActivity");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    this, userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }


    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(this, this.getClass().getName());

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }


    /**
     * Shows Media Style notification, with an action that depends on the current MediaSession
     * PlaybackState.
     *
     * @param state The PlaybackState of the MediaSession.
     */
    private void showNotification(PlaybackStateCompat state) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        int icon;
        String play_pause;
        if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            icon = R.drawable.exo_controls_pause;
            play_pause = getString(R.string.pause);
            builder.setOngoing(true);
        } else {
            builder.setOngoing(false);
            icon = R.drawable.exo_controls_play;
            play_pause = getString(R.string.play);
        }


        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                icon, play_pause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(this,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE));

        NotificationCompat.Action restartAction = new android.support.v4.app.NotificationCompat
                .Action(R.drawable.exo_controls_previous, getString(R.string.restart),
                MediaButtonReceiver.buildMediaButtonPendingIntent
                        (this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (this, 0, new Intent(this, PlayMediaActivity.class), 0);

        builder.setContentTitle(episode.getTitle())
                .setContentText(episode.getDescription())
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.mipmap.ic_podcast_launcher)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(restartAction)
                .addAction(playPauseAction)
                .setStyle(new NotificationCompat.MediaStyle()
                        .setMediaSession(mMediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1));


        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        mMediaSession.setActive(false);
    }

    private void releasePlayer() {
        mNotificationManager.cancelAll();
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
            pbLoadingIndicator.setVisibility(View.INVISIBLE);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            pbLoadingIndicator.setVisibility(View.INVISIBLE);
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if (playbackState != ExoPlayer.STATE_BUFFERING) {
            pbLoadingIndicator.setVisibility(View.VISIBLE);
        } else {
            pbLoadingIndicator.setVisibility(View.INVISIBLE);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
        showNotification(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

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


        String rootUrl = episode.getMp3FileUrl().substring(0, episode.getMp3FileUrl().indexOf("/", 7));
        String other = episode.getMp3FileUrl().substring(rootUrl.length());
        File file = AppUtils.getFileInInternalMemory(other.replaceAll("/", "_"));
        if (file != null) {
            reloadMedia(Uri.fromFile(file));
        } else {
            reloadMedia(Uri.parse(episode.getMp3FileUrl()));
        }
    }

    public void reloadMedia(Uri uri) {
        String userAgent = Util.getUserAgent(this, "PlayMediaActivity");
        MediaSource mediaSource = new ExtractorMediaSource(uri, new DefaultDataSourceFactory(
                this, userAgent), new DefaultExtractorsFactory(), null, null);
        mExoPlayer.prepare(mediaSource);
        mExoPlayer.setPlayWhenReady(true);
    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    public static class MediaReceiver extends BroadcastReceiver {
        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
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
}
