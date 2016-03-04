package pl.mg.rocketleaguecontroller.injection;

import android.app.Application;

/**
 * Created by Maciej on 27.09.2015.
 */
public class DaggerApplication
        extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Injector.INSTANCE.initializeApplicationComponent(this);
    }
}
