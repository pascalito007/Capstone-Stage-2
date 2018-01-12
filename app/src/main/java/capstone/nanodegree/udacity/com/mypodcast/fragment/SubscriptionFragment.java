package capstone.nanodegree.udacity.com.mypodcast.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toolbar;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.activity.EpisodeActivity;
import capstone.nanodegree.udacity.com.mypodcast.adapter.SubscriptionAdapter;
import capstone.nanodegree.udacity.com.mypodcast.model.Podcast;
import capstone.nanodegree.udacity.com.mypodcast.provider.MyPodcastContract;

/**
 * Created by jem001 on 04/12/2017.
 */
public class SubscriptionFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SubscriptionAdapter.PodcastClickListener {

    @BindView(R.id.rv_subscriptions)
    RecyclerView rv_subscriptions;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar pb_loading_indicator;
    SubscriptionAdapter adapter;
    private static final int SUBSCRIPTION_LOADER_ID = 11;
    //@BindView(R.id.toolbar)
    //Toolbar toolbar;
    private Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.subscription_fragment, container, false);
        unbinder =ButterKnife.bind(this,view);
        pb_loading_indicator.setVisibility(View.VISIBLE);
        adapter = new SubscriptionAdapter(this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        rv_subscriptions.setLayoutManager(layoutManager);
        rv_subscriptions.setHasFixedSize(true);
        rv_subscriptions.setAdapter(adapter);
        getLoaderManager().initLoader(SUBSCRIPTION_LOADER_ID, null, this);
        return view;
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SUBSCRIPTION_LOADER_ID:
                return new CursorLoader(getContext(), MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI, null, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_SUBSCRIBE_FLAG+" = ?", new String[]{"1"}, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            adapter.swapAdapter(data);
            pb_loading_indicator.setVisibility(View.GONE);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapAdapter(null);
    }

    @Override
    public void onItemClick(Podcast podcast) {
        Intent intent = new Intent(getContext(), EpisodeActivity.class);
        intent.putExtra("podcast_extra",Parcels.wrap(podcast));
        startActivity(intent);
    }
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
