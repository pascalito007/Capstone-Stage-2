package capstone.nanodegree.udacity.com.mypodcast.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.SimpleExoPlayer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import capstone.nanodegree.udacity.com.mypodcast.R;
import capstone.nanodegree.udacity.com.mypodcast.utils.Constant;

/**
 * Created by jem001 on 07/01/2018.
 */

public class PlayBackControlFragment extends Fragment {
    @BindView(R.id.album_art)
    ImageView imageView;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.tvSubTitle)
    TextView subTitle;
    SharedPreferences sharedPreferences;
    @BindView(R.id.play_pause)
    ImageButton imageButton;
    private Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playback_controls, container, false);
        unbinder = ButterKnife.bind(this, view);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Glide.with(getContext()).load(sharedPreferences.getString(Constant.bottom_img_cover, null)).into(imageView);
        title.setText(sharedPreferences.getString(Constant.bottom_title, null));
        String sub = sharedPreferences.getString(Constant.bottom_sub_title, null);
        if (sub != null)
            subTitle.setText(sub.substring(0, (sub.length() <= 15 ? sub.length() : 15)));

        imageButton.setImageResource(sharedPreferences.getInt(Constant.bottom_play_pause_icon, 0));
        return view;
    }


    @OnClick(R.id.play_pause)
    void playPauseClick() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void showState(int state) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (state == PlaybackStateCompat.STATE_PLAYING) {
                    Log.d("fraPlaybackStateCompat:", "Playing:" + state);
                } else if (state == PlaybackStateCompat.STATE_BUFFERING) {
                    Log.d("fraPlaybackStateCompat:", "Buffering:" + state);
                } else {
                    Log.d("fraPlaybackStateCompat:", "Pause:" + state);
                }
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}
