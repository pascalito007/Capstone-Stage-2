package capstone.nanodegree.udacity.com.mypodcast.fragment;

import android.content.ContentValues;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.activity.EpisodeDetailsActivity;
import capstone.nanodegree.udacity.com.mypodcast.adapter.DownloadAdapter;
import capstone.nanodegree.udacity.com.mypodcast.provider.MyPodcastContract;
import capstone.nanodegree.udacity.com.mypodcast.utils.AppUtils;
import capstone.nanodegree.udacity.com.mypodcast.utils.Constant;

/**
 * Created by jem001 on 04/12/2017.
 */
public class DownloadFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, DownloadAdapter.ItemClickListener {

    @BindView(R.id.rv_download)
    RecyclerView rv_downloads;
    DownloadAdapter adapter;
    private static final int DOWNLOAD_LOAD_ID = 33;
    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.download_fragment, container, false);
        unbinder =ButterKnife.bind(this,view);
        adapter = new DownloadAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv_downloads.setLayoutManager(layoutManager);
        rv_downloads.setHasFixedSize(true);
        rv_downloads.setAdapter(adapter);
        getLoaderManager().initLoader(DOWNLOAD_LOAD_ID, null, this);
        return view;
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
        Intent intent = new Intent(getContext(), EpisodeDetailsActivity.class);
        intent.putExtra(Constant.episode_id,episodeId);
        startActivity(intent);
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
                    Snackbar.make(view, R.string.file_deleted, Snackbar.LENGTH_LONG)
                            .setAction(Constant.action, null).show();
                }
            } else {
                Snackbar.make(view, R.string.file_not_exist, Snackbar.LENGTH_LONG)
                        .setAction(Constant.action, null).show();
            }
            cursor.close();
        }

    }
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
