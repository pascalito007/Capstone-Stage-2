package capstone.nanodegree.udacity.com.mypodcast.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.model.Category;
import capstone.nanodegree.udacity.com.mypodcast.model.GpodderTop;

/**
 * Created by jem001 on 04/12/2017.
 */


public class GpodderCategoryPodcastAdapter extends RecyclerView.Adapter<GpodderCategoryPodcastAdapter.GpodderCategoryPodcastViewHolder> {
    List<Category> list;
    public ItemClickListener mOnClickListener;


    public GpodderCategoryPodcastAdapter(ItemClickListener listener) {
        this.mOnClickListener=listener;
    }

    @Override
    public GpodderCategoryPodcastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GpodderCategoryPodcastViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gpodder_category_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(GpodderCategoryPodcastViewHolder holder, int position) {
        Category podcast = list.get(position);
        holder.tvTitle.setText(podcast.getTitle());
        holder.tvFeedCount.setText(podcast.getUsage());

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void swapAdapter(List<Category> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class GpodderCategoryPodcastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvFeedCount;


        public GpodderCategoryPodcastViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_gpodder_top_title);
            tvFeedCount = itemView.findViewById(R.id.tv_feed_count);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onItemClick(list.get(getAdapterPosition()));
        }
    }
    public interface ItemClickListener {
        void onItemClick(Category category);
    }
}
