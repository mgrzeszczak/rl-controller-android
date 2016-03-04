package pl.mg.rocketleaguecontroller.injection.components;

import android.content.Context;
import android.location.LocationManager;

import pl.mg.rocketleaguecontroller.injection.DaggerApplication;


/**
 * Created by Maciej on 27.09.2015.
 */
public interface AppContextComponent {
    DaggerApplication application();
    Context applicationContext();
    LocationManager locationManager();
}
