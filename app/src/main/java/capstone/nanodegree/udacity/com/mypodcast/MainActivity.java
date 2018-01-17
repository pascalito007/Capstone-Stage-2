package capstone.nanodegree.udacity.com.mypodcast;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.BottomNavigationView;
import android.support.test.espresso.IdlingResource;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import capstone.nanodegree.udacity.com.mypodcast.IdlingResource.SimpleIdlingResource;
import capstone.nanodegree.udacity.com.mypodcast.activity.EpisodeActivity;
import capstone.nanodegree.udacity.com.mypodcast.activity.PlayMediaActivity;
import capstone.nanodegree.udacity.com.mypodcast.eventbus.DataEvent;
import capstone.nanodegree.udacity.com.mypodcast.eventbus.MessageEvent;
import capstone.nanodegree.udacity.com.mypodcast.fragment.DownloadFragment;
import capstone.nanodegree.udacity.com.mypodcast.fragment.MainFragment;
import capstone.nanodegree.udacity.com.mypodcast.fragment.SubscriptionFragment;
import capstone.nanodegree.udacity.com.mypodcast.login.AccountFragment;
import capstone.nanodegree.udacity.com.mypodcast.login.LoginFragment;
import capstone.nanodegree.udacity.com.mypodcast.model.Episode;
import capstone.nanodegree.udacity.com.mypodcast.provider.MyPodcastContract;
import capstone.nanodegree.udacity.com.mypodcast.service.PlayMediaService;
import capstone.nanodegree.udacity.com.mypodcast.service.PodcastSyncUtils;
import capstone.nanodegree.udacity.com.mypodcast.utils.AppUtils;
import capstone.nanodegree.udacity.com.mypodcast.utils.Constant;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.navigation)
    BottomNavigationView bottomNavigationView;
    String fromEpisode;
    SharedPreferences sharedPreferences;

    @Nullable
    SimpleIdlingResource mIdlingResource;
    @BindView(R.id.album_art)
    ImageView imageView;
    @BindView(R.id.title)
    TextView tvBottomTitle;
    @BindView(R.id.tvSubTitle)
    TextView subTitle;
    @BindView(R.id.play_pause)
    ImageButton imageButton;
    @BindView(R.id.controls_container)
    CardView controlsContainer;
    MessageEvent event;
    boolean playerNull = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent intent = getIntent();
        if (intent != null) {
            fromEpisode = intent.getStringExtra(Constant.no_account_from_download);

        }
        if (sharedPreferences.getInt(Constant.cursor_count, 0) == 0) {
            findViewById(R.id.pb_loading).setVisibility(View.VISIBLE);
            hide();
        } else {
            findViewById(R.id.pb_loading).setVisibility(View.GONE);
            if (savedInstanceState == null) {
                show();
            }
        }

        Log.d("oncreatecontainer:", "Yes" + sharedPreferences.getString(Constant.bottom_title, null) + "|" + sharedPreferences.getString(Constant.bottom_mp3_url, null));
        getIdlingResource();

        PodcastSyncUtils.initialize(this);
        if (fromEpisode != null && fromEpisode.equals(getString(R.string.one_value))) {
            if (savedInstanceState == null) {
                LoginFragment loginFragment = new LoginFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, loginFragment).commit();

            }
        }
        //else {
            /*if (savedInstanceState == null) {
                if (findViewById(R.id.main_container) != null) {
                    findViewById(R.id.main_container).setVisibility(View.VISIBLE);
                    findViewById(R.id.img_refresh).setVisibility(View.GONE);
                    findViewById(R.id.img_sad).setVisibility(View.GONE);
                    bottomNavigationView.setClickable(true);
                    MainFragment mainFragment = new MainFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, mainFragment).commit();
                } else {
                    findViewById(R.id.main_container).setVisibility(View.GONE);
                    findViewById(R.id.img_refresh).setVisibility(View.GONE);
                    findViewById(R.id.img_sad).setVisibility(View.GONE);
                    bottomNavigationView.setClickable(false);
                }
            }*/
        // }


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (sharedPreferences.getInt(Constant.cursor_count, 0) > 0) {
                switch (item.getItemId()) {
                    case R.id.subscription:
                        SubscriptionFragment fragment = new SubscriptionFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
                        MainFragment.mIdlingResource = mIdlingResource;
                        break;
                    case R.id.action_discover:
                        MainFragment mainFragment = new MainFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, mainFragment).commit();
                        break;
                    case R.id.account:
                        String email = sharedPreferences.getString(Constant.email, null);
                        if (email == null) {
                            LoginFragment accountFragment = new LoginFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, accountFragment).commit();

                        } else {
                            AccountFragment accountFragment = new AccountFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, accountFragment).commit();
                        }
                        break;
                    case R.id.download:
                        DownloadFragment downloadFragment = new DownloadFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, downloadFragment).commit();
                        break;
                    default:
                        break;
                }
            }

            return true;
        });
      /*  if (controlsContainer != null) {

        }*/
        /*controlsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String episodeId = sharedPreferences.getString(Constant.episode_id, null);
                Log.d("episodidvalue", episodeId + "");
                if (episodeId != null && !episodeId.isEmpty()) {
                    Cursor cursor = getContentResolver().query(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI, null, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID + " = ?", new String[]{episodeId}, null);
                    if (cursor != null && cursor.getCount() != 0) {
                        cursor.moveToFirst();
                        Episode episode = Episode.getEpisodeFromCursor(cursor);
                        cursor.close();
                        Intent intent1 = new Intent(MainActivity.this, EpisodeActivity.class);

                        intent1.putExtra(Constant.img, sharedPreferences.getString(Constant.bottom_img_cover, null));
                        intent1.putExtra(Constant.episode_extra, Parcels.wrap(episode));
                        startActivity(intent1);
                    }
                }

            }
        });*/
    }


    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @OnClick(R.id.play_pause)
    public void playPauseBtnClick() {
        Log.d("eventmsgplayer:", event.exoPlayer + "");
        if (event != null && event.exoPlayer != null) {
            playerNull = false;
            if (event.state == PlaybackStateCompat.STATE_PLAYING) {
                event.exoPlayer.setPlayWhenReady(false);
                imageButton.setImageResource(R.drawable.exo_controls_play);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), PlayerWidgetProvider.class));
                PlayerWidgetProvider.updateMediaTitle(getApplicationContext(), appWidgetManager, sharedPreferences.getString(Constant.bottom_title, null), R.drawable.exo_controls_play, appWidgetIds);
            } else {
                imageButton.setImageResource(R.drawable.exo_controls_pause);
                event.exoPlayer.seekTo(sharedPreferences.getLong(Constant.player_current_position, 0));
                event.exoPlayer.setPlayWhenReady(true);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), PlayerWidgetProvider.class));
                PlayerWidgetProvider.updateMediaTitle(getApplicationContext(), appWidgetManager, sharedPreferences.getString(Constant.bottom_title, null), R.drawable.exo_controls_pause, appWidgetIds);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        bottomControlContainer();

    }

    public void bottomControlContainer() {
        Log.d("eventMsgBottom1:", event + "|service:" + AppUtils.isMyServiceRunning(PlayMediaService.class, this) + "|" + sharedPreferences.getString(Constant.bottom_mp3_url, null));
        if (event == null && !AppUtils.isMyServiceRunning(PlayMediaService.class, this) && sharedPreferences.getString(Constant.bottom_mp3_url, null) != null) {
            Log.d("eventMsgBottom1Player:", "Null");
            playerNull = true;
            Intent intent = new Intent(this, PlayMediaService.class);
            intent.setAction(Constant.ACTION_NEW_URL);
            startService(intent);
        } else {
            playerNull = false;
        }
        if (sharedPreferences.getString(Constant.bottom_mp3_url, null) != null) {
            Intent intent = new Intent(this, PlayMediaService.class);
            intent.setAction(Constant.ACTION_GET_EXOPLAYER_INSTANCE);
            startService(intent);

            controlsContainer.setVisibility(View.VISIBLE);
            Glide.with(this).load(sharedPreferences.getString(Constant.bottom_img_cover, null)).into(imageView);
            String tit = sharedPreferences.getString(Constant.bottom_title, null);
            tvBottomTitle.setText((tit == null || tit.isEmpty()) ? "n/a" : tit);
            String sub = sharedPreferences.getString(Constant.bottom_sub_title, null);
            subTitle.setText((sub == null || sub.isEmpty()) ? "n/a" : sub);
            imageButton.setImageResource(sharedPreferences.getInt(Constant.bottom_play_pause_icon, 0));
        } else {
            controlsContainer.setVisibility(View.GONE);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showState(MessageEvent eventMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                event = eventMsg;
                if (playerNull) {
                    event.exoPlayer.setPlayWhenReady(false);
                }
                if (eventMsg.state == PlaybackStateCompat.STATE_PLAYING) {
                    imageButton.setImageResource(R.drawable.exo_controls_pause);
                } else {
                    imageButton.setImageResource(R.drawable.exo_controls_play);
                }
                Log.d("eventmsg:", eventMsg + "");

            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dataState(DataEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (event == null || event.count == 0) {
                    Log.d("datasize:", event + "");
                    hide();
                } else {
                    Log.d("datasize:", event.count + "");
                    show();
                }
            }
        });
    }

    public void show() {
        findViewById(R.id.main_container).setVisibility(View.VISIBLE);
        findViewById(R.id.img_refresh).setVisibility(View.GONE);
        findViewById(R.id.img_sad).setVisibility(View.GONE);
        bottomNavigationView.setClickable(true);
        bottomNavigationView.setFocusable(true);
        findViewById(R.id.pb_loading).setVisibility(View.GONE);

        MainFragment mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, mainFragment).commit();
    }

    public void hide() {
        findViewById(R.id.main_container).setVisibility(View.GONE);
        findViewById(R.id.img_refresh).setVisibility(View.VISIBLE);
        findViewById(R.id.img_sad).setVisibility(View.VISIBLE);
        findViewById(R.id.pb_loading).setVisibility(View.GONE);
        bottomNavigationView.setClickable(false);
        bottomNavigationView.setFocusable(false);
    }


    @OnClick(R.id.img_refresh)
    public void refreshClicked() {
        findViewById(R.id.img_refresh).setVisibility(View.GONE);
        findViewById(R.id.pb_loading).setVisibility(View.VISIBLE);
        //findViewById(R.id.img_sad).setVisibility(View.GONE);
        PodcastSyncUtils.startImmediateSync(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
}
