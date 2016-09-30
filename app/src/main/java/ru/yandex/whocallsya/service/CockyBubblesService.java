package ru.yandex.whocallsya.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

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
        } else {
            return super.onStartCommand(intent, flags, startId);
        }
        addBubble(phoneNumber, 60, 20);
        return START_NOT_STICKY;
//        return super.onStartCommand(intent, flags, startId);
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

    public void addBubble(String number, int x, int y) {
        BubbleLayout bubbleView = (BubbleLayout) LayoutInflater.from(this).inflate(R.layout.bubble_main, null);
        final String searchString = "https://yandex.ru/search/?text=" + number;
        InformingLayout informingLayout = new InformingLayout(this, number);
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
                case R.drawable.ic_like:
                    v.setImageResource(R.drawable.ic_dislike);
                    v.setTag(R.drawable.ic_dislike);
                    break;
                case R.drawable.ic_dislike:
                    v.setImageResource(R.drawable.ic_fuck);
                    v.setTag(R.drawable.ic_fuck);
                    break;
                case R.drawable.ic_fuck:
                    v.setImageResource(R.drawable.ic_main);
                    v.setTag(R.drawable.ic_main);
                    break;
                case R.drawable.ic_main:
                default:
                    v.setImageResource(R.drawable.ic_like);
                    v.setTag(R.drawable.ic_like);
            }
        });

        bubbleView.setShouldStickToWall(true);
        WindowManager.LayoutParams layoutParams = buildLayoutParamsForBubble(x, y);
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

