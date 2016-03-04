package pl.mg.rocketleaguecontroller.injection.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.mg.rocketleaguecontroller.component.Controller;

/**
 * Created by Maciej on 27.02.2016.
 */
@Module
public class ControllerModule {

    @Provides
    @Singleton
    public Controller provideController(){
        return new Controller();
    }
}
