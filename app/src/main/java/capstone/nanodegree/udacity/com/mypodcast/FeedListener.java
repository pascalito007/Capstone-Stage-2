package capstone.nanodegree.udacity.com.mypodcast;

import org.json.JSONException;

/**
 * Created by jem001 on 09/01/2018.
 */

public interface FeedListener {
    void showResultItems(String result) throws JSONException;
    void showResult(String result) throws JSONException;
}
