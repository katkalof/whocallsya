package ru.yandex.whocallsya.network;

import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.yandex.whocallsya.network.pojo.YandexSearch;
import rx.Observable;

public interface MblzApi {

    @GET("search/xml")
    Observable<YandexSearch> getSearchItems(@Query("query") String query, @Query("token") String token);

}