package capstone.nanodegree.udacity.com.mypodcast;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.RemoteViews;

import capstone.nanodegree.udacity.com.mypodcast.service.AppWidgetPlayerIntentService;
import capstone.nanodegree.udacity.com.mypodcast.utils.Constant;

/**
 * Implementation of App Widget functionality.
 */
public class PlayerWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, String mediaTitle, int icon,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.player_widget);
        /*Intent intent = new Intent(context,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.tvTitle, pendingIntent);*/
        if (mediaTitle == null || mediaTitle.isEmpty())
            views.setTextViewText(R.id.tvTitle, context.getString(R.string.appwidget_text));
        else
            views.setTextViewText(R.id.tvTitle, mediaTitle);

        PendingIntent playPausePendingIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(context,
                PlaybackStateCompat.ACTION_PLAY_PAUSE);
        views.setOnClickPendingIntent(R.id.btnPlay, playPausePendingIntent);
            views.setImageViewResource(R.id.btnPlay, icon);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        /*for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }*/
        AppWidgetPlayerIntentService.startActionUpdateMediaTitle(context);
    }

    public static void updateMediaTitle(Context context, AppWidgetManager appWidgetManager, String mediaTitle, int icon, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, mediaTitle, icon, appWidgetId);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

