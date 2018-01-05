package capstone.nanodegree.udacity.com.mypodcast.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jem001 on 03/01/2018.
 */

public class MyPodcastDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "mypodcast.db";
    public static final int DATABASE_VERSION = 1;

    public MyPodcastDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //CREATE PODCAST TABLE
        final String SQL_CREATE_PODCAST_TABLE = "CREATE TABLE " + MyPodcastContract.MyPodcastEntry.PODCAST_TABLE_NAME + " (" +
                MyPodcastContract.MyPodcastEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID + " TEXT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_PROVIDER + " TEXT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_MAIN_SCREEEN + " TEXT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_CATEGORY_FLAG + " TEXT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_CATEGORY_NAME + " TEXT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_TITLE + " TEXT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_COVER_IMG + " TEXT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ARTISTE + " TEXT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_AUTHOR + " TEXT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_SUBSCRIBERS + " TEXT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_FEED_URL + " TEXT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_SUBSCRIBE_FLAG + " TEXT, " +
                " UNIQUE (" + MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID + " ) ON CONFLICT REPLACE);";
        //CREATE EPISODE TABLE
        final String SQL_CREATE_EPISODE_TABLE = "CREATE TABLE " + MyPodcastContract.MyPodcastEntry.EPISODE_TABLE_NAME + " (" +
                MyPodcastContract.MyPodcastEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID + " TEXT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_SIZE + " TEXT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_TITLE + " TEXT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_PUB_DATE + " TEXT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_HISTORY_FLAG + " TEXT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_DOWNLOAD_FLAG + " TEXT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_DESCRIPTION + " TEXT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_DURATION + " TEXT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_ID + " TEXT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_LINK + " TEXT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_MP3_URL + " TEXT, " +
                MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_AUTHOR + " TEXT, " +
                " UNIQUE (" + MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID + ", " + MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_LINK + " ) ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_PODCAST_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_EPISODE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS ";
        sqLiteDatabase.execSQL(SQL_DROP_TABLE + MyPodcastContract.MyPodcastEntry.PODCAST_TABLE_NAME);
        sqLiteDatabase.execSQL(SQL_DROP_TABLE + MyPodcastContract.MyPodcastEntry.EPISODE_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
