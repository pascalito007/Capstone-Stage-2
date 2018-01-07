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
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.util.List;

import capstone.nanodegree.udacity.com.mypodcast.MainActivity_;
import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.adapter.EpisodeAdapter;
import capstone.nanodegree.udacity.com.mypodcast.model.Download;
import capstone.nanodegree.udacity.com.mypodcast.model.Episode;
import capstone.nanodegree.udacity.com.mypodcast.model.Podcast;
import capstone.nanodegree.udacity.com.mypodcast.provider.EpisodeProvider;
import capstone.nanodegree.udacity.com.mypodcast.provider.MyPodcastContract;
import capstone.nanodegree.udacity.com.mypodcast.service.DownloadService;
import capstone.nanodegree.udacity.com.mypodcast.service.FeedItemsBackGroundTask;
import capstone.nanodegree.udacity.com.mypodcast.service.FeedUrlBackGroundTask;
import capstone.nanodegree.udacity.com.mypodcast.utils.AppDatabaseTasks;
import capstone.nanodegree.udacity.com.mypodcast.utils.AppUtils;
import capstone.nanodegree.udacity.com.mypodcast.utils.NetworkUtils;
import jp.wasabeef.glide.transformations.BlurTransformation;

@EActivity(R.layout.activity_episode)
public class EpisodeActivity extends AppCompatActivity implements EpisodeAdapter.EpisodeClickListener {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PLAY_ACTIVITY_REQUEST = 44;
    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById(R.id.rv_episode)
    RecyclerView rvEpisode;
    @ViewById(R.id.podcast_img)
    ImageView podcast_img;
    @Extra("podcast_extra")
    Podcast podcast;
    @ViewById(R.id.pb_loading_indicator)
    ProgressBar pbLoadingIndicator;


    String feedUrl = "";
    //@NonConfigurationInstance
    @Bean
    FeedUrlBackGroundTask feedUrlBackGroundTask;
    //@NonConfigurationInstance
    @Bean
    FeedItemsBackGroundTask feedItemsBackGroundTask;
    EpisodeAdapter episodeAdapter;
    @ViewById(R.id.app_bar)
    AppBarLayout appBarLayout;
    @ViewById(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    List<Episode> episodeList;
    @ViewById(R.id.fab_subscribe)
    FloatingActionButton fabSubscribe;
    @ViewById(R.id.podcast_img_clean)
    ImageView podcast_img_clean;
    @ViewById(R.id.podcast_cover_title)
    TextView tvPodcastCoverTitle;
    @ViewById(R.id.podcast_cover_subtitle)
    TextView tvPodcastCoverSubTitle;


    @AfterViews
    void myOnCreate() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.d("podcastvalues:", podcast + "");
        if (podcast == null) finish();
        registerReceiver();
        if (podcast.getSubscribeFlag() == null || podcast.getSubscribeFlag() != 1) {
            fabSubscribe.setImageDrawable(getResources().getDrawable(R.drawable.ic_subscribe_notification_active));
        } else if (podcast.getSubscribeFlag() != null && podcast.getSubscribeFlag() == 1) {
            fabSubscribe.setImageDrawable(getResources().getDrawable(R.drawable.ic_subscribe_notification_off));
        }
        Glide.with(this).load(podcast.getCoverImage()).apply(RequestOptions.bitmapTransform(new BlurTransformation(100))).into(podcast_img);
        Glide.with(this).load(podcast.getCoverImage()).into(podcast_img_clean);

        tvPodcastCoverTitle.setText(podcast.getTitle());
        pbLoadingIndicator.setVisibility(View.VISIBLE);
        if (!podcast.getProvider().equals("itunes")) {
            feedItemsBackGroundTask.backGroundTask(podcast.getFeedUrl());
        } else {
            feedUrlBackGroundTask.backGroundTask(podcast.getFeedUrl());
        }
        setTitle("");
        if (findViewById(R.id.frame1) == null) {
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        collapsingToolbarLayout.setTitle("Episode podcast");
                        isShow = true;
                    } else if (isShow) {
                        collapsingToolbarLayout.setTitle(" ");
                        isShow = false;
                    }
                }
            });
        } else {
            setTitle(podcast.getTitle() + " episodes");
        }
        episodeAdapter = new EpisodeAdapter(this, this, "");
        rvEpisode.setAdapter(episodeAdapter);
        rvEpisode.setNestedScrollingEnabled(false);
    }

    @Click(R.id.fab_subscribe)
    public void btnSubscribeClick() {

        if (episodeAdapter.getItemCount() != 0) {
            if (podcast.getSubscribeFlag()==null || podcast.getSubscribeFlag() != 1) {
                ContentValues[] episodesContentValues = NetworkUtils.getEpisodeContentValuesFromArrayList(episodeList, podcast.getPodcastId());
                Log.d("episodevaluesSize:", episodesContentValues.length + "");
                if (episodesContentValues.length != 0) {
                    updatePodcast(1);
                    getContentResolver().bulkInsert(MyPodcastContract.MyPodcastEntry.EPISODE_CONTENT_URI, episodesContentValues);
                    Snackbar.make(fabSubscribe, "Subscription done", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fabSubscribe.setImageDrawable(getResources().getDrawable(R.drawable.ic_subscribe_notification_off));
                }
            } else {
                getContentResolver().delete(MyPodcastContract.MyPodcastEntry.EPISODE_CONTENT_URI.buildUpon().appendPath(podcast.getPodcastId()).build(), MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID + " = ?", new String[]{podcast.getPodcastId()});
                ContentValues cv = new ContentValues();
                cv.putNull(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_SUBSCRIBE_FLAG);
                getContentResolver().update(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI.buildUpon().appendPath(podcast.getPodcastId()).build(), cv, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID + " = ?", new String[]{podcast.getPodcastId()});
                Snackbar.make(fabSubscribe, "Unsubscription done", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                fabSubscribe.setImageDrawable(getResources().getDrawable(R.drawable.ic_subscribe_notification_active));
            }
        }
    }

    public void updatePodcast(Integer value) {
        podcast.setSubscribeFlag(value);
        ContentValues cv = new ContentValues();
        cv.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_SUBSCRIBE_FLAG, 1);
        getContentResolver().update(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI.buildUpon().appendPath(podcast.getPodcastId()).build(), cv, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID + " = ?", new String[]{podcast.getPodcastId()});
    }

    public void showResult(String result) throws JSONException, IOException {
        if (result != null && !result.isEmpty()) {
            JSONObject resultObject = new JSONObject(result);
            feedUrl = resultObject.getJSONArray("results").getJSONObject(0).getString("feedUrl");
            if (!feedUrl.isEmpty())
                feedItemsBackGroundTask.backGroundTask(feedUrl);
        }
    }

    public void showResultItems(String result) throws JSONException {
        if (result != null) {
            episodeList = NetworkUtils.getEpisodeListFromFeed(result, podcast.getPodcastId());
            if (!episodeList.isEmpty()) {
                episodeAdapter.swapAdapter(episodeList);
            }
            pbLoadingIndicator.setVisibility(View.GONE);
        }
    }


    @Override
    public void onItemClick(Episode episode, View view) {
        PlayMediaActivity_.intent(this).extra("episode_extra", Parcels.wrap(episode)).extra("img", podcast.getCoverImage()).startForResult(PLAY_ACTIVITY_REQUEST);
    }

    @Override
    public void onDownloadItemClick(Episode episode) {

        if (checkPermission()) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            String email = sp.getString("email", null);
            if (email == null) {
                Toast.makeText(this,
                        "Create account and login befor downloading",
                        Toast.LENGTH_SHORT).show();
                MainActivity_.intent(this).extra("no_account_from_download", "1").start();
            } else {
                Cursor cursor = AppDatabaseTasks.getEpisodeListByPodcastId(podcast.getPodcastId(), this);
                Log.d("episodelist:", cursor + "");
                if (cursor == null || cursor.getCount() == 0) {
                    Toast.makeText(this, "Subscribe before downloading", Toast.LENGTH_LONG).show();
                } else {
                    if (!episode.getMp3FileUrl().isEmpty()) {
                        String rootUrl = episode.getMp3FileUrl().substring(0, episode.getMp3FileUrl().indexOf("/", 7));
                        String other = episode.getMp3FileUrl().substring(rootUrl.length());
                        File file = AppUtils.getFileInInternalMemory(other.replaceAll("/", "_"));
                        if (file != null) {
                            Log.d("fileavailable:", file.getName());
                            Toast.makeText(this,
                                    "Episode already downloaded",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            //Cursor cursor1 = AppDatabaseTasks.getSingleEpisodeById(episode.getLink(), EpisodeActivity.this);
                            startDownload(rootUrl, other, episode.getLink());
                        }
                    }
                }

            }

        } else {
            requestPermission();
        }
    }

    private void startDownload(String rootUrl, String other, String link) {
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra("root_url", rootUrl);
        intent.putExtra("other", other);
        intent.putExtra("episode_id", link);
        startService(intent);

    }

    private void registerReceiver() {
        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("messageProgress");
        bManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("messageProgress")) {

                Download download = intent.getParcelableExtra("download");
                if (download.getProgress() == 100) {
                    Toast.makeText(EpisodeActivity.this, String.format("Downloaded (%d/%d) MB :" + download.getFileName(), download.getCurrentFileSize(), download.getTotalFileSize()), Toast.LENGTH_LONG).show();
                    Log.d("download finished:", String.format("Downloaded (%d/%d) MB :" + download.getFileName(), download.getCurrentFileSize(), download.getTotalFileSize()) + "|episode_id:" + download.getEpisodeId());

                  /*  Cursor cursorx = getContentResolver().query(MyPodcastContract.MyPodcastEntry.EPISODE_CONTENT_URI.buildUpon().appendPath(download.getEpisodeId()).build(), null, MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_LINK + " = ?", new String[]{download.getEpisodeId()}, null, null);
                    if (cursorx != null && cursorx.getCount() != 0) {
                        cursorx.moveToFirst();
                        Log.d("cursorvalues:", cursorx.getString(cursorx.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_TITLE)) + "");
                        cursorx.close();
                    }

                    ContentValues cv = new ContentValues();
                    cv.put(MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_DOWNLOAD_FLAG, "Yes");
                    Log.d("episodeidvalue:", download.getEpisodeId() + "|" + download.getEpisodeId().trim() + "|" + download.getEpisodeId());
                    getApplicationContext().getContentResolver().update(MyPodcastContract.MyPodcastEntry.EPISODE_CONTENT_URI.buildUpon().appendPath(download.getEpisodeId().trim()).build(), cv, MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_LINK + " = ?", new String[]{download.getEpisodeId().trim()});*/

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
                Cursor cursor = getContentResolver().query(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI.buildUpon().appendPath(data.getStringExtra("podcast_id")).build(), null, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID + " = ?", new String[]{data.getStringExtra("podcast_id")}, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    podcast = Podcast.getPodcastFromCursor(cursor);
                    Log.d("podcastresult:", podcast + "");
                }
            }
        }
    }
}
