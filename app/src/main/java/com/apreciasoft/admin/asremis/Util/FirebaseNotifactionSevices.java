package com.apreciasoft.admin.asremis.Util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.apreciasoft.admin.asremis.Activity.HomeActivity;
import com.apreciasoft.admin.asremis.Activity.HomeClientActivity;
import com.apreciasoft.admin.asremis.Entity.InfoTravelEntity;
import com.apreciasoft.admin.asremis.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.ExecutionException;

/**
 * Created by jorge gutierrez on 13/02/2017.
 */

public class FirebaseNotifactionSevices extends FirebaseMessagingService {


    public static final String TAG = "NOTICIAS";
    public GlovalVar gloval;
    public Uri soundUri;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        try {

            String from = remoteMessage.getFrom();
            Log.d(TAG, "Notificación: " + from);

            if (remoteMessage.getNotification() != null) {
                Log.d(TAG, "Notificación: " + remoteMessage.getNotification().getBody());

            }

            if (remoteMessage.getData().size() > 0) {

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();

                gloval = ((GlovalVar) getApplicationContext());

                System.out.println(gson.toJson(remoteMessage.getData()));
                gloval.setGv_travel_current(gson.fromJson(gson.toJson(remoteMessage.getData()), InfoTravelEntity.class));


                Intent intent = new Intent("update-message");
                // intent.putExtra("message", gson.fromJson(gson.toJson(remoteMessage.getData()), InfoTravelEntity.class));
                intent.putExtra("message", "CANGUE INFO FIREBASE (ASREMIS)");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


                if (gloval.getGv_id_profile() == 2 || gloval.getGv_id_profile() == 5) {
                    Log.d("Notificación", String.valueOf("YESS"));

                    mostrarNotificacion(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), HomeClientActivity.class);

                } else {
                    Log.d("Notificación", String.valueOf("YESS"));

                    mostrarNotificacion(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), HomeActivity.class);

                }


            }
        }catch (Exception e){
            Log.d("ERROR" , e.getMessage());
        }

    }

    private void mostrarNotificacion(String title, String body,Class<?> cls) {

        Context context = this;

        Intent intent;

        if(gloval.getGv_id_profile() == 2 || gloval.getGv_id_profile() == 5)
        {
             intent = new Intent(context, HomeClientActivity.class);
        }else
        {
             intent = new Intent(context, HomeActivity.class);
        }



        //intent.putExtra(".HomeActivity");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);


       // try {
            // Perform the operation associated with our pendingIntent
           // pendingIntent.send();
            Log.d("NOTIFICATE", "Abrir");
        //} catch (PendingIntent.CanceledException e) {
          //  e.printStackTrace();
        //}

        if(gloval.getGv_travel_current().getSound() != null) {
            switch (gloval.getGv_travel_current().getSound()) {
                case "nuevareserva":
                    soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.nuevareserva);//Here is FILE_NAME is the name of file that you want to play
                case "nuevoviaje":
                    soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.nuevoviaje);//Here is FILE_NAME is the name of file that you want to play
                case "remis":
                    soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.remis);//Here is FILE_NAME is the name of file that you want to play
                case "remis2":
                    soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.remis2);//Here is FILE_NAME is the name of file that you want to play
                case "tienesreserva":
                    soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.tienesreserva);//Here is FILE_NAME is the name of file that you want to play

                default:
                    soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.remis);//Here is FILE_NAME is the name of file that you want to play

            }
        }else{
            soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.remis);//Here is FILE_NAME is the name of file that you want to play
        }


        Log.d("Notificación", String.valueOf(soundUri));


        if(gloval.getGv_travel_current() != null)
        {
           if(gloval.getGv_travel_current().getNameOrigin() != null) {

                Log.d("Notificación", String.valueOf("FINAL"));

                // Patrón de vibración: 1 segundo vibra, 0.5 segundos para, 1 segundo vibra
                 long[] pattern = new long[]{1000,500,1000};




                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                        .setContentTitle(title)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentText(gloval.getGv_travel_current().getNameOrigin())
                        .setAutoCancel(true)
                        .setSound(soundUri)
                        .setContentIntent(pendingIntent);

                // Uso en API 11 o mayor
                notificationBuilder.setVibrate(pattern);


                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notificationBuilder.build());




            }
        }



    }
}
