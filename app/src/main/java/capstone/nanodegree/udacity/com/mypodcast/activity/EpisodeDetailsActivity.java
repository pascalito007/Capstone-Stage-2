package capstone.nanodegree.udacity.com.mypodcast.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.fragment.EpisodeDetailsFragment;
import capstone.nanodegree.udacity.com.mypodcast.provider.MyPodcastContract;
import capstone.nanodegree.udacity.com.mypodcast.utils.Constant;

public class EpisodeDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.pager)
    ViewPager mPager;
    Cursor mCursor;
    MyPagerAdapter myPagerAdapter;
    long selectedItemId;
    Long episode_id;
    private static final int EPISODE_DETAILS_LOAD_ID = 77;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_details);
        ButterKnife.bind(this);
        getSupportLoaderManager().initLoader(EPISODE_DETAILS_LOAD_ID, null, this);

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(myPagerAdapter);

        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) mCursor.moveToPosition(position);
                selectedItemId = mCursor.getLong(mCursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_ID));
            }
        });

        if (episode_id == null) finish();

        if (getIntent() != null && episode_id != null) {
            selectedItemId = episode_id;
        }

    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case EPISODE_DETAILS_LOAD_ID:
                return new CursorLoader(this, MyPodcastContract.MyPodcastEntry.EPISODE_CONTENT_URI, null, MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_DOWNLOAD_FLAG+" = ?", new String[]{getString(R.string.yes)}, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursor = cursor;
        myPagerAdapter.notifyDataSetChanged();

        // Select the start ID
        if (selectedItemId > 0) {
            mCursor.moveToFirst();
            while (!mCursor.isAfterLast()) {
                if (mCursor.getLong(mCursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_ID)) == selectedItemId) {
                    final int position = mCursor.getPosition();
                    mPager.setCurrentItem(position, false);
                    break;
                }
                mCursor.moveToNext();
            }
            selectedItemId = 0;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
        myPagerAdapter.notifyDataSetChanged();
    }


    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            mCursor.moveToPosition(position);
            EpisodeDetailsFragment fragment=new EpisodeDetailsFragment();
            Bundle bundle=new Bundle();
            bundle.putLong(Constant.ARG_ITEM_ID,mCursor.getLong(mCursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_ID)));
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return mCursor != null ? mCursor.getCount() : 0;
        }
    }
}
