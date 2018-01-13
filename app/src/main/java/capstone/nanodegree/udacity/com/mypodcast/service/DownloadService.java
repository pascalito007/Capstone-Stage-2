package capstone.nanodegree.udacity.com.mypodcast.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.api.DownloaApi;
import capstone.nanodegree.udacity.com.mypodcast.model.Download;
import capstone.nanodegree.udacity.com.mypodcast.utils.Constant;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;


public class DownloadService extends IntentService {

    public DownloadService() {
        super("DownloadService");
    }

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private int totalFileSize;
    private String fileName;
    //String episode_id;


    @Override
    protected void onHandleIntent(Intent intent) {

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_drawer_download)
                .setContentTitle(getString(R.string.download))
                .setContentText(getString(R.string.downloading_file))
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());
        String file = intent.getStringExtra(Constant.other);
        fileName = file.replaceAll("/", "_");
        //episode_id = intent.getStringExtra(Constant.episode_id);
        initDownload(intent.getStringExtra(Constant.root_url), intent.getStringExtra(Constant.other));

    }

    private void initDownload(String rootUrl, String fileUrl) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(rootUrl)
                .build();

        DownloaApi retrofitInterface = retrofit.create(DownloaApi.class);

        final Call<ResponseBody> request = retrofitInterface.downloadFile(fileUrl);
        try {
            downloadFile(request.execute().body());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

        }


    }

    private void downloadFile(ResponseBody body) throws IOException {

        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        OutputStream output = new FileOutputStream(outputFile);
        long total = 0;
        long startTime = System.currentTimeMillis();
        int timeCount = 1;
        while ((count = bis.read(data)) != -1) {

            total += count;
            totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
            double current = Math.round(total / (Math.pow(1024, 2)));

            int progress = (int) ((total * 100) / fileSize);

            long currentTime = System.currentTimeMillis() - startTime;

            Download download = new Download();
            download.setTotalFileSize(totalFileSize);
            download.setFileName(fileName);
            //download.setEpisodeId(episode_id);
            if (currentTime > 1000 * timeCount) {

                download.setCurrentFileSize((int) current);
                download.setProgress(progress);
                sendNotification(download);
                timeCount++;
            }

            output.write(data, 0, count);
        }
        onDownloadComplete();
        output.flush();
        output.close();
        bis.close();

    }

    private void sendNotification(Download download) {
        Log.d("download:", download.toString());
        //download.setEpisodeId(episode_id);
        sendIntent(download);
        notificationBuilder.setProgress(100, download.getProgress(), false);
        notificationBuilder.setContentText(String.format(getString(R.string.downloaded1), download.getCurrentFileSize(), download.getTotalFileSize()));
        notificationManager.notify(0, notificationBuilder.build());
    }


    private void sendIntent(Download download) {

        Intent intent = new Intent(Constant.message_progress);
        intent.putExtra(Constant.download, download);
        intent.putExtra(Constant.file_name, fileName);
        //intent.putExtra(Constant.episode_id, episode_id);
        LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
    }

    private void onDownloadComplete() {

        Download download = new Download();
        download.setProgress(100);
        download.setFileName(fileName);
        //download.setEpisodeId(episode_id);
        sendIntent(download);

        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText(getString(R.string.file_downloaded));
        notificationManager.notify(0, notificationBuilder.build());

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }

}
