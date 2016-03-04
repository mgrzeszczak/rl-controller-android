package pl.mg.rocketleaguecontroller;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import pl.mg.rocketleaguecontroller.activities.ConnectActivity;
import pl.mg.rocketleaguecontroller.activities.communication.BroadcastDispatcher;
import pl.mg.rocketleaguecontroller.activities.communication.Dispatch;
import pl.mg.rocketleaguecontroller.component.Controller;
import pl.mg.rocketleaguecontroller.injection.Injector;
import pl.mg.rocketleaguecontroller.messaging.MessageType;
import pl.mg.rocketleaguecontroller.service.Connection;
import pl.mg.rocketleaguecontroller.service.Logger;
import pl.mg.rocketleaguecontroller.service.TaskScheduler;

public class MainActivity extends AppCompatActivity {

    @Inject
    Connection connection;
    @Inject
    Logger logger;
    @Inject
    Controller controller;

    @Bind(R.id.boost)
    RelativeLayout boostLayout;
    @Bind(R.id.jump)
    RelativeLayout jumpLayout;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_layout);
        init();
    }

    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(getString(R.string.receiver_filter)));
        sensorManager.registerListener(controller, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        if (!connection.isConnected()) {
            onConnectionDisconnect();
        }
    }

    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        sensorManager.unregisterListener(controller);
    }

    private void init(){
        Injector.INSTANCE.getApplicationComponent().inject(this);
        ButterKnife.bind(this);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(controller, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        //setOnTouchListener(jumpLayout, MessageType.JUMP);

        boostLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("MULTITOUCH","JUMP_ACTION_DOWN");
                        controller.jump(true);
                        return true;
                    case MotionEvent.ACTION_UP:
                        Log.d("MULTITOUCH","JUMP_ACTION_UP");
                        controller.jump(false);
                        return true;
                }
                return false;
            }
        });

        //setOnTouchListener(boostLayout,MessageType.BOOST);

        jumpLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("MULTITOUCH","BOOST_ACTION_DOWN");
                        controller.boost(true);
                        return true;
                    case MotionEvent.ACTION_UP:
                        Log.d("MULTITOUCH","BOOST_ACTION_UP");
                        controller.boost(false);
                        return true;
                }
                return false;
            }
        });


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Dispatch code = (Dispatch)intent.getSerializableExtra(getString(R.string.dispatch_extra));
                logger.log(code.name());
                switch (code){
                    case DISCONNECTED:
                        onConnectionDisconnect();
                        break;
                }
            }
        };
    }

    private void onConnectionDisconnect(){
        controller.setOn(false);
        Intent intent = new Intent(MainActivity.this, ConnectActivity.class);
        startActivity(intent);
    }



    /***
     * TOUCH CONTROLS
     */
    @OnCheckedChanged(R.id.on)
    public void onCheckedChange(boolean isChecked){
        controller.setOn(isChecked);
    }

    @OnClick(R.id.disconnectBtn)
    public void onDisconnectButtonClick(){
        connection.disconnect();
    }

    private void setOnTouchListener(View view, int type){
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_DOWN:
                        Log.d("MULTITOUCH","ACTION_DOWN");
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        Log.d("MULTITOUCH","ACTION_POINTER_DOWN");
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d("MULTITOUCH","ACTION_UP");
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        Log.d("MULTITOUCH","ACTION_POINTER_UP");
                        break;
                }
                return true;
            }
        });
    }


    /***
     * HARDWARE KEY INPUT
     *
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_MENU){
            controller.onMenuChange(true);
            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            controller.onVolUpChange(true);
            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            controller.onVolDownChange(true);
            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_MENU){
            controller.onMenuChange(false);
            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            controller.onVolUpChange(false);
            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            controller.onVolDownChange(false);
            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_BACK){

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
