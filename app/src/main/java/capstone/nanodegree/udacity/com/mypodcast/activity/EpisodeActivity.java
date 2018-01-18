package capstone.nanodegree.udacity.com.mypodcast.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import capstone.nanodegree.udacity.com.mypodcast.FeedListener;
import capstone.nanodegree.udacity.com.mypodcast.IdlingResource.SimpleIdlingResource;
import capstone.nanodegree.udacity.com.mypodcast.*;
import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.adapter.EpisodeAdapter;
import capstone.nanodegree.udacity.com.mypodcast.model.Download;
import capstone.nanodegree.udacity.com.mypodcast.model.Episode;
import capstone.nanodegree.udacity.com.mypodcast.model.Podcast;
import capstone.nanodegree.udacity.com.mypodcast.provider.MyPodcastContract;
import capstone.nanodegree.udacity.com.mypodcast.service.DownloadService;
import capstone.nanodegree.udacity.com.mypodcast.service.FeedItemsBackGroundTask;
import capstone.nanodegree.udacity.com.mypodcast.service.FeedUrlBackGroundTask;
import capstone.nanodegree.udacity.com.mypodcast.utils.AppDatabaseTasks;
import capstone.nanodegree.udacity.com.mypodcast.utils.AppUtils;
import capstone.nanodegree.udacity.com.mypodcast.utils.Constant;
import capstone.nanodegree.udacity.com.mypodcast.utils.NetworkUtils;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class EpisodeActivity extends AppCompatActivity implements EpisodeAdapter.EpisodeClickListener, FeedListener {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PLAY_ACTIVITY_REQUEST = 44;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_episode)
    RecyclerView rvEpisode;
    @BindView(R.id.img_podcast)
    ImageView podcast_img;
    public Podcast podcast;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar pbLoadingIndicator;


    String feedUrl = "";
    EpisodeAdapter episodeAdapter;
    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;

    public List<Episode> episodeList;
    @BindView(R.id.fab_subscribe)
    FloatingActionButton fabSubscribe;
    @BindView(R.id.podcast_img_clean)
    ImageView podcast_img_clean;
    @BindView(R.id.podcast_cover_title)
    TextView tvPodcastCoverTitle;
    @BindView(R.id.podcast_cover_subtitle)
    TextView tvPodcastCoverSubTitle;
    @BindView(R.id.refresh)
    ImageView refresh;
    RecyclerView.LayoutManager layoutManager;
    @Nullable
    SimpleIdlingResource mIdlingResource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            podcast = Parcels.unwrap(intent.getParcelableExtra(Constant.podcast_extra));
        } else {
        }
        if (podcast == null)
            //This is the default value for testing
            podcast = new Podcast("1228554342", "itunes", "Change ma vie : Outils pour l'esprit - Clotilde Dusoulier", null,
                    "http://is1.mzstatic.com/image/thumb/Podcasts117/v4/a1/99/43/a199430e-5af5-0472-70a6-aaf1771bd0d5/mza_1971773531874137213.jpg/170x170bb-85.jpg"
                    , null, null, null, "https://itunes.apple.com/lookup?id=1228554342", "1");

        if (mIdlingResource != null)
            mIdlingResource.setIdleState(false);

        Log.d("podcastvalues:", podcast + "");
        if (podcast == null) finish();
        registerReceiver();
        if (podcast.getSubscribeFlag() == null || !podcast.getSubscribeFlag().equals("1")) {
            fabSubscribe.setImageDrawable(getResources().getDrawable(R.drawable.ic_subscribe_notification_active));
        } else if (podcast.getSubscribeFlag() != null && podcast.getSubscribeFlag().equals("1")) {
            fabSubscribe.setImageDrawable(getResources().getDrawable(R.drawable.ic_subscribe_notification_off));
        }
        Glide.with(this).load(podcast.getCoverImage()).apply(RequestOptions.bitmapTransform(new BlurTransformation(100))).into(podcast_img);
        Glide.with(this).load(podcast.getCoverImage()).into(podcast_img_clean);

        tvPodcastCoverTitle.setText(podcast.getTitle());

        pbLoadingIndicator.setVisibility(View.VISIBLE);
        if (!podcast.getProvider().equals(Constant.itunes)) {
            new FeedItemsBackGroundTask(podcast.getFeedUrl(), this).execute();

        } else {
            new FeedUrlBackGroundTask(podcast.getFeedUrl(), this).execute();
        }


        setTitle(podcast.getTitle() + getString(R.string.episodes));
        //}
        layoutManager = new LinearLayoutManager(this);
        episodeAdapter = new EpisodeAdapter(this, this, "");
        rvEpisode.setAdapter(episodeAdapter);
        rvEpisode.setLayoutManager(layoutManager);
        rvEpisode.setNestedScrollingEnabled(false);

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


    @OnClick(R.id.fab_subscribe)
    public void btnSubscribeClick() {

        if (episodeAdapter.getItemCount() != 0) {
            if (podcast.getSubscribeFlag() != null && podcast.getSubscribeFlag().equals(getString(R.string.one_value))) {
                getContentResolver().delete(MyPodcastContract.MyPodcastEntry.EPISODE_CONTENT_URI.buildUpon().appendPath(podcast.getPodcastId()).build(), MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID + " = ?", new String[]{podcast.getPodcastId()});
                ContentValues cv = new ContentValues();
                cv.putNull(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_SUBSCRIBE_FLAG);
                getContentResolver().update(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI.buildUpon().appendPath(podcast.getPodcastId()).build(), cv, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID + " = ?", new String[]{podcast.getPodcastId()});
                podcast.setSubscribers(null);
                Snackbar.make(fabSubscribe, R.string.unsubscription_done, Snackbar.LENGTH_LONG)
                        .setAction(Constant.action, null).show();
                fabSubscribe.setImageDrawable(getResources().getDrawable(R.drawable.ic_subscribe_notification_active));
            } else {
                ContentValues[] episodesContentValues = NetworkUtils.getEpisodeContentValuesFromArrayList(episodeList, podcast.getPodcastId());
                if (episodesContentValues.length != 0) {
                    updatePodcast();
                    getContentResolver().bulkInsert(MyPodcastContract.MyPodcastEntry.EPISODE_CONTENT_URI, episodesContentValues);
                    Snackbar.make(fabSubscribe, R.string.subscription_done, Snackbar.LENGTH_LONG)
                            .setAction(Constant.action, null).show();
                    fabSubscribe.setImageDrawable(getResources().getDrawable(R.drawable.ic_subscribe_notification_off));
                }
            }
        }
    }

    public void updatePodcast() {
        podcast.setSubscribeFlag(getString(R.string.one_value));
        ContentValues cv = new ContentValues();
        cv.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_SUBSCRIBE_FLAG, getString(R.string.one_value));
        getContentResolver().update(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI.buildUpon().appendPath(podcast.getPodcastId()).build(), cv, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID + " = ?", new String[]{podcast.getPodcastId()});
    }

    public void showResult(String result) throws JSONException {
        Log.d("resultvalue:", result + "");
        if (result != null && !result.isEmpty()) {
            pbLoadingIndicator.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.GONE);
            JSONObject resultObject = new JSONObject(result);
            feedUrl = resultObject.getJSONArray(Constant.results).getJSONObject(0).getString(Constant.FEED_URL);
            Log.d("feedUrlValue:", feedUrl + "");
            if (!feedUrl.isEmpty()) {
                refresh.setVisibility(View.GONE);
                new FeedItemsBackGroundTask(feedUrl, this).execute();
            } else {
                refresh.setVisibility(View.VISIBLE);
                pbLoadingIndicator.setVisibility(View.GONE);
            }
        } else {
            refresh.setVisibility(View.VISIBLE);
            pbLoadingIndicator.setVisibility(View.GONE);
        }
    }

    public void showResultItems(String result) throws JSONException {
        Log.d("finalresult:", result + "");
        if (result != null) {
            refresh.setVisibility(View.GONE);
            pbLoadingIndicator.setVisibility(View.VISIBLE);
            episodeList = NetworkUtils.getEpisodeListFromFeed(result, podcast.getPodcastId());
            Log.d("episodeListvalue:", episodeList + "");
            if (!episodeList.isEmpty()) {
                refresh.setVisibility(View.GONE);
                episodeAdapter.swapAdapter(episodeList, podcast.getCoverImage());
                if (mIdlingResource != null)
                    mIdlingResource.setIdleState(true);
            } else {
                refresh.setVisibility(View.VISIBLE);
                pbLoadingIndicator.setVisibility(View.GONE);
            }
            pbLoadingIndicator.setVisibility(View.GONE);
        } else {
            refresh.setVisibility(View.VISIBLE);
            pbLoadingIndicator.setVisibility(View.GONE);
        }
    }


    @Override
    public void onItemClick(Episode episode, View view) {
        Log.d("episodecontent", episode.toString());
        Intent intent = new Intent(this, PlayMediaActivity.class);

        intent.putExtra(Constant.episode_extra, Parcels.wrap(episode));
        intent.putExtra(Constant.img, podcast.getCoverImage());
        intent.putExtra(Constant.podcast_extra, Parcels.wrap(podcast));
        startActivityForResult(intent, PLAY_ACTIVITY_REQUEST);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (episodeList != null && !episodeList.isEmpty())
            pbLoadingIndicator.setVisibility(View.GONE);

    }

    @OnClick(R.id.refresh)
    public void onRefreshClicked() {
        pbLoadingIndicator.setVisibility(View.VISIBLE);
        refresh.setVisibility(View.GONE);
        if (!podcast.getProvider().equals(Constant.itunes)) {
            new FeedItemsBackGroundTask(podcast.getFeedUrl(), this).execute();
        } else {
            new FeedUrlBackGroundTask(podcast.getFeedUrl(), this).execute();
        }
    }

    @Override
    public void onDownloadItemClick(Episode episode) {

        if (checkPermission()) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            String email = sp.getString(Constant.email, null);
            if (email == null) {
                Toast.makeText(this,
                        R.string.create_account_before,
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra(Constant.no_account_from_download, getString(R.string.one_value));
                startActivity(intent);
            } else {
                Cursor cursor = AppDatabaseTasks.getEpisodeListByPodcastId(podcast.getPodcastId(), this);
                if (cursor == null || cursor.getCount() == 0) {
                    Toast.makeText(this, R.string.subscribe_before, Toast.LENGTH_LONG).show();
                } else {
                    if (!episode.getMp3FileUrl().isEmpty()) {
                        String rootUrl = episode.getMp3FileUrl().substring(0, episode.getMp3FileUrl().indexOf("/", 8));
                        String other = episode.getMp3FileUrl().substring(rootUrl.length());
                        File file = AppUtils.getFileInInternalMemory(other.replaceAll("/", "_"));
                        if (file != null) {
                            Toast.makeText(this,
                                    R.string.already_downloaded,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("Downloaddata:", rootUrl + "|" + other + "|" + episode.getMp3FileUrl());
                            startDownload(rootUrl, other);
                        }
                    }
                }

            }

        } else {
            requestPermission();
        }
    }



    private void startDownload(String rootUrl, String other) {
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra(Constant.root_url, rootUrl);
        intent.putExtra(Constant.other, other);
        //intent.putExtra(Constant.episode_id, mp3Url);
        startService(intent);

    }

    private void registerReceiver() {
        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.message_progress);
        bManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constant.message_progress)) {

                Download download = intent.getParcelableExtra(Constant.download);
                if (download.getProgress() == 100) {
                    Toast.makeText(EpisodeActivity.this, String.format(getString(R.string.downloaded1) + download.getFileName(), download.getCurrentFileSize(), download.getTotalFileSize()), Toast.LENGTH_LONG).show();
                    Log.d("download finished:", String.format("Downloaded (%d/%d) MB :" + download.getFileName(), download.getCurrentFileSize(), download.getTotalFileSize()) + "|episode_id:" + download.getEpisodeId());
                }
            }
        }
    };


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        /*switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //startDownload();
                } else {

                    Snackbar.make(getActivity().findViewById(R.id.fragment_step), "Permission Denied, Please allow to proceed !", Snackbar.LENGTH_LONG).show();

                }
                break;


        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLAY_ACTIVITY_REQUEST) {
            if (resultCode == RESULT_OK) {
                pbLoadingIndicator.setVisibility(View.GONE);
                podcast = Parcels.unwrap(data.getParcelableExtra(Constant.podcast_extra));
                Log.d("podcastresult:", podcast + "");
                // }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setText(getString(R.string.i_love) + podcast.getTitle() + getString(R.string.podcast_cover_label) + podcast.getCoverImage())
                    .getIntent();
            startActivity(shareIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_episode_detail, menu);
        return true;
    }
}
