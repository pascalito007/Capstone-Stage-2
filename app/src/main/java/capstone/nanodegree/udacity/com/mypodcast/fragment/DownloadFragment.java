package capstone.nanodegree.udacity.com.mypodcast.fragment;

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
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.adapter.DownloadAdapter;
import capstone.nanodegree.udacity.com.mypodcast.model.Episode;
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
    List<Episode> list = new ArrayList<>();
    @BindView(R.id.available)
    TextView available;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.download_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        adapter = new DownloadAdapter(this, getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv_downloads.setLayoutManager(layoutManager);
        rv_downloads.setHasFixedSize(true);
        rv_downloads.setAdapter(adapter);
        getLoaderManager().initLoader(DOWNLOAD_LOAD_ID, null, this);
        available.setVisibility(View.VISIBLE);
        return view;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case DOWNLOAD_LOAD_ID:
                return new CursorLoader(getContext(), MyPodcastContract.MyPodcastEntry.EPISODE_CONTENT_URI, null, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount()!=0) {
            available.setVisibility(View.GONE);
            while (data.moveToNext()) {
                Episode episode = Episode.getEpisodeFromCursor(data);
                String rootUrl = episode.getMp3FileUrl().substring(0, episode.getMp3FileUrl().indexOf("/", 7));
                String other = episode.getMp3FileUrl().substring(rootUrl.length());
                Log.d("other:", other.replaceAll("/", "_"));
                File file = AppUtils.getFileInInternalMemory(other.replaceAll("/", "_"));
                if (file != null) {
                    list.add(episode);
                }
            }
            if (!list.isEmpty())
                adapter.swapAdapter(list);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapAdapter(null);
    }

    @Override
    public void onItemClick(Episode episodeId) {
        /*Log.d("episodeidcontent:", episodeId + "");
        Intent intent = new Intent(getActivity(), EpisodeDetailsActivity.class);
        intent.putExtra(Constant.episode_id, episodeId.getEpisodeId());
        startActivity(intent);*/
    }

    @Override
    public void onItemDeleteClick(Episode episodeId, View view) {
        String mp3_url = episodeId.getMp3FileUrl();
        String rootUrl = mp3_url.substring(0, mp3_url.indexOf("/", 7));
        String other = mp3_url.substring(rootUrl.length());
        Log.d("other:", other.replaceAll("/", "_"));
        File file = AppUtils.getFileInInternalMemory(other.replaceAll("/", "_"));
        if (file != null) {
            file.delete();
            list.remove(episodeId);
            adapter.swapAdapter(list);
            Snackbar.make(view, R.string.file_deleted, Snackbar.LENGTH_LONG)
                    .setAction(Constant.action, null).show();
        } else {
            Snackbar.make(view, R.string.file_not_exist, Snackbar.LENGTH_LONG)
                    .setAction(Constant.action, null).show();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
