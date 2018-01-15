package capstone.nanodegree.udacity.com.mypodcast;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import capstone.nanodegree.udacity.com.mypodcast.IdlingResource.SimpleIdlingResource;
import capstone.nanodegree.udacity.com.mypodcast.eventbus.MessageEvent;
import capstone.nanodegree.udacity.com.mypodcast.fragment.DownloadFragment;
import capstone.nanodegree.udacity.com.mypodcast.fragment.MainFragment;
import capstone.nanodegree.udacity.com.mypodcast.fragment.SubscriptionFragment;
import capstone.nanodegree.udacity.com.mypodcast.login.AccountFragment;
import capstone.nanodegree.udacity.com.mypodcast.login.LoginFragment;
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


        Log.d("oncreatecontainer:", "Yes" + sharedPreferences.getString(Constant.bottom_title, null) + "|" + sharedPreferences.getString(Constant.bottom_mp3_url, null));
        getIdlingResource();

        PodcastSyncUtils.initialize(this);
        if (fromEpisode != null && fromEpisode.equals(getString(R.string.one_value))) {
            if (savedInstanceState == null) {
                LoginFragment loginFragment = new LoginFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, loginFragment).commit();

            }
        } else {
            if (savedInstanceState == null) {
                MainFragment mainFragment = new MainFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, mainFragment).commit();
            }
        }


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
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
            return true;
        });
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
