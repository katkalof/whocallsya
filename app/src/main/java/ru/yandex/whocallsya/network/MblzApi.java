package ru.yandex.whocallsya.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.yandex.whocallsya.network.pojo.YandexSearch;

public interface MblzApi {
    //    @GET("search/xml?query={query}&token={token}")
//    Call<List<SearchItem>> getSearchItems(@Path("query") String query, @Path("token") String token);
    @GET("search/xml")
    Call<YandexSearch> getSearchItems(@Query("query") String query, @Query("token") String token);
}