package pl.mg.rocketleaguecontroller.service;

import android.os.Message;

import javax.inject.Inject;

import pl.mg.rocketleaguecontroller.injection.Injector;
import pl.mg.rocketleaguecontroller.messaging.ContentWrapper;
import pl.mg.rocketleaguecontroller.messaging.MessagePriority;
import pl.mg.rocketleaguecontroller.messaging.MessageWrapper;

/**
 * Created by Maciej on 27.02.2016.
 */
public class ConnectionWrapper {

    @Inject
    Connection connection;

    public ConnectionWrapper(){
        Injector.INSTANCE.getApplicationComponent().inject(this);
    }

    public void write(int type, MessagePriority priority,int... content){
        connection.write(MessageWrapper.wrapMessage(type,ContentWrapper.wrapContent(content)),priority);
    }

    public void write(int type, byte[] content, MessagePriority priority){
        connection.write(MessageWrapper.wrapMessage(type, content),priority);
    }

    public void write(int type, MessagePriority priority){
        connection.write(MessageWrapper.wrapMessage(type),priority);
    }

}
