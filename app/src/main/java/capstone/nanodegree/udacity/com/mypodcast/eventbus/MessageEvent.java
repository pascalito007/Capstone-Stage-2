package capstone.nanodegree.udacity.com.mypodcast.eventbus;

import com.google.android.exoplayer2.SimpleExoPlayer;

/**
 * Created by jem001 on 10/01/2018.
 */

public class MessageEvent {
    public final int state;
    public final long position;
    public final SimpleExoPlayer exoPlayer;

    public MessageEvent(int message,long position, SimpleExoPlayer exoPlayer) {
        this.state = message;
        this.position=position;
        this.exoPlayer = exoPlayer;
    }
}
