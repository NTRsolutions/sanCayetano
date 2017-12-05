package com.apreciasoft.admin.asremis.Util;


import android.app.Activity;
import android.os.Build;
import android.util.Log;

import com.apreciasoft.admin.asremis.Activity.HomeClientActivity;
import com.apreciasoft.admin.asremis.Entity.TravelLocationEntity;
import com.apreciasoft.admin.asremis.Fracments.HomeFragment;
import com.apreciasoft.admin.asremis.Http.HttpConexion;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Admin on 08/01/2017.
 */

public class WsTravel extends Activity {

    //public  WebSocketClient mWebSocketClient;
    public static Socket mWebSocketClient;
    public static String URL_SOCKET;
    public static String MY_EVENT = "message";

    public GlovalVar gloval;

    public  void connectWebSocket(int idUser) {

        try{
        /* Instance object socket */

            WsTravel.URL_SOCKET =  HttpConexion.PROTOCOL+"://"+HttpConexion.ip+":"+HttpConexion.portWsCliente+"?idUser="+idUser+"&uri="+ HttpConexion.base;
            Log.d("SOCKET IO","va a conectar: "+URL_SOCKET);

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            IO.setDefaultSSLContext(sc);
            HttpsURLConnection.setDefaultHostnameVerifier(new HomeFragment.RelaxedHostNameVerifier());


            IO.Options options = new IO.Options();
            options.sslContext = sc;
            options.secure = true;
            options.port = HttpConexion.portWsCliente;

            mWebSocketClient = IO.socket(URL_SOCKET,options);



            mWebSocketClient.on(Socket.EVENT_CONNECT, new Emitter.Listener(){
                @Override
                public void call(Object... args) {
                /* Our code */
                    Log.d("SOCKET IO","CONECT");


                    // variable global //
                   // gloval = ((GlovalVar) HomeClientActivity.gloval);

                    //gloval.setLocationDriverFromClient("");

                }
            }).on(mWebSocketClient.EVENT_DISCONNECT, new Emitter.Listener(){
                @Override
                public void call(Object... args) {
                /* Our code */
                    Log.d("SOCKET IO","DISCONESCT");
                }
            });



            mWebSocketClient.emit(MY_EVENT, new Ack() {

                @Override
                public void call(Object... args) {
             /* Our code */

                    Log.d("SOCK IO"," SOCKET NOTIFICO ");
                }
            });

            mWebSocketClient.connect();

        }catch (URISyntaxException e){
            Log.d("SOCK IO",e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            Log.d("SOCK IO",e.getMessage());
        } catch (KeyManagementException e) {
            Log.d("SOCK IO",e.getMessage());
        }

       /* URI uri;
        try {
            uri = new URI("wss://"+HttpConexion.ip+":3389?idUser="+idUser+"&uri="+ HttpConexion.base);

            Log.d("Websocket", String.valueOf(uri));
        } catch (URISyntaxException e) {

            Log.d("Websocket",  e.getMessage());
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri,new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.d("Websocket", "Opened");

                String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                mWebSocketClient.send(mydate+" - CONECATADO DESDE UN (*" + Build.MANUFACTURER + "*)" + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                       Log.d("Websocket m",message);
                       // mWebSocketClient.send("hola menor");


                        GsonBuilder builder = new GsonBuilder();
                         Gson gson = builder.create();

                        TravelLocationEntity info = new Gson().fromJson(message, TravelLocationEntity.class);

                        // variable global //
                        gloval = ((GlovalVar) HomeClientActivity.gloval);

                        gloval.setLocationDriverFromClient(info);

                       // HomeClientFragment.a

                    }
                });
            }



            @Override
            public void onClose(int i, String s, boolean b) {
                Log.d("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.d("Websocket", "Error " + e.getMessage());
            }
        };

        mWebSocketClient.connect();*/
    }

    private TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[] {};
        }

        public void checkClientTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {
        }
    } };

    public static class RelaxedHostNameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }


    public void coseWebSocket() {
        mWebSocketClient.close();
        Log.d("Websocket","closee");
    }


}
