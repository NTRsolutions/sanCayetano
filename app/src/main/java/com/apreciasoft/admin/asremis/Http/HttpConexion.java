package com.apreciasoft.admin.asremis.Http;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Admin on 01-01-2017.
 */

public class HttpConexion {


    public static String base;
    public static String ip = "192.168.0.4";/*public static String ip = "as-nube.com";*/
    public  static String instance = "as_remis_web";
    public static int portWsWeb = 8085;
    public static int portWsCliente = 3000;
    public  static  String PROTOCOL = "http";


    public static final String BASE_URL = PROTOCOL+"://"+ip+"/";

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
