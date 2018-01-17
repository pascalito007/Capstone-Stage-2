package capstone.nanodegree.udacity.com.mypodcast.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import capstone.nanodegree.udacity.com.mypodcast.model.Podcast;

/**
 * Created by jem001 on 04/12/2017.
 */


public class ItunePodcastAdapter extends RecyclerView.Adapter<ItunePodcastAdapter.ItunePodcastViewHolder> {
    List<Podcast> list;
    private Interpolator mInterpolator;
    private int lastAnimPosition = -1;
    Context context;

    public PodcastClickListener mOnClickListener;

    public ItunePodcastAdapter(List<Podcast> list,PodcastClickListener mOnClickListener,Context context) {
        this.list = list;
        this.mOnClickListener=mOnClickListener;
        this.context=context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mInterpolator = AnimationUtils.loadInterpolator(context, android.R.interpolator.linear_out_slow_in);
        }

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setAnimation(holder.itemView, position);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
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
            img = itemView.findViewById(R.id.img_podcast);
            title = itemView.findViewById(R.id.podcast_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("adapter position:", list.get(getAdapterPosition()).toString());
            mOnClickListener.onItemClick(list.get(getAdapterPosition()));
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

    @Override
    public void onViewDetachedFromWindow(ItunePodcastViewHolder holder) {
        // super.onViewDetachedFromWindow(holder);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.itemView.clearAnimation();
        }

    }

    public interface PodcastClickListener {
        void onItemClick(Podcast podcast);
    }
}
