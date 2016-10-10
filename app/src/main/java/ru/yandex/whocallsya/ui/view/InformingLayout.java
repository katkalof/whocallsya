package ru.yandex.whocallsya.ui.view;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import ru.yandex.whocallsya.R;
import ru.yandex.whocallsya.bubble.BubbleBaseLayout;
import ru.yandex.whocallsya.service.SearchAsyncTask;
import ru.yandex.whocallsya.ui.adapter.SearchAdapter;
import ru.yandex.whocallsya.ui.adapter.SearchItemDivider;


public class InformingLayout extends BubbleBaseLayout {

    private RecyclerView recyclerView;
    private TextView TextPhoneNumber;
    private boolean showed;

    public InformingLayout(Context context) {
        super(context);
    }

    public InformingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InformingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        TextPhoneNumber = (TextView) findViewById(R.id.phone_number);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_search);
    }

    public void setData(String phone) {
        final String searchString = "https://yandex.ru/search/?text=" + phone;

        new SearchAsyncTask(this).execute(searchString);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            phone = PhoneNumberUtils.formatNumber(phone, getCurrentCountryCode());
        } else {
            phone = PhoneNumberUtils.formatNumber(phone);
        }
        TextPhoneNumber.setText(phone);
    }

    public void show() {
        if (!showed) {
            showed = true;
            setVisibility(View.VISIBLE);
        }
    }

    public void unShow() {
        if (showed) {
            showed = false;
            setVisibility(View.GONE);
        }
    }

    public void setPreview(final List<SearchAsyncTask.Response> responses) {
        Log.d("INFORMING_LAYOUT", "SetPreviewresponses");
        Log.d("INFORMING_LAYOUT", TextPhoneNumber.getText().toString());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SearchItemDivider(getContext()));
        recyclerView.setAdapter(new SearchAdapter(responses, position -> {
//            Intent intent = new Intent(ACTION_VIEW, Uri.parse(responses.get(position).getUrl()));
//            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//            service.startActivity(intent);
            Log.d("INFORMING_LAYOUT", "click-" + position);
            //TODO не выйдет, надо же еще кнопку поменять. нужны колбеки в сервис
        }));
        new Handler(Looper.getMainLooper()).post(this::requestLayout);
    }

    public boolean isShowed() {
        return showed;
    }

    private String getCurrentCountryCode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return getResources().getConfiguration().getLocales().get(0).getCountry();
        } else {
            return getResources().getConfiguration().locale.getCountry();
        }
    }

}