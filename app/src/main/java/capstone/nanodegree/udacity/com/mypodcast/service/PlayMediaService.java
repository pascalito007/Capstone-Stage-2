package capstone.nanodegree.udacity.com.mypodcast.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

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
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import capstone.nanodegree.udacity.com.mypodcast.eventbus.MessageEvent;
import capstone.nanodegree.udacity.com.mypodcast.PlayerWidgetProvider;
import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.activity.PlayMediaActivity;
import capstone.nanodegree.udacity.com.mypodcast.utils.AppUtils;
import capstone.nanodegree.udacity.com.mypodcast.utils.Constant;

/**
 * Created by jem001 on 08/01/2018.
 */

public class PlayMediaService extends Service implements ExoPlayer.EventListener {
    public ExoPlayer mExoPlayer;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private NotificationManager mNotificationManager;
    String mp3_url;
    public SharedPreferences sharedPreferences;


    public PlayMediaService() {
    }

    //S'execute la premier fois qu'on fait startService. Toutes les autres fois il ne passe plus ici
    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //EventBus.getDefault().register(this);
        initializeMediaSession();


    }

    //S'execute chaque fois qu'on fait startService
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("intentserviceinfoPlay:", "|startid:" + startId + "|action:" + intent.getAction());
        if (intent != null) {
            if (startId == 1) {
                if (intent.getAction().equals(Constant.ACTION_NEW_URL)) {
                    //mp3_url = intent.getStringExtra("mp3_url");
                    mp3_url = sharedPreferences.getString(Constant.bottom_mp3_url, null);
                    Log.d("mp3Url:", mp3_url);
                    String rootUrl = mp3_url.substring(0, mp3_url.indexOf("/", 7));
                    String other = mp3_url.substring(rootUrl.length());
                    File file = AppUtils.getFileInInternalMemory(other.replaceAll("/", "_"));

                    //Initalize the player with provided Uri
                    if (file != null) {
                        Log.d("fileavailable:", file.getName());
                        initializePlayer(Uri.fromFile(file));
                    } else {
                        initializePlayer(Uri.parse(mp3_url));
                    }
                }
            } else {
                if (intent.getAction().equals(Constant.ACTION_GET_EXOPLAYER_INSTANCE)) {
                    EventBus.getDefault().post(new MessageEvent(sharedPreferences.getInt(Constant.player_state, 0), sharedPreferences.getLong(Constant.player_current_position, 0), (SimpleExoPlayer) this.mExoPlayer));
                }
            }
        }


        return START_NOT_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //return new MyServiceBinder(this);
        return null;
    }


    //This is to initialize player with the media source
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            //mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            //AppConfig.getBus().post("player");
            /*if (myServiceListener != null)
                myServiceListener.onServiceMessage(mExoPlayer);*/
            //Log.d("listenervalue:", myServiceListener + "");
            SimpleExoPlayer exoPlayer = (SimpleExoPlayer) mExoPlayer;
            Log.d("playervalue:", exoPlayer + "");
            EventBus.getDefault().post(new MessageEvent(mExoPlayer.getPlaybackState(), sharedPreferences.getLong(Constant.player_current_position, 0), exoPlayer));
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(Constant.player_current_position, mExoPlayer.getCurrentPosition());
            editor.putInt(Constant.bottom_play_pause_icon,R.drawable.exo_controls_pause);
            editor.apply();
            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(this, "PlayMediaService");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    this, userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.seekTo(sharedPreferences.getLong(Constant.player_current_position, 0));
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


    public ExoPlayer getExoPlayer() {
        return this.mExoPlayer;
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        Log.d("Exoplayerchanges:", "onTimelineChanged");
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        Log.d("Exoplayerchanges:", "Exoplayerchanges");
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        Log.d("Exoplayerchanges:", "Exoplayerchanges");
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
            //pbLoadingIndicator.setVisibility(View.INVISIBLE);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            //pbLoadingIndicator.setVisibility(View.INVISIBLE);
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if (playbackState != ExoPlayer.STATE_BUFFERING) {
            //pbLoadingIndicator.setVisibility(View.VISIBLE);
        } else {
            //pbLoadingIndicator.setVisibility(View.INVISIBLE);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
        Log.d("stateserviceplay:", mStateBuilder.build().getState() + "|" + mExoPlayer + "|currentPosition:" + mExoPlayer.getCurrentPosition());
        showNotification(mStateBuilder.build());
        SimpleExoPlayer exoPlayer = (SimpleExoPlayer) mExoPlayer;
        EventBus.getDefault().post(new MessageEvent(mStateBuilder.build().getState(), mExoPlayer.getCurrentPosition(), exoPlayer));
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
        Log.d("Exoplayerchanges:", "onPlaybackParametersChanged");
    }

    @Override
    public void onSeekProcessed() {
        Log.d("Exoplayerchanges:", "onSeekProcessed");
    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {

            mExoPlayer.setPlayWhenReady(true);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), PlayerWidgetProvider.class));
            PlayerWidgetProvider.updateMediaTitle(getApplicationContext(), appWidgetManager, sharedPreferences.getString(Constant.bottom_title, null), R.drawable.exo_controls_pause, appWidgetIds);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(Constant.bottom_play_pause_icon, R.drawable.exo_controls_pause);
            editor.putLong(Constant.player_current_position, mExoPlayer.getCurrentPosition());
            editor.apply();
            //EventBus.getDefault().post(new MessageEvent(PlaybackStateCompat.STATE_PAUSED,(SimpleExoPlayer) mExoPlayer));
        }

        @Override
        public void onPause() {

            mExoPlayer.setPlayWhenReady(false);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), PlayerWidgetProvider.class));
            PlayerWidgetProvider.updateMediaTitle(getApplicationContext(), appWidgetManager, sharedPreferences.getString(Constant.bottom_title, null), R.drawable.exo_controls_play, appWidgetIds);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(Constant.bottom_play_pause_icon, R.drawable.exo_controls_play);
            editor.putLong(Constant.player_current_position, mExoPlayer.getCurrentPosition());
            editor.apply();
            //EventBus.getDefault().post(new MessageEvent(PlaybackStateCompat.STATE_PLAYING,(SimpleExoPlayer) mExoPlayer));
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
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

        builder.setContentTitle(sharedPreferences.getString(Constant.bottom_title, null))
                .setContentText(sharedPreferences.getString(Constant.bottom_sub_title, null))
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
        EventBus.getDefault().post(new MessageEvent(state.getState(), sharedPreferences.getLong(Constant.player_current_position, 0), (SimpleExoPlayer) mExoPlayer));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(Constant.player_current_position, mExoPlayer.getCurrentPosition());
        editor.putInt(Constant.bottom_play_pause_icon, icon);
        editor.apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        mMediaSession.setActive(false);

    }


    public MediaSessionCompat getMediaSession() {
        return mMediaSession;
    }

    private void releasePlayer() {
        if (mNotificationManager != null)
            mNotificationManager.cancelAll();
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }

    }


}
