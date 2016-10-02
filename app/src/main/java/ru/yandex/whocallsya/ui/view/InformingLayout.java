package ru.yandex.whocallsya.ui.view;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import java.util.List;

import ru.yandex.whocallsya.R;
import ru.yandex.whocallsya.service.CockyBubblesService;
import ru.yandex.whocallsya.service.SearchAsyncTask;
import ru.yandex.whocallsya.ui.adapter.SearchAdapter;
import ru.yandex.whocallsya.ui.adapter.SearchItemDivider;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;
import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.graphics.PixelFormat.TRANSLUCENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;

public class InformingLayout {
    final private WindowManager windowManager;
    final private CockyBubblesService service;
    final private ViewGroup windowLayout;
    final private RecyclerView recyclerView;
    private boolean showed = false;


    public InformingLayout(final CockyBubblesService service, String phone) {
        this.service = service;
        windowManager = (WindowManager) service.getSystemService(WINDOW_SERVICE);

        LayoutInflater layoutInflater = (LayoutInflater) service.getSystemService(LAYOUT_INFLATER_SERVICE);
        windowLayout = (ViewGroup) layoutInflater.inflate(R.layout.info_layout, null);

        TextView phoneNumber = (TextView) windowLayout.findViewById(R.id.phone_number);
        phoneNumber.setText(phone);
        recyclerView = (RecyclerView) windowLayout.findViewById(R.id.recycler_view_search);
    }

    public void show() {
        if (!showed) {
            LayoutParams windowParams = new LayoutParams(
                    WRAP_CONTENT,
                    WRAP_CONTENT,
                    TYPE_SYSTEM_ERROR,
                    FLAG_NOT_FOCUSABLE | FLAG_SHOW_WHEN_LOCKED,
                    TRANSLUCENT
            );
            windowParams.gravity = Gravity.TOP;
            windowParams.y = dpToPx(8);
            windowManager.addView(windowLayout, windowParams);
            showed = true;
        }
    }

    public void unShow() {
        if (showed) {
            windowManager.removeViewImmediate(windowLayout);
            showed = false;
        }
    }


    public void setPreview(final List<SearchAsyncTask.Response> responses) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.addItemDecoration(new SearchItemDivider(recyclerView.getContext()));
        recyclerView.setAdapter(new SearchAdapter(responses, position -> {
            Intent intent = new Intent(ACTION_VIEW, Uri.parse(responses.get(position).getUrl()));
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            service.startActivity(intent);
            //TODO не выйдет, надо же еще кнопку поменять. нужны колбеки в сервис
            unShow();
        }));
    }

    public boolean isShowed() {
        return showed;
    }

    private int dpToPx(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}