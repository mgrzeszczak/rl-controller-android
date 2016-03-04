package pl.mg.rocketleaguecontroller.service;

import android.content.Context;
import android.widget.Toast;

import javax.inject.Inject;

import pl.mg.rocketleaguecontroller.injection.Injector;

/**
 * Created by Maciej on 24.02.2016.
 */
public class Logger {

    @Inject
    Context context;

    public Logger() {
        Injector.INSTANCE.getApplicationComponent().inject(this);
    }

    public void log(String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

}
