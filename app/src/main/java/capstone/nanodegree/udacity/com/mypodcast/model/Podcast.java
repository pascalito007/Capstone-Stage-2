package capstone.nanodegree.udacity.com.mypodcast.model;

import android.database.Cursor;

import org.parceler.Parcel;

import capstone.nanodegree.udacity.com.mypodcast.provider.MyPodcastContract;

/**
 * Created by jem001 on 04/12/2017.
 */

@Parcel
public class Podcast {
    String podcastId;
    String provider;
    String title;
    String artiste;
    String coverImage;
    String author;
    String subscribers;
    String feedCount;
    String feedUrl;
    Integer subscribeFlag;


    public Podcast() {
    }

    public String getFeedCount() {
        return feedCount;
    }

    public void setFeedCount(String feedCount) {
        this.feedCount = feedCount;
    }

    public Integer getSubscribeFlag() {
        return subscribeFlag;
    }

    public void setSubscribeFlag(Integer subscribeFlag) {
        this.subscribeFlag = subscribeFlag;
    }

    public String getArtiste() {
        return artiste;
    }

    public void setArtiste(String artiste) {
        this.artiste = artiste;
    }

    public String getPodcastId() {
        return podcastId;
    }

    public void setPodcastId(String podcastId) {
        this.podcastId = podcastId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(String subscribers) {
        this.subscribers = subscribers;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }


    public static Podcast getPodcastFromCursor(Cursor cursor) {

        Podcast podcast = new Podcast();

        podcast.setFeedUrl(cursor.getString(cursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_FEED_URL)));
        podcast.setTitle(cursor.getString(cursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_TITLE)));
        podcast.setCoverImage(cursor.getString(cursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_COVER_IMG)));
        podcast.setPodcastId(cursor.getString(cursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID)));
        podcast.setProvider(cursor.getString(cursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_PROVIDER)));
        podcast.setSubscribeFlag(cursor.getInt(cursor.getColumnIndex(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_SUBSCRIBE_FLAG)));
        return podcast;
    }

    @Override
    public String toString() {
        return "Podcast{" +
                "podcastId='" + podcastId + '\'' +
                ", provider='" + provider + '\'' +
                ", title='" + title + '\'' +
                ", artiste='" + artiste + '\'' +
                ", coverImage='" + coverImage + '\'' +
                ", author='" + author + '\'' +
                ", subscribers='" + subscribers + '\'' +
                ", feedCount='" + feedCount + '\'' +
                ", feedUrl='" + feedUrl + '\'' +
                ", subscribeFlag=" + subscribeFlag +
                '}';
    }
}
