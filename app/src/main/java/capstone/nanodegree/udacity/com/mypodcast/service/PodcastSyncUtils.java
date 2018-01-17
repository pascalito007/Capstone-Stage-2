package capstone.nanodegree.udacity.com.mypodcast.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.provider.MyPodcastContract;

/**
 * Created by jem001 on 12/12/2017.
 */

public class PodcastSyncUtils {
    private static final int SYNC_INTERVAL_HOURS = 24;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 24;
    private static boolean sInitialized;

    private static final String PODCAST_SYNC_TAG = "podcast-sync";

    static void scheduleFirebaseJobDispatcherSync(final Context context) {
        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(context);
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job syncPodcastJob = dispatcher.newJobBuilder()
                .setService(PodcastFirebaseJobService.class)
                .setTag(PODCAST_SYNC_TAG)
                .setConstraints(sharedPreferences.getBoolean(context.getString(R.string.pref_connexion_key),context.getResources().getBoolean(R.bool.pref_connexion_default))?Constraint.ON_UNMETERED_NETWORK:Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(SYNC_INTERVAL_SECONDS, SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();
        dispatcher.schedule(syncPodcastJob);
    }


    synchronized public static void initialize(final Context context) {
        if (sInitialized) return;
        sInitialized = true;

        scheduleFirebaseJobDispatcherSync(context);

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                Cursor cursor = context.getContentResolver().query(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI, null, null, null, null);
                if (cursor==null || cursor.getCount()==0) {
                    startImmediateSync(context);
                }
                cursor.close();

                return null;
            }
        }.execute();
    }

    public static void startImmediateSync(final Context context) {
        Intent intent = new Intent(context, PodcastSyncIntentService.class);
        context.startService(intent);
    }
}
