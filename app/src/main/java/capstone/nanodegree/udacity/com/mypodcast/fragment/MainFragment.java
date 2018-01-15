package capstone.nanodegree.udacity.com.mypodcast.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import capstone.nanodegree.udacity.com.mypodcast.IdlingResource.SimpleIdlingResource;
import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.activity.EpisodeActivity;
import capstone.nanodegree.udacity.com.mypodcast.activity.FetchGpodderMainActivity;
import capstone.nanodegree.udacity.com.mypodcast.activity.FetchItunePodcastActivity;
import capstone.nanodegree.udacity.com.mypodcast.activity.SettingsActivity;
import capstone.nanodegree.udacity.com.mypodcast.adapter.MainCategory1FragmentAdapter;
import capstone.nanodegree.udacity.com.mypodcast.adapter.MainCategory2FragmentAdapter;
import capstone.nanodegree.udacity.com.mypodcast.adapter.MainFragmentAdapter;
import capstone.nanodegree.udacity.com.mypodcast.adapter.MainFragmentTopListAdapter;
import capstone.nanodegree.udacity.com.mypodcast.model.Podcast;
import capstone.nanodegree.udacity.com.mypodcast.provider.MyPodcastContract;
import capstone.nanodegree.udacity.com.mypodcast.service.PodcastSyncIntentService;
import capstone.nanodegree.udacity.com.mypodcast.service.PodcastSyncUtils;
import capstone.nanodegree.udacity.com.mypodcast.utils.AppConfig;
import capstone.nanodegree.udacity.com.mypodcast.utils.AppUtils;
import capstone.nanodegree.udacity.com.mypodcast.utils.Constant;

/**
 * Created by jem001 on 04/12/2017.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, MainFragmentAdapter.PodcastClickListener, MainFragmentTopListAdapter.PodcastClickListener,
        MainCategory1FragmentAdapter.PodcastClickListener, MainCategory2FragmentAdapter.PodcastClickListener {

    @BindView(R.id.rv_recommendations)
    RecyclerView rvRecommendations;
    @BindView(R.id.rv_top_podcast)
    RecyclerView rvTopList;
    @BindView(R.id.rv_category1)
    RecyclerView rvCategory1;
    @BindView(R.id.rv_category2)
    RecyclerView rvCategory2;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.tv_category_name1)
    TextView category1Name;
    @BindView(R.id.tv_category_name2)
    TextView category2Name;
    SharedPreferences sp;
    private Unbinder unbinder;
    @BindView(R.id.tv_top_podcast_label)
    TextView topListLabel;
    @BindView(R.id.tv_recommendations_label)
    TextView recommendationLabel;
    @BindView(R.id.tv_recommendations_plus)
    TextView recommendationPlus;
    @BindView(R.id.tv_top_podcast_plus)
    TextView topListPlus;
    @BindView(R.id.refresh)
    ImageView refresh;


    MainFragmentAdapter recommendationAdapter;
    MainCategory1FragmentAdapter category1Adapter;
    MainCategory2FragmentAdapter category2Adapter;
    MainFragmentTopListAdapter topListAdapter;

    private static final int RECOMMENDATIONS_LOADER_ID = 55;
    private static final int TOP_LIST_LOADER_ID = 33;
    private static final int FIRST_CATEGORY_LOADER_ID = 44;
    private static final int SECOND_CATEGORY_LOADER_ID = 66;
    public static SimpleIdlingResource mIdlingResource;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        String email = sp.getString(Constant.email, null);
        Log.d("email:", email + "");
        MainFragmentPagerAdapter pagerAdapter = new MainFragmentPagerAdapter(getFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager, true);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        recommendationAdapter = new MainFragmentAdapter(this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), getActivity().getResources().getInteger(R.integer.grid_column_count));
        rvRecommendations.setHasFixedSize(true);
        rvRecommendations.setNestedScrollingEnabled(false);
        rvRecommendations.setLayoutManager(layoutManager);
        rvRecommendations.setAdapter(recommendationAdapter);

        //CATEGORY LIST 1
        category1Adapter = new MainCategory1FragmentAdapter(this);
        RecyclerView.LayoutManager layoutManagerCat1 = new GridLayoutManager(getContext(), getActivity().getResources().getInteger(R.integer.grid_column_count));
        rvCategory1.setHasFixedSize(true);
        rvCategory1.setNestedScrollingEnabled(false);
        rvCategory1.setLayoutManager(layoutManagerCat1);
        rvCategory1.setAdapter(category1Adapter);

        //CATEGORY LIST 2
        category2Adapter = new MainCategory2FragmentAdapter(this);
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
        return view;
    }

    @OnClick(R.id.tv_top_podcast_plus)
    public void plusTopListClicked() {
        Intent intent = new Intent(getContext(), FetchGpodderMainActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.tv_recommendations_plus)
    public void plusRecommendationClicked() {
        Intent intent = new Intent(getContext(), FetchItunePodcastActivity.class);
        startActivity(intent);
    }


    @OnClick(R.id.img_itune)
    public void imgItuneClick() {
        Intent intent = new Intent(getContext(), FetchItunePodcastActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.img_gpodder_category)
    public void imgGpodderCategoryClick() {
        Intent intent = new Intent(getContext(), FetchGpodderMainActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.refresh)
    public void onRefreshClicked() {
        Toast.makeText(getContext(), "Data is refreshing in background", Toast.LENGTH_LONG).show();
        PodcastSyncUtils.startImmediateSync(getContext());
    }

    @OnClick(R.id.settings)
    public void settingsClicked() {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case RECOMMENDATIONS_LOADER_ID:
                if (mIdlingResource != null)
                    mIdlingResource.setIdleState(false);
                return new CursorLoader(getContext(), MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI, null, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_PROVIDER + " = ? and " + MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_MAIN_SCREEEN + " = ? and " + MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_CATEGORY_FLAG + " = ?", new String[]{"itunes", "Yes", "No"}, null);
            case TOP_LIST_LOADER_ID:
                return new CursorLoader(getContext(), MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI, null, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_PROVIDER + " = ? and " + MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_MAIN_SCREEEN + " = ? and " + MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_CATEGORY_FLAG + " = ?", new String[]{"gpodder.net", "Yes", "No"}, null);
            case FIRST_CATEGORY_LOADER_ID:
                return new CursorLoader(getContext(), MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI, null, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_PROVIDER + " = ? and " + MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_MAIN_SCREEEN + " = ? and " + MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_CATEGORY_FLAG + " = ?", new String[]{"gpodder.net", "Yes", "Yes 1"}, null);
            case SECOND_CATEGORY_LOADER_ID:
                return new CursorLoader(getContext(), MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI, null, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_PROVIDER + " = ? and " + MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_MAIN_SCREEEN + " = ? and " + MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_CATEGORY_FLAG + " = ?", new String[]{"gpodder.net", "Yes", "Yes 2"}, null);
            default:
                return null;
        }
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("dataavailable:", data + "");
        if (data != null && data.getCount() != 0) {
            switch (loader.getId()) {
                case RECOMMENDATIONS_LOADER_ID:
                    recommendationAdapter.swapAdapter(data);
                    recommendationPlus.setVisibility(View.VISIBLE);
                    recommendationLabel.setText(sharedPreferences.getString(Constant.recommandation, null));
                    if (mIdlingResource != null && !AppUtils.isMyServiceRunning(PodcastSyncIntentService.class, getContext()))
                        mIdlingResource.setIdleState(true);
                    break;
                case TOP_LIST_LOADER_ID:
                    topListPlus.setVisibility(View.VISIBLE);
                    topListAdapter.swapAdapter(data);
                    topListLabel.setText(sharedPreferences.getString(Constant.toplist, null));
                    break;
                case FIRST_CATEGORY_LOADER_ID:
                    category1Name.setText(sp.getString(Constant.category1, ""));
                    category1Adapter.swapAdapter(data);
                    break;
                case SECOND_CATEGORY_LOADER_ID:
                    category2Name.setText(sp.getString(Constant.category2, ""));
                    category2Adapter.swapAdapter(data);
                    break;
                default:
                    break;
            }
        } else {
            recommendationPlus.setVisibility(View.INVISIBLE);
            topListPlus.setVisibility(View.INVISIBLE);
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
    public void onItemClick(Podcast podcast, View view) {
        ImageView imageView = view.findViewById(R.id.img_podcast);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(getActivity(), imageView, imageView.getTransitionName()
                    //getString(R.string.podcast_image_transition)
            ).toBundle();
           /*startActivity(new Intent(Intent.ACTION_VIEW,
                    ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))), bundle);*/

            Log.d("podcastcontent:", podcast + "");
            Cursor cursor = getActivity().getContentResolver().query(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI.buildUpon().appendPath(podcast.getPodcastId()).build(), null, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID + " = ?", new String[]{podcast.getPodcastId()}, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                podcast = Podcast.getPodcastFromCursor(cursor);
            }
            Intent intent = new Intent(getContext(), EpisodeActivity.class);
            intent.putExtra(Constant.podcast_extra, Parcels.wrap(podcast));
            startActivity(intent, bundle);
        } else {
            Log.d("podcastcontent:", podcast + "");
            Cursor cursor = getActivity().getContentResolver().query(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI.buildUpon().appendPath(podcast.getPodcastId()).build(), null, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID + " = ?", new String[]{podcast.getPodcastId()}, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                podcast = Podcast.getPodcastFromCursor(cursor);
            }
            Intent intent = new Intent(getContext(), EpisodeActivity.class);
            intent.putExtra(Constant.podcast_extra, Parcels.wrap(podcast));
            startActivity(intent);
        }


    }

    @Override
    public void onGpodderItemClickListener(Podcast podcast, View view) {
        Log.d("podcastcontent:", podcast + "");
        ImageView imageView = view.findViewById(R.id.img_podcast);
        startEpisode(podcast, imageView);
    }

    @Override
    public void onCategory2ItemClick(Podcast podcast, View view) {
        Log.d("podcastcontent:", podcast + "");
        ImageView imageView = view.findViewById(R.id.img_podcast);
        startEpisode(podcast, imageView);
    }

    @Override
    public void onCategory1ItemClick(Podcast podcast, View view) {
        Log.d("podcastcontent:", podcast + "");
        ImageView imageView = view.findViewById(R.id.img_podcast);
        startEpisode(podcast, imageView);
    }

    public void startEpisode(Podcast podcast, View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(getActivity(), view, view.getTransitionName()
            ).toBundle();

            Intent intent = new Intent(getContext(), EpisodeActivity.class);
            intent.putExtra(Constant.podcast_extra, Parcels.wrap(podcast));
            startActivity(intent, bundle);

        } else {
            Intent intent = new Intent(getContext(), EpisodeActivity.class);
            intent.putExtra(Constant.podcast_extra, Parcels.wrap(podcast));
            startActivity(intent);
        }
    }


    public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

        public MainFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ViewPagerSlide1Fragment();
                case 1:
                    return new ViewPagerSlide1Fragment();
                case 2:
                    return new ViewPagerSlide1Fragment();
                default:
                    return null;
            }
        }


        @Override
        public int getCount() {
            return 3;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
