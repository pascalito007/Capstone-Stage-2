package capstone.nanodegree.udacity.com.mypodcast.utils;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import capstone.nanodegree.udacity.com.mypodcast.model.Category;
import capstone.nanodegree.udacity.com.mypodcast.model.Episode;
import capstone.nanodegree.udacity.com.mypodcast.model.GpodderTop;
import capstone.nanodegree.udacity.com.mypodcast.model.Podcast;
import capstone.nanodegree.udacity.com.mypodcast.provider.MyPodcastContract;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jem001 on 04/12/2017.
 */

public class NetworkUtils {
    public static Podcast fromToplist(JSONObject json) throws JSONException {
        Podcast podcast = new Podcast();
        podcast.setTitle(json.getJSONObject("title").getString("label"));
        podcast.setArtiste(json.getJSONObject("im:artist").getString("label"));
        podcast.setPodcastId(json.getJSONObject("id").getJSONObject("attributes").getString("im:id"));
        String imageUrl = null;
        JSONArray images = json.getJSONArray("im:image");
        for (int i = 0; imageUrl == null && i < images.length(); i++) {
            JSONObject image = images.getJSONObject(i);
            String height = image.getJSONObject("attributes").getString("height");
            if (Integer.parseInt(height) >= 100) {
                imageUrl = image.getString("label");
            }
        }
        podcast.setCoverImage(imageUrl);
        String feedUrl = "https://itunes.apple.com/lookup?id=" +
                json.getJSONObject("id").getJSONObject("attributes").getString("im:id");
        podcast.setFeedUrl(feedUrl);
        return podcast;
    }

    public static List<Podcast> ituneTopListFromJson(String result) throws JSONException {

        JSONObject resultObject = new JSONObject(result);
        JSONArray jsonArray = resultObject.getJSONObject(Constant.feed).getJSONArray(Constant.entry);
        List<Podcast> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Podcast podcast = new Podcast();
            podcast.setTitle(jsonObject.getJSONObject("title").getString("label"));
            podcast.setArtiste(jsonObject.getJSONObject("im:artist").getString("label"));
            podcast.setPodcastId(jsonObject.getJSONObject("id").getJSONObject("attributes").getString("im:id"));
            podcast.setProvider("itunes");

            String imageUrl = null;
            JSONArray images = jsonObject.getJSONArray("im:image");
            for (int j = 0; imageUrl == null && j < images.length(); j++) {
                JSONObject image = images.getJSONObject(j);
                String height = image.getJSONObject("attributes").getString("height");
                if (Integer.parseInt(height) >= 100) {
                    imageUrl = image.getString("label");
                }
            }
            podcast.setCoverImage(imageUrl);
            String feedUrl = Constant.root_itune_podcast_url +
                    jsonObject.getJSONObject("id").getJSONObject("attributes").getString("im:id");
            podcast.setFeedUrl(feedUrl);
            list.add(podcast);
        }
        return list;
    }

    public static Episode fromFeedItems(JSONObject json) throws JSONException {
        Episode episode = new Episode();
        episode.setDescription(json.has("description") ? json.getString("description") : "");
        episode.setFullDescription(json.has("description") ? json.getString("description") : "");
        episode.setTitle(json.has("title") ? json.getString("title") : "");
        episode.setLink(json.has("link") ? json.getString("link").replaceAll("-", "").replaceAll(":", "").replaceAll("/", "") : (UUID.randomUUID().toString().replaceAll("-","")));
        Log.d("linkepisode:",json.getString("link")+"|"+episode.getLink());
        episode.setPubDate(json.has("pubDate") ? json.getString("pubDate") : "");
        episode.setDuration(json.has("itunes:duration")?json.getString("itunes:duration"):null);
        episode.setAuthor(json.has("itunes:author") ? json.getString("itunes:author") : "");
        episode.setMp3FileUrl(json.getJSONObject("enclosure").getString("url"));
        return episode;
    }

    public static Podcast fromTopFeeds(JSONObject json) throws JSONException {
        Podcast gp = new Podcast();
        gp.setFeedCount(json.getString("subscribers"));
        gp.setFeedUrl(json.getString("url"));
        gp.setCoverImage(json.getString("logo_url"));
        gp.setTitle(json.getString("title"));
        return gp;
    }

    public static GpodderTop fromCategoryFeeds(JSONObject json) throws JSONException {
        GpodderTop gp = new GpodderTop();
        gp.setFeedCount(json.getString("usage"));
        gp.setTitle(json.getString("title"));
        return gp;
    }

    public static List<Category> getGpodderCategoryList(String result) throws JSONException {
        JSONArray jsonArray = new JSONArray(result);
        List<Category> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Category category = new Category();
            category.setTitle(jsonObject.getString("title"));
            category.setUsage(jsonObject.getInt("usage"));
            category.setTag(jsonObject.getString("tag"));
            list.add(category);
        }
        return list;
    }

    public static String okHttpGetRequestStringResult(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream okHttpGetRequestInputStreamResult(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().byteStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Episode> getEpisodeListFromFeed(String result, String podcastId) throws JSONException {
        List<Episode> list = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(result);
        JSONArray jsonArray = jsonObject.getJSONObject("rss").getJSONObject("channel").getJSONArray("item");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            Episode episode = NetworkUtils.fromFeedItems(object);
            episode.setPodcastId(podcastId);
            Log.d("podcastepisode:", episode.toString());
            list.add(episode);
        }
        return list;
    }

    public static ContentValues[] getEpisodeContentValuesFromFeed(String result) throws JSONException {
        JSONObject jsonObject = new JSONObject(result);
        JSONArray jsonArray = jsonObject.getJSONObject("rss").getJSONObject("channel").getJSONArray("item");
        ContentValues[] contentValues = new ContentValues[jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            ContentValues contentValue = new ContentValues();
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_TITLE, object.getString("title"));
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_DESCRIPTION, object.getString("description"));
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_PUB_DATE, object.getString("pubDate"));
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_DURATION, object.getString("itunes:duration"));
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_AUTHOR, object.getString("itunes:author"));
            contentValues[i] = contentValue;
        }
        return contentValues;
    }

    public static ContentValues[] getPodcastGpodderTopListContentValues(String result) throws JSONException {

        JSONArray jsonArray = new JSONArray(result);
        ContentValues[] contentValues = new ContentValues[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            ContentValues contentValue = new ContentValues();
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_TITLE, jsonObject.getString("title"));
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_FEED_URL, jsonObject.getString("url"));
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_SUBSCRIBERS, jsonObject.getString("subscribers"));
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_COVER_IMG, jsonObject.getString("logo_url"));
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_PROVIDER, "gpodder.net");
            if (jsonObject.getString("url") == null || jsonObject.getString("url").isEmpty())
                contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID, UUID.randomUUID().toString().replaceAll("-",""));
            else
                contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID, jsonObject.getString("url"));


            contentValues[i] = contentValue;
        }
        return contentValues;
    }

    public static List<Podcast> gPodderTopListFromJson(String result) throws JSONException {

        JSONArray jsonArray = new JSONArray(result);
        List<Podcast> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Podcast podcast = new Podcast();
            podcast.setTitle(jsonObject.getString("title"));
            podcast.setFeedUrl(jsonObject.getString("url"));
            podcast.setSubscribers(jsonObject.getString("subscribers"));
            podcast.setCoverImage(jsonObject.getString("logo_url"));
            podcast.setProvider("gpodder.net");
            podcast.setPodcastId(jsonObject.getString("url"));
            if (podcast.getPodcastId() == null || podcast.getPodcastId().isEmpty())
                podcast.setPodcastId(UUID.randomUUID().toString().replaceAll("-",""));
            else

                list.add(podcast);
        }
        return list;
    }

    public static ContentValues[] gPodderContentValueListFromArrayList(List<Podcast> list, String mainSreenTag, String categoryFlag) throws JSONException {

        ContentValues[] contentValues = new ContentValues[list.size()];
        for (int i = 0; i < list.size(); i++) {
            Podcast podcast = list.get(i);
            ContentValues contentValue = new ContentValues();
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_TITLE, podcast.getTitle());
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_FEED_URL, podcast.getFeedUrl());
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_SUBSCRIBERS, podcast.getSubscribers());
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_COVER_IMG, podcast.getCoverImage());
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_PROVIDER, "gpodder.net");
            //if (podcast.getPodcastId() == null || podcast.getPodcastId().isEmpty())
                contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID, UUID.randomUUID().toString().replaceAll("-",""));
            //else
                //contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID, podcast.getPodcastId().replaceAll(".", "").replaceAll("-", "").replaceAll(":", "").replaceAll("/", ""));
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_MAIN_SCREEEN, mainSreenTag);
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_CATEGORY_FLAG, categoryFlag);
            contentValues[i] = contentValue;
        }
        return contentValues;
    }

    public static ContentValues[] getEpisodeContentValuesFromArrayList(List<Episode> list, String podcastId) {

        ContentValues[] contentValues = new ContentValues[list.size()];
        for (int i = 0; i < list.size(); i++) {
            Episode episode = list.get(i);
            ContentValues contentValue = new ContentValues();
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_TITLE, episode.getTitle());
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_LINK, episode.getLink());
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_DESCRIPTION, episode.getFullDescription());
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_EPISODE_MP3_URL, episode.getMp3FileUrl());
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID, podcastId);
            Log.d("episodecontentvalues:", contentValue + "");
            contentValues[i] = contentValue;
        }
        return contentValues;
    }


    public static ContentValues[] ituneContentValueListFromArrayList(List<Podcast> list) throws JSONException {
        ContentValues[] contentValues = new ContentValues[list.size()];
        for (int i = 0; i < list.size(); i++) {
            Podcast podcast = list.get(i);
            ContentValues contentValue = new ContentValues();
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_TITLE, podcast.getTitle());
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ARTISTE, podcast.getArtiste());
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID, podcast.getPodcastId());
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_PROVIDER, "itunes");

            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_COVER_IMG, podcast.getCoverImage());
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_FEED_URL, podcast.getFeedUrl());
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_CATEGORY_FLAG, "No");
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_MAIN_SCREEEN, "Yes");
            contentValues[i] = contentValue;
        }
        return contentValues;
    }


    public static ContentValues[] getGpodderCategoryListContentValues(String result) throws JSONException {

        JSONArray jsonArray = new JSONArray(result);
        ContentValues[] contentValues = new ContentValues[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            ContentValues contentValue = new ContentValues();
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_TITLE, jsonObject.getString("title"));
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_CATEGORY_USAGE, jsonObject.getString("usage"));
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_CATEGORY_TAG, jsonObject.getString("tag"));
            contentValues[i] = contentValue;
        }
        return contentValues;
    }

    public static ContentValues[] getPodcastItuneTopListContentValues(String result) throws JSONException {
        JSONObject resultObject = new JSONObject(result);
        JSONArray jsonArray = resultObject.getJSONObject(Constant.feed).getJSONArray(Constant.entry);
        ContentValues[] contentValues = new ContentValues[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            ContentValues contentValue = new ContentValues();
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_TITLE, jsonObject.getJSONObject("title").getString("label"));
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ARTISTE, jsonObject.getJSONObject("im:artist").getString("label"));
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_ID, jsonObject.getJSONObject("id").getJSONObject("attributes").getString("im:id"));
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_PROVIDER, "itunes");

            String imageUrl = null;
            JSONArray images = jsonObject.getJSONArray("im:image");
            for (int j = 0; imageUrl == null && j < images.length(); j++) {
                JSONObject image = images.getJSONObject(j);
                String height = image.getJSONObject("attributes").getString("height");
                if (Integer.parseInt(height) >= 100) {
                    imageUrl = image.getString("label");
                }
            }
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_COVER_IMG, imageUrl);
            String feedUrl = Constant.root_itune_podcast_url +
                    jsonObject.getJSONObject("id").getJSONObject("attributes").getString("im:id");
            contentValue.put(MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_FEED_URL, feedUrl);
            contentValues[i] = contentValue;
        }
        return contentValues;
    }

}
