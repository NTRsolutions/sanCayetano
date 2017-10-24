package com.apreciasoft.admin.asremis.Activity;

import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apreciasoft.admin.asremis.Dialog.TravelDialog;
import com.apreciasoft.admin.asremis.Entity.InfoTravelEntity;
import com.apreciasoft.admin.asremis.Entity.RemisSocketInfo;
import com.apreciasoft.admin.asremis.Entity.TraveInfoSendEntity;
import com.apreciasoft.admin.asremis.Entity.TravelLocationEntity;
import com.apreciasoft.admin.asremis.Entity.notification;
import com.apreciasoft.admin.asremis.Entity.token;
import com.apreciasoft.admin.asremis.Entity.tokenFull;
import com.apreciasoft.admin.asremis.Fracments.AcountDriver;
import com.apreciasoft.admin.asremis.Fracments.HistoryTravelDriver;
import com.apreciasoft.admin.asremis.Fracments.HomeFragment;
import com.apreciasoft.admin.asremis.Fracments.NotificationsFrangment;
import com.apreciasoft.admin.asremis.Fracments.ProfileClientFr;
import com.apreciasoft.admin.asremis.Fracments.ProfileDriverFr;
import com.apreciasoft.admin.asremis.Fracments.ReservationsFrangment;
import com.apreciasoft.admin.asremis.Http.HttpConexion;
import com.apreciasoft.admin.asremis.R;
import com.apreciasoft.admin.asremis.Services.ServicesDriver;
import com.apreciasoft.admin.asremis.Services.ServicesLoguin;
import com.apreciasoft.admin.asremis.Services.ServicesTravel;
import com.apreciasoft.admin.asremis.Util.GlovalVar;
import com.apreciasoft.admin.asremis.Util.RequestHandler;
import com.apreciasoft.admin.asremis.Util.Signature;
import com.apreciasoft.admin.asremis.Util.Tiempo;
import com.apreciasoft.admin.asremis.Util.Utils;
import com.apreciasoft.admin.asremis.Util.WsTravel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    public static final String UPLOAD_URL =  HttpConexion.BASE_URL+HttpConexion.base+"/Frond/safeimg.php";
    public static final String UPLOAD_KEY = "image";

    public GlovalVar gloval;

    public ServicesTravel daoTravel = null;
    public ServicesLoguin daoLoguin = null;
    public ServicesDriver daoDriver = null;


    public Timer timer;
    public ProgressDialog loadingCronometro;
    public static final int SIGNATURE_ACTIVITY = 1;
    public static final int PROFILE_DRIVER_ACTIVITY = 2;
    public static final int CREDIT_CAR_ACTIVITY = 3;

    public static  InfoTravelEntity currentTravel;

    public double km_total =  0.001;
    public double m_total  = 0;
    public double kilometros_total = m_total*km_total;

    public double km_vuelta =  0.001;
    public double m_vuelta  = 0;
    public double kilometros_vuelta = m_vuelta*km_vuelta;

    public double km_ida =  0.001;
    public double m_ida  = 0;
    public double kilometros_ida = m_vuelta*km_vuelta;



    public DecimalFormat df = new DecimalFormat("####0.00");
    public double amounCalculateGps;
    protected PowerManager.WakeLock wakelock;
    public boolean viewAlert = false;
    private DatabaseReference databaseReference;//defining a database reference
    public Bitmap bitmap;
    private Uri filePath;
    public String path_image;
    public WsTravel ws = null;
    public Tiempo tiempo = new Tiempo();
    public int tiempoTxt = 0;
    public int idPaymentFormKf = 0;

    public  Button btnPreFinish;
    public  Button btnReturn;
    public static boolean isRoundTrip =  false;
    double extraTime  = 0;

    public  TextView textTiempo;
    public FloatingActionButton fab =  null;

    public View parentLayout =  null;

    int PARAM_20  = 0;
    int PARAM_39  = 0; // ACTIVAR BOTON DE VUELTA
    public static  int param25 = 0;

    /*DIALOG*/
    public TravelDialog dialogTravel = null;

    /* CONSTATES PARA PERMISOS */
    private static final int ACCESS_FINE_LOCATION_PERMISSIONS = 123;


    public  Button btnFinishCar;
    public  Button btnFinishVo;
    public  Button btnFinishCash;
    public    SharedPreferences.Editor editor;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        SharedPreferences pref = getApplicationContext().getSharedPreferences(HttpConexion.instance, 0); // 0 - for private mode
        editor = pref.edit();

        //evitar que la pantalla se apague
        final PowerManager pm=(PowerManager)getSystemService(Context.POWER_SERVICE);
        this.wakelock=pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "etiqueta");
        wakelock.acquire();

        setContentView(R.layout.activity_home);
         parentLayout = findViewById(R.id.content_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        // variable global //
        gloval = ((GlovalVar)getApplicationContext());

        param25 = Integer.parseInt(gloval.getGv_param().get(25).getValue());// SE PUEDE VER PRECIO EN VIAJE EN APP


        PARAM_20 =  Integer.parseInt(gloval.getGv_param().get(19).getValue());// PRECIO DE LISTA

        // BOTON PARA PRE FINALIZAR UN VIAJE //
        btnPreFinish = (Button) findViewById(R.id.btn_pre_finish);
        btnPreFinish.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                try {
                    showFinshTravel();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // BOTON PARA PONER RETORNO DE  UN VIAJE //
        btnReturn = (Button) findViewById(R.id.btn_return);

        PARAM_39 = Integer.parseInt(gloval.getGv_param().get(38).getValue());
        if(PARAM_39 == 1)
        {
            btnReturn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click

                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    builder.setMessage("Presione 'Retornar'  Confirmar el  Retorno del Viaje!")
                            .setCancelable(false)
                            .setPositiveButton("Retornar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    isRoundTrip();
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
            });
        }else
        {
            btnReturn.setEnabled(false);
        }





        // BOTON PARA FINALIZAR UN VIAJE TARJETA //
        btnFinishCar = (Button) findViewById(R.id.bnt_pay_car);
        btnFinishCar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                idPaymentFormKf = 3;
                finishTravelCreditCar();
            }
        });

        // BOTON PARA FINALIZAR UN VIAJE VOUCHER //
        btnFinishVo = (Button) findViewById(R.id.btn_firm_voucher);
        btnFinishVo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                idPaymentFormKf = 5;
                finishTravelVouche();
            }
        });



        // BOTON PARA FINALIZAR UN VIAJE CASH //
        btnFinishCash = (Button) findViewById(R.id.btn_pay_cash);
        btnFinishCash.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                idPaymentFormKf = 4;
                finishTravel();
            }
        });

        // BOTON PARA INICIAR UN VIAJE //
        final Button btnInit = (Button) findViewById(R.id.btn_init);
        btnInit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                initTravel();
            }
        });

        // BOTON PARA CANCELAR UN VIAJE //
        final Button btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                cancelTravel(-1);
            }
        });




        // BOTON PARA INICIAR TIEMPO DE ESPERA DE UN VIAJE //
        final Button btnIniTimeSlep = (Button) findViewById(R.id.btn_iniTimeSlep);
        btnIniTimeSlep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                iniTimeSlep();
            }
        });



        /* KEY UP PEAJE  */
        final EditText peajes_txt = (EditText) findViewById(R.id.peajes_txt);
        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Log.d("afterTextChanged","afterTextChanged");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("beforeTextChanged","beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("onTextChanged","onTextChanged");
                    pay();

            }

            private boolean filterLongEnough() {
                return peajes_txt.getText().toString().trim().length() > 2;
            }
        };
        peajes_txt.addTextChangedListener(fieldValidatorTextWatcher);



         /* KEY UP ESTACIONAMIENTO   */
        final  EditText parkin_txt = (EditText) findViewById(R.id.parkin_txt);
        TextWatcher fieldValidatorTextWatcher2 = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Log.d("afterTextChanged","afterTextChanged");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("beforeTextChanged","beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("onTextChanged","onTextChanged");
                pay();

            }

            private boolean filterLongEnough() {
                return peajes_txt.getText().toString().trim().length() > 2;
            }
        };
        parkin_txt.addTextChangedListener(fieldValidatorTextWatcher2);



        //getting the database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        android.app.FragmentManager fr =  getFragmentManager();
        fr.beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();

       // ETOOO RECIEN COMENTADO 24/05/2017 OJOOOOOO
      //  android.app.FragmentManager fr =  getFragmentManager();
       // fr.beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();


        final LinearLayout lg = (LinearLayout) findViewById(R.id.payment);
        lg.setVisibility(View.INVISIBLE);

        // HEADER MENU //
        View header = navigationView.getHeaderView(0);
        TextView name = (TextView)header.findViewById(R.id.username);
        TextView email = (TextView)header.findViewById(R.id.email);
        name.setText(gloval.getGv_user_name());
        email.setText(gloval.getGv_user_mail());

        //btFinishVisible(false);

       // Llamamos que control si tenemos un viaje //
        controlVieTravel();

        //ACTIVAMOS EL TOkEN FIRE BASE//
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token: ", token);
        enviarTokenAlServidor(token,gloval.getGv_user_id());




        _activeTimer();

        textTiempo = (TextView) findViewById(R.id.textTiempo);
        textTiempo.setVisibility(View.INVISIBLE);



        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    changueStatusService();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


        //LLAMAMOS A EL METODO PARA HABILITAR PERMISOS//
        checkPermision();


        getPick(gloval.getGv_user_id());



    }


    public  void changueStatusService(){


        Call<Boolean> call = null;

        if (this.daoDriver == null) { this.daoDriver = HttpConexion.getUri().create(ServicesDriver.class); }

        try
        {
            if(gloval.getGv_srviceActive() == 1)
                {
                     call = this.daoDriver.inactive(gloval.getGv_id_driver());
                    gloval.setGv_srviceActive(0);
                }else
                {
                     call = this.daoDriver.active(gloval.getGv_id_driver());
                    gloval.setGv_srviceActive(1);
                }


            Log.d("Call request", call.request().toString());
            Log.d("Call request header", call.request().headers().toString());

            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                    Log.d("Call request", call.request().toString());
                    Log.d("Call request header", call.request().headers().toString());
                    Log.d("Response raw header", response.headers().toString());
                    Log.d("Response raw", String.valueOf(response.raw().body()));
                    Log.d("Response code", String.valueOf(response.code()));


                    if (response.code() == 200) {

                        //
                        String str = "";
                        if(gloval.getGv_srviceActive() == 1){
                            str = "Servicio Activado!";

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                fab.setImageDrawable(getResources().getDrawable(R.drawable.cast_ic_expanded_controller_pause, HomeActivity.this.getApplication().getBaseContext().getTheme()));
                            } else {
                                fab.setImageDrawable(getResources().getDrawable(R.drawable.cast_ic_expanded_controller_pause));
                            }

                            fab.setBackgroundTintList(ColorStateList.valueOf(Color
                                    .parseColor("#26c281")));
                        }else{
                            str = "Servicio Inactivado! 'No Recibira Viajes'";

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                fab.setImageDrawable(getResources().getDrawable(R.drawable.cast_ic_expanded_controller_play, HomeActivity.this.getApplication().getBaseContext().getTheme()));
                            } else {
                                fab.setImageDrawable(getResources().getDrawable(R.drawable.cast_ic_expanded_controller_play));
                            }

                            fab.setBackgroundTintList(ColorStateList.valueOf(Color
                                    .parseColor("#e35b5a")));
                        }

                        Snackbar.make(parentLayout , str, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        //
                    }
                    else {
                        AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                        alertDialog.setTitle("ERROR" + "(" + response.code() + ")");
                        alertDialog.setMessage(response.errorBody().source().toString());
                        Log.w("***", response.errorBody().source().toString());


                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }


                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                    alertDialog.setTitle("ERROR");
                    alertDialog.setMessage(t.getMessage());

                    Log.d("**", t.getMessage());

                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            });

        } finally {
            this.daoDriver = null;
        }



    }


    public void checkPermision()
    {
        LocationManager locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        // Comprobamos si está disponible el proveedor GPS.
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
            alertDialog.setTitle("GPS INACTIVO!");
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setMessage("Active el GPS para continuar!");


            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }// end if.

       /* if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            Toast.makeText(this, "This version is not Android 6 or later " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();

        } else {

            int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.CAMERA);

            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[] {Manifest.permission.CAMERA},
                        REQUEST_CODE_ASK_PERMISSIONS);

                Toast.makeText(this, "Requesting permissions", Toast.LENGTH_LONG).show();

            }else if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED){

                Toast.makeText(this, "The permissions are already granted ", Toast.LENGTH_LONG).show();
                openCamera();

            }

        }

        return;*/
    }

    public  void  showDialogTravel()
    {
        try
        {


            if(currentTravel.getIdSatatusTravel() == 0)
            {

                Toast.makeText(getApplicationContext(), "VIAJE Cancelado!", Toast.LENGTH_LONG).show();
                btPreFinishVisible(false);
                btnFlotingVisible(true);

                btInitVisible(false);
                btCancelVisible(false);

                viewAlert = false;
                currentTravel = null;
                HomeFragment.MarkerPoints = null;
                gloval.setGv_travel_current(null);
                setInfoTravel();

                tiempoTxt = 0;
                textTiempo = (TextView) findViewById(R.id.textTiempo);
                textTiempo.setVisibility(View.INVISIBLE);

               // _activeTimer();
            }
            else  if(currentTravel.getIdSatatusTravel() == 8)
            {
                Toast.makeText(getApplicationContext(), "VIAJE Cancelado por Cliente!", Toast.LENGTH_LONG).show();
                btPreFinishVisible(false);
                btnFlotingVisible(true);

                btInitVisible(false);
                btCancelVisible(false);

                viewAlert = false;
                currentTravel = null;
                HomeFragment.MarkerPoints = null;
                gloval.setGv_travel_current(null);
                setInfoTravel();

                tiempoTxt = 0;
                textTiempo = (TextView) findViewById(R.id.textTiempo);
                textTiempo.setVisibility(View.INVISIBLE);

              //  _activeTimer();
            }else  if(currentTravel.getIdSatatusTravel() == 6)
            {
                Toast.makeText(getApplicationContext(), "VIAJE Finalizado desde la Web, por el Operador!", Toast.LENGTH_LONG).show();

                btPreFinishVisible(false);
                btnFlotingVisible(true);
                viewAlert = false;

                currentTravel = null;
                HomeFragment.MarkerPoints = null;
                gloval.setGv_travel_current(null);
                setInfoTravel();

                tiempoTxt = 0;
                textTiempo = (TextView) findViewById(R.id.textTiempo);
                textTiempo.setVisibility(View.INVISIBLE);

                //_activeTimer();
            }else
            {
               // viewAlert = false;
                FragmentManager fm = getFragmentManager();
                dialogTravel = new TravelDialog();
                dialogTravel.show(fm, "Sample Fragment");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public void fn_exit() throws IOException {
        // LAMAMOS A EL MAIN ACTIVITY//

        Log.d("Websocket","closee");

        currentTravel = null;
        gloval.setGv_logeed(false);
        new GlovalVar();

        enviarTokenAlServidor("",gloval.getGv_user_id());

        if(ws != null) {
            ws.coseWebSocket();
        }

        if(timer != null)
        {
            timer.cancel();
        }


        editor.clear();
        editor.commit(); // commit changes
        finish();


        Intent main = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(main);


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void controlVieTravel()
    {
        try {
            cliaerNotificationAndoid();

            if (gloval.getGv_id_profile() == 3) {

                currentTravel = gloval.getGv_travel_current();

                if (currentTravel != null) {



                    // CHOFER BUSQUEDA DE CLIENTE //
                    if (currentTravel.getIdSatatusTravel() == 4) {
                        btInitVisible(true);
                        btCancelVisible(true);
                        btPreFinishVisible(false);
                        btnFlotingVisible(false);
                        setInfoTravel();
                        textTiempo = (TextView) findViewById(R.id.textTiempo);
                        textTiempo.setVisibility(View.VISIBLE);

                        // EN CURSO //
                    } else if (currentTravel.getIdSatatusTravel() == 5) {
                        //activeTimerInit();
                        btPreFinishVisible(true);
                        btInitVisible(false);
                        btCancelVisible(false);
                        btnFlotingVisible(false);
                        setInfoTravel();
                        textTiempo = (TextView) findViewById(R.id.textTiempo);
                        textTiempo.setVisibility(View.VISIBLE);

                        // POR ACEPTAR//
                    } else if (currentTravel.getIdSatatusTravel() == 2) {

                        setNotification(currentTravel);
                        btInitVisible(false);
                        btCancelVisible(false);
                        btnFlotingVisible(false);
                        btPreFinishVisible(false);
                        textTiempo = (TextView) findViewById(R.id.textTiempo);
                        textTiempo.setVisibility(View.INVISIBLE);
                        // POR RECHAZADO POR OTRO CHOFER//
                    } else if (currentTravel.getIdSatatusTravel() == 7) {
                        setNotification(currentTravel);
                        btInitVisible(false);
                        btCancelVisible(false);
                        btnFlotingVisible(false);
                        btPreFinishVisible(false);
                        textTiempo = (TextView) findViewById(R.id.textTiempo);
                        textTiempo.setVisibility(View.INVISIBLE);
                    }


                } else {



                    btInitVisible(false);
                    btCancelVisible(false);
                    btPreFinishVisible(false);
                    textTiempo = (TextView) findViewById(R.id.textTiempo);
                    textTiempo.setVisibility(View.INVISIBLE);
                   // _activeTimer();
                }


                // WEB SOCKET //
                ws = new WsTravel();
                ws.connectWebSocket(gloval.getGv_user_id());

            } else {
                ///WsTravel ws = new WsTravel();
                // ws.connectWebSocket();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Enviar token al servidor //
    private void enviarTokenAlServidor(String str_token,int idUser) {


        if(idUser > 0) {


            if (this.daoLoguin == null) {
                this.daoLoguin = HttpConexion.getUri().create(ServicesLoguin.class);
            }

            try {
                token T = new token();
                T.setToken(new tokenFull(str_token, idUser, gloval.getGv_id_driver(),MainActivity.version));

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                Log.d("Response JSON", gson.toJson(T));

                Call<Boolean> call = this.daoLoguin.token(T);

                call.enqueue(new Callback<Boolean>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        Log.d("Response request", call.request().toString());
                        Log.d("Response request header", call.request().headers().toString());
                        Log.d("Response raw header", response.headers().toString());
                        Log.d("Response raw", String.valueOf(response.raw().body()));
                        Log.d("Response code", String.valueOf(response.code()));

                    }

                    public void onFailure(Call<Boolean> call, Throwable t) {


                        Log.d("ERROR", t.getMessage());
                    }
                });

            } finally {
                this.daoTravel = null;
            }
        }


    }

    public void iniTimeSlep()
    {
        isWait(1);

        loadingCronometro = new ProgressDialog(HomeActivity.this);
        loadingCronometro.setTitle("Calculando Minutos de Espera");
        loadingCronometro.setMessage("Sumando Minutos...");
        loadingCronometro.setCancelable(false);
        loadingCronometro.setButton(DialogInterface.BUTTON_NEGATIVE, "Seguir con Viaje", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopTime();
            }
        });
        loadingCronometro.show();


        tiempo.Contar();
        btnPreFinish.setEnabled(false);
    }

    public void stopTime()
    {
        loadingCronometro.dismiss();
        tiempo.Detener();
        tiempoTxt = tiempoTxt + tiempo.getSegundos();
        btnPreFinish.setEnabled(true);

        textTiempo = (TextView) findViewById(R.id.textTiempo);
        textTiempo.setVisibility(View.VISIBLE);
        textTiempo.setText(tiempoTxt+"s");

        isWait(0);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverLoadTodays, new IntentFilter("update-message"));
        super.onResume();


        if(currentTravel !=  null) {
            if (currentTravel.getIdSatatusTravel() == 2) {
                controlVieTravel();
            }
        }else {
            searchTravelByIdDriver();
        }

       // _activeTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private BroadcastReceiver broadcastReceiverLoadTodays = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
           //ojoooo tolti modalll Toast.makeText(getApplicationContext(),  intent.getExtras().getBundle("message"), Toast.LENGTH_SHORT).show();

            currentTravel = gloval.getGv_travel_current();
            Log.d("BroadcastReceiver", String.valueOf(currentTravel));
            setNotification(currentTravel);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setInfoTravel()
    {



        if(currentTravel != null)
        {
            HomeFragment.setInfoTravel(currentTravel);
        }
        else
        {
            HomeFragment.clearInfo();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBoolean("uri", true);

    }


    /*METODO PARA NOTIFICACIONES CON MOBIL EN SEGUNDO PLANO*/
    public  void searchTravelByIdDriver()
    {
        if (this.daoTravel == null) {  this.daoTravel = HttpConexion.getUri().create(ServicesTravel.class);  }

        try {

            String lat = "";
            String lon = "";
            String add = "";



            if (HomeFragment.getmLastLocation() != null) {
                lat = String.valueOf(HomeFragment.getmLastLocation().getLatitude());
                lon = String.valueOf(HomeFragment.getmLastLocation().getLongitude());
                add = HomeFragment.nameLocation;
            }



                int idTrave = 0;
                int  idClientKf = 0;


                TraveInfoSendEntity travel =
                        new TraveInfoSendEntity(new
                                TravelLocationEntity(
                                gloval.getGv_user_id(),
                                idTrave,
                                add,
                                lon,
                                lat,
                                gloval.getGv_id_driver(),
                                gloval.getGv_id_vehichle(),
                                idClientKf,
                                HomeFragment.calculateMiles()[0]

                        )
                        );


                GsonBuilder builder = new GsonBuilder();
                final Gson gson = builder.create();
                Log.d("Object location ", gson.toJson(travel));


                Call<List<InfoTravelEntity>> call = this.daoTravel.infoTravelByDriver(travel);


                Log.d("Call location", call.request().body().toString());
                Log.d("Call location", call.request().toString());
                Log.d("Call location", call.request().headers().toString());

                call.enqueue(new Callback<List<InfoTravelEntity>>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(Call<List<InfoTravelEntity>> call, Response<List<InfoTravelEntity>> response) {

                        Log.d("Response raw header", response.headers().toString());
                        Log.d("Response raw", String.valueOf(response.raw().body()));
                        Log.d("Response code", String.valueOf(response.code()));

                        if (response.code() == 200) {

                            List<InfoTravelEntity> list = (List<InfoTravelEntity>) response.body();


                            gloval.setGv_travel_current(list.get(0));
                            currentTravel = gloval.getGv_travel_current();

                            if (currentTravel.getIdSatatusTravel() == 2) {
                                controlVieTravel();
                            }



                        }

                    }

                    @Override
                    public void onFailure(Call<List<InfoTravelEntity>> call, Throwable t) {




                        Log.d("********* ERROR ********","ERROR ENVIADO UBICACION");
                        Log.d("t.getMessage()",t.getMessage());


                    }
                });



        } finally {
            this.daoTravel = null;
        }
    }



    public void showFinshTravel() throws IOException {

        pay();
        final LinearLayout lg = (LinearLayout) findViewById(R.id.payment);
        lg.setVisibility(View.VISIBLE);


        if(timer != null) {
            timer.cancel();
        }


    }


    public void pay()
    {

        final TextView txtMount = (TextView) findViewById(R.id.txt_mount);
        final TextView distance_txt = (TextView) findViewById(R.id.distance_txt);
        final TextView txtTimeslep = (TextView) findViewById(R.id.txtTimeslep);

        /* VERIFICAMOS METODOS DE PAGO DISPONIBLES */
        if(currentTravel.getIsTravelComany() == 1)// EMPRESA
        {
            if (currentTravel.isPaymentCash != 1) {
                btnFinishCash.setEnabled(false);
            } else {
                btnFinishCash.setEnabled(true);
            }
        }
        /* */


        double PARAM_1  = Double.parseDouble(gloval.getGv_param().get(0).getValue());// PRECIO DE LISTA
        double PARAM_5  = Double.parseDouble(gloval.getGv_param().get(4).getValue());// PRECIO LISTA TIEMPO DE ESPERA
        double PARAM_6  = Double.parseDouble(gloval.getGv_param().get(5).getValue());// PRECIO LISTA TIEMPO DE VUELTA
        double PARAM_16  = Double.parseDouble(gloval.getGv_param().get(15).getValue());// VALOR MINIMO DE VIAJE
        int param25 = Integer.parseInt(gloval.getGv_param().get(25).getValue());// SE PUEDE VER PRECIO EN VIAJE EN APP

        double hor;
        double min = 0;

        double EXTRA_BENEFICIO;
        double distance_beneficio;

        btPreFinishVisible(false);

        Log.d("-TRAVEL DistanceSave-", String.valueOf( currentTravel.getDistanceSave()));


        /* DITANCIA TOTAL RECORRIDA */
        m_total  = HomeFragment.calculateMiles()[0];//BUSCAMOS LA DISTANCIA TOTLA
        kilometros_total = (m_total + currentTravel.getDistanceSave()) * 0.001;//LO CONVERTIMOS A KILOMETRO y sumamos la distancia salvada
        //**************************//

        /* DITANCIA TOTAL VULETA */
        m_vuelta  = HomeFragment.calculateMiles()[1];//BUSCAMOS LA DISTANCIA VUELTA
        kilometros_vuelta = m_vuelta * 0.001;//LO CONVERTIMOS A KILOMETRO
        //**************************//

        /* DITANCIA TOTAL IDA */
        km_ida = kilometros_total - kilometros_vuelta;
        m_ida  = m_total - m_vuelta;//BUSCAMOS LA DISTANCIA IDA
        kilometros_ida = m_ida * 0.001;//LO CONVERTIMOS A KILOMETRO
        //**************************//




        /* DIFERENCIAR TIPO DE CLIENTE */
        Log.d("-TRAVEL isCompany-", String.valueOf(currentTravel.getIsTravelComany()));
        if(currentTravel.getIsTravelComany() == 1)// EMPRESA
        {
            /*VERIFICAMOS SI ESTA ACTIVO EL CAMPO BENEFICIO POR KILOMETRO PARA ESA EMPRESA*/
            Log.d("-TRAVEL Beneficio-", String.valueOf(currentTravel.getBenefitsPerKm()));
            if(currentTravel.getBenefitsPerKm() == 1)
            {
                Log.d("-TRAVEL kilometros_total-", String.valueOf(kilometros_total));
                Log.d("-TRAVEL BenefitsToKm-", String.valueOf(currentTravel.getBenefitsToKm()));
                Log.d("-TRAVEL BenefitsFromKm-", String.valueOf(currentTravel.getBenefitsFromKm()));
                /* VERIFICAMOS I ESTA DENTRO DE EL RANDO DE EL BENEFICIO ESTABLECIDO */
                if(kilometros_total >= currentTravel.getBenefitsToKm() && kilometros_total >= currentTravel.getBenefitsFromKm())
                {
                    distance_beneficio = currentTravel.getBenefitsToKm()-currentTravel.getBenefitsFromKm();

                    Log.d("-TRAVEL distance_beneficio-", String.valueOf(distance_beneficio));
                    EXTRA_BENEFICIO = distance_beneficio * currentTravel.getBenefitsPreceKm();
                    Log.d("-TRAVEL EXTRA_BENEFICIO-", String.valueOf(EXTRA_BENEFICIO));

                    double KILOMETROS = kilometros_total - distance_beneficio;// CONVERTIMOS LO KILOMETRO A METROS
                    Log.d("-TRAVEL KILOMETROS-", String.valueOf(KILOMETROS));
                    amounCalculateGps =  (KILOMETROS*currentTravel.getPriceDitanceCompany())+EXTRA_BENEFICIO;
                    Log.d("-TRAVEL amounCalculateGps (1)-", String.valueOf(amounCalculateGps));

                }else
                {
                    amounCalculateGps = kilometros_total * currentTravel.getPriceDitanceCompany();
                    Log.d("-TRAVEL amounCalculateGps (2)-", String.valueOf(amounCalculateGps));
                }


            }else{
                amounCalculateGps = kilometros_total * currentTravel.getPriceDitanceCompany();// PARA CLIENTES EMPREA BUSCAMOS EL PRECIO DE ESA EMPRESA

                Log.d("-TRAVEL amounCalculateGps (3)-", String.valueOf(amounCalculateGps));

                if(isRoundTrip)
                {
                    Log.d("-TRAVEL isRoundTrip -", String.valueOf(isRoundTrip));
                    amounCalculateGps = kilometros_total  * currentTravel.getPriceDitanceCompany();
                    Log.d("-TRAVEL amounCalculateGps (4)-", String.valueOf(amounCalculateGps));
                    amounCalculateGps =  amounCalculateGps + kilometros_vuelta * currentTravel.getPriceReturn();
                    Log.d("-TRAVEL amounCalculateGps (5)-", String.valueOf(amounCalculateGps));
                }
            }



        }
        else // PARTICULARES
        {
            amounCalculateGps = kilometros_total * PARAM_1;// PARA CLIENTES PARTICULARES BUSCAMOS EL PRECIO DE LISTA
            Log.d("-TRAVEL amounCalculateGps (6)-", String.valueOf(amounCalculateGps));
            if(isRoundTrip)
            {
                Log.d("-TRAVEL amounCalculateGps (7)-", String.valueOf(isRoundTrip));
                amounCalculateGps = kilometros_ida * PARAM_1;
                Log.d("-TRAVEL amounCalculateGps (8)-", String.valueOf(amounCalculateGps));
                amounCalculateGps =  amounCalculateGps + kilometros_vuelta * PARAM_6;
                Log.d("-TRAVEL amounCalculateGps (9)-", String.valueOf(amounCalculateGps));

            }
        }


        // SUMA EL ORIGEN PACTADO //
        amounCalculateGps =  amounCalculateGps + currentTravel.getAmountOriginPac();
        Log.d("-TRAVEL amounCalculateGps (10)-", String.valueOf(amounCalculateGps));

        //VALIDAMOS SI EL VIAJE NO SUPERA EL MINUMO//
        if(currentTravel.getIsTravelComany() == 1)// PARA EMPRESA
        {
            if(amounCalculateGps <  currentTravel.getPriceMinTravel()){
                amounCalculateGps = currentTravel.getPriceMinTravel();
                Log.d("-TRAVEL amounCalculateGps (11)-", String.valueOf(amounCalculateGps));
            }
        }else {// PARA PARTICULARES
          if(amounCalculateGps <  PARAM_16){
              amounCalculateGps = PARAM_16;
              Log.d("-TRAVEL amounCalculateGps (12)-", String.valueOf(amounCalculateGps));
          }

        }



        hor=min/3600;
        min=(tiempoTxt-(3600*hor))/60;//  BUSCAMOS SI REALIZO ESPERA

        Log.d("-TRAVEL min espera -", String.valueOf(min));


         /* DIFERENCIAR TIPO DE CLIENTE */
        if(currentTravel.getIsTravelComany() == 1)// EMPRESA
        {
            if(min > 0) {
                extraTime = min * currentTravel.getPriceMinSleepCompany();
            }else if(min > 0.1) {
                extraTime = 1 * currentTravel.getPriceMinSleepCompany();
            }

            Log.d("-TRAVEL min extraTime -", String.valueOf(extraTime));
        }
        else // PARTICULARES
        {
            if(min > 0) {
                extraTime = min * PARAM_5;
            }else if(min > 0.1) {
                extraTime = 1 * PARAM_5;
            }
        }

        if(param25 == 1) {
            txtTimeslep.setText(tiempoTxt + " Seg - " + Double.toString(round(extraTime, 2)) + "$");
        }else {
            txtTimeslep.setText(tiempoTxt + " Seg");
        }


        if(param25 == 1) {
            distance_txt.setText(df.format(kilometros_total) + " Km - "+Double.toString(round(amounCalculateGps,2))+"$");

        }else {
            distance_txt.setText(df.format(kilometros_total) + " Km ");

        }



        double totalFinal = 0;

        double myDouble;
        String myString = ((EditText) findViewById(R.id.peajes_txt)).getText().toString();
        if (myString != null && !myString.equals("")) {
            myDouble = Double.valueOf(myString);
        } else {
            myDouble = 0;
        }

        double parkin;
        String parkin_txt = ((EditText) findViewById(R.id.parkin_txt)).getText().toString();
        if (parkin_txt != null && !parkin_txt.equals("")) {
            parkin = Double.valueOf(parkin_txt);
        } else {
            parkin = 0;
        }


        totalFinal =  amounCalculateGps + extraTime + myDouble + parkin;
        Log.d("-TRAVEL  totalFinal -", String.valueOf(totalFinal));


        if(param25 == 1){
            txtMount.setText(Double.toString(round(totalFinal,2))+"$");
        }
        else {
            txtMount.setText("---");
        }



    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @Override
    public void onBackPressed() {

      /*  DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/


       // if(timer != null) {
        //    timer.cancel();
        //}
    }


    private int mNotificationsCount = 0;
    private int mNotificationsReservationsCount = 0;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);


        // ETO NOS PERMITE CARGAR EL ICONO DE MENU CON NOTIFICACIONBES //
        MenuItem item = menu.findItem(R.id.action_notifications);
        LayerDrawable icon = (LayerDrawable) item.getIcon();

        // ETO NOS PERMITE CARGAR EL ICONO DE MENU CON NOTIFICACIONBES DE RESERVAS //
        MenuItem item2 = menu.findItem(R.id.action_reervations);
        LayerDrawable icon2 = (LayerDrawable) item2.getIcon();

        // ACTUALIZAMOS EL NUMERO DE NOTIFICCIONES
        Utils.setBadgeCount(this, icon, mNotificationsCount,R.id.ic_badge);
        Utils.setBadgeCount(this, icon2, mNotificationsReservationsCount,R.id.ic_badge2);

        return true;
    }

    /*  modificar notificacione  */
    private void updateNotificationsBadge(int couterNotifications,int couterReservations) {
        mNotificationsCount = couterNotifications;
        mNotificationsReservationsCount = couterReservations;
        invalidateOptionsMenu();
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            try {
                fn_exit();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }
        else if (id == R.id.action_notifications) {

            fn_gotonotification();
            return true;
        }
        else if (id == R.id.action_reervations) {

            fn_gotoreservation();
            return true;
        }
        else if (id == R.id.action_profile) {

            fn_gotoprofile();
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    public  void  fn_gotoprofile()
    {
        btnFlotingVisible(false);
        FragmentManager fm = getFragmentManager();

        // VERIFICAMO SI E UN CHOFER //
        if(gloval.getGv_id_profile() == 3)
        {
            fm.beginTransaction().replace(R.id.content_frame,new ProfileDriverFr()).commit();
        }
        // VERIFICAMO SI E UN CLIENTE PÁRTICULAR//
        else if(gloval.getGv_id_profile() == 2)
        {
            Log.d("paso","pao");
            fm.beginTransaction().replace(R.id.content_frame,new ProfileClientFr()).commit();
        }
    }



    public void fn_gotonotification()
    {

        btnFlotingVisible(false);
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame,new NotificationsFrangment()).commit();
    }

    public void fn_gotoreservation()
    {

        btnFlotingVisible(false);
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame,new ReservationsFrangment()).commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentManager fm = getFragmentManager();


        if (id == R.id.nav_camera) {
            fm.beginTransaction().replace(R.id.content_frame,new HomeFragment()).commit();


        } else if (id == R.id.nav_gallery) {
            btnFlotingVisible(false);
            fm.beginTransaction().replace(R.id.content_frame,new HistoryTravelDriver()).commit();

        } else if (id == R.id.nav_slideshow) {
            btnFlotingVisible(false);
            fm.beginTransaction().replace(R.id.content_frame,new AcountDriver()).commit();


        } else if (id == R.id.nav_manage) {
            btnFlotingVisible(false);
            fm.beginTransaction().replace(R.id.content_frame,new NotificationsFrangment()).commit();

        } else if (id == R.id.nav_reservations) {
            btnFlotingVisible(false);
            fn_gotoreservation();

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }






    public void _activeTimer()
    {

        Log.d("location","TIMER");


            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setLocationVehicheDriver();

                        }
                    });

                }
            }, 0, 60000);

    }

    public  void setLocationVehicheDriver()
    {
       // Log.d("setLocationVehicheDriver","setLocationVehicheDriver");

        if (this.daoTravel == null) {
            this.daoTravel = HttpConexion.getUri().create(ServicesTravel.class);
        }

        try {

            String lat = "";
            String lon = "";
            String add = "";


            if (HomeFragment.getmLastLocation() != null) {
                lat = String.valueOf(HomeFragment.getmLastLocation().getLatitude());
                lon = String.valueOf(HomeFragment.getmLastLocation().getLongitude());
                add = HomeFragment.nameLocation;
            }

           // Log.d("setLocationVehicheDriver 00", String.valueOf(HomeFragment.nameLocation));
          //  Log.d("setLocationVehicheDriver  01", "**"+add);

            if(add != "") {

               // Log.d("setLocationVehicheDriver  ", "PASO 1");
                int idTrave = 0;
                int idClientKf = 0;

                if (currentTravel != null) {
                    idTrave = currentTravel.getIdTravel();
                    idClientKf = currentTravel.getIdClientKf();
                }

                TraveInfoSendEntity travel =
                        new TraveInfoSendEntity(new
                                TravelLocationEntity(
                                gloval.getGv_user_id(),
                                idTrave,
                                add,
                                lon,
                                lat,
                                gloval.getGv_id_driver(),
                                gloval.getGv_id_vehichle(),
                                idClientKf,
                                HomeFragment.calculateMiles()[0]


                        )
                        );


                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                Log.d("setLocationVehicheDriver", gson.toJson(travel));


                Call<RemisSocketInfo> call = this.daoTravel.sendPosition(travel);


                Log.d("setLocationVehicheDriver Call request", call.request().body().toString());
                Log.d("setLocationVehicheDriver Call request", call.request().toString());
                Log.d("setLocationVehicheDriver Call request header", call.request().headers().toString());

                call.enqueue(new Callback<RemisSocketInfo>() {
                    @Override
                    public void onResponse(Call<RemisSocketInfo> call, Response<RemisSocketInfo> response) {

                        Log.d("setLocationVehicheDriver Response raw header", response.headers().toString());
                        Log.d("setLocationVehicheDriver Response raw", String.valueOf(response.raw().body()));
                        Log.d("setLocationVehicheDriver Response code", String.valueOf(response.code()));

                        if (response.code() == 200) {

                            RemisSocketInfo list = (RemisSocketInfo) response.body();
                            notificate(list.getListNotification(), list.getListReservations());


                        }

                    }

                    @Override
                    public void onFailure(Call<RemisSocketInfo> call, Throwable t) {
                        Log.d("**ERROR**", t.getMessage());
                        // Toast.makeText(getApplicationContext(), "ERROR ENVIADO UBICACION!", Toast.LENGTH_LONG).show();

                    }
                });

            }

            Log.d("setLocationVehicheDriver  ", "PASO FINAL");


        }
        catch (Exception e)
        {
            Log.d("setLocationVehicheDriver",e.getMessage());
        }

        finally {
            this.daoTravel = null;
        }
    }


    public void notificate(List<notification> list,List<InfoTravelEntity> listReservations)
    {

        int couterNotifications = 0;
        int couterRervations = 0;

        if(list != null)
        {
            couterNotifications = list.size();
        }

        if(listReservations != null)
        {
            couterRervations = listReservations.size();
        }

        updateNotificationsBadge(couterNotifications,couterRervations);
    }


    public  void intiTravel()
    {


        // GUARDAMOS LA UBICACION EN EL  FIREBASE //
        if(currentTravel != null) {
            String lat = "";
            String lon = "";
            String add = "";

            if (HomeFragment.getmLastLocation() != null) {
                lat = String.valueOf(HomeFragment.getmLastLocation().getLatitude());
                lon = String.valueOf(HomeFragment.getmLastLocation().getLongitude());
                add = HomeFragment.nameLocation;
            }



            TravelLocationEntity travel = new  TravelLocationEntity(
                gloval.getGv_user_id(),
                currentTravel.getIdTravel(),
                add,
                lon,
                lat,
                0,
                 0,
                 currentTravel.getIdClientKf(),
                    HomeFragment.calculateMiles()[0]
            );

            DatabaseReference locationRef = databaseReference.child("Travel");
            locationRef.push().setValue(travel);





        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setNotification(final InfoTravelEntity travel)
    {
            Log.d("viewAlert", String.valueOf(viewAlert));

        if(viewAlert == false)
        {
            viewAlert = true;
            currentTravel =  travel;
            gloval.setGv_travel_current(currentTravel);

            showDialogTravel();// MOSTRAMO EL FRAGMENT DIALOG

        }


    }

    // RECHAZAR VIAJE //
    public void cancelTravel(int idTravel)
    {
        if (this.daoTravel == null) { this.daoTravel = HttpConexion.getUri().create(ServicesTravel.class); }


        if(idTravel == -1)
        {
            idTravel = gloval.getGv_travel_current().idTravel;
        }

        try {



            Call<InfoTravelEntity> call = this.daoTravel.refuse(idTravel);

            Log.d("fatal", call.request().toString());
            Log.d("fatal", call.request().headers().toString());

            call.enqueue(new Callback<InfoTravelEntity>() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onResponse(Call<InfoTravelEntity> call, Response<InfoTravelEntity> response) {

                    Toast.makeText(getApplicationContext(), "VIAJE RECHAZADO!", Toast.LENGTH_LONG).show();
                    Log.d("fatal",response.body().toString());

                    // cerramo el dialog //

                    if(dialogTravel != null)
                    {
                        dialogTravel.dismiss();
                    }

                    cliaerNotificationAndoid();
                   // viewAlert = false;
                    gloval.setGv_travel_current(null);
                    currentTravel = null;
                    btCancelVisible(false);
                    btInitVisible(false);
                    HomeFragment.clearInfo();
                    viewAlert = false;


                }

                public void onFailure(Call<InfoTravelEntity> call, Throwable t) {
                    AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                    alertDialog.setTitle("ERROR");
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setMessage(t.getMessage());


                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }


            });

        } finally {
            this.daoTravel = null;
        }
    }

    public  void btCancelVisible(boolean b)
    {
        final Button btn = (Button) findViewById(R.id.btn_cancel);

        if(b)
        {
            btn.setVisibility(View.VISIBLE);
        }else {
            btn.setVisibility(View.INVISIBLE);
        }
    }

    public  void btInitVisible(boolean b)
    {
        final Button btn = (Button) findViewById(R.id.btn_init);

        if(b)
        {
            btn.setVisibility(View.VISIBLE);
        }else {
            btn.setVisibility(View.INVISIBLE);
        }
    }

    public  void btPreFinishVisible(boolean b)
    {
        final Button btnLogin = (Button) findViewById(R.id.btn_pre_finish);
        final Button btnInitSplep = (Button) findViewById(R.id.btn_iniTimeSlep);
        final Button btn_return = (Button) findViewById(R.id.btn_return);


        if(b)
        {
            btnLogin.setVisibility(View.VISIBLE);
            btnInitSplep.setVisibility(View.VISIBLE);
            btn_return.setVisibility(View.VISIBLE);
        }else {
            btnLogin.setVisibility(View.INVISIBLE);
            btnInitSplep.setVisibility(View.INVISIBLE);
            btn_return.setVisibility(View.INVISIBLE);
        }
    }


    /* SERVICIO PARA RETORNAR UN VIAJE EN EL MONITOR DDE VIAJES */
    public  void  isRoundTrip()
    {

        if (this.daoTravel == null) { this.daoTravel = HttpConexion.getUri().create(ServicesTravel.class); }


        try {
            Call<Boolean> call = this.daoTravel.isRoundTrip(currentTravel.idTravel);

            Log.d("fatal", call.request().toString());
            Log.d("fatal", call.request().headers().toString());

            call.enqueue(new Callback<Boolean>() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                    isRoundTrip = true;
                    Toast.makeText(getApplicationContext(), "Vuelta Activada!", Toast.LENGTH_LONG).show();
                    currentTravel.setRoundTrip(true);
                    setInfoTravel();

                }

                public void onFailure(Call<Boolean> call, Throwable t) {
                    AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                    alertDialog.setTitle("ERROR");
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setMessage(t.getMessage());



                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }


            });

        } finally {
            this.daoTravel = null;
        }
    }

    /* SERVICIO PARA INDICAR ESPERA DE UN VIAJE EN EL MONITOR DDE VIAJES */
    public  void  isWait(int value)
    {

        if (this.daoTravel == null) { this.daoTravel = HttpConexion.getUri().create(ServicesTravel.class); }


        try {
            Call<Boolean> call = this.daoTravel.isWait(currentTravel.idTravel,value);

            Log.d("fatal", call.request().toString());
            Log.d("fatal", call.request().headers().toString());

            call.enqueue(new Callback<Boolean>() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                    isRoundTrip = true;

                    if(currentTravel != null) {
                        currentTravel.setRoundTrip(true);
                    }
                    setInfoTravel();

                }

                public void onFailure(Call<Boolean> call, Throwable t) {
                    AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                    alertDialog.setTitle("ERROR");
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setMessage(t.getMessage());



                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }


            });

        } finally {
            this.daoTravel = null;
        }
    }



    /* METODO PARA ACEPATAR EL VIAJE*/
    public  void  aceptTravel(int idTravel)
    {

        if (this.daoTravel == null) { this.daoTravel = HttpConexion.getUri().create(ServicesTravel.class); }


        try {
            Call<InfoTravelEntity> call = this.daoTravel.accept(idTravel);

            Log.d("fatal", call.request().toString());
            Log.d("fatal", call.request().headers().toString());

            call.enqueue(new Callback<InfoTravelEntity>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Call<InfoTravelEntity> call, Response<InfoTravelEntity> response) {

                Toast.makeText(getApplicationContext(), "VIAJE ACEPTADO...", Toast.LENGTH_LONG).show();
              //  Log.d("fatal",response.body().toString());


                btnFlotingVisible(false);
                btInitVisible(true);
                btCancelVisible(true);
                cliaerNotificationAndoid();
                viewAlert = false;
                dialogTravel.dismiss();

                currentTravel = response.body();
                setInfoTravel();

            }

            public void onFailure(Call<InfoTravelEntity> call, Throwable t) {
                AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                alertDialog.setTitle("ERROR");
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setMessage(t.getMessage());



                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }


        });

        } finally {
            this.daoTravel = null;
        }
    }

    public  void  initTravel()
    {
        if (this.daoTravel == null) { this.daoTravel = HttpConexion.getUri().create(ServicesTravel.class); }

        try {
                Call<InfoTravelEntity> call = this.daoTravel.init(currentTravel.getIdTravel());

                call.enqueue(new Callback<InfoTravelEntity>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(Call<InfoTravelEntity> call, Response<InfoTravelEntity> response) {

                        btInitVisible(false);
                        btCancelVisible(false);
                        btPreFinishVisible(true);
                        btnFlotingVisible(false);

                        currentTravel = response.body();
                        setInfoTravel();


                        //activeTimerInit();
                    }

                    public void onFailure(Call<InfoTravelEntity> call, Throwable t) {
                        AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                        alertDialog.setTitle("ERROR");
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setMessage(t.getMessage());
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                });

        } finally {
            this.daoTravel = null;
        }
    }


    /*LLAMAMOS A LA PATALLA DE BOUCHE*/
    public void finishTravelVouche()
    {
        Intent intent = new Intent(getApplicationContext(), Signature.class);
        startActivityForResult(intent, SIGNATURE_ACTIVITY);
    }


    /*LLAMAMOS A LA PATALLA DE MERCADO  PAGO */
    public void finishTravelCreditCar()
    {

        idPaymentFormKf = 3;
        finishTravel();

        //Intent intent = new Intent(getApplicationContext(), PaymentCreditCar.class);
        //startActivityForResult(intent, CREDIT_CAR_ACTIVITY);
    }





    /* METODO PARA FINALIZAR O PREFINALIZAR  UN VIAJE*/
    public  void  finishTravel() {

        if (this.daoTravel == null) { this.daoTravel = HttpConexion.getUri().create(ServicesTravel.class); }

        try {

                String lat = "";
                String lon = "";
                String add = "";

                if(HomeFragment.getmLastLocation() != null)
                {
                    lat = String.valueOf(HomeFragment.getmLastLocation().getLatitude());
                    lon = String.valueOf(HomeFragment.getmLastLocation().getLongitude());
                    add = HomeFragment.nameLocation;
                }

                   // BUSCAMOS EL VALOR DE EL PEAJE //

                    double myDouble;
                    String myString = ((EditText) findViewById(R.id.peajes_txt)).getText().toString();
                    if (myString != null && !myString.equals("")) {
                        myDouble = Double.valueOf(myString);
                    } else {
                        myDouble = 0;
                    }
                    ((EditText) findViewById(R.id.peajes_txt)).setText("");

                    double parkin;
                    String parkin_txt = ((EditText) findViewById(R.id.parkin_txt)).getText().toString();
                    if (parkin_txt != null && !parkin_txt.equals("")) {
                        parkin = Double.valueOf(parkin_txt);
                    } else {
                        parkin = 0;
                    }
                    ((EditText) findViewById(R.id.parkin_txt)).setText("");






                    TraveInfoSendEntity travel =
                            new TraveInfoSendEntity(new
                                    TravelLocationEntity
                                    (
                                            currentTravel.getIdTravel(),
                                            amounCalculateGps,
                                            //Double.parseDouble(val),
                                            kilometros_total,
                                            df.format(kilometros_total),
                                            add,
                                            lon,
                                            lat,
                                            myDouble,
                                            parkin,
                                            extraTime,
                                            tiempoTxt+" Segundos",
                                            idPaymentFormKf

                                    )
                            );


                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    Log.d("Object Info", gson.toJson(travel));


                    Call<InfoTravelEntity> call = null;

                    /* VERIFICAMOS I ESTA HABILITADO EL CIERRE DE VIAJES DEDE LA APP O NO*/
                    if(PARAM_20 == 1)
                    {
                        call = this.daoTravel.finishPost(travel);
                    }else
                    {
                        call = this.daoTravel.preFinishMobil(travel);
                    }


                    call.enqueue(new Callback<InfoTravelEntity>() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onResponse(Call<InfoTravelEntity> call, Response<InfoTravelEntity> response) {

                            Log.d("Response request", call.request().toString());
                            Log.d("Response request header", call.request().headers().toString());
                            Log.d("Response raw header", response.headers().toString());
                            Log.d("Response raw", String.valueOf(response.raw().body()));
                            Log.d("Response code", String.valueOf(response.code()));

                            if(PARAM_20 == 1) {
                                Toast.makeText(HomeActivity.this, "VIAJE  FINALIZADO", Toast.LENGTH_SHORT).show();
                            }else
                            {
                                Toast.makeText(HomeActivity.this, "VIAJE ENVIADO PARA SU APROBACION", Toast.LENGTH_SHORT).show();
                            }


                            btPreFinishVisible(false);
                            btnFlotingVisible(true);


                            final LinearLayout lg = (LinearLayout) findViewById(R.id.payment);
                            lg.setVisibility(View.INVISIBLE);


                            currentTravel = null;
                            HomeFragment.MarkerPoints = null;
                            HomeFragment.options = null;
                            gloval.setGv_travel_current(null);
                            setInfoTravel();



                            tiempoTxt = 0;
                            textTiempo = (TextView) findViewById(R.id.textTiempo);
                            textTiempo.setVisibility(View.INVISIBLE);

                        }

                        public void onFailure(Call<InfoTravelEntity> call, Throwable t) {
                            AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                            alertDialog.setTitle("ERROR");
                            alertDialog.setMessage(t.getMessage());
                            alertDialog.setCanceledOnTouchOutside(false);



                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                    });

        } finally {
            this.daoTravel = null;
        }

    }

    public void cliaerNotificationAndoid()
    {
        NotificationManager notifManager= (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();
    }

    // FIRMAAAA //
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode) {
            case SIGNATURE_ACTIVITY:
                if (resultCode == RESULT_OK) {

                    Bundle bundle       = data.getExtras();
                    String status       = bundle.getString("status");
                    path_image   = bundle.getString("image");
                    filePath        = Uri.parse(path_image);

                    if(status.equalsIgnoreCase("done")){
                        Toast.makeText(this, "Firma Guardada", Toast.LENGTH_SHORT).show();


                        try {
                            //Getting the Bitmap from Gallery
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                            //Setting the Bitmap to ImageView

                            postImageData();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            case PROFILE_DRIVER_ACTIVITY:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }




    }

    public void postImageData()
    {

        uploadImage();
    }

    public String getStringImage(Bitmap bmp)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    /* SERVICIO QUE GUARDA LA FIRMA */
    private void uploadImage()
    {
        class UploadImage extends AsyncTask<Bitmap, Void, String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(HomeActivity.this, "Procesando Firma", "Espere unos Segundos...", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
               // Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                idPaymentFormKf = 5;
                finishTravel();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadImage = getStringImage(bitmap);

                HashMap<String, String> data = new HashMap<>();
                data.put(UPLOAD_KEY, uploadImage);

                // Get the Image's file name
                String fileNameSegments[] = path_image.split("/");
                String fileName = fileNameSegments[fileNameSegments.length - 1];
                // Put file name in Async Http Post Param which will used in Php web app
                data.put("filename", String.valueOf(currentTravel.getIdTravel()));

                String result = rh.sendPostRequest(UPLOAD_URL, data);

                return result;
            }
        }


        UploadImage ui = new UploadImage();
        ui.execute(bitmap);

    }



    // CONONTRO BOTON FLOTANTE //
    public  void  btnFlotingVisible(boolean isVisible)
    {
        FloatingActionButton btnService = (FloatingActionButton) findViewById(R.id.fab);

        if(!isVisible)
        {
            btnService.setVisibility(View.INVISIBLE);
        }else
        {
            btnService.setVisibility(View.VISIBLE);
        }

    }

    // METODO OBTENER FOTO DE CHOFER //
    public void getPick(int idUserDriver)
    {
        DowloadImg dwImg = new DowloadImg();
        dwImg.execute(HttpConexion.BASE_URL+HttpConexion.base+"/Frond/img_users/"+idUserDriver);

    }

    public class DowloadImg extends AsyncTask<String, Void, Bitmap> {



        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            Log.i("doInBackground" , "Entra en doInBackground");
            String url = params[0]+".JPEG";
            Bitmap imagen = descargarImagen(url);
            return imagen;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);


            try {
                CircleImageView your_imageView = (CircleImageView) findViewById(R.id.imageViewUser);

                if (result != null) {
                    your_imageView.setImageBitmap(result);

                }
            }catch (Exception e)
            {

            }



        }

        private Bitmap descargarImagen (String imageHttpAddress){
            URL imageUrl = null;
            Bitmap imagen = null;
            try{
                imageUrl = new URL(imageHttpAddress);
                HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                conn.connect();
                imagen = BitmapFactory.decodeStream(conn.getInputStream());
            }catch(IOException ex){
                ex.printStackTrace();
            }

            return imagen;
        }

    }



}
