package ru.atomofiron.translator.Utils.Retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    @GET("/api/v1.5/tr.json/getLangs")
    Call<LangsResponse> getLangs(@Query("key") String key, @Query("ui") String code);

    @GET("/api/v1.5/tr.json/detect")
    Call<DetectResponse> detect(@Query("key") String key, @Query("text") String text);
}
