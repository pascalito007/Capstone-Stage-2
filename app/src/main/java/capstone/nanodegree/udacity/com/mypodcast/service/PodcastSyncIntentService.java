package capstone.nanodegree.udacity.com.mypodcast.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.json.JSONException;

/**
 * Created by jem001 on 12/12/2017.
 */

public class PodcastSyncIntentService extends IntentService {
    public PodcastSyncIntentService() {
        super("PodcastSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        PodcastSyncTask.syncMainScreenPodcast(this);
    }
}
