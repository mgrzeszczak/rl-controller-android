package pl.mg.rocketleaguecontroller.service;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pl.mg.rocketleaguecontroller.injection.Injector;


/**
 * Created by Maciej on 24.02.2016.
 */
public class TaskScheduler {

    public TaskScheduler(){
        Injector.INSTANCE.getApplicationComponent().inject(this);
    }

    private Executor executor = Executors.newCachedThreadPool();

    public void schedule(Runnable r){
        executor.execute(r);
    }

}
