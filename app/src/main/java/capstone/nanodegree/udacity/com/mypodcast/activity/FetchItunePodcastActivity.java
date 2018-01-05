package capstone.nanodegree.udacity.com.mypodcast.activity;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.adapter.ItunePodcastAdapter;
import capstone.nanodegree.udacity.com.mypodcast.model.Podcast;
import capstone.nanodegree.udacity.com.mypodcast.utils.Constant;
import capstone.nanodegree.udacity.com.mypodcast.utils.NetworkUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@EActivity(R.layout.activity_fetch_itune)
public class FetchItunePodcastActivity extends AppCompatActivity implements ItunePodcastAdapter.PodcastClickListener {
    private static final String TAG = FetchItunePodcastActivity.class.getName();
    @ViewById(R.id.toolbar)
    Toolbar toolbar;
    @ViewById(R.id.rv_itune)
    RecyclerView rvItune;
    @ViewById(R.id.pb_loading_indicator)
    ProgressBar pb_loading_indicator;
    List<Podcast> results = new ArrayList<>();
    ItunePodcastAdapter adapter;


    @AfterViews
    void myOnCreate() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String lang = Locale.getDefault().getLanguage();
        String url = Constant.itune_root_url + lang + Constant.itune_top_list_url;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        pb_loading_indicator.setVisibility(View.VISIBLE);
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    try {
                        String resultString = response.body().string();
                        JSONObject result = new JSONObject(resultString);
                        JSONObject feed = result.getJSONObject(Constant.feed);
                        JSONArray entries = feed.getJSONArray(Constant.entry);

                        for (int i = 0; i < entries.length(); i++) {
                            JSONObject json = entries.getJSONObject(i);
                            Podcast podcast = NetworkUtils.fromToplist(json);
                            results.add(podcast);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    FetchItunePodcastActivity.this.runOnUiThread(() -> {
                        adapter = new ItunePodcastAdapter(results, FetchItunePodcastActivity.this);
                        rvItune.setAdapter(adapter);
                        pb_loading_indicator.setVisibility(View.GONE);
                    });
                }
            }
        });


    }

    @Override
    public void onItemClick(Podcast podcast) {
        podcast.setProvider("itunes");
        EpisodeActivity_.intent(this).extra("podcast_extra", Parcels.wrap(podcast)).start();
    }


}