package capstone.nanodegree.udacity.com.mypodcast.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.adapter.EpisodeAdapter;
import capstone.nanodegree.udacity.com.mypodcast.model.Episode;
import capstone.nanodegree.udacity.com.mypodcast.model.Podcast;
import capstone.nanodegree.udacity.com.mypodcast.provider.MyPodcastContract;
import capstone.nanodegree.udacity.com.mypodcast.utils.NetworkUtils;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;

/**
 * Created by jem001 on 04/12/2017.
 */
@EFragment(R.layout.episode_list_fragment)
public class EpisodeListFragment extends Fragment implements EpisodeAdapter.EpisodeClickListener {

    @ViewById(R.id.rv_episode)
    RecyclerView rv_episode_list;
    @ViewById(R.id.pb_loading_indicator)
    ProgressBar pb_loading_indicator;
    EpisodeAdapter episodeAdapter;
    Podcast podcast;
    String feedUrl = "";
    List<Episode> episodeList;
    @FragmentArg("podcast_id_extra")
    String podcastId;
    public PlayListClickListener listener;


    @AfterViews
    void myOnCreateView() {
        Cursor cursor = getActivity().getContentResolver().query(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI.buildUpon().appendPath(podcastId).build(), null, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID+" = ?", new String[]{podcastId}, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            podcast = Podcast.getPodcastFromCursor(cursor);
            feedUrl = podcast.getFeedUrl();
            cursor.close();
        }
        Log.d("podcastvaluesfragment", podcast + "|arg:" + podcastId);
        if (!podcast.equals("itunes")) {
            new BackTask().execute();
        } else {
            new BackTaskItune().execute();
        }
        pb_loading_indicator.setVisibility(View.VISIBLE);
        episodeAdapter = new EpisodeAdapter(this, getContext(), "play");
        //RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        //rv_episode_list.setLayoutManager(layoutManager);
        //rv_episode_list.setHasFixedSize(true);
        rv_episode_list.setAdapter(episodeAdapter);
        rv_episode_list.setNestedScrollingEnabled(false);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener= (PlayListClickListener) context;
        }catch (ClassCastException ex){
            throw new ClassCastException(context.toString()+" must implement OnItemClickListener");
        }
    }

    @Override
    public void onItemClick(Episode podcast, View view) {
        listener.onPlayItemClick(podcast);
    }

    @Override
    public void onDownloadItemClick(Episode podcast) {

    }


    public class BackTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            InputStream result = NetworkUtils.okHttpGetRequestInputStreamResult(feedUrl);
            if (result != null) {
                XmlToJson xmlToJson = new XmlToJson.Builder(result, null).build();
                return xmlToJson.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    episodeList = NetworkUtils.getEpisodeListFromFeed(result, podcastId);
                    if (!episodeList.isEmpty()) {
                        episodeAdapter.swapAdapter(episodeList);
                    }
                    pb_loading_indicator.setVisibility(View.GONE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
    }

    public class BackTaskItune extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            return NetworkUtils.okHttpGetRequestStringResult(feedUrl);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && !result.isEmpty()) {
                try {

                    JSONObject resultObject = new JSONObject(result);
                    feedUrl = resultObject.getJSONArray("results").getJSONObject(0).getString("feedUrl");
                    if (!feedUrl.isEmpty())
                        new BackTask().execute();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public interface PlayListClickListener {
        void onPlayItemClick(Episode episode);
    }
}
