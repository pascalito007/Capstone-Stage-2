package capstone.nanodegree.udacity.com.mypodcast.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.List;

import capstone.nanodegree.udacity.com.mypodcast.model.Podcast;

/**
 * Created by jem001 on 28/12/2017.
 */

public class AppUtils {
    public static File getFileInInternalMemory(String fileUrl) {
        File foundFile = null;
        File outputFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File f[] = outputFile.listFiles();
        if (f != null) {
            for (File file : f) {
                //if (file.getName().equals(fileUrl.substring((fileUrl.lastIndexOf("/") + 1)))) {
                Log.d("filename:", file.getName());
                if (file.getName().equals(fileUrl)) {
                    foundFile = file;
                    break;
                }
            }
        }

        return foundFile;
    }

    public static List<Podcast> removeTwiceValue(List<Podcast> randList, Podcast podcast) {
        int count = 0;
        for (int j = 0; j < randList.size(); j++) {
            if (randList.get(j).getPodcastId().equals(podcast.getPodcastId())) {
                count++;
            }
        }
        if (count > 1) {
            randList.remove(podcast);
        }
        return randList;
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
