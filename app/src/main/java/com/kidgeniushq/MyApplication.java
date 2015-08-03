package com.kidgeniushq;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by benjamin.harvey on 8/2/15.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "f2akudqhAIlsXdgEKP0Yple9hMhwjhV4lprJ2pYy", "5CNZTfiHYrOIRhyWsGWRrRAhhDKaCLfR9fjgvpmF");
    }
}
