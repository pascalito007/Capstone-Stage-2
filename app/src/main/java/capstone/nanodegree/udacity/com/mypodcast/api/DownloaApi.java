package capstone.nanodegree.udacity.com.mypodcast.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by jem001 on 18/10/2017.
 */

public interface DownloaApi {
    @GET
    @Streaming
    Call<ResponseBody> downloadFile(@Url String fileUrl);
}
