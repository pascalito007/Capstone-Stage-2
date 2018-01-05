package capstone.nanodegree.udacity.com.mypodcast.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.model.Podcast;
import capstone.nanodegree.udacity.com.mypodcast.provider.MyPodcastContract;

/**
 * Created by jem001 on 08/12/2017.
 */

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder> {
    Cursor cursor;

    public PodcastClickListener mOnClickListener;

    public SubscriptionAdapter(PodcastClickListener mOnClickListener) {
        //this.list = list;
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public SubscriptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SubscriptionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.subscription_item, parent, false));
    }

    @Override
    public void onBindViewHolder(SubscriptionViewHolder holder, int position) {
        if (cursor!=null) {
            cursor.moveToPosition(position);
            String podcast = cursor.getString(cursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_COVER_IMG));
            Glide.with(holder.img.getContext()).load(podcast).into(holder.img);
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

    public class SubscriptionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img;


        public SubscriptionViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_subscribe_podcast);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            cursor.moveToPosition(getAdapterPosition());
            mOnClickListener.onItemClick(Podcast.getPodcastFromCursor(cursor));
        }
    }

    public interface PodcastClickListener {
        void onItemClick(Podcast podcast);
    }
}
