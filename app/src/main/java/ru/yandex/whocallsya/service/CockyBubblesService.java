package ru.yandex.whocallsya.service;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;

import java.util.WeakHashMap;

import ru.yandex.whocallsya.R;
import ru.yandex.whocallsya.bubble.BubbleLayout;
import ru.yandex.whocallsya.bubble.BubbleTrashLayout;
import ru.yandex.whocallsya.bubble.BubblesLayoutCoordinator;
import ru.yandex.whocallsya.bubble.InformingLayout;

public class CockyBubblesService extends BaseBubblesService {

    public static final String PHONE_NUMBER = "PHONE_NUMBER";
    WeakHashMap<String, BubbleLayout> bubbles = new WeakHashMap<>();
    private BubbleTrashLayout bubblesTrash;
    private InformingLayout infoLayout;
    private BubblesLayoutCoordinator layoutCoordinator;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String phoneNumber;
        if (intent.getExtras().containsKey(PHONE_NUMBER)) {
            phoneNumber = intent.getStringExtra(PHONE_NUMBER);
            if (unknownPhoneNumber(phoneNumber) && !bubbles.containsKey(phoneNumber)) {
                DisplayMetrics dM = Resources.getSystem().getDisplayMetrics();
                // TODO: 02.10.2016 по хорошему нужно вынести константный размер вьюшки отсюда
                int xCenter = 180;
                int yCenter = 320;
                int x = Math.round((xCenter - 56 / 2) * dM.density);
                int y = Math.round((yCenter - 56 / 2) * dM.density);
                addBubble(phoneNumber, x, y);
                return START_NOT_STICKY;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("whocallsya", "onCreateService");
        bubblesTrash = new BubbleTrashLayout(this);
        addBubbleLayout(R.layout.bubble_trash, bubblesTrash, buildLayoutParamsForTrash());
        infoLayout = new InformingLayout(this);
        addBubbleLayout(R.layout.info_layout, infoLayout, buildLayoutParamsForInfo());
        layoutCoordinator = new BubblesLayoutCoordinator.Builder(this)
                .setWindowManager(getWindowManager())
                .setTrashView(bubblesTrash)
                .build();
    }

    public void addBubble(String number, int xBubbleCenter, int yBubbleCenter) {
        logBubble(number, "addBubble");
        BubbleLayout bubbleView = (BubbleLayout) LayoutInflater.from(this).inflate(R.layout.bubble_main, null);
        WindowManager.LayoutParams layoutParams = buildLayoutParamsForBubble(xBubbleCenter, yBubbleCenter);
        bubbleView.setNumber(number);
        bubbleView.setWindowManager(getWindowManager());
        bubbleView.setViewParams(layoutParams);
        bubbleView.setLayoutCoordinator(layoutCoordinator);
        bubbleView.setShouldStickToWall(true);
        bubbleView.setOnBubbleClickListener(bubble -> {
            if (bubbleView.isShownOpen()) {
                infoLayout.unShow();
            } else {
                String lastNumber = infoLayout.getLastSearchingNumber();
                infoLayout.setData(number);
                if (infoLayout.isOpen()) {
                    if (!lastNumber.isEmpty() && bubbles.containsKey(lastNumber)) {
                        bubbles.get(lastNumber).changeImageView();
                    }
                } else {
                    infoLayout.show();
                }
            }
        });
        bubbles.put(number, bubbleView);
        addViewToWindow(bubbleView);
    }

    public void removeBubble(String number) {
        new Handler(Looper.getMainLooper()).post(() -> {
            getWindowManager().removeView(bubbles.get(number));
            if (bubbles.containsKey(number)) {
                logBubble(number, "removeBubble");
                bubbles.remove(number);
            }
            if (bubbles.isEmpty()) {
                getWindowManager().removeView(bubblesTrash);
                getWindowManager().removeView(infoLayout);
                stopSelf();
            }
        });
    }

}