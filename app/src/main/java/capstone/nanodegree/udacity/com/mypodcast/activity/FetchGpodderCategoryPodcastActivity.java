package capstone.nanodegree.udacity.com.mypodcast.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.parceler.Parcels;

import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.fragment.GpodderCategoryFragment;
import capstone.nanodegree.udacity.com.mypodcast.fragment.GpodderCategoryFragment_;
import capstone.nanodegree.udacity.com.mypodcast.fragment.GpodderPopularFragment;
import capstone.nanodegree.udacity.com.mypodcast.fragment.GpodderPopularFragment_;
import capstone.nanodegree.udacity.com.mypodcast.model.Category;
import capstone.nanodegree.udacity.com.mypodcast.model.GpodderTop;

@EActivity(R.layout.activity_fetch_gpodder_category_podcast)
public class FetchGpodderCategoryPodcastActivity extends AppCompatActivity {
    @Extra("category_extra")
    Category category;


    @AfterViews
    public void myOnCreate() {
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (category == null)
            finish();
        GpodderPopularFragment fragment = GpodderPopularFragment_.builder().arg("tag", category.getTag()).build();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment).commit();
    }
}
