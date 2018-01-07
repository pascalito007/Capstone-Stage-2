package capstone.nanodegree.udacity.com.mypodcast.service;

import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import capstone.nanodegree.udacity.com.mypodcast.PlayerWidgetProvider;
import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.utils.Constant;


/**
 * Created by jem001 on 07/01/2018.
 */

public class AppWidgetPlayerIntentService extends IntentService {
    public static int icon;

    public AppWidgetPlayerIntentService() {
        super("AppWidgetPlayerIntentService");
    }

    public static void startActionUpdateMediaTitle(Context context) {
        Intent intent = new Intent(context, AppWidgetPlayerIntentService.class);
        intent.setAction(Constant.ACTION_UPDATE_MEDIA_TITLE);
        context.startService(intent);
    }




    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (Constant.ACTION_UPDATE_MEDIA_TITLE.equals(action)) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                final String title = sp.getString("media_title", "");
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, PlayerWidgetProvider.class));
                //Now update all widgets

                PlayerWidgetProvider.updateMediaTitle(this, appWidgetManager, title, R.drawable.exo_controls_pause, appWidgetIds);
            }
        }
    }


}
