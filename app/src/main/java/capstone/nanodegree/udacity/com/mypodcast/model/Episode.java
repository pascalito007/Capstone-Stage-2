package capstone.nanodegree.udacity.com.mypodcast.model;

import org.parceler.Parcel;

import java.io.Serializable;

/**
 * Created by jem001 on 05/12/2017.
 */

@Parcel
public class Episode {
     String title;

     String feedIdentifier;
     String podcastId;

     String link;
     String description;
     String language;

     String author;

     String lastUpdate;

     String paymentLink;

     String type;
     String pubDate;
     String duration;
     String mp3FileUrl;
     String fullDescription;

    public Episode() {
    }

    public String getPodcastId() {
        return podcastId;
    }

    public void setPodcastId(String podcastId) {
        this.podcastId = podcastId;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public String getMp3FileUrl() {
        return mp3FileUrl;
    }

    public void setMp3FileUrl(String mp3FileUrl) {
        this.mp3FileUrl = mp3FileUrl;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFeedIdentifier() {
        return feedIdentifier;
    }

    public void setFeedIdentifier(String feedIdentifier) {
        this.feedIdentifier = feedIdentifier;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getPaymentLink() {
        return paymentLink;
    }

    public void setPaymentLink(String paymentLink) {
        this.paymentLink = paymentLink;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Episode{" +
                "title='" + title + '\'' +
                ", feedIdentifier='" + feedIdentifier + '\'' +
                ", podcastId='" + podcastId + '\'' +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                ", language='" + language + '\'' +
                ", author='" + author + '\'' +
                ", lastUpdate='" + lastUpdate + '\'' +
                ", paymentLink='" + paymentLink + '\'' +
                ", type='" + type + '\'' +
                ", pubDate='" + pubDate + '\'' +
                ", duration='" + duration + '\'' +
                ", mp3FileUrl='" + mp3FileUrl + '\'' +
                ", fullDescription='" + fullDescription + '\'' +
                '}';
    }
}
