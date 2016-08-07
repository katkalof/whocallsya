package ru.yandex.whocallsya.ui.view;

import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import ru.yandex.whocallsya.R;
import ru.yandex.whocallsya.service.CockyBubblesService;
import ru.yandex.whocallsya.service.SearchAsyncTask;

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
    final private TextView infoTitle;
    final private ImageView infoIcon;
    final private TextView infoDesc;
    private boolean showed = false;


    public InformingLayout(final CockyBubblesService service, String phone) {
        this.service = service;
        windowManager = (WindowManager) service.getSystemService(WINDOW_SERVICE);

        LayoutInflater layoutInflater = (LayoutInflater) service.getSystemService(LAYOUT_INFLATER_SERVICE);
        windowLayout = (ViewGroup) layoutInflater.inflate(R.layout.info_layout, null);

        TextView phoneNumber = (TextView) windowLayout.findViewById(R.id.phone_number);
        phoneNumber.setText(phone);
        infoTitle = (TextView) windowLayout.findViewById(R.id.info_title);
        infoIcon = (ImageView) windowLayout.findViewById(R.id.info_to_browser);
        infoDesc = (TextView) windowLayout.findViewById(R.id.info_desc);
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


    public void setPreview(final SearchAsyncTask.Response response) {
        infoTitle.setText(response.getTitle());
        infoIcon.setOnClickListener(view -> {
            Intent intent = new Intent(ACTION_VIEW, Uri.parse(response.getLink()));
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            service.startActivity(intent);
        });
        infoDesc.setText(response.getDescription());
    }

    public boolean isShowed() {
        return showed;
    }


}