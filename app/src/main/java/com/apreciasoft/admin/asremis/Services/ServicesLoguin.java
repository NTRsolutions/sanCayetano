package com.apreciasoft.admin.asremis.Services;

import com.apreciasoft.admin.asremis.Entity.DriverFull;
import com.apreciasoft.admin.asremis.Entity.RequetClient;
import com.apreciasoft.admin.asremis.Entity.client;
import com.apreciasoft.admin.asremis.Entity.login;
import com.apreciasoft.admin.asremis.Entity.reporte;
import com.apreciasoft.admin.asremis.Entity.token;
import com.apreciasoft.admin.asremis.Entity.userFull;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Admin on 01-01-2017.
 */

public interface ServicesLoguin {

    @Headers("Content-Type: application/json")
    @POST("auth")
    Call<userFull> getUser(@Body login user);

    @Headers("Content-Type: application/json")
    @POST("support/add")
    Call<reporte> reporteFalla(@Body reporte falla);

    @Headers("Content-Type: application/json")
    @POST("auth/token")
    Call<Boolean> token(@Body token token);

    @Headers("Content-Type: application/json")
    @POST("auth/updateSocketWeb")
    Call<Boolean> updateSocketWeb(@Body token token);

    @Headers("Content-Type: application/json")
    @POST("auth/updateClientLiteMobil")
    Call<client> updateClientLiteMobil(@Body client cl);

    @Headers("Content-Type: application/json")
    @GET("client/find/{id}")
    Call<RequetClient> find(@Path("id") int id);

    @Headers("Content-Type: application/json")
    @GET("auth/checkVersion/{ver}")
    Call<Boolean> checkVersion(@Path("ver") String ver);
}
