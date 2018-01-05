package capstone.nanodegree.udacity.com.mypodcast.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.activity.EpisodeActivity_;
import capstone.nanodegree.udacity.com.mypodcast.adapter.GpodderTopPodcastAdapter;
import capstone.nanodegree.udacity.com.mypodcast.model.Category;
import capstone.nanodegree.udacity.com.mypodcast.model.GpodderTop;
import capstone.nanodegree.udacity.com.mypodcast.model.Podcast;
import capstone.nanodegree.udacity.com.mypodcast.utils.Constant;
import capstone.nanodegree.udacity.com.mypodcast.utils.NetworkUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static capstone.nanodegree.udacity.com.mypodcast.utils.Constant.gpodder_top_podcast_url;

/**
 * Created by jem001 on 06/12/2017.
 */

@EFragment(R.layout.gpodder_fragment)
public class GpodderPopularFragment extends Fragment implements GpodderTopPodcastAdapter.ItemClickListener {
    @ViewById(R.id.rv_gpodder_top)
    RecyclerView rv_gpodder_top;
    GpodderTopPodcastAdapter adapter;
    @ViewById(R.id.pb_loading_indicator)
    ProgressBar pb_loading_indicator;
    @FragmentArg("tag")
    String tag;

    @AfterViews
    public void myOnCreateView() {
        pb_loading_indicator.setVisibility(View.VISIBLE);
        new GpodderTopPodcastTask().execute();
        adapter = new GpodderTopPodcastAdapter(this);
        rv_gpodder_top.setHasFixedSize(true);
        rv_gpodder_top.setAdapter(adapter);
        Log.d("tag_extra:", tag + "");

    }

    @Override
    public void onItemClick(Podcast podcast) {
        podcast.setProvider("gpodder.net");
        EpisodeActivity_.intent(getContext()).extra("podcast_extra", Parcels.wrap(podcast)).start();
    }


    public class GpodderTopPodcastTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            Request request;
            if (tag != null && !tag.isEmpty()) {
                Log.d("backgroundfromcategory:", Constant.root_gpodder_feed_url_part + tag + "/50.json");
                request = new Request.Builder()
                        .url(Constant.root_gpodder_feed_url_part + tag + "/50.json")
                        .build();
            } else {
                Log.d("backgroundfromtoplist:",gpodder_top_podcast_url);
                request = new Request.Builder()
                        .url(gpodder_top_podcast_url)
                        .build();
            }

            String result = null;
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    result = response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    List<Podcast> list = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Podcast gp = NetworkUtils.fromTopFeeds(jsonObject);
                        list.add(gp);
                    }
                    if (!list.isEmpty()) {
                        adapter.swapAdapter(list);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            pb_loading_indicator.setVisibility(View.GONE);
        }

    }
}
