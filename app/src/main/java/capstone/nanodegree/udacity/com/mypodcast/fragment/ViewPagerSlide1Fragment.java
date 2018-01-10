package capstone.nanodegree.udacity.com.mypodcast.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import capstone.nanodegree.udacity.com.mypodcast.R;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by jem001 on 06/12/2017.
 */

public class ViewPagerSlide1Fragment extends Fragment {
    @BindView(R.id.podcast_img)
    ImageView imageView;
    private Unbinder unbinder;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.view_pager_slide, container, false);
        unbinder =ButterKnife.bind(this,view);
        Glide.with(this)
                .load("http://is5.mzstatic.com/image/thumb/Podcasts71/v4/8f/38/15/8f381588-85e3-c183-4e67-6e9814c8d808/mza_6236236708462037843.jpg/170x170bb-85.jpg")
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(150)))
                .into(imageView);
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
