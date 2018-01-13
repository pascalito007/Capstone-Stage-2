package capstone.nanodegree.udacity.com.mypodcast.utils;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by jem001 on 07/12/2017.
 */

public class AppConfig extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}
