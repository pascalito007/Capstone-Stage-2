package capstone.nanodegree.udacity.com.mypodcast.fragment;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.parceler.Parcels;

import capstone.nanodegree.udacity.com.mypodcast.IdlingResource.SimpleIdlingResource;
import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.activity.EpisodeActivity_;
import capstone.nanodegree.udacity.com.mypodcast.activity.FetchGpodderMainActivity_;
import capstone.nanodegree.udacity.com.mypodcast.activity.FetchItunePodcastActivity_;
import capstone.nanodegree.udacity.com.mypodcast.adapter.MainFragmentAdapter;
import capstone.nanodegree.udacity.com.mypodcast.adapter.MainFragmentTopListAdapter;
import capstone.nanodegree.udacity.com.mypodcast.model.Podcast;
import capstone.nanodegree.udacity.com.mypodcast.provider.MyPodcastContract;

/**
 * Created by jem001 on 04/12/2017.
 */
@EFragment(R.layout.main_fragment)
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, MainFragmentAdapter.PodcastClickListener, MainFragmentTopListAdapter.PodcastClickListener {

    @ViewById(R.id.rv_recommendations)
    RecyclerView rvRecommendations;
    @ViewById(R.id.rv_top_podcast)
    RecyclerView rvTopList;
    @ViewById(R.id.rv_category1)
    RecyclerView rvCategory1;
    @ViewById(R.id.rv_category2)
    RecyclerView rvCategory2;
    @ViewById(R.id.view_pager)
    ViewPager viewPager;
    @ViewById(R.id.tab_layout)
    TabLayout tabLayout;
    @ViewById(R.id.tv_category_name1)
    TextView category1Name;
    @ViewById(R.id.tv_category_name2)
    TextView category2Name;
    SharedPreferences sp;



    MainFragmentAdapter recommendationAdapter, category1Adapter, category2Adapter;
    MainFragmentTopListAdapter topListAdapter;

    private static final int RECOMMENDATIONS_LOADER_ID = 55;
    private static final int TOP_LIST_LOADER_ID = 33;
    private static final int FIRST_CATEGORY_LOADER_ID = 44;
    private static final int SECOND_CATEGORY_LOADER_ID = 66;
    public static final String itunes = "itunes";
    public static SimpleIdlingResource mIdlingResource;



    @AfterViews
    public void myOnCreateView() {

        sp = PreferenceManager.getDefaultSharedPreferences(getContext());

        String email = sp.getString("email", null);
        Log.d("email:", email + "");
        MainFragmentPagerAdapter pagerAdapter = new MainFragmentPagerAdapter(getFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager, true);

        recommendationAdapter = new MainFragmentAdapter(this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), getActivity().getResources().getInteger(R.integer.grid_column_count));
        rvRecommendations.setHasFixedSize(true);
        rvRecommendations.setNestedScrollingEnabled(false);
        rvRecommendations.setLayoutManager(layoutManager);
        rvRecommendations.setAdapter(recommendationAdapter);

        //CATEGORY LIST 1
        category1Adapter = new MainFragmentAdapter(this);
        RecyclerView.LayoutManager layoutManagerCat1 = new GridLayoutManager(getContext(), getActivity().getResources().getInteger(R.integer.grid_column_count));
        rvCategory1.setHasFixedSize(true);
        rvCategory1.setNestedScrollingEnabled(false);
        rvCategory1.setLayoutManager(layoutManagerCat1);
        rvCategory1.setAdapter(category1Adapter);

        //CATEGORY LIST 2
        category2Adapter = new MainFragmentAdapter(this);
        RecyclerView.LayoutManager layoutManagerCat2 = new GridLayoutManager(getContext(), getActivity().getResources().getInteger(R.integer.grid_column_count));
        rvCategory2.setHasFixedSize(true);
        rvCategory2.setNestedScrollingEnabled(false);
        rvCategory2.setLayoutManager(layoutManagerCat2);
        rvCategory2.setAdapter(category2Adapter);

        //TOP LISTco
        topListAdapter = new MainFragmentTopListAdapter(this);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        rvTopList.setHasFixedSize(true);
        rvTopList.setNestedScrollingEnabled(false);
        rvTopList.setLayoutManager(layoutManager2);
        rvTopList.setAdapter(topListAdapter);

        getLoaderManager().initLoader(RECOMMENDATIONS_LOADER_ID, null, this);
        getLoaderManager().initLoader(TOP_LIST_LOADER_ID, null, this);
        getLoaderManager().initLoader(FIRST_CATEGORY_LOADER_ID, null, this);
        getLoaderManager().initLoader(SECOND_CATEGORY_LOADER_ID, null, this);


    }

    @Click(R.id.img_itune)
    public void imgItuneClick() {
        FetchItunePodcastActivity_.intent(getContext()).start();
    }

    @Click(R.id.img_gpodder_category)
    public void imgGpodderCategoryClick() {
        FetchGpodderMainActivity_.intent(getContext()).start();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case RECOMMENDATIONS_LOADER_ID:
                if (mIdlingResource != null)
                    mIdlingResource.setIdleState(false);
                return new CursorLoader(getContext(), MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI, null, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_PROVIDER+" = ? and "+ MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_MAIN_SCREEEN+" = ? and "+ MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_CATEGORY_FLAG+" = ?", new String[]{"itunes", "Yes", "No"}, null);
            case TOP_LIST_LOADER_ID:
                return new CursorLoader(getContext(), MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI, null, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_PROVIDER+" = ? and "+ MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_MAIN_SCREEEN+" = ? and "+ MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_CATEGORY_FLAG+" = ?", new String[]{"gpodder.net", "Yes", "No"}, null);
            case FIRST_CATEGORY_LOADER_ID:
                return new CursorLoader(getContext(), MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI, null, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_PROVIDER+" = ? and "+ MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_MAIN_SCREEEN+" = ? and "+ MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_CATEGORY_FLAG+" = ?", new String[]{"gpodder.net", "Yes", "Yes 1"}, null);
            case SECOND_CATEGORY_LOADER_ID:
                return new CursorLoader(getContext(), MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI, null, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_PROVIDER+" = ? and "+ MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_MAIN_SCREEEN+" = ? and "+ MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_CATEGORY_FLAG+" = ?", new String[]{"gpodder.net", "Yes", "Yes 2"}, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            switch (loader.getId()) {
                case RECOMMENDATIONS_LOADER_ID:
                    recommendationAdapter.swapAdapter(data);
                    if (mIdlingResource != null)
                        mIdlingResource.setIdleState(true);
                    break;
                case TOP_LIST_LOADER_ID:
                    topListAdapter.swapAdapter(data);
                    break;
                case FIRST_CATEGORY_LOADER_ID:
                    category1Name.setText(sp.getString("category1", ""));
                    category1Adapter.swapAdapter(data);
                    break;
                case SECOND_CATEGORY_LOADER_ID:
                    category2Name.setText(sp.getString("category2", ""));
                    category2Adapter.swapAdapter(data);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        recommendationAdapter.swapAdapter(null);
        topListAdapter.swapAdapter(null);
        category1Adapter.swapAdapter(null);
        category2Adapter.swapAdapter(null);
    }

    @Override
    public void onItemClick(Podcast podcast) {
        Cursor cursor = getActivity().getContentResolver().query(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI.buildUpon().appendPath(podcast.getPodcastId()).build(), null, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID+" = ?", new String[]{podcast.getPodcastId()}, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            podcast = Podcast.getPodcastFromCursor(cursor);
        }
        Log.d("podcastcontent:", podcast + "");
        EpisodeActivity_.intent(this).extra("podcast_extra", Parcels.wrap(podcast)).start();
    }

    @Override
    public void onGpodderItemClickListener(Podcast podcast) {
        Log.d("podcastcontent:", podcast + "");
        EpisodeActivity_.intent(this).extra("podcast_extra", Parcels.wrap(podcast)).start();
    }


    public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

        public MainFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ViewPagerSlide1Fragment_();
                case 1:
                    return new ViewPagerSlide1Fragment_();
                case 2:
                    return new ViewPagerSlide1Fragment_();
                default:
                    return null;
            }
        }


        @Override
        public int getCount() {
            return 3;
        }
    }
}
