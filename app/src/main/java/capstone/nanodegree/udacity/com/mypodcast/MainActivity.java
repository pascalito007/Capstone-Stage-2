package capstone.nanodegree.udacity.com.mypodcast;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.BottomNavigationView;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import capstone.nanodegree.udacity.com.mypodcast.IdlingResource.SimpleIdlingResource;
import capstone.nanodegree.udacity.com.mypodcast.fragment.DownloadFragment_;
import capstone.nanodegree.udacity.com.mypodcast.fragment.MainFragment;
import capstone.nanodegree.udacity.com.mypodcast.fragment.MainFragment_;
import capstone.nanodegree.udacity.com.mypodcast.fragment.SubscriptionFragment_;
import capstone.nanodegree.udacity.com.mypodcast.login.LoginFragment_;
import capstone.nanodegree.udacity.com.mypodcast.service.PodcastSyncUtils;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    @ViewById(R.id.navigation)
    BottomNavigationView bottomNavigationView;
    @Extra("no_account_from_download")
    String fromEpisode;


    @Nullable
    SimpleIdlingResource mIdlingResource;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource(getApplicationContext());
        }
        return mIdlingResource;
    }

    @AfterViews
    void myOnCreate() {
        getIdlingResource();

        PodcastSyncUtils.initialize(this);
        if (fromEpisode != null && fromEpisode.equals("1")) {
            Fragment loginFragment = new LoginFragment_();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, loginFragment).commit();
        } else {
            Fragment mainFragment = new MainFragment_();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, mainFragment).commit();
        }


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.subscription:
                    Fragment fragment = new SubscriptionFragment_();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
                    MainFragment.mIdlingResource=mIdlingResource;
                    break;
                case R.id.action_discover:
                    Fragment mainFragment = new MainFragment_();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, mainFragment).commit();
                    break;
                case R.id.account:
                    Fragment accountFragment = new LoginFragment_();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, accountFragment).commit();
                    break;
                case R.id.download:
                    Fragment downloadFragment = new DownloadFragment_();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, downloadFragment).commit();
                    break;
                default:
                    break;
            }
            return true;
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void showItuneTopListResult(String result) {
    }
}
