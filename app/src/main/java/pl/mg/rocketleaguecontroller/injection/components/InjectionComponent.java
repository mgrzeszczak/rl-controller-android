package pl.mg.rocketleaguecontroller.injection.components;

import pl.mg.rocketleaguecontroller.MainActivity;
import pl.mg.rocketleaguecontroller.activities.ConnectActivity;
import pl.mg.rocketleaguecontroller.component.Controller;
import pl.mg.rocketleaguecontroller.service.Connection;
import pl.mg.rocketleaguecontroller.service.ConnectionWrapper;
import pl.mg.rocketleaguecontroller.service.Logger;
import pl.mg.rocketleaguecontroller.service.TaskScheduler;

/**
 * Created by Maciej on 27.09.2015.
 */
public interface InjectionComponent {

    void inject(MainActivity activity);
    void inject(Logger logger);
    void inject(Connection connection);
    void inject(TaskScheduler taskScheduler);
    void inject(Controller controller);
    void inject(ConnectionWrapper connectionWrapper);
    void inject(ConnectActivity activity);

}
