package pl.mg.rocketleaguecontroller.injection.components;

import javax.inject.Singleton;
import dagger.Component;
import pl.mg.rocketleaguecontroller.injection.modules.AppBeanModule;
import pl.mg.rocketleaguecontroller.injection.modules.AppContextModule;

/**
 * Created by Maciej on 27.09.2015.
 */
@Singleton
@Component(modules = {
        AppContextModule.class,
        AppBeanModule.class})
public interface ApplicationComponent
        extends AppContextComponent,
        InjectionComponent,
        BeanComponent {}
