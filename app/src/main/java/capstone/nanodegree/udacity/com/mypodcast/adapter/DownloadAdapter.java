package capstone.nanodegree.udacity.com.mypodcast.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.provider.MyPodcastContract;

/**
 * Created by jem001 on 08/12/2017.
 */

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.SubscriptionViewHolder> {
    Cursor cursor;

    public ItemClickListener mOnClickListener;

    public DownloadAdapter(ItemClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public SubscriptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SubscriptionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.episode_item, parent, false));
    }

    @Override
    public void onBindViewHolder(SubscriptionViewHolder holder, int position) {
        if (cursor != null) {
            holder.img_play.setVisibility(View.GONE);
            holder.img_download.setImageResource(R.drawable.ic_delete_for_ever);
            cursor.moveToPosition(position);
            Cursor cursorPodcast = holder.img_podcast.getContext().getContentResolver().query(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI.buildUpon().appendPath(cursor.getString(cursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID))).build(), null, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID+" = ?", new String[]{cursor.getString(cursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID))}, null);
            if (cursorPodcast != null && cursorPodcast.getCount() != 0) {
                cursorPodcast.moveToFirst();
                String podcastImg = cursorPodcast.getString(cursorPodcast.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_COVER_IMG));
                Glide.with(holder.img_podcast.getContext()).load(podcastImg).into(holder.img_podcast);
                cursorPodcast.close();
            }
            holder.tvTitle.setText(cursor.getString(cursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_TITLE)));

            String description = cursor.getString(cursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_DESCRIPTION));
            String finalText = description.substring(0, (description.length() <= 100 ? description.length() : 100));

            holder.tvDescription.setText(finalText.length() < 100 ? Html.fromHtml(finalText) : (Html.fromHtml(finalText) + "..."));

            if (position == 0) {
                holder.divider.setVisibility(View.GONE);
            } else {
                holder.divider.setVisibility(View.VISIBLE);
            }
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
            cursor.moveToPosition(getAdapterPosition());
            if (view.getId() == img_download.getId()) {
                mOnClickListener.onItemDeleteClick(cursor.getLong(cursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_ID)),view);
            } else {
                mOnClickListener.onItemClick(cursor.getLong(cursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_ID)));
            }
        }
    }

    public interface ItemClickListener {
        void onItemClick(long episodeId);

        void onItemDeleteClick(long episodeId,View view);
    }
}
