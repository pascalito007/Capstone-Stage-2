package capstone.nanodegree.udacity.com.mypodcast.adapter;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
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

import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.model.Podcast;
import capstone.nanodegree.udacity.com.mypodcast.provider.MyPodcastContract;

/**
 * Created by jem001 on 08/12/2017.
 */

public class MainCategory1FragmentAdapter extends RecyclerView.Adapter<MainCategory1FragmentAdapter.PodcastViewHolder> {
    Cursor cursor;

    public PodcastClickListener mOnClickListener;

    public MainCategory1FragmentAdapter(PodcastClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public PodcastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PodcastViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PodcastViewHolder holder, int position) {
        if (cursor != null) {
            cursor.moveToPosition(position);

            String title = cursor.getString(cursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_TITLE));
            if (title != null) {
                String titleFinal = title.substring(0, (title.length() <= 50 ? title.length() : 50));
                holder.title.setText(titleFinal);
            }
            String subtitle = cursor.getString(cursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ARTISTE));
            if (subtitle != null) {
                String finalText = subtitle.substring(0, (subtitle.length() <= 50 ? subtitle.length() : 50));
                holder.subTitle.setText(finalText);
            }

            String podcast = cursor.getString(cursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_COVER_IMG));
            Glide.with(holder.img.getContext()).load(podcast).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    holder.loading_indicator.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.loading_indicator.setVisibility(View.GONE);
                    return false;
                }
            }).into(holder.img);
        }
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    public void swapAdapter(Cursor newCursor) {
        this.cursor = newCursor;
        notifyDataSetChanged();
    }

    public class PodcastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img;
        TextView title;
        TextView subTitle;
        ProgressBar loading_indicator;


        public PodcastViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_podcast);
            title = itemView.findViewById(R.id.tv_podcast_title);
            subTitle = itemView.findViewById(R.id.tv_podcast_subtitle);
            loading_indicator = itemView.findViewById(R.id.pb_loading_indicator);
            itemView.setOnClickListener(this);
            img.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            cursor.moveToPosition(getAdapterPosition());
            Log.d("podcastcontentvalue:",Podcast.getPodcastFromCursor(cursor)+"");
            mOnClickListener.onCategory1ItemClick(Podcast.getPodcastFromCursor(cursor),view);
        }
    }

    public interface PodcastClickListener {
        void onCategory1ItemClick(Podcast podcast,View view);
    }
}
