package pl.mg.rocketleaguecontroller.injection.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.mg.rocketleaguecontroller.service.Connection;
import pl.mg.rocketleaguecontroller.service.ConnectionWrapper;

/**
 * Created by Maciej on 24.02.2016.
 */
@Module
public class ConnectionModule {

    @Provides
    @Singleton
    public Connection connection(){
        return new Connection();
    }

    @Provides
    @Singleton
    public ConnectionWrapper connectionWrapper(){return new ConnectionWrapper();}
}
