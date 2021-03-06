package capstone.nanodegree.udacity.com.mypodcast.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.activity.EpisodeActivity;
import capstone.nanodegree.udacity.com.mypodcast.adapter.GpodderTopPodcastAdapter;
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

public class GpodderPopularFragment extends Fragment implements GpodderTopPodcastAdapter.ItemClickListener {
    @BindView(R.id.rv_gpodder_top)
    RecyclerView rv_gpodder_top;
    GpodderTopPodcastAdapter adapter;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar pb_loading_indicator;
    String tag;
    private Unbinder unbinder;
    SharedPreferences sharedPreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.gpodder_fragment, container, false);
        unbinder =ButterKnife.bind(this,view);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getContext());
        Bundle bundle=getArguments();
        if (bundle!=null)
        tag=bundle.getString(Constant.tag);
        pb_loading_indicator.setVisibility(View.VISIBLE);
        new GpodderTopPodcastTask().execute();
        adapter = new GpodderTopPodcastAdapter(this);
        rv_gpodder_top.setHasFixedSize(true);
        rv_gpodder_top.setAdapter(adapter);
        Log.d("tag_extra:", tag + "");
        return view;
    }



    @Override
    public void onItemClick(Podcast podcast) {
        podcast.setProvider(Constant.gpodder_net);
        Intent intent=new Intent(getContext(),EpisodeActivity.class);
        intent.putExtra(Constant.podcast_extra,Parcels.wrap(podcast));
        startActivity(intent);
    }


    public class GpodderTopPodcastTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            Request request;
            if (tag != null && !tag.isEmpty()) {
                Log.d("backgroundfromcategory:", Constant.root_gpodder_feed_url_part + tag + Constant.fifty_json_value);

                request = new Request.Builder()
                        .url(Constant.root_gpodder_feed_url_part + tag + "/"+sharedPreferences.getString(getString(R.string.pref_top_podcast_key),getResources().getInteger(R.integer.pref_top_podcast_default)+"")+Constant.fifty_json_value)
                        .build();
            } else {
               // Log.d("backgroundfromtoplist:",sharedPreferences.getString(getResources().getString(R.string.pref_top_podcast_key),getContext().getResources().getInteger(R.integer.pref_top_podcast_default)+"")+getString(R.string.json_ext));
                request = new Request.Builder()
                        .url(gpodder_top_podcast_url+sharedPreferences.getString(getResources().getString(R.string.pref_top_podcast_key),getResources().getInteger(R.integer.pref_top_podcast_default)+"")+getString(R.string.json_ext))
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
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
