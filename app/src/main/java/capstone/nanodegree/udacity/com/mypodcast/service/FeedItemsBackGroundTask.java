package capstone.nanodegree.udacity.com.mypodcast.service;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;

import capstone.nanodegree.udacity.com.mypodcast.activity.EpisodeActivity;
import capstone.nanodegree.udacity.com.mypodcast.utils.NetworkUtils;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jem001 on 06/12/2017.
 */
@EBean
public class FeedItemsBackGroundTask {
    @RootContext
    EpisodeActivity activity;


    @Background
    public void backGroundTask(String url) {
        InputStream result = NetworkUtils.okHttpGetRequestInputStreamResult(url);
        if (result != null) {
            XmlToJson xmlToJson = new XmlToJson.Builder(result, null).build();
            updateUI(xmlToJson.toString());
        }

    }

    @UiThread
    public void updateUI(String result) {
        try {
            activity.showResultItems(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
