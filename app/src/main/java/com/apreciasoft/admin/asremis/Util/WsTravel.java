package com.apreciasoft.admin.asremis.Util;


import android.app.Activity;
import android.os.Build;
import android.util.Log;

import com.apreciasoft.admin.asremis.Activity.HomeClientActivity;
import com.apreciasoft.admin.asremis.Entity.TravelLocationEntity;
import com.apreciasoft.admin.asremis.Http.HttpConexion;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

/**
 * Created by Admin on 08/01/2017.
 */

public class WsTravel extends Activity {

    public  WebSocketClient mWebSocketClient;
    public GlovalVar gloval;

    public  void connectWebSocket(int idUser) {
        URI uri;
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

        mWebSocketClient.connect();
    }

    public void coseWebSocket() {
        mWebSocketClient.close();
        Log.d("Websocket","closee");
    }


}
