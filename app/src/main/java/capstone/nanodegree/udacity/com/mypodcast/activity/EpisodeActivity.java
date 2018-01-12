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
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import capstone.nanodegree.udacity.com.mypodcast.FeedListener;
import capstone.nanodegree.udacity.com.mypodcast.MainActivity;
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
import capstone.nanodegree.udacity.com.mypodcast.utils.NetworkUtils;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class EpisodeActivity extends AppCompatActivity implements EpisodeAdapter.EpisodeClickListener, FeedListener {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PLAY_ACTIVITY_REQUEST = 44;
    private static final String EPISODE_LIST = "items";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv_episode)
    RecyclerView rvEpisode;
    @BindView(R.id.podcast_img)
    ImageView podcast_img;
    Podcast podcast;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar pbLoadingIndicator;


    String feedUrl = "";
    EpisodeAdapter episodeAdapter;
    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    public List<Episode> episodeList;
    @BindView(R.id.fab_subscribe)
    FloatingActionButton fabSubscribe;
    @BindView(R.id.podcast_img_clean)
    ImageView podcast_img_clean;
    @BindView(R.id.podcast_cover_title)
    TextView tvPodcastCoverTitle;
    @BindView(R.id.podcast_cover_subtitle)
    TextView tvPodcastCoverSubTitle;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            podcast = Parcels.unwrap(intent.getParcelableExtra("podcast_extra"));
        }


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
        if (!podcast.getProvider().equals("itunes")) {
            new FeedItemsBackGroundTask(podcast.getFeedUrl(), this).execute();

        } else {
            new FeedUrlBackGroundTask(podcast.getFeedUrl(), this).execute();
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
        layoutManager = new LinearLayoutManager(this);
        episodeAdapter = new EpisodeAdapter(this, this, "");
        rvEpisode.setAdapter(episodeAdapter);
        rvEpisode.setLayoutManager(layoutManager);
        rvEpisode.setNestedScrollingEnabled(false);

    }


    @OnClick(R.id.fab_subscribe)
    public void btnSubscribeClick() {

        if (episodeAdapter.getItemCount() != 0) {
            if (podcast.getSubscribeFlag() != null && podcast.getSubscribeFlag().equals("1")) {
                getContentResolver().delete(MyPodcastContract.MyPodcastEntry.EPISODE_CONTENT_URI.buildUpon().appendPath(podcast.getPodcastId()).build(), MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID + " = ?", new String[]{podcast.getPodcastId()});
                ContentValues cv = new ContentValues();
                cv.putNull(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_SUBSCRIBE_FLAG);
                getContentResolver().update(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI.buildUpon().appendPath(podcast.getPodcastId()).build(), cv, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID + " = ?", new String[]{podcast.getPodcastId()});
                podcast.setSubscribers(null);
                Snackbar.make(fabSubscribe, "Unsubscription done", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                fabSubscribe.setImageDrawable(getResources().getDrawable(R.drawable.ic_subscribe_notification_active));
            } else {
                ContentValues[] episodesContentValues = NetworkUtils.getEpisodeContentValuesFromArrayList(episodeList, podcast.getPodcastId());
                Log.d("episodevaluesSize:", episodesContentValues.length + "");
                if (episodesContentValues.length != 0) {
                    updatePodcast();
                    getContentResolver().bulkInsert(MyPodcastContract.MyPodcastEntry.EPISODE_CONTENT_URI, episodesContentValues);
                    Snackbar.make(fabSubscribe, "Subscription done", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fabSubscribe.setImageDrawable(getResources().getDrawable(R.drawable.ic_subscribe_notification_off));
                }
            }
        }
    }

    public void updatePodcast() {
        podcast.setSubscribeFlag("1");
        ContentValues cv = new ContentValues();
        cv.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_SUBSCRIBE_FLAG, "1");
        getContentResolver().update(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI.buildUpon().appendPath(podcast.getPodcastId()).build(), cv, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID + " = ?", new String[]{podcast.getPodcastId()});
    }

    public void showResult(String result) throws JSONException {
        if (result != null && !result.isEmpty()) {
            JSONObject resultObject = new JSONObject(result);
            feedUrl = resultObject.getJSONArray("results").getJSONObject(0).getString("feedUrl");
            if (!feedUrl.isEmpty())
                //feedItemsBackGroundTask.backGroundTask(feedUrl);
                new FeedItemsBackGroundTask(feedUrl, this).execute();
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
        Intent intent = new Intent(this, PlayMediaActivity.class);
        intent.putExtra("episode_extra", Parcels.wrap(episode));
        intent.putExtra("img", podcast.getCoverImage());
        startActivityForResult(intent, PLAY_ACTIVITY_REQUEST);
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
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("no_account_from_download", "1");
                startActivity(intent);
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

   /* @Override
    protected void onResume() {
        super.onResume();
        PlayMediaService ps = new PlayMediaService();
        Log.d("exoplayervalue:", ps.getExoPlayer() + "");
    }*/

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
