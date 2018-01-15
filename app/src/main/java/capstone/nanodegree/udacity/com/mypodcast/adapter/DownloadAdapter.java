package capstone.nanodegree.udacity.com.mypodcast.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.model.Episode;
import capstone.nanodegree.udacity.com.mypodcast.provider.MyPodcastContract;

/**
 * Created by jem001 on 08/12/2017.
 */

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.SubscriptionViewHolder> {
    List<Episode> list;

    public ItemClickListener mOnClickListener;
    private Interpolator mInterpolator;
    private int lastAnimPosition = -1;
    Context context;

    public DownloadAdapter(ItemClickListener mOnClickListener, Context context) {
        this.mOnClickListener = mOnClickListener;
        this.context = context;
    }

    @Override
    public SubscriptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SubscriptionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.episode_item, parent, false));
    }

    @Override
    public void onBindViewHolder(SubscriptionViewHolder holder, int position) {
        if (list != null && list.size() != 0) {
            Episode episode = list.get(position);
            holder.img_play.setVisibility(View.GONE);
            holder.img_download.setImageResource(R.drawable.ic_delete_for_ever);
            Cursor cursorPodcast = holder.img_podcast.getContext().getContentResolver().query(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI.buildUpon().appendPath(episode.getPodcastId()).build(), null, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID + " = ?", new String[]{episode.getPodcastId()}, null);
            if (cursorPodcast != null && cursorPodcast.getCount() != 0) {
                cursorPodcast.moveToFirst();
                String podcastImg = cursorPodcast.getString(cursorPodcast.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_COVER_IMG));
                Glide.with(holder.img_podcast.getContext()).load(podcastImg).into(holder.img_podcast);
                cursorPodcast.close();
            }
            holder.tvTitle.setText(episode.getTitle());

            String description = episode.getDescription();
            String finalText = description.substring(0, (description.length() <= 100 ? description.length() : 100));

            holder.tvDescription.setText(finalText.length() < 100 ? Html.fromHtml(finalText) : (Html.fromHtml(finalText) + "..."));

            if (position == 0) {
                holder.divider.setVisibility(View.GONE);
            } else {
                holder.divider.setVisibility(View.VISIBLE);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setAnimation(holder.itemView, position);
        }
    }


    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void swapAdapter(List<Episode> newList) {
        this.list = newList;
        notifyDataSetChanged();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mInterpolator = AnimationUtils.loadInterpolator(context, android.R.interpolator.linear_out_slow_in);
        }
    }

    public class SubscriptionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvDescription;
        View divider;
        ImageView img_podcast;
        ImageView img_play;
        ImageView img_download;

        public SubscriptionViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_podcast_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            divider = itemView.findViewById(R.id.divider);
            img_podcast = itemView.findViewById(R.id.img_podcast);
            img_play = itemView.findViewById(R.id.img_play);
            img_download = itemView.findViewById(R.id.img_download);
            itemView.setOnClickListener(this);
            img_download.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == img_download.getId()) {
                mOnClickListener.onItemDeleteClick(list.get(getAdapterPosition()), view);
            } else {
                mOnClickListener.onItemClick(list.get(getAdapterPosition()));
            }
        }
    }

    public void setAnimation(View holder, int position) {
        if (position > lastAnimPosition) {
            holder.setTranslationY((position + 1) * 1000);
            holder.setAlpha(0.85f);
            holder.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setInterpolator(mInterpolator)
                    .setDuration(1000L)
                    .start();
            lastAnimPosition = position;
        }
    }

    public interface ItemClickListener {
        void onItemClick(Episode episodeId);

        void onItemDeleteClick(Episode episodeId, View view);
    }


}
