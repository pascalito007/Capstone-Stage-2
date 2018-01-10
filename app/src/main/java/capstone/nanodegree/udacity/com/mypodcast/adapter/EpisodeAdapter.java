package capstone.nanodegree.udacity.com.mypodcast.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
        } else if (playList.equals("play")) {
            holder.img_play.setVisibility(View.GONE);
            holder.img_download.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void swapAdapter(List<Episode> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class ItuneEpisodeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvDescription;
        View divider;
        ImageView img_download;
        ImageView img_play;

        public ItuneEpisodeViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_podcast_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            divider = itemView.findViewById(R.id.divider);
            img_download = itemView.findViewById(R.id.img_download);
            img_play = itemView.findViewById(R.id.img_play);
            itemView.setOnClickListener(this);
            img_download.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view == img_download) {
                mOnClickListener.onDownloadItemClick(list.get(getAdapterPosition()));
            } else {
                mOnClickListener.onItemClick(list.get(getAdapterPosition()), view);
            }
        }
    }

    public interface EpisodeClickListener {
        void onItemClick(Episode podcast, View view);

        void onDownloadItemClick(Episode podcast);
    }


}
