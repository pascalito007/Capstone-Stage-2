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
import capstone.nanodegree.udacity.com.mypodcast.model.Podcast;

/**
 * Created by jem001 on 04/12/2017.
 */


public class ItunePodcastAdapter extends RecyclerView.Adapter<ItunePodcastAdapter.ItunePodcastViewHolder> {
    List<Podcast> list;

    public PodcastClickListener mOnClickListener;

    public ItunePodcastAdapter(List<Podcast> list,PodcastClickListener mOnClickListener) {
        this.list = list;
        this.mOnClickListener=mOnClickListener;
    }

    @Override
    public ItunePodcastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItunePodcastViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.itune_podcast_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ItunePodcastViewHolder holder, int position) {
        Podcast podcast = list.get(position);
        holder.title.setText(podcast.getTitle());
        Glide.with(holder.img.getContext()).load(podcast.getCoverImage()).into(holder.img);
    }

    @Override
    public int getItemCount() {
        return list.size() == 0 ? 0 : list.size();
    }

    public void swapAdapter(List<Podcast> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class ItunePodcastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        ImageView img;


        public ItunePodcastViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.podcast_img);
            title = itemView.findViewById(R.id.podcast_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("adapter position:", list.get(getAdapterPosition()).toString());
            mOnClickListener.onItemClick(list.get(getAdapterPosition()));
        }
    }

    public interface PodcastClickListener {
        void onItemClick(Podcast podcast);
    }
}
