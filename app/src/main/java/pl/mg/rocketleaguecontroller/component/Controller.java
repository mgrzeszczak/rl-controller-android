package pl.mg.rocketleaguecontroller.component;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import javax.inject.Inject;
import pl.mg.rocketleaguecontroller.injection.Injector;
import pl.mg.rocketleaguecontroller.messaging.MessagePriority;
import pl.mg.rocketleaguecontroller.messaging.MessageType;
import pl.mg.rocketleaguecontroller.service.ConnectionWrapper;
import pl.mg.rocketleaguecontroller.service.Logger;
import static pl.mg.rocketleaguecontroller.messaging.MessageType.*;
/**
 * Created by Maciej on 27.02.2016.
 */
public class Controller implements SensorEventListener {

    @Inject
    Logger logger;
    @Inject
    ConnectionWrapper conn;

    private boolean on = false;

    public Controller(){
        init();
    }

    private void init(){
        Injector.INSTANCE.getApplicationComponent().inject(this);
        on = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if ( event.sensor.getType()!=Sensor.TYPE_ACCELEROMETER ) return;
        double x = event.values[0];
        double y = event.values[1];
        double z = event.values[2];

        if (y>5) y = 5;
        if (y<-5) y = -5;
        double xpercent = (1+y/5.0d)/2.0d;
        int maxval = 32767;
        int xval = (int)(xpercent*maxval);
        notify(L_AXIS_X,MessagePriority.LOW,xval);

        double zpercent = (1+z/10.0d)/2.0d;
        int zval = (int)(zpercent*maxval);
        notify(DRIVE, MessagePriority.LOW, zval);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void notify(int type, MessagePriority priority, int... content){
        if (!on) return;
        conn.write(type, priority, content);
    }

    public void boost(boolean b){
        notify(BOOST, MessagePriority.HIGH, b ? 1 : 0);
    }
    public void jump(boolean b){
        notify(JUMP,MessagePriority.HIGH,b?1:0);
    }
    public void onVolUpChange(boolean pressed){
        notify(START,MessagePriority.HIGH,pressed ? 1:0);
    }
    public void onMenuChange(boolean pressed){
        notify(DRIFT,MessagePriority.HIGH,pressed?1:0);
    }
    public void onVolDownChange(boolean pressed){
        notify(CAMERA,MessagePriority.HIGH,pressed ? 1:0);
    }

    public void setOn(boolean on) {
        this.on = on;
        if (!on){
            conn.write(MessageType.CONTROL_LOCK,MessagePriority.HIGH);
            //notify(MessageType.CONTROL_LOCK,MessagePriority.HIGH);
        }
    }

    public boolean isOn() {
        return on;
    }
}
