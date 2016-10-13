package ru.yandex.whocallsya.service;

import android.Manifest;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.yandex.metrica.YandexMetrica;

import java.util.HashMap;
import java.util.Map;

import ru.yandex.whocallsya.bubble.BubbleBaseLayout;

import static android.graphics.PixelFormat.TRANSLUCENT;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
import static android.widget.ListPopupWindow.WRAP_CONTENT;

public class BaseBubblesService extends Service {

    private WindowManager windowManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        Log.d("whocallsya", "onDestroyService");
        Answers.getInstance().logCustom(new CustomEvent("BubbleService")
                .putCustomAttribute("SystemAction", "onDestroy"));
        Map<String, Object> eventAttributes = new HashMap<>();
        eventAttributes.put("SystemAction", "onDestroy");
        YandexMetrica.reportEvent("BubbleService", eventAttributes);
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Answers.getInstance().logCustom(new CustomEvent("BubbleService")
                .putCustomAttribute("SystemAction", "onUnbind"));
        Map<String, Object> eventAttributes = new HashMap<>();
        eventAttributes.put("SystemAction", "onUnbind");
        YandexMetrica.reportEvent("BubbleService", eventAttributes);
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Answers.getInstance().logCustom(new CustomEvent("BubbleService")
                .putCustomAttribute("SystemAction", "onTaskRemoved"));
        Map<String, Object> eventAttributes = new HashMap<>();
        eventAttributes.put("SystemAction", "onTaskRemoved ");
        YandexMetrica.reportEvent("BubbleService", eventAttributes);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onLowMemory() {
        Answers.getInstance().logCustom(new CustomEvent("BubbleService")
                .putCustomAttribute("SystemAction", "LowMemory"));
        Map<String, Object> eventAttributes = new HashMap<>();
        eventAttributes.put("SystemAction", "LowMemory");
        YandexMetrica.reportEvent("BubbleService", eventAttributes);
        super.onLowMemory();
    }

    protected void logBubble(String number, String action) {
        Answers.getInstance().logCustom(new CustomEvent("BubbleService")
                .putCustomAttribute("Action", action)
                .putCustomAttribute("Number", number)
        );
        Map<String, Object> eventAttributes = new HashMap<>();
        eventAttributes.put("Action", action);
        eventAttributes.put("Number", number);
        YandexMetrica.reportEvent("BubbleService", eventAttributes);
    }

    WindowManager.LayoutParams buildLayoutParamsForBubble() {
        DisplayMetrics dM = Resources.getSystem().getDisplayMetrics();
        int xCenter = dM.widthPixels / 2 - dpToPx(34, dM.density);
        int yCenter = dM.heightPixels / 2 - dpToPx(10, dM.density);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT,
                TYPE_SYSTEM_ERROR,
                FLAG_NOT_FOCUSABLE | FLAG_SHOW_WHEN_LOCKED,
                PixelFormat.TRANSPARENT);

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = xCenter;
        params.y = yCenter;
        return params;
    }

    WindowManager.LayoutParams buildLayoutParamsForTrash() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                MATCH_PARENT,
                MATCH_PARENT,
                TYPE_SYSTEM_ERROR,
                FLAG_NOT_FOCUSABLE | FLAG_SHOW_WHEN_LOCKED,
                PixelFormat.TRANSPARENT);
        params.x = 0;
        params.y = 0;
        return params;
    }

    WindowManager.LayoutParams buildLayoutParamsForInfo() {
        DisplayMetrics dM = Resources.getSystem().getDisplayMetrics();
        int width = dM.widthPixels - dpToPx(16, dM.density);
        int height = dM.heightPixels - dpToPx(16 + 24, dM.density);//margin plus status bar
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                width,
                height,
                TYPE_SYSTEM_ERROR,
                FLAG_NOT_FOCUSABLE | FLAG_SHOW_WHEN_LOCKED,
                TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.START;
        params.y = dpToPx(8, dM.density);
        params.x = dpToPx(8, dM.density);
        return params;
    }

    protected WindowManager getWindowManager() {
        if (windowManager == null) {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        }
        return windowManager;
    }

    private int dpToPx(float dp, float density) {
        return (int) (dp * density);
    }

    protected boolean unknownPhoneNumber(String incomingPhoneNumber) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Для правильной работы приложения АОН необходимо разрешить" +
                    " доступ к Вашей телефонной книге", Toast.LENGTH_LONG).show();
            return true;
        }

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur != null && cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    if (pCur != null) {
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                            if (PhoneNumberUtils.compare(phoneNo, incomingPhoneNumber)) {
                                cur.close();
                                pCur.close();
                                return false;
                            }
                        }
                        pCur.close();
                    }
                }
            }
            cur.close();
        }
        return true;
    }

    protected void addViewToWindow(final BubbleBaseLayout view) {
        new Handler(Looper.getMainLooper()).post(() -> getWindowManager().addView(view, view.getViewParams()));
    }

    protected void addBubbleLayout(int layoutResourceId, BubbleBaseLayout layout, WindowManager.LayoutParams layoutParams) {
        if (layoutResourceId != 0 && layout != null) {
            layout.setViewParams(layoutParams);
            layout.setVisibility(View.GONE);
            LayoutInflater.from(this).inflate(layoutResourceId, layout, true);
            addViewToWindow(layout);
        }
    }

}