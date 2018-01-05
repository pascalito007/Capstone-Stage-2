package capstone.nanodegree.udacity.com.mypodcast.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.model.GpodderTop;
import capstone.nanodegree.udacity.com.mypodcast.model.Podcast;

/**
 * Created by jem001 on 04/12/2017.
 */


public class GpodderTopPodcastAdapter extends RecyclerView.Adapter<GpodderTopPodcastAdapter.GpodderTopPodcastViewHolder> {
    List<Podcast> list;

    public ItemClickListener mOnClickListener;
    public GpodderTopPodcastAdapter(ItemClickListener listener) {
        this.list = list;
        this.mOnClickListener=listener;
    }

    @Override
    public GpodderTopPodcastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GpodderTopPodcastViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gpodder_top_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(GpodderTopPodcastViewHolder holder, int position) {
        if (!list.isEmpty()) {
            Podcast podcast = list.get(position);
            holder.tvTitle.setText(podcast.getTitle());
            holder.tvFeedUrl.setText(podcast.getFeedUrl());
            holder.tvFeedCount.setText(podcast.getFeedCount());
            Glide.with(holder.imgFeed.getContext()).load(podcast.getCoverImage()).into(holder.imgFeed);
        }

    }

    @Override
    public int getItemCount() {
        return list==null ? 0 : list.size();
    }

    public void swapAdapter(List<Podcast> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class GpodderTopPodcastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvTitle;
        TextView tvFeedUrl;
        TextView tvFeedCount;
        ImageView imgFeed;


        public GpodderTopPodcastViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_gpodder_top_title);
            tvFeedUrl = itemView.findViewById(R.id.tv_gpodder_top_feed_url);
            tvFeedCount = itemView.findViewById(R.id.tv_feed_count);
            imgFeed = itemView.findViewById(R.id.img_gpodder_top);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onItemClick(list.get(getAdapterPosition()));
        }
    }
    public interface ItemClickListener {
        void onItemClick(Podcast podcast);
    }
}
