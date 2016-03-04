package pl.mg.rocketleaguecontroller.activities.communication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import pl.mg.rocketleaguecontroller.R;

/**
 * Created by Maciej on 28.02.2016.
 */
public class BroadcastDispatcher {

    public static void dispatch(Dispatch dispatch, Context context){
        Intent intent = new Intent(context.getString(R.string.receiver_filter));
        intent.putExtra(context.getString(R.string.dispatch_extra),dispatch);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void dispatch(Dispatch dispatch, Context context, int type){
        Intent intent = new Intent(context.getString(R.string.receiver_filter));
        intent.putExtra(context.getString(R.string.dispatch_extra),dispatch);
        intent.putExtra(context.getString(R.string.dispatch_content),type);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


}
