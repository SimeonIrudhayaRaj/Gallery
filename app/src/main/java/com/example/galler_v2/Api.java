package com.example.galler_v2;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {
    String BASE_URL = "https://api.flickr.com/";
    @GET("services/rest/")
    Call<String> getUrls(
            @Query("method") String m,
            @Query("per_page") int ppage,
            @Query("page") int page,
            @Query("api_key") String key,
            @Query("format") String format,
            @Query("nojsoncallback") int i,
            @Query("extras") String s
    );


    @GET("services/rest/")
    Call<String> getSUrls(
            @Query("method") String m,
            @Query("api_key") String key,
            @Query("format") String format,
            @Query("nojsoncallback") int i,
            @Query("extras") String s,
            @Query("text") String search
    );
}
