package pl.mg.rocketleaguecontroller.injection.components;


import pl.mg.rocketleaguecontroller.component.Controller;
import pl.mg.rocketleaguecontroller.service.Connection;
import pl.mg.rocketleaguecontroller.service.ConnectionWrapper;
import pl.mg.rocketleaguecontroller.service.TaskScheduler;

/**
 * Created by Maciej on 27.09.2015.
 */
public interface BeanComponent {
    Connection connection();
    TaskScheduler taskScheduler();
    Controller controller();
    ConnectionWrapper connectionWrapper();
}
