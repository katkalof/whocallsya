package ru.yandex.whocallsya.service;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
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
                addBubble(phoneNumber);
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
        layoutCoordinator = new BubblesLayoutCoordinator.Builder(this)
                .setWindowManager(getWindowManager())
                .setTrashView(bubblesTrash)
                .build();
        infoLayout = new InformingLayout(this);
        addBubbleLayout(R.layout.info_layout, infoLayout, buildLayoutParamsForInfo());
        infoLayout.setLayoutCoordinator(layoutCoordinator);
    }

    public void addBubble(String number) {
        logBubble(number, "addBubble");
        BubbleLayout bubbleView = (BubbleLayout) LayoutInflater.from(this).inflate(R.layout.bubble_main, null);
        WindowManager.LayoutParams layoutParams = buildLayoutParamsForBubble();
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
                    changeBubble(lastNumber);
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

    public void changeLastBubble() {
        if (infoLayout != null) {
            changeBubble(infoLayout.getLastSearchingNumber());
        }
    }

    private void changeBubble(String number) {
        if (!number.isEmpty() && bubbles.containsKey(number)) {
            bubbles.get(number).changeImageView();
        }
    }
}