package capstone.nanodegree.udacity.com.mypodcast.fragment;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.adapter.GpodderTopPodcastAdapter;
import capstone.nanodegree.udacity.com.mypodcast.model.GpodderTop;
import capstone.nanodegree.udacity.com.mypodcast.utils.Constant;
import capstone.nanodegree.udacity.com.mypodcast.utils.NetworkUtils;
import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jem001 on 06/12/2017.
 */

@EFragment(R.layout.view_pager_slide)
public class ViewPagerSlide1Fragment extends Fragment {
    @ViewById(R.id.podcast_img)
    ImageView imageView;

    @AfterViews
    public void myOnCreateView() {

        Glide.with(this)
                .load("http://is5.mzstatic.com/image/thumb/Podcasts71/v4/8f/38/15/8f381588-85e3-c183-4e67-6e9814c8d808/mza_6236236708462037843.jpg/170x170bb-85.jpg")
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(150)))
                .into(imageView);
    }


}
