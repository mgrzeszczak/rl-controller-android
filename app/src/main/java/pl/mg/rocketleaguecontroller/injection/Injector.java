package pl.mg.rocketleaguecontroller.injection;

import pl.mg.rocketleaguecontroller.injection.components.ApplicationComponent;
import pl.mg.rocketleaguecontroller.injection.components.DaggerApplicationComponent;
import pl.mg.rocketleaguecontroller.injection.modules.AppContextModule;

/**
 * Created by Maciej on 27.09.2015.
 */
public enum Injector {
    INSTANCE;

    private ApplicationComponent applicationComponent;

    private Injector(){

    }

    void initializeApplicationComponent(DaggerApplication daggerApplication){
        ApplicationComponent applicationComponent = DaggerApplicationComponent.builder()
                .appContextModule(new AppContextModule(daggerApplication))
                .build();
        this.applicationComponent = applicationComponent;
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
