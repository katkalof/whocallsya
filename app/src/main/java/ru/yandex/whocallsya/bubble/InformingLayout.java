package ru.yandex.whocallsya.bubble;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import ru.yandex.whocallsya.R;
import ru.yandex.whocallsya.network.MblzApi;
import ru.yandex.whocallsya.network.SearchItem;
import ru.yandex.whocallsya.network.pojo.YandexGroup;
import ru.yandex.whocallsya.ui.adapter.SearchAdapter;
import ru.yandex.whocallsya.ui.adapter.SearchItemDivider;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class InformingLayout extends BubbleBaseLayout {

    private WeakReference<RecyclerView> recyclerView;
    private TextView textPhoneNumber;
    private View loadingView;
    private Button buttonError;
    private boolean shown;
    private Retrofit retrofit;
    private String lastSearchingNumber = "";


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
        textPhoneNumber = (TextView) findViewById(R.id.phone_number);
        textPhoneNumber = (TextView) findViewById(R.id.phone_number);
        loadingView = findViewById(R.id.loading_view);
        buttonError = (Button) findViewById(R.id.button_error);
        buttonError.setOnClickListener(v -> {
            if (!lastSearchingNumber.isEmpty()) {
                setData(lastSearchingNumber);
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SearchItemDivider(getContext()));
        this.recyclerView = new WeakReference<>(recyclerView);

        retrofit = new Retrofit.Builder()
                .baseUrl(getContext().getString(R.string.ApiHttp))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create(new Persister(new AnnotationStrategy())))
                .build();
    }


    public void setData(String phone) {
        if (!phone.equals(lastSearchingNumber) || buttonError.getVisibility() != GONE) {
            downloadSearchResponse(phone);
        }
    }

    private void downloadSearchResponse(String phone) {
        lastSearchingNumber = phone;
        loadingView.setVisibility(VISIBLE);
        buttonError.setVisibility(GONE);
        recyclerView.get().setVisibility(GONE);
        String formattedPhone;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            formattedPhone = PhoneNumberUtils.formatNumber(phone, getCurrentCountryCode());
        } else {
            formattedPhone = PhoneNumberUtils.formatNumber(phone);
        }
        textPhoneNumber.setText(formattedPhone);

        String token = phone + getContext().getString(R.string.ApiSalt);
        Log.d("whocallsya", "InfoLayout " + phone + " " + md5(token));

        retrofit.create(MblzApi.class).getSearchItems(phone, md5(token))
                .subscribeOn(Schedulers.io())
                .flatMapIterable(yandexSearch -> yandexSearch.getGrouping().getList())
                .flatMap(groupToSearchItem)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        searchItems -> {
                            Log.d("whocallsya", "InfoLayout onResponse good");
                            RecyclerView recyclerView = this.recyclerView.get();
                            if (recyclerView != null) {
                                recyclerView.setAdapter(new SearchAdapter(searchItems, position -> {
                                    Intent intent = new Intent(ACTION_VIEW, Uri.parse(searchItems.get(position).getUrl().toString()));
                                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                    getContext().startActivity(intent);
                                    getLayoutCoordinator().changeLastBubble();
                                    unShow();
                                }));
                                loadingView.setVisibility(GONE);
                                recyclerView.setVisibility(VISIBLE);
                            }
                        },
                        e -> {
                            Log.e("whocallsya", "InfoLayout onFailure " + e);
                            buttonError.setVisibility(VISIBLE);
                            loadingView.setVisibility(GONE);
                        }
                );
    }

    public void show() {
        if (!shown) {
            shown = true;
            setVisibility(View.VISIBLE);
        }
    }

    public void unShow() {
        if (shown) {
            shown = false;
            setVisibility(View.GONE);
        }
    }

    public boolean isOpen() {
        return shown;
    }

    public String getLastSearchingNumber() {
        return lastSearchingNumber;
    }

    private String getCurrentCountryCode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return getResources().getConfiguration().getLocales().get(0).getCountry();
        } else {
            return getResources().getConfiguration().locale.getCountry();
        }
    }

    private String md5(String s) {
        final String MD5 = "MD5";
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private Func1<YandexGroup, Observable<SearchItem>> groupToSearchItem = group -> {

        SpannableStringBuilder desc;
        if (group.getDoc().getPassages() == null) {
            desc = new SpannableStringBuilder(group.getDoc().getHeadline());
        } else {
            desc = group.getDoc().getPassages().getPassagesList().get(0);
        }

        SearchItem searchItem = new SearchItem(
                group.getDoc().getTitle(),
                new SpannableStringBuilder(group.getDoc().getUrl()),
                desc);

        return Observable.just(searchItem);

    };

}