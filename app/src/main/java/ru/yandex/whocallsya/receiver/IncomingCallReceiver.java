package ru.yandex.whocallsya.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ru.yandex.whocallsya.service.CockyBubblesService;

import static android.telephony.TelephonyManager.EXTRA_INCOMING_NUMBER;
import static android.telephony.TelephonyManager.EXTRA_STATE;
import static android.telephony.TelephonyManager.EXTRA_STATE_RINGING;
import static ru.yandex.whocallsya.service.CockyBubblesService.PHONE_NUMBER;

public class IncomingCallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("whocallsya","IncomingCallReceiver");
        if (!intent.getStringExtra(EXTRA_STATE).equals(EXTRA_STATE_RINGING)) return;
        Intent i = new Intent(context, CockyBubblesService.class);
        i.putExtra(PHONE_NUMBER, intent.getStringExtra(EXTRA_INCOMING_NUMBER));
        Log.e("whocallsya","IncomingCallReceiver" + intent.getStringExtra(EXTRA_INCOMING_NUMBER));
        context.startService(i);
    }
}
