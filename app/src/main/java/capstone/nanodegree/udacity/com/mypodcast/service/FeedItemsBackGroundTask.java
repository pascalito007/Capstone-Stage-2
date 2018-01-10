package capstone.nanodegree.udacity.com.mypodcast.service;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.InputStream;

import capstone.nanodegree.udacity.com.mypodcast.FeedListener;
import capstone.nanodegree.udacity.com.mypodcast.utils.NetworkUtils;
import fr.arnaudguyon.xmltojsonlib.XmlToJson;

/**
 * Created by jem001 on 06/12/2017.
 */
public class FeedItemsBackGroundTask extends AsyncTask<Void, Void, String> {
    String url;
    FeedListener listener = null;

    public FeedItemsBackGroundTask(String url, FeedListener listener) {
        this.url = url;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        InputStream result = NetworkUtils.okHttpGetRequestInputStreamResult(url);
        XmlToJson xmlToJson = null;
        if (result != null) {
            xmlToJson = new XmlToJson.Builder(result, null).build();
        }
        return xmlToJson.toString();
    }

    @Override
    protected void onPostExecute(String result) {

        try {
            listener.showResultItems(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
