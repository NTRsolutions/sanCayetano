package com.apreciasoft.mobile.SanCayetano.Services;

import com.apreciasoft.mobile.SanCayetano.Entity.Company;
import com.apreciasoft.mobile.SanCayetano.Entity.DriverCurrentAcountEntity;
import com.apreciasoft.mobile.SanCayetano.Entity.DriverFull;
import com.apreciasoft.mobile.SanCayetano.Entity.InfoTravelEntity;
import com.apreciasoft.mobile.SanCayetano.Entity.RequetClient;
import com.apreciasoft.mobile.SanCayetano.Entity.acountCompany;
import com.apreciasoft.mobile.SanCayetano.Entity.costCenterCompany;
import com.apreciasoft.mobile.SanCayetano.Entity.dataAddPlusDriverEntity;
import com.apreciasoft.mobile.SanCayetano.Entity.driver;
import com.apreciasoft.mobile.SanCayetano.Entity.fleetType;
import com.apreciasoft.mobile.SanCayetano.Entity.modelEntity;
import com.apreciasoft.mobile.SanCayetano.Entity.resp;
import com.apreciasoft.mobile.SanCayetano.Entity.responseFilterVehicle;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Admin on 19/1/2017.
 */

public interface ServicesDriver {

    @Headers("Content-Type: application/json")
    @GET("driver/getAllTravel/{id}")
    Call<List<InfoTravelEntity>> getAllTravel(@Path("id") int id);

    @Headers("Content-Type: application/json")
    @GET("travel/travelsByIdUser/{id}")
    Call<List<InfoTravelEntity>> getAllTravelClient(@Path("id") int id);

    @Headers("Content-Type: application/json")
    @GET("driver/inactive/{id}")
    Call<Boolean> inactive(@Path("id") int id);

    @Headers("Content-Type: application/json")
    @GET("driver/active/{id}")
    Call<Boolean> active(@Path("id") int id);

    @Headers("Content-Type: application/json")
    @GET("invoice/listLiquidationDriver/{id}")
    Call<DriverCurrentAcountEntity> listLiquidationDriver(@Path("id") int id);

    @Headers("Content-Type: application/json")
    @POST("driver/updateLiteMobil")
    Call<driver> updateLiteMobil(@Body driver dr);


    @Headers("Content-Type: application/json")
    @GET("driver/find/{id}")
    Call<DriverFull> find(@Path("id") int id);

    @Headers("Content-Type: application/json")
    @GET("Brand")
    Call<List<modelEntity>> filterForm();

    @Headers("Content-Type: application/json")
    @GET("fleetType")
    Call<List<fleetType>> filterFormfleetType();


    @Headers("Content-Type: application/json")
    @GET("model/byidBrand/{id}")
    Call<responseFilterVehicle> getModelDetail(@Path("id") int id);

    @Headers("Content-Type: application/json")
    @POST("driver/plusLite")
    Call<Integer> addPluDriver(@Body dataAddPlusDriverEntity data);

    @Headers("Content-Type: application/json")
    @POST("client")
    Call<resp> addClient(@Body RequetClient data);

    @Headers("Content-Type: application/json")
    @GET("company/validatorDomaint/{mail}")
    Call<List<Company>> validatorDomaint(@Path("mail") String mail);

    @Headers("Content-Type: application/json")
    @GET("company/costCenterByidAcount/{id}")
    Call<List<costCenterCompany>> costCenterByidAcount(@Path("id") int id);

    @Headers("Content-Type: application/json")
    @GET("company/getAcountByidCompany/{id}")
    Call<List<acountCompany>> getAcountByidCompany(@Path("id") int id);



}
