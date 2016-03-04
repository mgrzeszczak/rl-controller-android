package pl.mg.rocketleaguecontroller.injection.modules;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.mg.rocketleaguecontroller.service.TaskScheduler;

/**
 * Created by Maciej on 24.02.2016.
 */
@Module
public class TaskSchedulerModule {

    @Provides
    @Singleton
    public TaskScheduler taskScheduler(){
        return new TaskScheduler();
    }
}
