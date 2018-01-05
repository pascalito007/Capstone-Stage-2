package capstone.nanodegree.udacity.com.mypodcast.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.File;

import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.activity.EpisodeDetailsActivity_;
import capstone.nanodegree.udacity.com.mypodcast.adapter.DownloadAdapter;
import capstone.nanodegree.udacity.com.mypodcast.provider.MyPodcastContract;
import capstone.nanodegree.udacity.com.mypodcast.utils.AppUtils;

/**
 * Created by jem001 on 04/12/2017.
 */
@EFragment(R.layout.download_fragment)
public class DownloadFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, DownloadAdapter.ItemClickListener {

    @ViewById(R.id.rv_download)
    RecyclerView rv_downloads;
    DownloadAdapter adapter;
    private static final int DOWNLOAD_LOAD_ID = 33;


    @AfterViews
    void myOnCreateView() {
        adapter = new DownloadAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv_downloads.setLayoutManager(layoutManager);
        rv_downloads.setHasFixedSize(true);
        rv_downloads.setAdapter(adapter);
        getLoaderManager().initLoader(DOWNLOAD_LOAD_ID, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case DOWNLOAD_LOAD_ID:
                return new CursorLoader(getContext(), MyPodcastContract.MyPodcastEntry.EPISODE_CONTENT_URI, null, MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_DOWNLOAD_FLAG+" = ?", new String[]{"Yes"}, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            adapter.swapAdapter(data);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapAdapter(null);
    }

    @Override
    public void onItemClick(long episodeId) {
        EpisodeDetailsActivity_.intent(this).extra("episode_id", episodeId).start();
    }

    @Override
    public void onItemDeleteClick(long episodeId, View view) {
        Cursor cursor = getContext().getContentResolver().query(MyPodcastContract.MyPodcastEntry.EPISODE_CONTENT_URI.buildUpon().appendPath(episodeId+"").build(), null, MyPodcastContract.MyPodcastEntry.COLUMN_ID+" = ?", new String[]{episodeId+""}, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String mp3_url = cursor.getString(cursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_MP3_URL));
            String rootUrl = mp3_url.substring(0, mp3_url.indexOf("/", 7));
            String other = mp3_url.substring(rootUrl.length());
            Log.d("other:", other.replaceAll("/", "_"));
            File file = AppUtils.getFileInInternalMemory(other.replaceAll("/", "_"));
            if (file != null) {
                file.delete();
                ContentValues cv = new ContentValues();
                cv.put(MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_DOWNLOAD_FLAG, "");
                getContext().getContentResolver().update(MyPodcastContract.MyPodcastEntry.EPISODE_CONTENT_URI.buildUpon().appendPath(episodeId+"").build(), cv, MyPodcastContract.MyPodcastEntry.COLUMN_ID+" = ?", new String[]{episodeId+""});
                Cursor cursor1 = getContext().getContentResolver().query(MyPodcastContract.MyPodcastEntry.EPISODE_CONTENT_URI, null, MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_DOWNLOAD_FLAG+" = ?", new String[]{"Yes"}, null);
                if (cursor1 != null && cursor1.getCount() > 0) {
                    adapter.swapAdapter(cursor1);
                    Snackbar.make(view, "File deleted from hard disc", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            } else {
                Snackbar.make(view, "File not exist", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            cursor.close();
        }

    }
}
