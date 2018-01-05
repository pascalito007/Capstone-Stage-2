package capstone.nanodegree.udacity.com.mypodcast.service;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import org.json.JSONException;

/**
 * Created by jem001 on 12/12/2017.
 */

public class PodcastFirebaseJobService extends JobService {
    private AsyncTask<Void,Void,Void> asyncTask;
    @Override
    public boolean onStartJob(JobParameters job) {
        asyncTask=new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                PodcastSyncTask.syncMainScreenPodcast(getApplicationContext());
                jobFinished(job,false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(job,false);
            }
        }.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (asyncTask!=null) asyncTask.cancel(true);
        return true;
    }
}
