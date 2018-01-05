package capstone.nanodegree.udacity.com.mypodcast.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jem001 on 03/01/2018.
 */

public class MyPodcastContract {
    public static final String PODCAST_CONTENT_AUTORITY="capstone.nanodegree.udacity.com.mypodcast.podcast";
    public static final String EPISODE_CONTENT_AUTORITY="capstone.nanodegree.udacity.com.mypodcast.episode";

    public static final Uri PODCAST_BASE_CONTENT_URI=Uri.parse("content://"+PODCAST_CONTENT_AUTORITY);
    public static final Uri EPISODE_BASE_CONTENT_URI=Uri.parse("content://"+EPISODE_CONTENT_AUTORITY);

    public static final String PATH_PODCAST="podcast";
    public static final String PATH_EPISODE="episode";

    public static final class MyPodcastEntry implements BaseColumns{

        public static final Uri PODCAST_CONTENT_URI=PODCAST_BASE_CONTENT_URI.buildUpon().appendPath(PATH_PODCAST).build();
        public static final Uri EPISODE_CONTENT_URI=EPISODE_BASE_CONTENT_URI.buildUpon().appendPath(PATH_EPISODE).build();


        //TABLES NAMES
        public static final String PODCAST_TABLE_NAME="podcast";
        public static final String EPISODE_TABLE_NAME="episode";

        //TABLES COLUMNS
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_PODCAST_ID = "podcast_id";
        public static final String COLUMN_PODCAST_PROVIDER = "provider";
        public static final String COLUMN_PODCAST_MAIN_SCREEEN = "main_screen";
        public static final String COLUMN_PODCAST_CATEGORY_FLAG = "category_flag";
        public static final String COLUMN_PODCAST_CATEGORY_NAME = "category_name";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_PODCAST_COVER_IMG = "cover_img";
        public static final String COLUMN_PODCAST_ARTISTE = "artiste";
        public static final String COLUMN_PODCAST_AUTHOR = "author";
        public static final String COLUMN_PODCAST_SUBSCRIBERS = "subscribers";
        public static final String COLUMN_PODCAST_FEED_URL = "feed_url";
        public static final String COLUMN_PODCAST_SUBSCRIBE_FLAG = "subscribe_flag";


        public static final String COLUMN_EPISODE_SIZE = "size";
        public static final String COLUMN_EPISODE_PUB_DATE = "pub_date";
        public static final String COLUMN_EPISODE_HISTORY_FLAG = "history_flag";
        public static final String COLUMN_EPISODE_DOWNLOAD_FLAG = "download_flag";
        public static final String COLUMN_EPISODE_DESCRIPTION = "description";
        public static final String COLUMN_EPISODE_DURATION = "duration";
        public static final String COLUMN_EPISODE_ID = "episode_id";
        public static final String COLUMN_EPISODE_LINK = "link";
        public static final String COLUMN_EPISODE_MP3_URL = "mp3_url";
        public static final String COLUMN_EPISODE_AUTHOR = "author";


        public static final String COLUMN_CATEGORY_USAGE = "usage";
        public static final String COLUMN_CATEGORY_TAG = "tag";

    }

}
