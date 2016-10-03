package ru.yandex.whocallsya.service;

import android.Manifest;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.yandex.metrica.YandexMetrica;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import ru.yandex.whocallsya.R;
import ru.yandex.whocallsya.bubble.BubbleBaseLayout;
import ru.yandex.whocallsya.bubble.BubbleLayout;
import ru.yandex.whocallsya.bubble.BubbleTrashLayout;
import ru.yandex.whocallsya.bubble.BubblesLayoutCoordinator;
import ru.yandex.whocallsya.ui.view.InformingLayout;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
import static android.widget.ListPopupWindow.WRAP_CONTENT;

public class CockyBubblesService extends Service {
    public static final String PHONE_NUMBER = "PHONE_NUMBER";
    WeakHashMap<BubbleBaseLayout, String> bubbles = new WeakHashMap<>();
    private BubbleTrashLayout bubblesTrash;
    private WindowManager windowManager;
    private BubblesLayoutCoordinator layoutCoordinator;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String phoneNumber;
        if (intent.getExtras().containsKey(PHONE_NUMBER)) {
            phoneNumber = intent.getStringExtra(PHONE_NUMBER);
            if (unknownPhoneNumber(phoneNumber)) {
                DisplayMetrics dM = Resources.getSystem().getDisplayMetrics();
                // TODO: 02.10.2016 по хорошему нужно вынести константный размер вьюшки отсюда
                int x = Math.round((180 - 56 / 2) * dM.density);
                int y = Math.round((320 - 56 / 2) * dM.density);

                addBubble(phoneNumber, x, y);

                return START_NOT_STICKY;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        addTrash(R.layout.bubble_trash);
        layoutCoordinator = new BubblesLayoutCoordinator.Builder(this)
                .setWindowManager(getWindowManager())
                .setTrashView(bubblesTrash)
                .build();
    }


    private WindowManager getWindowManager() {
        if (windowManager == null) {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        }
        return windowManager;
    }

    public void addBubble(String number, int xBubbleCenter, int yBubbleCenter) {
        BubbleLayout bubbleView = (BubbleLayout) LayoutInflater.from(this).inflate(R.layout.bubble_main, null);
        final String searchString = "https://yandex.ru/search/?text=" + number;

        InformingLayout informingLayout = new InformingLayout(this, number, getCurrentCountryCode());
        new SearchAsyncTask(informingLayout).execute(searchString);
        bubbleView.setOnBubbleRemoveListener(bubble -> informingLayout.unShow());
        bubbleView.setOnBubbleClickListener(bubble -> {
            ImageView v = ((ImageView) bubbleView.findViewById(R.id.bubble_image));
            Integer tag = (Integer) v.getTag();
            tag = tag == null ? 0 : tag;
            if (informingLayout.isShowed()) {
                informingLayout.unShow();
            } else {
                informingLayout.show();
            }
            switch (tag) {
                case R.drawable.ic_close:
                    v.setImageResource(R.drawable.ic_unknown);
                    v.setTag(R.drawable.ic_unknown);
                    break;
                case R.drawable.ic_unknown:
                default:
                    v.setImageResource(R.drawable.ic_close);
                    v.setTag(R.drawable.ic_close);
            }
        });
        bubbleView.setShouldStickToWall(false);
        WindowManager.LayoutParams layoutParams = buildLayoutParamsForBubble(xBubbleCenter, yBubbleCenter);
        bubbleView.setWindowManager(getWindowManager());
        bubbleView.setViewParams(layoutParams);
        bubbleView.setLayoutCoordinator(layoutCoordinator);
        bubbles.put(bubbleView, number);
        addViewToWindow(bubbleView);
        Answers.getInstance().logCustom(new CustomEvent("BubbleService")
                .putCustomAttribute("Action", "addBubble")
                .putCustomAttribute("Number", number)
        );
        Map<String, Object> eventAttributes = new HashMap<>();
        eventAttributes.put("Action", "addBubble");
        eventAttributes.put("Number", number);
        YandexMetrica.reportEvent("BubbleService", eventAttributes);
    }

    public void addTrash(int trashLayoutResourceId) {
        if (trashLayoutResourceId != 0) {
            bubblesTrash = new BubbleTrashLayout(this);
            bubblesTrash.setWindowManager(windowManager);
            bubblesTrash.setViewParams(buildLayoutParamsForTrash());
            bubblesTrash.setVisibility(View.GONE);
            LayoutInflater.from(this).inflate(trashLayoutResourceId, bubblesTrash, true);
            addViewToWindow(bubblesTrash);
        }
    }


    public void addViewToWindow(final BubbleBaseLayout view) {
        new Handler(Looper.getMainLooper()).post(() -> getWindowManager().addView(view, view.getViewParams()));
    }

    WindowManager.LayoutParams buildLayoutParamsForBubble(int x, int y) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WRAP_CONTENT,
                WRAP_CONTENT,
                TYPE_SYSTEM_ERROR,
                FLAG_NOT_FOCUSABLE | FLAG_SHOW_WHEN_LOCKED,
                PixelFormat.TRANSPARENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = x;
        params.y = y;
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

    public void removeBubble(BubbleLayout bubble) {
        new Handler(Looper.getMainLooper()).post(() -> {
            getWindowManager().removeView(bubble);
            if (bubbles.containsKey(bubble)) {
                bubble.notifyBubbleRemoved();
                Answers.getInstance().logCustom(new CustomEvent("BubbleService")
                        .putCustomAttribute("Action", "removeBubble")
                        .putCustomAttribute("Number", bubbles.get(bubble))
                );
                Map<String, Object> eventAttributes = new HashMap<>();
                eventAttributes.put("Action", "removeBubble");
                eventAttributes.put("Number", bubbles.get(bubble));
                YandexMetrica.reportEvent("BubbleService", eventAttributes);
                bubbles.remove(bubble);
            }
        });
    }


    private boolean unknownPhoneNumber(String incomingPhoneNumber) {
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

    private String getCurrentCountryCode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return getResources().getConfiguration().getLocales().get(0).getCountry();
        } else {
            return getResources().getConfiguration().locale.getCountry();
        }
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

    @Override
    public void onDestroy() {
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

}