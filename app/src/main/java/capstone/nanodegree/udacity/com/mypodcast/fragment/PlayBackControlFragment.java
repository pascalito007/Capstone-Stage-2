package capstone.nanodegree.udacity.com.mypodcast.fragment;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import capstone.nanodegree.udacity.com.mypodcast.PlayerWidgetProvider;
import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.activity.PlayMediaActivity;
import capstone.nanodegree.udacity.com.mypodcast.utils.Constant;

/**
 * Created by jem001 on 07/01/2018.
 */

@EFragment(R.layout.fragment_playback_controls)
public class PlayBackControlFragment extends Fragment {
    @ViewById(R.id.album_art)
    ImageView imageView;
    @ViewById(R.id.title)
    TextView title;
    @ViewById(R.id.tvSubTitle)
    TextView subTitle;
    SharedPreferences sharedPreferences;
    @ViewById(R.id.play_pause)
    ImageButton imageButton;

    @AfterViews
    public void myOnCreateView() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Glide.with(getContext()).load(sharedPreferences.getString(Constant.bottom_img_cover, null)).into(imageView);
        title.setText(sharedPreferences.getString(Constant.bottom_title, null));
        String sub=sharedPreferences.getString(Constant.bottom_sub_title, null);
        if (sub!=null)
        subTitle.setText(sub.substring(0, (sub.length() <= 15 ? sub.length() : 15)));

        imageButton.setImageResource(sharedPreferences.getInt(Constant.bottom_play_pause_icon, 0));

    }

    @Click(R.id.play_pause)
    void playPauseClick() {
        if (sharedPreferences.getString(Constant.bottom_mp3_url, null) != null) {
            //MediaButtonReceiver.buildMediaButtonPendingIntent(getContext(), PlaybackStateCompat.ACTION_PLAY_PAUSE);

           /* String userAgent = Util.getUserAgent(getContext(), "PlayMediaActivity");
            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(sharedPreferences.getString(Constant.bottom_mp3_url, null)), new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            long mResumePosition = Math.max(0, PlayMediaActivity.mPlayerView.getPlayer().getContentPosition());
            PlayMediaActivity.mPlayerView.getPlayer().seekTo(mResumePosition);
            PlayMediaActivity.mExoPlayer.prepare(mediaSource);
            PlayMediaActivity.mExoPlayer.setPlayWhenReady(true);*/


            /*AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getContext());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getContext(), PlayerWidgetProvider.class));
            PlayerWidgetProvider.updateMediaTitle(getContext(), appWidgetManager, sharedPreferences.getString(Constant.bottom_title, null), sharedPreferences.getInt(Constant.bottom_play_pause_icon, 0), appWidgetIds);*/
        } else {
            Toast.makeText(getContext(), "Exoplayer is null", Toast.LENGTH_LONG).show();
        }
    }
}
