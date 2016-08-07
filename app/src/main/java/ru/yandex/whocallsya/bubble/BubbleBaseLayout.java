package ru.yandex.whocallsya.bubble;

import android.content.Context;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class BubbleBaseLayout extends FrameLayout {
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private BubblesLayoutCoordinator layoutCoordinator;

    public void setLayoutCoordinator(BubblesLayoutCoordinator layoutCoordinator) {
        this.layoutCoordinator = layoutCoordinator;
    }

    BubblesLayoutCoordinator getLayoutCoordinator() {
        return layoutCoordinator;
    }

    public void setWindowManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    WindowManager getWindowManager() {
        return this.windowManager;
    }

    public void setViewParams(WindowManager.LayoutParams params) {
        this.params = params;
    }

    public WindowManager.LayoutParams getViewParams() {
        return this.params;
    }

    public BubbleBaseLayout(Context context) {
        super(context);
    }

    public BubbleBaseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BubbleBaseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
