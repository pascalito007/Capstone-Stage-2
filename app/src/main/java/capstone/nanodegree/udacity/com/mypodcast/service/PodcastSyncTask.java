package capstone.nanodegree.udacity.com.mypodcast.service;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.eventbus.DataEvent;
import capstone.nanodegree.udacity.com.mypodcast.model.Category;
import capstone.nanodegree.udacity.com.mypodcast.model.Podcast;
import capstone.nanodegree.udacity.com.mypodcast.provider.MyPodcastContract;
import capstone.nanodegree.udacity.com.mypodcast.utils.AppUtils;
import capstone.nanodegree.udacity.com.mypodcast.utils.Constant;
import capstone.nanodegree.udacity.com.mypodcast.utils.NetworkUtils;

/**
 * Created by jem001 on 11/12/2017.
 */

public class PodcastSyncTask {

    synchronized public static void syncMainScreenPodcast(Context context) {
        try {
            SharedPreferences sp2 = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor2 = sp2.edit();


            //Retrieve topList from iTune (for Recommendations)

            String iTuneTopListStringJson = NetworkUtils.okHttpGetRequestStringResult(Constant.itune_root_url + Locale.getDefault().getLanguage() + Constant.itune_top_list_url1 + sp2.getInt(context.getString(R.string.pref_top_podcast_key), context.getResources().getInteger(R.integer.pref_top_podcast_default)) + Constant.itune_top_list_url2);
            List<Podcast> ituneList = null;
            if (iTuneTopListStringJson != null)
                ituneList = NetworkUtils.ituneTopListFromJson(iTuneTopListStringJson);
            if (ituneList != null && ituneList.size() != 0) {
                List<Podcast> randList = new ArrayList<>();
                for (int i = 0; i < ituneList.size(); i++) {
                    Podcast podcast = ituneList.get(new Random().nextInt(ituneList.size()));
                    if (!podcast.getCoverImage().equals("null")) {
                        randList.add(podcast);

                        AppUtils.removeTwiceValue(randList, podcast);
                        Log.d("podcastcatrecommends:", podcast.toString());
                    }

                    if (randList.size() == 6) {
                        break;
                    }
                }
                if (!randList.isEmpty()) {
                    ContentValues[] values = NetworkUtils.ituneContentValueListFromArrayList(randList);
                    if (values != null && values.length != 0) {
                        try {
                            context.getContentResolver().delete(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_PROVIDER + " = ? and " + MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_MAIN_SCREEEN + " = ? and " + MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_CATEGORY_FLAG + " = ?", new String[]{"itunes", "Yes", "No"});
                        } catch (Exception e) {

                        }
                        context.getContentResolver().bulkInsert(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI, values);
                    }
                    editor2.putString(Constant.recommandation, context.getString(R.string.recommend_label));
                    editor2.apply();
                }
            }


            //Retrieve topList from gPodder (for Top List)
            String gPodderTopListStringJson = NetworkUtils.okHttpGetRequestStringResult(Constant.gpodder_top_podcast_url + sp2.getInt(context.getString(R.string.pref_top_podcast_key), context.getResources().getInteger(R.integer.pref_top_podcast_default)) + ".json");
            List<Podcast> gpodderList = null;
            if (gPodderTopListStringJson != null)
                gpodderList = NetworkUtils.gPodderTopListFromJson(gPodderTopListStringJson);

            List<Podcast> randList1 = new ArrayList<>();
            if (gpodderList != null && !gpodderList.isEmpty()) {
                for (int i = 0; i < gpodderList.size(); i++) {
                    Podcast podcast = gpodderList.get(new Random().nextInt(gpodderList.size()));
                    if (!podcast.getCoverImage().equals("null")) {
                        randList1.add(podcast);
                        AppUtils.removeTwiceValue(randList1, podcast);
                        Log.d("podcastcattoplist:", podcast.toString());
                    }
                    if (randList1.size() == 6) {
                        break;
                    }
                }
                if (!randList1.isEmpty()) {
                    ContentValues[] values = NetworkUtils.gPodderContentValueListFromArrayList(randList1, "Yes", "No");
                    if (values != null && values.length != 0) {
                        try {
                            context.getContentResolver().delete(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_PROVIDER + " = ? and " + MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_MAIN_SCREEEN + " = ? and " + MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_CATEGORY_FLAG + " = ?", new String[]{"gpodder.net", "Yes", "No"});
                        } catch (Exception e) {

                        }

                        context.getContentResolver().bulkInsert(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI, values);
                    }
                    editor2.putString(Constant.toplist, context.getString(R.string.top_pod_label));
                    editor2.apply();
                }
            }


            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sp.edit();
            //Retrieve categories
            String gPodderCategoriesStringJson = NetworkUtils.okHttpGetRequestStringResult(Constant.gpodder_category_podcast_url + sp.getInt(context.getString(R.string.pref_top_podcast_key), context.getResources().getInteger(R.integer.pref_top_podcast_default)) + ".json");
            List<Category> list = null;
            if (gPodderCategoriesStringJson != null)
                list = NetworkUtils.getGpodderCategoryList(gPodderCategoriesStringJson);
            //Get random 2 categories
            if (list != null && !list.isEmpty()) {
                //First category
                Category category1 = list.get(new Random().nextInt(Stream.of(list).filter(e -> e.getUsage() >= 6).collect(Collectors.toList()).size()));
                Log.d("podcastcat11:", category1.toString());
                editor.putString(Constant.category1, category1.getTitle());
                editor.apply();
                String category1PodcastListJson = NetworkUtils.okHttpGetRequestStringResult(Constant.root_gpodder_feed_url_part + category1.getTag() + "/" + sp.getInt(context.getString(R.string.pref_top_podcast_key), context.getResources().getInteger(R.integer.pref_top_podcast_default)) + ".json");
                List<Podcast> category1PodcastList = null;
                if (category1PodcastListJson != null)
                    category1PodcastList = NetworkUtils.gPodderTopListFromJson(category1PodcastListJson);
                Log.d("category1size:", category1PodcastList.size() + "");
                if (category1PodcastList != null && !category1PodcastList.isEmpty()) {
                    List<Podcast> randList = new ArrayList<>();
                    for (int i = 0; i < category1PodcastList.size(); i++) {
                        Podcast podcast = category1PodcastList.get(new Random().nextInt(category1PodcastList.size()));
                        if (!podcast.getCoverImage().equals("null")) {
                            randList.add(podcast);
                            AppUtils.removeTwiceValue(randList, podcast);
                            Log.d("podcastcat1:", podcast.toString() + "|tag:" + category1.getTag());
                        }
                        if (randList.size() == 6) {
                            break;
                        }
                    }
                    if (!randList.isEmpty()) {
                        ContentValues[] values = NetworkUtils.gPodderContentValueListFromArrayList(randList, "Yes", "Yes 1");
                        try {
                            context.getContentResolver().delete(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_PROVIDER + " = ? and " + MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_MAIN_SCREEEN + " = ? and " + MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_CATEGORY_FLAG + " = ?", new String[]{"gpodder.net", "Yes", "Yes 1"});
                        } catch (Exception e) {
                        }
                        context.getContentResolver().bulkInsert(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI, values);
                    }
                }

                //Second category

                Category category2 = list.get(new Random().nextInt(Stream.of(list).filter(e -> e.getUsage() >= 6).collect(Collectors.toList()).size()));
                Log.d("podcastcat22:", category2.toString());
                editor.putString(Constant.category2, category2.getTitle());
                editor.apply();
                String category2PodcastListJson = NetworkUtils.okHttpGetRequestStringResult(Constant.root_gpodder_feed_url_part + category2.getTag() + "/" + sp.getInt(context.getString(R.string.pref_top_podcast_key), context.getResources().getInteger(R.integer.pref_top_podcast_default)) + ".json");
                List<Podcast> category2PodcastList = null;
                if (category2PodcastListJson != null)
                    category2PodcastList = NetworkUtils.gPodderTopListFromJson(category2PodcastListJson);
                Log.d("category1size:", category2PodcastList.size() + "");
                if (category2PodcastList != null && !category2PodcastList.isEmpty()) {
                    List<Podcast> randList = new ArrayList<>();
                    for (int i = 0; i < category2PodcastList.size(); i++) {
                        Podcast podcast = category2PodcastList.get(new Random().nextInt(category2PodcastList.size()));
                        if (!podcast.getCoverImage().equals("null")) {
                            randList.add(podcast);
                            AppUtils.removeTwiceValue(randList, podcast);
                            Log.d("podcastcat2:", podcast.toString() + "|tag:" + category2.getTag());
                        }
                        if (randList.size() == 6) {
                            break;
                        }
                    }
                    if (!randList.isEmpty()) {
                        ContentValues[] values = NetworkUtils.gPodderContentValueListFromArrayList(randList, "Yes", "Yes 2");
                        try {
                            context.getContentResolver().delete(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI, MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_PROVIDER + " = ? and " + MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_MAIN_SCREEEN + " = ? and " + MyPodcastContract.MyPodcastEntry.COLUMN_PODCAST_CATEGORY_FLAG + " = ?", new String[]{"gpodder.net", "Yes", "Yes 2"});
                        } catch (Exception e) {
                        }
                        context.getContentResolver().bulkInsert(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI, values);
                    }
                }

            }
            Cursor c = context.getContentResolver().query(MyPodcastContract.MyPodcastEntry.PODCAST_CONTENT_URI, null, null, null, null);
            if (c == null || c.getCount() == 0) {
                EventBus.getDefault().post(new DataEvent(0));
                editor.putInt(Constant.cursor_count, 0);
                editor.apply();
            } else {
                EventBus.getDefault().post(new DataEvent(c.getCount()));
                editor.putInt(Constant.cursor_count, c.getCount());
                editor.apply();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
