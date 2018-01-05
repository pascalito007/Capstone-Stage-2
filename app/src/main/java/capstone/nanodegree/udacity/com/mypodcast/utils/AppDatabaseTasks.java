package capstone.nanodegree.udacity.com.mypodcast.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.List;

import capstone.nanodegree.udacity.com.mypodcast.model.Podcast;
import capstone.nanodegree.udacity.com.mypodcast.provider.MyPodcastContract;

/**
 * Created by jem001 on 07/12/2017.
 */

public class AppDatabaseTasks {

    public static ContentValues[] getAllPodcastContentValues(List<Podcast> list, Context context) {
        if (!list.isEmpty()) {
            ContentValues[] cvs = new ContentValues[list.size()];
            for (int i = 0; i < list.size(); i++) {
                Podcast podcast = list.get(i);
                ContentValues cv = new ContentValues();
                cv.put(MyPodcastContract.MyPodcastEntry.COLUMN_TITLE, podcast.getTitle());
                cv.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_FEED_URL, podcast.getFeedUrl());
                cv.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_COVER_IMG, podcast.getCoverImage());
                cv.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ARTISTE, podcast.getArtiste());
                cv.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID, podcast.getPodcastId());
                cvs[i] = cv;
            }
            return cvs;
        }
        return null;

    }

    public static ContentValues getPodcastContentValues(Podcast podcast) {
        // AtomicInteger count = new AtomicInteger(0);
        ContentValues cv = new ContentValues();
        cv.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ARTISTE, podcast.getArtiste());
        cv.put(MyPodcastContract.MyPodcastEntry.COLUMN_TITLE, podcast.getTitle());
        cv.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_FEED_URL, podcast.getFeedUrl());
        cv.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_COVER_IMG, podcast.getCoverImage());
        cv.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID, podcast.getPodcastId());
        return cv;
    }

    public static void saveSinglePodcast(ContentValues contentValues, Context context) {
        context.getContentResolver().insert(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI, contentValues);
    }

    public static Cursor getSingleEpisodeById(String episodeId, Context context) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(MyPodcastContract.MyPodcastEntry.EPISODE_CONTENT_URI.buildUpon().appendPath(episodeId+"").build(), null, MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_LINK+" = ?", new String[]{episodeId+""}, null);
        } catch (Exception e) {

        }
        return cursor;
    }

    public static Cursor getEpisodeListByPodcastId(String podcastId, Context context) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(MyPodcastContract.MyPodcastEntry.EPISODE_CONTENT_URI.buildUpon().appendPath(podcastId).build(), null, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID+" = ?", new String[]{podcastId}, null);
        } catch (Exception e) {

        }
        return cursor;
    }


}
