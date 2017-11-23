package com.apreciasoft.admin.asremis.Http;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Admin on 01-01-2017.
 */

public class HttpConexion {


    public static String base;
    //public static String ip = "192.168.0.3";
    public  static String instance = "developer";
     public static String ip = "54.245.175.10";



    public static final String BASE_URL = "http://"+ip+"/";

        public static Retrofit retrofit = null;

        public  static void  setBase(String folder)
        {
            HttpConexion.base =  folder;
        }

   /* public static  String getBase()
    {
       return HttpConexion.base;
    }*/

        public static Retrofit getUri() {


            retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL+base+"/Api/index.php/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();




            return retrofit;
        }
}
