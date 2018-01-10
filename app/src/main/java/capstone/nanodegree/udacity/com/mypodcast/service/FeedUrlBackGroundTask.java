package capstone.nanodegree.udacity.com.mypodcast.service;

import android.os.AsyncTask;

import org.json.JSONException;

import capstone.nanodegree.udacity.com.mypodcast.FeedListener;
import capstone.nanodegree.udacity.com.mypodcast.utils.NetworkUtils;

/**
 * Created by jem001 on 06/12/2017.
 */
public class FeedUrlBackGroundTask extends AsyncTask<Void, Void, String> {
    FeedListener listener;
    String url;

    public FeedUrlBackGroundTask(String url, FeedListener listener) {
        this.url = url;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        return NetworkUtils.okHttpGetRequestStringResult(url);
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            try {
                listener.showResult(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
