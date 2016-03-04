package pl.mg.rocketleaguecontroller.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.mg.rocketleaguecontroller.MainActivity;
import pl.mg.rocketleaguecontroller.R;
import pl.mg.rocketleaguecontroller.activities.communication.BroadcastDispatcher;
import pl.mg.rocketleaguecontroller.activities.communication.Dispatch;
import pl.mg.rocketleaguecontroller.exception.ConnectionException;
import pl.mg.rocketleaguecontroller.injection.Injector;
import pl.mg.rocketleaguecontroller.messaging.MessageType;
import pl.mg.rocketleaguecontroller.service.Connection;
import pl.mg.rocketleaguecontroller.service.Logger;
import pl.mg.rocketleaguecontroller.service.TaskScheduler;

/**
 * Created by Maciej on 28.02.2016.
 */
public class ConnectActivity extends AppCompatActivity{

    @Inject
    Logger logger;
    @Inject
    Connection connection;
    @Inject
    TaskScheduler scheduler;

    @Bind(R.id.controllerIdSpinner)
    Spinner controllerIdSpinner;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.errorText)
    TextView errorText;
    @Bind(R.id.inputAddress)
    EditText addressInput;
    @Bind(R.id.btnConnect)
    Button connectButton;

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_layout);
        init();
    }

    private void init(){
        ButterKnife.bind(this);
        Injector.INSTANCE.getApplicationComponent().inject(this);
        initBroadcastReceiver();
        fillAddress();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.controller_id_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        controllerIdSpinner.setAdapter(adapter);
    }

    private int getControllerId(){
        return Integer.valueOf(controllerIdSpinner.getSelectedItem().toString());
    }

    private void fillAddress(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.contains(getString(R.string.saved_address))) return;
        addressInput.setText(prefs.getString(getString(R.string.saved_address), ""));
    }

    private void saveAddress(String address){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString(getString(R.string.saved_address),address).commit();
    }

    private boolean checkAddress(String address){
        if (address.equals("")) return false;
        return true;
    }

    @OnClick(R.id.btnConnect)
    public void onConnectButtonClick(){
        errorText.setVisibility(View.GONE);
        String address = addressInput.getText().toString();
        if (!checkAddress(address)) {
            addressInput.setText("");
            errorText.setText("Invalid address.");
            errorText.setVisibility(View.VISIBLE);
            return;
        }
        /*
        int id;
        try {
            id = Integer.parseInt(controllerIdInput.getText().toString());
            if (id<0 || id > 15) throw new NumberFormatException();
        } catch (NumberFormatException e){
            controllerIdInput.setText("");
            errorText.setText("Invalid id.");
            errorText.setVisibility(View.VISIBLE);
            return;
        }*/

        addressInput.setEnabled(false);
        connectButton.setClickable(false);
        progressBar.setVisibility(View.VISIBLE);
        connect(address, getControllerId());
    }

    private void connect(final String address, final int id){
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                connection.connect(address, getResources().getInteger(R.integer.server_port), id);
            }
        });
    }

    private void onConnectionSuccess(){
        saveAddress(addressInput.getText().toString());
        progressBar.setVisibility(View.GONE);
        addressInput.setFocusable(true);
        connectButton.setClickable(true);
        Intent intent = new Intent(ConnectActivity.this,MainActivity.class);
        startActivity(intent);
    }

    private void onConnectionFailure(int cause){
        progressBar.setVisibility(View.GONE);
        switch(cause){
            case MessageType.ID_INVALID:
                errorText.setText(getString(R.string.conn_err_id_invalid));
                break;
            case MessageType.ID_TAKEN:
                errorText.setText(getString(R.string.conn_err_id_taken));
                break;
            default:
                errorText.setText(getString(R.string.conn_err_default));
                break;
        }
        errorText.setVisibility(View.VISIBLE);
        connectButton.setClickable(true);
        addressInput.setEnabled(true);
    }

    private void initBroadcastReceiver(){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Dispatch dispatch = (Dispatch)intent.getSerializableExtra(getString(R.string.dispatch_extra));
                switch (dispatch){
                    case CONNECTED:
                        onConnectionSuccess();
                        break;
                    case CONNECTION_ERROR:
                        int cause = intent.getIntExtra(getString(R.string.dispatch_content),0);
                        onConnectionFailure(cause);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,new IntentFilter(getString(R.string.receiver_filter)));
        if (connection.isConnected()) onConnectionSuccess();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
