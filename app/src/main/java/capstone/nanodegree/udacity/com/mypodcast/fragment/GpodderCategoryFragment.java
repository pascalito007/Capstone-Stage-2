package capstone.nanodegree.udacity.com.mypodcast.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.activity.FetchGpodderCategoryPodcastActivity;
import capstone.nanodegree.udacity.com.mypodcast.adapter.GpodderCategoryPodcastAdapter;
import capstone.nanodegree.udacity.com.mypodcast.model.Category;
import capstone.nanodegree.udacity.com.mypodcast.utils.Constant;
import capstone.nanodegree.udacity.com.mypodcast.utils.NetworkUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jem001 on 06/12/2017.
 */

public class GpodderCategoryFragment extends Fragment implements GpodderCategoryPodcastAdapter.ItemClickListener {
    @BindView(R.id.rv_gpodder_top)
    RecyclerView rv_gpodder;
    GpodderCategoryPodcastAdapter adapter;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar pb_loading_indicator;
    private Unbinder unbinder;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.gpodder_fragment, container, false);
        unbinder =ButterKnife.bind(this,view);
        pb_loading_indicator.setVisibility(View.VISIBLE);
        new GpodderCategoryTask().execute();
        adapter = new GpodderCategoryPodcastAdapter(this);

        RecyclerView.LayoutManager layoutManagerCat = new GridLayoutManager(getContext(), 2);
        rv_gpodder.setLayoutManager(layoutManagerCat);

        rv_gpodder.setHasFixedSize(true);
        rv_gpodder.setAdapter(adapter);
        return view;
    }



    @Override
    public void onItemClick(Category category) {
        Intent intent=new Intent(getContext(),FetchGpodderCategoryPodcastActivity.class);
        intent.putExtra(Constant.category_extra,Parcels.wrap(category));
        startActivity(intent);
    }


    public class GpodderCategoryTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(Constant.gpodder_category_podcast_url)
                    .build();
            Response response = null;
            String result = null;
            try {
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    result = response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    List<Category> list = NetworkUtils.getGpodderCategoryList(result);
                    if (!list.isEmpty()) {
                        adapter.swapAdapter(list);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            pb_loading_indicator.setVisibility(View.GONE);
        }

    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
