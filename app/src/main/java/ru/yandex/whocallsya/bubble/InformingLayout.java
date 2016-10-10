package ru.yandex.whocallsya.bubble;

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

import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import ru.yandex.whocallsya.R;
import ru.yandex.whocallsya.network.MblzApi;
import ru.yandex.whocallsya.network.SearchItem;
import ru.yandex.whocallsya.network.pojo.YandexSearch;
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
        // TODO: 10.10.2016 Добавить Проверка на открытый лейаут с тем же номером
        String formattedPhone;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            formattedPhone = PhoneNumberUtils.formatNumber(phone, getCurrentCountryCode());
        } else {
            formattedPhone = PhoneNumberUtils.formatNumber(phone);
        }
        TextPhoneNumber.setText(formattedPhone);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getContext().getString(R.string.ApiHttp))
                .addConverterFactory(SimpleXmlConverterFactory.create(new Persister(new AnnotationStrategy())))
                .build();
        phone = "hello";  // TODO: 10.10.2016 REMOVE, for testing
        String token = phone + getContext().getString(R.string.ApiSalt);
        Log.d("whocallsya", "InfoLayout " + phone + " " + md5(token));
        Call<YandexSearch> repos = retrofit.create(MblzApi.class).getSearchItems(phone, md5(token));
        repos.enqueue(new Callback<YandexSearch>() {
            @Override
            public void onResponse(Call<YandexSearch> call, Response<YandexSearch> response) {
                // TODO: 10.10.2016 Преобразовать в модель для адаптера и просетить в него, показать
                Log.e("whocallsya", "InfoLayout onResponse good");
            }

            @Override
            public void onFailure(Call<YandexSearch> call, Throwable t) {
                // TODO: 10.10.2016 Ошибку показать
                Log.e("whocallsya", "InfoLayout onFailure " + t);
            }
        });

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

    public void setPreview(final List<SearchItem> searchItems) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new SearchItemDivider(getContext()));
        recyclerView.setAdapter(new SearchAdapter(searchItems, position -> {
//            Intent intent = new Intent(ACTION_VIEW, Uri.parse(searchItems.get(position).getUrl()));
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

    private String md5(String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
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

}