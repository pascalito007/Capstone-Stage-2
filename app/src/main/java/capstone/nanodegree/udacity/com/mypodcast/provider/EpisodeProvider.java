package capstone.nanodegree.udacity.com.mypodcast.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.UUID;

/**
 * Created by jem001 on 04/01/2018.
 */

public class EpisodeProvider extends ContentProvider {
    public static final int CODE_EPISODE_LIST = 200;
    public static final int CODE_EPISODE_LIST_WITH_EPISODE_ID = 201;


    public static final UriMatcher sUriMatcher = buildUriMatcher();
    private MyPodcastDbHelper mOpenHelper;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MyPodcastContract.EPISODE_CONTENT_AUTORITY;
        matcher.addURI(authority, MyPodcastContract.PATH_EPISODE, CODE_EPISODE_LIST);
        matcher.addURI(authority, MyPodcastContract.PATH_EPISODE + "/*", CODE_EPISODE_LIST_WITH_EPISODE_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MyPodcastDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_EPISODE_LIST:
                cursor = db.query(MyPodcastContract.MyPodcastEntry.EPISODE_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_EPISODE_LIST_WITH_EPISODE_ID:
                cursor = db.query(MyPodcastContract.MyPodcastEntry.EPISODE_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                //cursor = null;
            //case CODE_EPISODE_LIST:
                cursor = db.query(MyPodcastContract.MyPodcastEntry.EPISODE_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case CODE_EPISODE_LIST:
                long id = db.insert(MyPodcastContract.MyPodcastEntry.EPISODE_TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MyPodcastContract.MyPodcastEntry.EPISODE_CONTENT_URI, id);
                } else {
                    returnUri = null;
                }
                break;
            default:
                returnUri = null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_EPISODE_LIST:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MyPodcastContract.MyPodcastEntry.EPISODE_TABLE_NAME, null, value);
                        Log.d("insertvalues:", value + "||" + _id);

                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (rowsInserted > 0) getContext().getContentResolver().notifyChange(uri, null);
                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsDeleted;
        switch (sUriMatcher.match(uri)) {
            case CODE_EPISODE_LIST_WITH_EPISODE_ID:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MyPodcastContract.MyPodcastEntry.EPISODE_TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_EPISODE_LIST:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MyPodcastContract.MyPodcastEntry.EPISODE_TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:
                return 0;
        }

        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_EPISODE_LIST_WITH_EPISODE_ID:
                int rowsUpdated = 0;
                rowsUpdated = db.update(MyPodcastContract.MyPodcastEntry.EPISODE_TABLE_NAME, contentValues, selection, selectionArgs);
                if (rowsUpdated > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                    return rowsUpdated;
                }
            default:
                return 0;
        }
    }
}
