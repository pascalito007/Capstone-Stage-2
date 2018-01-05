package capstone.nanodegree.udacity.com.mypodcast.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.fragment.GpodderCategoryFragment_;
import capstone.nanodegree.udacity.com.mypodcast.fragment.GpodderPopularFragment_;

@EActivity(R.layout.activity_fetch_gpodder_main)
public class FetchGpodderMainActivity extends AppCompatActivity {
    @ViewById(R.id.view_pager)
    ViewPager viewPager;
    @ViewById(R.id.sliding_tabs)
    TabLayout tabLayout;

    @AfterViews
    public void myOnCreate() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GpodderPagerAdapter pagerAdapter = new GpodderPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }


    public class GpodderPagerAdapter extends FragmentPagerAdapter {

        public GpodderPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new GpodderPopularFragment_();
                case 1:
                    return new GpodderCategoryFragment_();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "MOST POPULAR";
                case 1:
                    return "CATEGORY";
                default:
                    return super.getPageTitle(position);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
