package capstone.nanodegree.udacity.com.mypodcast.eventbus;

/**
 * Created by jem001 on 13/01/2018.
 */

public class RecommandationEvent {
    public final int recommendedSize;

    public RecommandationEvent(int recommendedSize) {
        this.recommendedSize = recommendedSize;
    }


}
