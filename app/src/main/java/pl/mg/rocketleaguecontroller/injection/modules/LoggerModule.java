package pl.mg.rocketleaguecontroller.injection.modules;



import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.mg.rocketleaguecontroller.service.Logger;

/**
 * Created by Maciej on 27.09.2015.
 */
@Module
public class LoggerModule {

    @Provides
    @Singleton
    public Logger provideLogger(){
        return new Logger();
    }
}