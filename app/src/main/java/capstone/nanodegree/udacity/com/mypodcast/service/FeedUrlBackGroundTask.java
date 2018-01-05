package capstone.nanodegree.udacity.com.mypodcast.service;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.json.JSONException;

import java.io.IOException;

import capstone.nanodegree.udacity.com.mypodcast.activity.EpisodeActivity;
import capstone.nanodegree.udacity.com.mypodcast.utils.NetworkUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jem001 on 06/12/2017.
 */
@EBean
public class FeedUrlBackGroundTask {
    @RootContext
    EpisodeActivity activity;

    @Background
    public void backGroundTask(String url) {
        updateUI(NetworkUtils.okHttpGetRequestStringResult(url));
    }

    @UiThread
    public void updateUI(String result) {
        try {
            if (result != null) {
                activity.showResult(result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
