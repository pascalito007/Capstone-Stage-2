package capstone.nanodegree.udacity.com.mypodcast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.BottomNavigationView;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import capstone.nanodegree.udacity.com.mypodcast.IdlingResource.SimpleIdlingResource;
import capstone.nanodegree.udacity.com.mypodcast.fragment.DownloadFragment;
import capstone.nanodegree.udacity.com.mypodcast.fragment.MainFragment;
import capstone.nanodegree.udacity.com.mypodcast.fragment.PlayBackControlFragment;
import capstone.nanodegree.udacity.com.mypodcast.fragment.SubscriptionFragment;
import capstone.nanodegree.udacity.com.mypodcast.login.LoginFragment;
import capstone.nanodegree.udacity.com.mypodcast.service.PodcastSyncUtils;
import capstone.nanodegree.udacity.com.mypodcast.utils.Constant;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.navigation)
    BottomNavigationView bottomNavigationView;
    String fromEpisode;
    SharedPreferences sharedPreferences;

    @Nullable
    SimpleIdlingResource mIdlingResource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent != null) {
            fromEpisode = intent.getStringExtra("no_account_from_download");
        }
        Log.d("oncreatecontainer:", "Yes");
        getIdlingResource();

        PodcastSyncUtils.initialize(this);
        if (fromEpisode != null && fromEpisode.equals("1")) {
            LoginFragment loginFragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, loginFragment).commit();
        } else {
            MainFragment mainFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, mainFragment).commit();
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
                    LoginFragment accountFragment = new LoginFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, accountFragment).commit();
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
            mIdlingResource = new SimpleIdlingResource(getApplicationContext());
        }
        return mIdlingResource;
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onresmeContainer:", "Yes");
        setBottomContainer();

    }

    public void setBottomContainer() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d("bottomTitle:", sharedPreferences.getString(Constant.bottom_title, null) + "");
        if (sharedPreferences.getString(Constant.bottom_title, null) == null) {
            findViewById(R.id.controls_container).setVisibility(View.GONE);
        } else {
            findViewById(R.id.controls_container).setVisibility(View.VISIBLE);
            Bundle bundle = new Bundle();
            PlayBackControlFragment fragment = new PlayBackControlFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_playback_controls, fragment).commit();
        }
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void showItuneTopListResult(String result) {
    }

}
