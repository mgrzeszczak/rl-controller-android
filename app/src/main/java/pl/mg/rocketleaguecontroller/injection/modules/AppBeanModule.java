package pl.mg.rocketleaguecontroller.injection.modules;

import dagger.Module;

/**
 * Created by Maciej on 27.09.2015.
 */
@Module(includes = {LoggerModule.class, TaskSchedulerModule.class, ConnectionModule.class,ControllerModule.class})
public class AppBeanModule {}
