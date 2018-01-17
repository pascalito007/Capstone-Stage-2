package capstone.nanodegree.udacity.com.mypodcast.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.model.Episode;
import capstone.nanodegree.udacity.com.mypodcast.utils.AppUtils;

/**
 * Created by jem001 on 04/12/2017.
 */


public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ItuneEpisodeViewHolder> {
    List<Episode> list;
    public EpisodeClickListener mOnClickListener;
    public Context context;
    public static final String TAG = "EpisodeAdapter";
    String playList = "";
    private Interpolator mInterpolator;
    private int lastAnimPosition = -1;
    String img;

    public EpisodeAdapter(EpisodeClickListener clickListener, Context context, String play) {
        this.mOnClickListener = clickListener;
        this.context = context;
        this.playList = play;
    }

    @Override
    public ItuneEpisodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItuneEpisodeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.episode_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ItuneEpisodeViewHolder holder, int position) {
        if (position < 20) {
            Episode episode = list.get(position);
            holder.tvTitle.setText(episode.getTitle());
            String description = episode.getDescription();
            String finalText = description.substring(0, (description.length() <= 100 ? description.length() : 100));

            holder.tvDescription.setText(finalText.length() < 100 ? Html.fromHtml(finalText) : (Html.fromHtml(finalText) + "..."));
            if (position == 0) {
                holder.divider.setVisibility(View.GONE);
            } else {
                holder.divider.setVisibility(View.VISIBLE);
            }
            if (playList.isEmpty()) {
                String rootUrl = episode.getMp3FileUrl().substring(0, episode.getMp3FileUrl().indexOf("/", 7));
                String other = episode.getMp3FileUrl().substring(rootUrl.length());
                Log.d("other:", other.replaceAll("/", "_"));
                File file = AppUtils.getFileInInternalMemory(other.replaceAll("/", "_"));
                if (file != null) {
                    Log.d("fileavailable:", file.getName());
                    holder.img_play.setVisibility(View.VISIBLE);
                    holder.img_download.setVisibility(View.INVISIBLE);
                } else {
                    holder.img_play.setVisibility(View.INVISIBLE);
                    holder.img_download.setVisibility(View.VISIBLE);
                }
            } else if (playList.equals(context.getString(R.string.play_value))) {
                holder.img_play.setVisibility(View.GONE);
                holder.img_download.setVisibility(View.GONE);

            }
            Glide.with(holder.img_podcast.getContext()).load(img).into(holder.img_podcast);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setAnimation(holder.itemView, position);
            }
        }

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void swapAdapter(List<Episode> list, String coverImage) {
        this.list = list;
        img = coverImage;
        notifyDataSetChanged();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mInterpolator = AnimationUtils.loadInterpolator(context, android.R.interpolator.linear_out_slow_in);
        }
    }

    @Override
    public void onViewDetachedFromWindow(ItuneEpisodeViewHolder holder) {
        // super.onViewDetachedFromWindow(holder);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.itemView.clearAnimation();
        }

    }

    public class ItuneEpisodeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvDescription;
        View divider;
        ImageView img_download;
        ImageView img_play;
        ImageView img_podcast;
        //ImageView overflow;

        public ItuneEpisodeViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_podcast_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            divider = itemView.findViewById(R.id.divider);
            img_download = itemView.findViewById(R.id.img_download);
            img_play = itemView.findViewById(R.id.img_play);
            img_podcast = itemView.findViewById(R.id.img_podcast);
            //overflow = itemView.findViewById(R.id.overflow);
            itemView.setOnClickListener(this);
            img_download.setOnClickListener(this);
            //overflow.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view == img_download) {
                mOnClickListener.onDownloadItemClick(list.get(getAdapterPosition()));
            /*} else if (view == overflow) {
                mOnClickListener.onOverFlowItemClick(list.get(getAdapterPosition()),view);*/
            } else {
                mOnClickListener.onItemClick(list.get(getAdapterPosition()), view);
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


    public interface EpisodeClickListener {
        void onItemClick(Episode episode, View view);

        void onDownloadItemClick(Episode episode);
        //void onOverFlowItemClick(Episode episode,View view);
    }


}
