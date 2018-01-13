package capstone.nanodegree.udacity.com.mypodcast.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.parceler.Parcels;

import butterknife.ButterKnife;
import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.fragment.GpodderPopularFragment;
import capstone.nanodegree.udacity.com.mypodcast.model.Category;
import capstone.nanodegree.udacity.com.mypodcast.utils.Constant;

public class FetchGpodderCategoryPodcastActivity extends AppCompatActivity {
    Category category;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_gpodder_category_podcast);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (category == null)
            finish();
        Intent intent=getIntent();
        if (intent!=null)
            category= Parcels.unwrap(intent.getParcelableExtra(Constant.category_extra));

        GpodderPopularFragment fragment = new GpodderPopularFragment();
        Bundle bundle=new Bundle();
        bundle.putString(Constant.tag,category.getTag());
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment).commit();

    }


}
