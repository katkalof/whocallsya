package ru.yandex.whocallsya.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import ru.yandex.whocallsya.BuildConfig;
import ru.yandex.whocallsya.service.CockyBubblesService;

import static android.telephony.TelephonyManager.EXTRA_INCOMING_NUMBER;
import static android.telephony.TelephonyManager.EXTRA_STATE;
import static android.telephony.TelephonyManager.EXTRA_STATE_IDLE;
import static android.telephony.TelephonyManager.EXTRA_STATE_RINGING;
import static ru.yandex.whocallsya.service.CockyBubblesService.PHONE_NUMBER;

public class IncomingCallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, CockyBubblesService.class);
        i.putExtra(EXTRA_STATE, intent.getStringExtra(EXTRA_STATE));
        i.putExtra(PHONE_NUMBER, intent.getStringExtra(EXTRA_INCOMING_NUMBER));
        Log.d("whocallsya", "IncomingCallReceiver "
                + intent.getStringExtra(EXTRA_INCOMING_NUMBER) + " "
                + intent.getStringExtra(EXTRA_STATE));
        context.startService(i);
    }
}
