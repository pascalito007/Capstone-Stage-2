package capstone.nanodegree.udacity.com.mypodcast.adapter;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
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

public class MainFragmentTopListAdapter extends RecyclerView.Adapter<MainFragmentTopListAdapter.PodcastViewHolder> {
    Cursor cursor;

    public PodcastClickListener mOnClickListener;

    public MainFragmentTopListAdapter(PodcastClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public PodcastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PodcastViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_top_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PodcastViewHolder holder, int position) {
        if (cursor != null) {
            cursor.moveToPosition(position);

            String title = cursor.getString(cursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_TITLE));
            if (title != null) {
                String titleFinal = title.substring(0, (title.length() <= 20 ? title.length() : 20));
                holder.title.setText(titleFinal);
            }
            String subtitle = cursor.getString(cursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ARTISTE));
            if (subtitle != null) {
                String finalText = subtitle.substring(0, (subtitle.length() <= 50 ? subtitle.length() : 50));
                holder.subTitle.setText(finalText);
            }

            if (position == 0) {
                holder.divider.setVisibility(View.GONE);
            } else {
                holder.divider.setVisibility(View.VISIBLE);
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
            /*holder.imgSubscribePodcast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.imgSubscribePodcast.setImageResource(R.drawable.ic_subscribe_notification_off);
                }
            });*/
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
        //ImageView imgSubscribePodcast;
        TextView title;
        TextView subTitle;
        TextView subscribers;
        View divider;
        ProgressBar loading_indicator;


        public PodcastViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_podcast);
            //imgSubscribePodcast = itemView.findViewById(R.id.img_subscribe_podcast);
            title = itemView.findViewById(R.id.tv_podcast_title);
            subTitle = itemView.findViewById(R.id.tv_podcast_subtitle);
            divider = itemView.findViewById(R.id.divider);
            subscribers = itemView.findViewById(R.id.tv_subscribers);
            loading_indicator = itemView.findViewById(R.id.pb_loading_indicator);
            itemView.setOnClickListener(this);
            //img.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            cursor.moveToPosition(getAdapterPosition());
            mOnClickListener.onGpodderItemClickListener(Podcast.getPodcastFromCursor(cursor),view);
        }
    }

    public interface PodcastClickListener {
        void onGpodderItemClickListener(Podcast podcast,View view);
    }
}
