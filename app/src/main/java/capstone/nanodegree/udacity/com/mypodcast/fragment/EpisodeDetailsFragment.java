package capstone.nanodegree.udacity.com.mypodcast.fragment;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.provider.MyPodcastContract;
import capstone.nanodegree.udacity.com.mypodcast.utils.Constant;

/**
 * Created by jem001 on 04/12/2017.
 */
public class EpisodeDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    Long ARG_ITEM_ID;
    long mItemId;
    Cursor mCursor;
    private static final int EPISODE_LOAD_ID = 66;
    @BindView(R.id.photo)
    ImageView backImage;
    @BindView(R.id.article_body)
    TextView body;
    @BindView(R.id.tvSeeMore)
    TextView readMore;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar loading_indicator;
    @BindView(R.id.article_title)
    TextView title;
    @BindView(R.id.article_byline)
    TextView subTitle;
    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_episode_detail, container, false);
        unbinder =ButterKnife.bind(this,view);
        Bundle bundle=getArguments();
        ARG_ITEM_ID=bundle.getLong(Constant.ARG_ITEM_ID);
        if (ARG_ITEM_ID != null) {
            mItemId = ARG_ITEM_ID;
            getLoaderManager().initLoader(EPISODE_LOAD_ID, null, this);
        }
        Cursor cursor = getActivity().getContentResolver().query(MyPodcastContract.MyPodcastEntry.EPISODE_CONTENT_URI.buildUpon().appendPath(mItemId+"").build(), null, MyPodcastContract.MyPodcastEntry.COLUMN_ID+" = ?", new String[]{mItemId+""}, null);
        if (cursor != null) {
            cursor.moveToFirst();
            cursor.close();
        }
        return view;
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case EPISODE_LOAD_ID:
                return new CursorLoader(getActivity(), MyPodcastContract.MyPodcastEntry.EPISODE_CONTENT_URI.buildUpon().appendPath(mItemId+"").build(), null, MyPodcastContract.MyPodcastEntry.COLUMN_ID+" = ?", new String[]{mItemId+""}, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!isAdded()) {
            if (data != null) {
                data.close();
            }
            return;
        }
        mCursor = data;
        if (mCursor != null && !mCursor.moveToFirst()) {
            mCursor.close();
            mCursor = null;
        }
        if (mCursor != null && mCursor.getCount() != 0) {
            mCursor.moveToFirst();
            String podcastId = mCursor.getString(mCursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID));
            Cursor cursor = getActivity().getContentResolver().query(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI.buildUpon().appendPath(podcastId).build(), null, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID+" = ?", new String[]{podcastId}, null);
            if (cursor != null && cursor.getCount() != 0) {
                cursor.moveToFirst();
                //Glide.with(this).load(cursor.getString(cursor.getColumnIndex(PodcastContract.COLUMN_PODCAST_COVER_IMG))).apply(RequestOptions.bitmapTransform(new BlurTransformation(200))).into(backImage);
                Glide.with(this).load(cursor.getString(cursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_COVER_IMG))).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        loading_indicator.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        loading_indicator.setVisibility(View.GONE);
                        return false;
                    }
                }).into(backImage);


                cursor.close();
            }

            String title = mCursor.getString(mCursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_TITLE));
            this.title.setText(title);
            this.subTitle.setText(title);

            final CharSequence detail = Html.fromHtml(mCursor.getString(mCursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_DESCRIPTION)).replaceAll("(\r\n|\n)", "<br />"));
            Log.d("detailview:", detail.toString());
            body.setText(detail.toString().substring(0, (detail.length() <= 150 ? detail.length() : 150)));
            readMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    body.setText(detail.toString());
                    view.setVisibility(View.INVISIBLE);
                }
            });

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // mCursor.close();
        // mCursor = null;
    }
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
