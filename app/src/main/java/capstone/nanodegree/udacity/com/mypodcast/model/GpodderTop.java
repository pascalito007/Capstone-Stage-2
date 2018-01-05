package capstone.nanodegree.udacity.com.mypodcast.model;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by jem001 on 06/12/2017.
 */
@Parcel
public class GpodderTop implements Serializable {
     String title;
     String feedUrl;
     String img;
     String feedCount;

    public GpodderTop() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getFeedCount() {
        return feedCount;
    }

    public void setFeedCount(String feedCount) {
        this.feedCount = feedCount;
    }

    @Override
    public String toString() {
        return "GpodderTop{" +
                "title='" + title + '\'' +
                ", feedUrl='" + feedUrl + '\'' +
                ", img='" + img + '\'' +
                ", feedCount='" + feedCount + '\'' +
                '}';
    }
}
