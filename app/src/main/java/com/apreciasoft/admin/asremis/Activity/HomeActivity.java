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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
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
import com.apreciasoft.admin.asremis.Entity.BeneficioEntity;
import com.apreciasoft.admin.asremis.Entity.InfoTravelEntity;
import com.apreciasoft.admin.asremis.Entity.PagoEntity;
import com.apreciasoft.admin.asremis.Entity.RemisSocketInfo;
import com.apreciasoft.admin.asremis.Entity.ResponsePHP;
import com.apreciasoft.admin.asremis.Entity.TraveInfoSendEntity;
import com.apreciasoft.admin.asremis.Entity.TravelLocationEntity;
import com.apreciasoft.admin.asremis.Entity.notification;
import com.apreciasoft.admin.asremis.Entity.paramEntity;
import com.apreciasoft.admin.asremis.Entity.resp;
import com.apreciasoft.admin.asremis.Entity.token;
import com.apreciasoft.admin.asremis.Entity.tokenFull;
import com.apreciasoft.admin.asremis.Fracments.AcountDriver;
import com.apreciasoft.admin.asremis.Fracments.HistoryTravelDriver;
import com.apreciasoft.admin.asremis.Fracments.HomeFragment;
import com.apreciasoft.admin.asremis.Fracments.NotificationsFrangment;
import com.apreciasoft.admin.asremis.Fracments.PaymentCreditCar;
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
import com.google.gson.reflect.TypeToken;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    public static final String UPLOAD_URL =  HttpConexion.BASE_URL+HttpConexion.base+"/Frond/safeimg.php";
    public static final String UPLOAD_KEY = "image";

    public GlovalVar gloval;

    public ServicesTravel daoTravel = null;
    public ServicesLoguin daoLoguin = null;

    public ProgressDialog loading;
    public static double totalFinal = 0;
    public static double priceFlet = 0;

    boolean _NOCONEXION = false;


    public Timer timer,timerConexion;
    public ProgressDialog loadingCronometro;
    public static final int SIGNATURE_ACTIVITY = 1;
    public static final int PROFILE_DRIVER_ACTIVITY = 2;
    public static final int CREDIT_CAR_ACTIVITY = 3;

    ServicesLoguin apiService = null;

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


    /*
    PAGOS DE TARJETA
     */
    public static String mp_jsonPaymentCard = "";
    public static Long mp_cardIssuerId;
    public static int mp_installment;
    public static String mp_cardToken;
    public static Long  mp_campaignId;
    public static String mp_paymentMethodId = "";
    public static String mp_paymentTypeId = "";
    public static String mp_paymentstatus = "";
    public static  boolean _PAYCREDITCAR_OK = false;


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

    //int PARAM_20  = 0; // CIERRE DE VIAJE EMPRESA EN WEB
    int PARAM_3  = 0; // CIERRE DE VIAJE EMPRESA EN WEB
    int PARAM_67  = 0; // CIERRE DE VIAJE PARTICULARES Y EXPRESSS EN WEB
    public static  int PARAM_39,PARAM_66  = 0; // ACTIVAR BOTON DE VUELTA
    public static  int param25 = 0;
    public static double PARAM_1 , PARAM_6  = 0;
    public static  int PARAM_68 = 0; // ACTIVAR PAGO CON TARJETA
    public static  String PARAM_69 = ""; // ACTIVAR PAGO CON TARJETA
    public static  String PARAM_79 = ""; // ACTIVAR PAGO CON TARJETA


    /*DIALOG*/
    public TravelDialog dialogTravel = null;

    /* CONSTATES PARA PERMISOS */
    private static final int ACCESS_FINE_LOCATION_PERMISSIONS = 123;


    public  Button btnFinishCar;
    public  Button btnFinishVo;
    public  Button btnFinishCash;
    public  SharedPreferences.Editor editor;
    public static  SharedPreferences pref;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverLoadTodays, new IntentFilter("update-message"));

        checkVersion(); // VERIFICAMOS LA VERSION

        pref = getApplicationContext().getSharedPreferences(HttpConexion.instance, 0); // 0 - for private mode
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



        TypeToken<List<paramEntity>> token3 = new TypeToken<List<paramEntity>>(){};
        Gson gson = new Gson();
        List<paramEntity> listParam = gson.fromJson(pref.getString("param",""), token3.getType());

        gloval.setGv_param(listParam);

        setParamLocal();



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
                try {
                    pay();
                }catch (Exception e){
                    Log.d("ERRROR",e.getMessage());
                }

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
      //  controlVieTravel();

        //ACTIVAMOS EL TOkEN FIRE BASE//
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token: ", token);
        enviarTokenAlServidor(token,gloval.getGv_user_id());


        textTiempo = (TextView) findViewById(R.id.textTiempo);
        textTiempo.setVisibility(View.INVISIBLE);




        //LLAMAMOS A EL METODO PARA HABILITAR PERMISOS//
        checkPermision();


        getPick(gloval.getGv_user_id());



        // WEB SOCKET //
        ws = new WsTravel();
        ws.connectWebSocket(pref.getInt("user_id", 0));

        btPreFinishVisible(false);
        btInitVisible(false);
        btCancelVisible(false);

        refreshButomPermision();

        setTitle(gloval.getGv_nr_driver());
        toolbar.setLogo(R.drawable.ic_directions_car_black_24dp);
        toolbar.setSubtitle("Chofer");


        _activeTimer();


    }

    private void setParamLocal() {
        param25 = Integer.parseInt(gloval.getGv_param().get(25).getValue());// SE PUEDE VER PRECIO EN VIAJE EN APP
        PARAM_66 = Integer.parseInt(gloval.getGv_param().get(65).getValue());// SE PUEDE VER PRECIO EN VIAJE EN APP
        PARAM_68 = Integer.parseInt(gloval.getGv_param().get(67).getValue());// SE PAGAR CON TARJETA

        try {
            PARAM_69 = gloval.getGv_param().get(68).getValue();//
            PARAM_79 = gloval.getGv_param().get(78).getValue();//

        } catch ( IndexOutOfBoundsException e ) {
            PARAM_69 = "";
            PARAM_79 = "";
        }
        PARAM_3 =  Integer.parseInt(gloval.getGv_param().get(2).getValue());
        PARAM_39 = Integer.parseInt(gloval.getGv_param().get(38).getValue());
        PARAM_67 =  Integer.parseInt(gloval.getGv_param().get(66).getValue());


    }


    public boolean checkDistanceSucces() {

        try{


        int param30 = Integer.parseInt(gloval.getGv_param().get(29).getValue());
        if(param30 > 0){

            Location locationA = new Location(HomeFragment.nameLocation);
            locationA.setLatitude(HomeFragment.getmLastLocation().getLatitude());
            locationA.setLongitude(HomeFragment.getmLastLocation().getLongitude());

            Location locationB = new Location(currentTravel.getNameOrigin());
            locationB.setLatitude(Double.parseDouble(currentTravel.getLatOrigin()));
            locationB.setLongitude(Double.parseDouble(currentTravel.getLonOrigin()));

            float distance = locationA.distanceTo(locationB)/1000;;



            Log.d("distance", String.valueOf(distance));
            Log.d("distance", String.valueOf(param30));


            if(distance <= param30){

                return true;

            }else {
                return false;
            }


        }
            return false;




        }catch (Exception e){

            Log.d("ERROR",e.getMessage());
            return  false;

        }



    }

    public boolean checkPermision()
    {
        try{
                LocationManager locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
                // Comprobamos si está disponible el proveedor GPS.
                if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                {

                    AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                    alertDialog.setTitle("GPS INACTIVO!");
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage("Active el GPS para continuar!");


                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "ACTIVAR",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                                }
                            });
                    alertDialog.show();

                    return false;

                }else {
                    return true;
                }

        }catch (Exception e){
             Toast.makeText(getApplicationContext(), "GPS ERROR:"+e.getMessage(), Toast.LENGTH_LONG).show();
            return false;

        }
    }

    public  void  showDialogTravel()
    {
        try
        {

            Log.d("currentTravel", String.valueOf(currentTravel.isResignet));
            Log.d("currentTravel", String.valueOf(currentTravel.getIdSatatusTravel()));
            Log.d("currentTravel", String.valueOf(viewAlert));

            if(dialogTravel != null){
                dialogTravel.dismiss();
            }



                if (currentTravel.getIdSatatusTravel() == 0) {

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
                } else if (currentTravel.getIdSatatusTravel() == 8) {
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
                } else if (currentTravel.getIdSatatusTravel() == 6) {
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
                } else {

                    if(viewAlert == false) {
                        if (currentTravel.getIdSatatusTravel() == 2) {
                            viewAlert = true;



                           FragmentManager fm = getFragmentManager();
                            dialogTravel = new TravelDialog();
                            dialogTravel.show(fm, "Sample Fragment");
                            dialogTravel.setCancelable(false);

                           // this.cliaerNotificationAndoid();
                        }
                    }
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

        if(timerConexion != null)
        {
            timerConexion.cancel();
        }





        if(HomeFragment.SPCKETMAP != null){
            HomeFragment.SPCKETMAP.disconnect();
            HomeFragment.SPCKETMAP.close();
            HomeFragment.SPCKETMAP = null;

        }


        SharedPreferences preferences = getApplicationContext().getSharedPreferences(HttpConexion.instance, 0);
        SharedPreferences.Editor editor = preferences.edit();
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



            Log.d("currentTravel", "CONTROL");


            if(currentTravel == null){
                currentTravel = gloval.getGv_travel_current();
            }
            Log.d("VIAJE ESTATUS ", String.valueOf(currentTravel));


                if (currentTravel != null) {



                    Log.d("VIAJE ESTATUS ", String.valueOf(currentTravel.getIdSatatusTravel()));



                        // CHOFER BUSQUEDA DE CLIENTE //
                        if (currentTravel.getIdSatatusTravel() == 4) {
                            btInitVisible(true);
                            btCancelVisible(true);
                            btPreFinishVisible(false);
                            btnFlotingVisible(false);
                            setInfoTravel();


                            Log.d("VIAJE ESTATUS ", "1");


                            // EN CURSO //
                        } else if (currentTravel.getIdSatatusTravel() == 5) {
                            //activeTimerInit();
                            btPreFinishVisible(true);
                            btInitVisible(false);
                            btCancelVisible(false);
                            btnFlotingVisible(false);
                            setInfoTravel();

                            // POR ACEPTAR//
                        } else if (currentTravel.getIdSatatusTravel() == 2) {

                            viewAlert = false;
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

                        } else if (currentTravel.getIdSatatusTravel() == 0) {


                            final int idTravel = currentTravel.getIdTravel();

                            AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                            alertDialog.setTitle("Viaje Cancelado! " + currentTravel.getCodTravel());
                            alertDialog.setMessage(currentTravel.getReason());
                            alertDialog.setCancelable(false);
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            confirmCancelByDriver(idTravel);
                                        }
                                    });
                            alertDialog.show();

                            btInitVisible(false);
                            btCancelVisible(false);
                            btPreFinishVisible(false);


                        }



                } else {


                    //Toast.makeText(getApplicationContext(), "Sin Viajes Asignados!", Toast.LENGTH_LONG).show();

                if(dialogTravel != null){
                    dialogTravel.dismiss();
                }
                    btInitVisible(false);
                    btCancelVisible(false);
                    btPreFinishVisible(false);
                    btnFlotingVisible(true);
                    textTiempo = (TextView) findViewById(R.id.textTiempo);
                    textTiempo.setVisibility(View.INVISIBLE);
                    currentTravel = null;
                   // _activeTimer();
                }


        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void  confirmCancelByDriver(int idTravel)
    {

        if (this.daoTravel == null) { this.daoTravel = HttpConexion.getUri().create(ServicesTravel.class); }


        try {
            Call<Boolean> call = this.daoTravel.confirmCancelByDriver(idTravel);

            Log.d("fatal", call.request().toString());
            Log.d("fatal", call.request().headers().toString());

            call.enqueue(new Callback<Boolean>() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {


                    currentTravel = null;
                    setInfoTravel();

                }

                public void onFailure(Call<Boolean> call, Throwable t) {
                    Snackbar.make(findViewById(android.R.id.content),
                            "ERROR ("+t.getMessage()+")", Snackbar.LENGTH_LONG).show();
                }


            });

        } finally {
            this.daoTravel = null;
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
                Log.d("JSON TOKEN ", gson.toJson(T));

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

        this.getParam();

        if(dialogTravel != null){
            dialogTravel.dismiss();
        }

        super.onResume();


        if(currentTravel !=  null) {
            Log.d("currentTravel", "onResume"+String.valueOf(currentTravel.getIdSatatusTravel()));

            controlVieTravel();

        }else {

            getCurrentTravelByIdDriver();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(HomeFragment.SPCKETMAP !=null) {
            HomeFragment.SPCKETMAP.disconnect();
        }
    }

    private BroadcastReceiver broadcastReceiverLoadTodays = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("BroadcastReceiver 1-", String.valueOf(currentTravel));


            if(currentTravel == null){
                getCurrentTravelByIdDriver();
            }else {
                controlVieTravel();
            }

            Log.d("BroadcastReceiver 2-", String.valueOf(ws));


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
            viewAlert = false;
            if(dialogTravel != null) {
                dialogTravel.dismiss();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBoolean("uri", true);

    }



    public void showFinshTravel() throws IOException {

        pay();
        final LinearLayout lg = (LinearLayout) findViewById(R.id.payment);
        lg.setVisibility(View.VISIBLE);




    }


    public void pay()
    {

        try{

        final TextView txtMount = (TextView) findViewById(R.id.txt_mount);
        final TextView distance_txt = (TextView) findViewById(R.id.distance_txt);
        final TextView txtTimeslep = (TextView) findViewById(R.id.txtTimeslep);
        final TextView txtamounFlet = (TextView) findViewById(R.id.amount_flet_txt);

        /* VERIFICAMOS METODOS DE PAGO DISPONIBLES */
        if(currentTravel != null)
        {
            if(currentTravel.getIsTravelComany() == 1)// EMPRESA
            {
                if (currentTravel.isPaymentCash != 1) {
                    btnFinishCash.setEnabled(false);
                } else {
                    btnFinishCash.setEnabled(true);
                }
                btnFinishVo.setEnabled(true);

            }else {
                btnFinishCash.setEnabled(true);
                btnFinishVo.setEnabled(false);

            }
        }else {
            btnFinishCash.setEnabled(true);
        }
        /* */


            PARAM_1  = Double.parseDouble(gloval.getGv_param().get(0).getValue());// PRECIO DE LISTA
            double PARAM_5  = Double.parseDouble(gloval.getGv_param().get(4).getValue());// PRECIO LISTA TIEMPO DE ESPERA
            PARAM_6  = Double.parseDouble(gloval.getGv_param().get(5).getValue());// PRECIO LISTA TIEMPO DE VUELTA
            double PARAM_16  = Double.parseDouble(gloval.getGv_param().get(15).getValue());// VALOR MINIMO DE VIAJE
            int param25 = Integer.parseInt(gloval.getGv_param().get(25).getValue());// SE PUEDE VER PRECIO EN VIAJE EN APP
            int param78 = Integer.parseInt(gloval.getGv_param().get(77).getValue());// BAJADA DE BANDERA
            double PARAM_74  = Double.parseDouble(gloval.getGv_param().get(73).getValue());// PRECIO POR HORA AYUDANTES
            double numAsiste  = currentTravel.isFleetTravelAssistance;//   AYUDANTES
            double hor;
            double min = 0;


        btPreFinishVisible(false);

        Log.d("-TRAVEL DistanceSave-", String.valueOf( currentTravel.getDistanceSave()));


        /* DITANCIA TOTAL RECORRIDA */
        m_total  = HomeFragment.calculateMiles(false)[0];//BUSCAMOS LA DISTANCIA TOTLA

        Log.d("-TRAVEL totalDistance-", String.valueOf( m_total));

       /// if(m_total > 0){
            kilometros_total = (m_total) * 0.001;//LO CONVERTIMOS A KILOMETRO y sumamos la distancia salvada
       /* }else {
            kilometros_total = (currentTravel.getDistanceSave()) * 0.001;//LO CONVERTIMOS A KILOMETRO y sumamos la distancia salvada

        }*/

          /*  float  DistanceSave  = pref.getFloat("distanceTravel", 0);

            if(DistanceSave > 0) {
                kilometros_total = kilometros_total DistanceSave * 0.001;//LO CONVERTIMOS A KILOMETRO y sumamos la distancia salvada
            }*/

            //**************************//

        /* DITANCIA TOTAL VULETA */
        m_vuelta  = HomeFragment.calculateMiles(false)[1];//BUSCAMOS LA DISTANCIA VUELTA
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

            /*VERIFICAMOS SI ES VIAJE POR HORA*/
            if(currentTravel.getIsTravelByHour() == 1) {
                int hours = new Time(System.currentTimeMillis()).getHours();
                int newHours = hours - gloval.getGv_hour_init_travel();

                if(newHours <1 || gloval.getGv_hour_init_travel() == 0) {
                    newHours = 1;
                }


                    Log.d("-hours- vehicleMinHourService", String.valueOf(hours));


                if(currentTravel.getKmex() >0 && kilometros_total > currentTravel.getKmex()){ // KILOMETROS EXEDENTES

                    double kilometros_extra = currentTravel.getKmex() - kilometros_total;
                    amounCalculateGps = (newHours * currentTravel.getPriceHour()) +  (kilometros_extra * currentTravel.getPricePerKmex());

                }else {
                    amounCalculateGps = newHours * currentTravel.getPriceHour();

                }



            }
            else{

            /*VERIFICAMOS SI ESTA ACTIVO EL CAMPO BENEFICIO POR KILOMETRO PARA ESA EMPRESA*/
                Log.d("-TRAVEL Beneficio-", String.valueOf(currentTravel.getIsBenefitKmList()));
                if (currentTravel.getIsBenefitKmList() == 1
                        && currentTravel.getListBeneficio() != null
                        && currentTravel.getListBeneficio().size() > 0) {

                    Log.d("-TRAVEL kilometros_total-", String.valueOf(kilometros_total));
                    Log.d("-TRAVEL BenefitsToKm-", String.valueOf(currentTravel.getBenefitsToKm()));
                    Log.d("-TRAVEL BenefitsFromKm-", String.valueOf(currentTravel.getBenefitsFromKm()));


                    /* VERIFICAMOS SI ESTA DENTRO DE EL RANGO DE EL BENEFICIO ESTABLECIDO */
                     amounCalculateGps = this.getPriceBylistBeneficion(currentTravel.getListBeneficio(),km_ida);

                    // VERIFICAMOS SI HAY RETORNO //
                    if (isRoundTrip) {
                        amounCalculateGps = amounCalculateGps + this.getPriceReturnBylistBeneficion(currentTravel.getListBeneficio(),kilometros_ida);
                    }

                    if(amounCalculateGps <1){
                        amounCalculateGps = kilometros_total * currentTravel.getPriceDitanceCompany();
                        Log.d("-TRAVEL amounCalculateGps (2)-", String.valueOf(amounCalculateGps));
                    }


                } else {
                    amounCalculateGps = kilometros_total * currentTravel.getPriceDitanceCompany();// PARA CLIENTES EMPREA BUSCAMOS EL PRECIO DE ESA EMPRESA

                    Log.d("-TRAVEL amounCalculateGps (3)-", String.valueOf(amounCalculateGps));

                    if (isRoundTrip) {
                        Log.d("-TRAVEL isRoundTrip -", String.valueOf(isRoundTrip));
                        amounCalculateGps = kilometros_total * currentTravel.getPriceDitanceCompany();
                        Log.d("-TRAVEL amounCalculateGps (4)-", String.valueOf(amounCalculateGps));
                        amounCalculateGps = amounCalculateGps + kilometros_vuelta * currentTravel.getPriceReturn();
                        Log.d("-TRAVEL amounCalculateGps (5)-", String.valueOf(amounCalculateGps));
                    }
                }
            }



        }
        else // PARTICULARES
        {



            // VERIFICAMOS EL BEMEFICIO POR KM PARA CLIENTES PARTICULARES //

            if(currentTravel.getIsBenefitKmClientList() == 1
                    && currentTravel.getListBeneficio() != null
                    && currentTravel.getListBeneficio().size() > 0) {

                amounCalculateGps = this.getPriceBylistBeneficion(currentTravel.getListBeneficio(),kilometros_ida);

                if(isRoundTrip) {
                    Log.d("-TRAVEL amounCalculateGps (7)-", String.valueOf(isRoundTrip));

                    if (currentTravel.getIsBenefitKmList() == 1) {
                        amounCalculateGps = amounCalculateGps + this.getPriceReturnBylistBeneficion(currentTravel.getListBeneficio(), kilometros_vuelta);
                    }
                }

                if(amounCalculateGps <1){
                    amounCalculateGps = kilometros_total * PARAM_1;// PARA CLIENTES PARTICULARES BUSCAMOS EL PRECIO DE LISTA

                }

            }else {


                Log.d("-TRAVEL amounCalculateGps (6)-", String.valueOf(amounCalculateGps));
                Log.d("-TRAVEL amounCalculateGps (7)-", String.valueOf(isRoundTrip));

                if(isRoundTrip)
                {
                    amounCalculateGps = kilometros_ida * PARAM_1;
                    Log.d("-TRAVEL amounCalculateGps (8)-", String.valueOf(amounCalculateGps));
                    amounCalculateGps =  amounCalculateGps + kilometros_vuelta * PARAM_6;
                    Log.d("-TRAVEL amounCalculateGps (9)-", String.valueOf(amounCalculateGps));

                }else{
                    amounCalculateGps = kilometros_total * PARAM_1;// PARA CLIENTES PARTICULARES BUSCAMOS EL PRECIO DE LISTA

                }

            }


        }


        // SUMA EL ORIGEN PACTADO //
        amounCalculateGps =  amounCalculateGps + currentTravel.getAmountOriginPac();
        Log.d("-TRAVEL amounCalculateGps (10)-", String.valueOf(amounCalculateGps));



        // VALIDAMOS VIAJES PUTO A PUNTO
            if(currentTravel.getIsPointToPoint() == 1){
                Log.d("getIsPointToPoint", String.valueOf(currentTravel.getIsPointToPoint()));
                amounCalculateGps = currentTravel.pricePoint; // sumamos el precio del punto a punto
                kilometros_total = 0;
            }


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
            txtTimeslep.setText(tiempoTxt + " Seg - $" + Double.toString(round(extraTime, 2)));
        }else {
            txtTimeslep.setText(tiempoTxt + " Seg");
        }


        if(param25 == 1) {
            distance_txt.setText(df.format(kilometros_total) + " Km - $"+Double.toString(round(amounCalculateGps,2)));

        }else {
            distance_txt.setText(df.format(kilometros_total) + " Km ");

        }


         priceFlet = 0;
        if(currentTravel.getIsFleetTravelAssistance() > 0){

            int hours = new Time(System.currentTimeMillis()).getHours();
            int newHours = hours - gloval.getGv_hour_init_travel();

            if(newHours <1 || gloval.getGv_hour_init_travel() == 0){
                newHours = 1;
            }

            priceFlet = (PARAM_74 * newHours) * numAsiste;
            txtamounFlet.setText("$"+df.format(priceFlet));
        }else {
            txtamounFlet.setText("$0.0");
        }






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



        double bandera = 0;
        if(param78 > 0) {
            if(currentTravel.getIsTravelComany() == 1){// EMPRESA
               if(param78 == 1){
                   bandera = currentTravel.getPriceMinTravel();
               }else if(param78 == 2) {
                   if(amounCalculateGps <  currentTravel.getPriceMinTravel()) {
                       bandera = currentTravel.getPriceMinTravel();

                   }
               }

            }else{
                if(param78 == 1) {
                    bandera = PARAM_16;
                }
                else if(param78 == 2) {
                    if(amounCalculateGps <  PARAM_16) {
                        bandera = PARAM_16;
                    }
                }
            }

        }





            totalFinal =  amounCalculateGps + extraTime + myDouble + parkin + bandera + priceFlet;
        Log.d("-TRAVEL  totalFinal -", String.valueOf(totalFinal));


        if(param25 == 1){
            txtMount.setText("$"+Double.toString(round(totalFinal,2)));
        }
        else {
            txtMount.setText("---");
        }

    }catch (Exception e){
        Log.d("ERRROR",e.getMessage());
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


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Confirma para salir de la App?")
                .setCancelable(false)
                .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            fn_exit();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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

        if(couterReservations >0) {
            Toast.makeText(getApplicationContext(), "Tienes (" + couterReservations + ") Reservas!", Toast.LENGTH_LONG).show();
        }
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
            btnFlotingVisible(false);

            return true;
        }
        else if (id == R.id.action_reervations) {

            fn_gotoreservation();
            btnFlotingVisible(false);

            return true;
        }
        else if (id == R.id.action_refhesh) {

            fn_refhesh();
            btnFlotingVisible(false);

            return true;
        }
        else if (id == R.id.action_profile) {

            fn_gotoprofile();
            btnFlotingVisible(false);
            return true;
        }



        return super.onOptionsItemSelected(item);
    }


    private void fn_verificateConexion() {

        if( Utils.verificaConexion(this) == true) {
            _NOCONEXION = false;

        }else
        {

            if(_NOCONEXION == false) {

                final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Sin Conexion a Internet,  cerifica tu Conexion");
                alertDialog.setCancelable(false);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Verificar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                                //finish();
                                alertDialog.dismiss();
                               // System.exit(0);


                            }
                        });
                alertDialog.show();
                _NOCONEXION = true;
            }


        }
    }


    private void fn_refhesh() {

            getParam();
            getCurrentTravelByIdDriver();

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




            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("TIMER","TIMER 1");
                            setLocationVehicheDriver();

                        }
                    });

                }
            }, 0, 60000);


        timerConexion = new Timer();
        timerConexion.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(
                        new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TIMER","TIMER 2");
                        fn_verificateConexion();

                    }
                });

            }
        }, 0, 10000);

    }

    public  void setLocationVehicheDriver()
    {



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

           Log.d("setLocationVehicheDriver 00", String.valueOf(HomeFragment.nameLocation));
            Log.d("setLocationVehicheDriver  01", "**"+add);

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
                                HomeFragment.calculateMiles(false)[0]


                        )
                        );


                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                Log.d("setLocationVehicheDriver", gson.toJson(travel));


                Call<RemisSocketInfo> call = this.daoTravel.sendPosition(travel);

                call.enqueue(new Callback<RemisSocketInfo>() {
                    @Override
                    public void onResponse(Call<RemisSocketInfo> call, Response<RemisSocketInfo> response) {

                        Log.d("setLocationVehicheDriver Response raw header", response.headers().toString());
                        Log.d("setLocationVehicheDriver Response raw", String.valueOf(response.raw().body()));
                        Log.d("setLocationVehicheDriver Response code", String.valueOf(response.code()));

                        if (response.code() == 200) {

                            RemisSocketInfo list = (RemisSocketInfo) response.body();
                            notificate(list.getListNotification(), list.getListReservations());
                            fn_refhesh();


                        }

                    }

                    @Override
                    public void onFailure(Call<RemisSocketInfo> call, Throwable t) {
                        Log.d("**ERROR**", t.getMessage());
                        // Toast.makeText(getApplicationContext(), "ERROR ENVIADO UBICACION!", Toast.LENGTH_LONG).show();

                    }
                });

            }


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
                    HomeFragment.calculateMiles(false)[0]
            );

            DatabaseReference locationRef = databaseReference.child("Travel");
            locationRef.push().setValue(travel);





        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setNotification(final InfoTravelEntity travel)
    {
            Log.d("currentTravel  full", String.valueOf(viewAlert));

        if(viewAlert == false)
        {
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

            loading = ProgressDialog.show(HomeActivity.this, "Enviado", "Espere unos Segundos...", true, false);



            Call<InfoTravelEntity> call = this.daoTravel.refuse(idTravel);

            Log.d("fatal", call.request().toString());
            Log.d("fatal", call.request().headers().toString());

            call.enqueue(new Callback<InfoTravelEntity>() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onResponse(Call<InfoTravelEntity> call, Response<InfoTravelEntity> response) {

                    loading.dismiss();
                    Toast.makeText(getApplicationContext(), "VIAJE RECHAZADO!", Toast.LENGTH_LONG).show();
//                    Log.d("fatal",response.body().toString());

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
                    btnFlotingVisible(true);
                    btInitVisible(false);
                    HomeFragment.clearInfo();
                    viewAlert = false;


                }

                public void onFailure(Call<InfoTravelEntity> call, Throwable t) {
                    loading.dismiss();

                    Snackbar.make(findViewById(android.R.id.content),
                            "ERROR ("+t.getMessage()+")", Snackbar.LENGTH_LONG).show();
                }


            });

        } finally {
            this.daoTravel = null;
        }
    }

    public  void btCancelVisible(boolean b)
    {
        final Button btn = (Button) findViewById(R.id.btn_cancel);

        Log.d("PARAM_66 2", String.valueOf(PARAM_66));

        if(b)
        {
            btn.setVisibility(View.VISIBLE);
            if(PARAM_66 == 1){
                btn.setEnabled(true);
            }else {
                btn.setEnabled(false);
            }

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

        Log.d("Pre final", String.valueOf(b));
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
                    Snackbar.make(findViewById(android.R.id.content),
                            "ERROR ("+t.getMessage()+")", Snackbar.LENGTH_LONG).show();
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

            call.enqueue(new Callback<Boolean>() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                    setInfoTravel();

                }

                public void onFailure(Call<Boolean> call, Throwable t) {
                    Snackbar.make(findViewById(android.R.id.content),
                            "ERROR ("+t.getMessage()+")", Snackbar.LENGTH_LONG).show();
                }


            });

        } finally {
            this.daoTravel = null;
        }
    }

    public  void  getCurrentTravelByIdDriver()
    {

        if (this.daoTravel == null) { this.daoTravel = HttpConexion.getUri().create(ServicesTravel.class); }


        try {
            Call<InfoTravelEntity> call = this.daoTravel.getCurrentTravelByIdDriver(gloval.getGv_id_driver());

            Log.d("fatal", call.request().toString());

            call.enqueue(new Callback<InfoTravelEntity>() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onResponse(Call<InfoTravelEntity> call, Response<InfoTravelEntity> response) {

                    InfoTravelEntity TRAVEL = (InfoTravelEntity) response.body();

                    gloval.setGv_travel_current(TRAVEL);
                    currentTravel = gloval.getGv_travel_current();
                    controlVieTravel();
                }

                public void onFailure(Call<InfoTravelEntity> call, Throwable t) {
                    Snackbar.make(findViewById(android.R.id.content),
                            "ERROR ("+t.getMessage()+")", Snackbar.LENGTH_LONG).show();
                }


            });

        } finally {
            this.daoTravel = null;
        }
    }

    /* METODO PARA ACEPATAR EL VIAJE*/
    public  void  aceptTravel(int idTravel)
    {



            if (this.daoTravel == null) {
                this.daoTravel = HttpConexion.getUri().create(ServicesTravel.class);
            }


            try {
                loading = ProgressDialog.show(HomeActivity.this, "Enviado", "Espere unos Segundos...", true, false);

                Call<InfoTravelEntity> call = this.daoTravel.accept(idTravel);

                Log.d("fatal", call.request().toString());
                Log.d("fatal", call.request().headers().toString());

                call.enqueue(new Callback<InfoTravelEntity>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(Call<InfoTravelEntity> call, Response<InfoTravelEntity> response) {
                        loading.dismiss();

                        Toast.makeText(getApplicationContext(), "VIAJE ACEPTADO...", Toast.LENGTH_LONG).show();
//                        Log.d("fatal", response.body().toString());


                        btnFlotingVisible(false);
                        btInitVisible(true);
                        btCancelVisible(true);
                        cliaerNotificationAndoid();
                        viewAlert = true;
                        dialogTravel.dismiss();
                        currentTravel = response.body();
                        setInfoTravel();

                    }

                    public void onFailure(Call<InfoTravelEntity> call, Throwable t) {
                        loading.dismiss();

                        Snackbar.make(findViewById(android.R.id.content),
                                "ERROR (" + t.getMessage() + ")", Snackbar.LENGTH_LONG).show();
                    }


                });

            } finally {

                this.daoTravel = null;
            }

    }

    public  void  initTravel()
    {
        if (this.daoTravel == null) { this.daoTravel = HttpConexion.getUri().create(ServicesTravel.class); }


        if(this.checkPermision()) {
            if (this.checkDistanceSucces()) {

                try {
                    loading = ProgressDialog.show(HomeActivity.this, "Enviado", "Espere unos Segundos...", true, false);

                    Call<InfoTravelEntity> call = this.daoTravel.init(currentTravel.getIdTravel());

                    call.enqueue(new Callback<InfoTravelEntity>() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onResponse(Call<InfoTravelEntity> call, Response<InfoTravelEntity> response) {

                            loading.dismiss();
                            btInitVisible(false);
                            btCancelVisible(false);
                            btPreFinishVisible(true);
                            btnFlotingVisible(false);

                            currentTravel = response.body();
                            setInfoTravel();


                            //activeTimerInit();
                        }

                        public void onFailure(Call<InfoTravelEntity> call, Throwable t) {
                            loading.dismiss();
                            Snackbar.make(findViewById(android.R.id.content),
                                    "ERROR (" + t.getMessage() + ")", Snackbar.LENGTH_LONG).show();
                        }
                    });
                    int hours = new Time(System.currentTimeMillis()).getHours();
                    Log.d("hours", String.valueOf(hours));
                    gloval.setGv_hour_init_travel(hours);// GUARDAMOS LA HORA QUE LO INICIO
                } finally {
                    this.daoTravel = null;
                }
            }else {
                Snackbar.make(findViewById(android.R.id.content),
                        "No puedes iniciar este viaje, debe aproximarse mas al punto de origen", Snackbar.LENGTH_LONG).show();
            }
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Presiona continuar para realizar el pago con Tarjetas")
                .setCancelable(false)
                .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        idPaymentFormKf = 3;
                        //finishTravel();

                        //   Intent intent = new Intent(getApplicationContext(), PaymentCreditCar.class);
                        // intent.putExtra("TotalAmount",this.totalFinal);

                        //startActivityForResult(intent, CREDIT_CAR_ACTIVITY);


                        preFinihMpago();
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


    /* METODO PARA FINALIZAR O PREFINALIZAR  UN VIAJE*/
    public  void  finishTravel() {

        if (this.daoTravel == null) { this.daoTravel = HttpConexion.getUri().create(ServicesTravel.class); }

        try {
            loading = ProgressDialog.show(HomeActivity.this, "Finalizando Viaje", "Espere unos Segundos...", true, false);


            if(currentTravel != null) {

                String lat = "";
                String lon = "";
                String add = "";

                if (HomeFragment.getmLastLocation() != null) {
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


                double _RECORIDO_TOTAL = 0;
                if (kilometros_total > 0) {
                    _RECORIDO_TOTAL = Utils.round(kilometros_total, 2);
                }


                TraveInfoSendEntity travel =
                        new TraveInfoSendEntity(new
                                TravelLocationEntity
                                (
                                        currentTravel.getIdTravel(),
                                        amounCalculateGps,
                                        //Double.parseDouble(val),
                                        _RECORIDO_TOTAL,
                                        df.format(kilometros_total),
                                        add,
                                        lon,
                                        lat,
                                        myDouble,
                                        parkin,
                                        extraTime,
                                        tiempoTxt + " Segundos",
                                        idPaymentFormKf,
                                        mp_jsonPaymentCard,
                                        mp_paymentMethodId,
                                        mp_paymentTypeId,
                                        mp_paymentstatus,
                                        priceFlet

                                )
                        );


                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();


                Call<InfoTravelEntity> call = null;
                /* VERIFICAMOS I ESTA HABILITADO EL CIERRE DE VIAJES DEDE LA APP O NO*/


                int evaluationOperator;
                final int isTravelComany = currentTravel.isTravelComany;
                if(isTravelComany == 1) {
                    evaluationOperator = PARAM_3;
                }else {
                    evaluationOperator = PARAM_67;
                }

                if (evaluationOperator == 0) {
                    call = this.daoTravel.finishPost(travel);
                } else {
                    call = this.daoTravel.preFinishMobil(travel);
                }

                Log.d("Response request", call.request().toString());
                Log.d("Response request header", call.request().headers().toString());
                //System.out.println(gson.toJson(travel));


                call.enqueue(new Callback<InfoTravelEntity>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onResponse(Call<InfoTravelEntity> call, Response<InfoTravelEntity> response) {
                        loading.dismiss();
                        Log.d("Response raw header", response.headers().toString());
                        Log.d("Response raw", String.valueOf(response.raw().body()));
                        Log.d("Response code", String.valueOf(response.code()));



                        int evaluationOperator;
                        if(isTravelComany == 1) {
                            evaluationOperator = PARAM_3;
                        }else {
                            evaluationOperator = PARAM_67;
                        }


                        if (evaluationOperator == 0) {
                            Toast.makeText(HomeActivity.this, "VIAJE  FINALIZADO", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HomeActivity.this, "VIAJE ENVIADO PARA SU APROBACION", Toast.LENGTH_SHORT).show();
                        }


                        btInitVisible(false);
                        btCancelVisible(false);
                        btPreFinishVisible(false);
                        btnFlotingVisible(true);


                        currentTravel = null;
                        HomeFragment.MarkerPoints = null;
                        if (HomeFragment.options != null) {
                            HomeFragment.options.getPoints().clear();
                        }
                        gloval.setGv_travel_current(null);
                        setInfoTravel();
                        viewAlert = false;


                        tiempoTxt = 0;
                        textTiempo = (TextView) findViewById(R.id.textTiempo);
                        textTiempo.setVisibility(View.INVISIBLE);


                        final LinearLayout lg = (LinearLayout) findViewById(R.id.payment);
                        lg.setVisibility(View.INVISIBLE);

                        gloval.setGv_hour_init_travel(0);// GUARDAMOS LA HORA QUE LO INICIO
                       // editor.putFloat("distanceTravel", 0);
                        //editor.commit();

                    }

                    public void onFailure(Call<InfoTravelEntity> call, Throwable t) {
                        loading.dismiss();
                        Snackbar.make(findViewById(android.R.id.content),
                                "ERROR (" + t.getMessage() + ")", Snackbar.LENGTH_LONG).show();
                    }
                });
            }else {
                this.getCurrentTravelByIdDriver();
            }
        } finally {
            this.daoTravel = null;
        }

    }

    public void cliaerNotificationAndoid()
    {
        NotificationManager notifManager= (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();
    }

    // FIRMA O TARJETA //
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
            case CREDIT_CAR_ACTIVITY:
                super.onActivityResult(requestCode, resultCode, data);
                if(HomeActivity._PAYCREDITCAR_OK) {
                    finishTravel();
                }
                break;

        }

    }

    public void preFinihMpago(){

        loading = ProgressDialog.show(HomeActivity.this, "Cargando pago", "Espere unos Segundos...", true, false);
        new SendPostRequest().execute();


/*

        if (this.daoTravel == null) { this.daoTravel = HttpConexion.getUriMpago().create(ServicesTravel.class); }

        try {
            loading = ProgressDialog.show(HomeActivity.this, "Enviando pago", "Espere unos Segundos...", true, false);


            PagoEntity PAGO = new PagoEntity(
                    "6182935257674284",
                    "R6nTqEiH4GO0p5b8Z68rUyD7FmQgeAh7",
                    "ARG",
                    totalFinal
            );


            Call<ResponseBody> call = this.daoTravel.payMpago(PAGO);
            Log.d("Response request", call.request().toString());
            Log.d("Response request header", call.request().headers().toString());

            call.enqueue(new Callback<ResponseBody>() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    loading.dismiss();

                      GsonBuilder builder = new GsonBuilder();
                     Gson gson = builder.create();
                     System.out.println(gson.toJson(response));

                    ResponseBody result  = (ResponseBody) response.body();
                    HomeActivity.URL_MPAGO = String.valueOf(result);
                    Log.d("URL_MPAGO",HomeActivity.URL_MPAGO);

                }

                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    loading.dismiss();

                    Log.d("ERROR", t.getMessage());
                    Log.d("ERROR", t.getLocalizedMessage());
                    Log.d("ERROR", String.valueOf(call));

                    Snackbar.make(findViewById(android.R.id.content),
                            "ERROR (" + t.getMessage() + ")", Snackbar.LENGTH_LONG).show();
                }
            });

        }finally {
            this.daoTravel = null;
        }
        */

    }


    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL(HttpConexion.BASE_URL+"as_mpago/index.php"); // here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("clienteid", HomeActivity.PARAM_69);
                postDataParams.put("clientesecret", HomeActivity.PARAM_79);
                postDataParams.put("currency_id", HttpConexion.COUNTRY);
                postDataParams.put("unit_price", totalFinal);
                postDataParams.put("agency", gloval.getGv_base_intance());
                postDataParams.put("idTravel", currentTravel.getIdTravel());


                Log.d("postDataParams", String.valueOf(postDataParams));



                String message = postDataParams.toString();

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setFixedLengthStreamingMode(message.getBytes().length);



                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                writer.write(message);
                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {



                    InputStream inputStream = conn.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String response = "";
                    String line = "";
                    while ((line = bufferedReader.readLine())!=null){
                        response+= line;
                    }

                    bufferedReader.close();
                    inputStream.close();
                    conn.disconnect();
                    os.close();
                    return response.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {

              loading.dismiss();
              Intent intent = new Intent(getApplicationContext(), PaymentCreditCar.class);
              HomeActivity.mp_jsonPaymentCard = result;
              startActivityForResult(intent, CREDIT_CAR_ACTIVITY);


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
      /*  FloatingActionButton btnService = (FloatingActionButton) findViewById(R.id.fab);

        if(!isVisible)
        {
            btnService.setVisibility(View.INVISIBLE);
        }else
        {
            btnService.setVisibility(View.VISIBLE);
        }*/

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

            Bitmap imagen = null;
            try{


                Log.i("doInBackground" , "Entra en doInBackground");
                String url = params[0]+".JPEG";
                 imagen = descargarImagen(url);
                return imagen;

            }catch (Exception e){
                Log.d("ERROR",e.getMessage());
                return imagen;
            }


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

    public double getPriceReturnBylistBeneficion(List<BeneficioEntity> list,double distance){
        double value = 0;

        int indexBrack = 0;
        for (int i =0;i < list.size();i++){

            indexBrack++;

            // EVALUAMOS LOS DISTINTOS BENEFICIOS //
            if(distance >=  Double.parseDouble(list.get(i).getBenefitsFromKm())){
                value = value + Double.parseDouble(list.get(i).getBenefitPreceReturnKm());
            }
        }

        // VALIDAMOS SI EL TOTAL KM ES MAYOR A //
        if(distance > Double.parseDouble(list.get(indexBrack-1).getBenefitsFromKm())){
            double distanceExtraBeneficio = distance - Double.parseDouble(list.get(indexBrack-1).getBenefitsToKm());



            double amountExtra =0;
            // ES VIAJE A EMPRESA //
            if(currentTravel.getIsTravelComany() == 1){// EMPRESA
                amountExtra =   distanceExtraBeneficio * currentTravel.getPriceReturn();
            }else{
                amountExtra = distanceExtraBeneficio * PARAM_6;

            }
            value = amountExtra + value;
        }

        return  value;
    }

    public double getPriceBylistBeneficion(List<BeneficioEntity> list,double distance){
        double value = 0;

        int indexBrack = 0;
        for (int i =0;i < list.size();i++){

            indexBrack++;

            // EVALUAMOS LOS DISTINTOS BENEFICIOS //
            if(distance >=  Double.parseDouble(list.get(i).getBenefitsFromKm())){
                value  = value + Double.parseDouble(list.get(i).getBenefitsPreceKm());
            }
            else {
                break;
            }
        }

        // VALIDAMOS SI EL TOTAL KM ES MAYOR A //
        if(distance > Double.parseDouble(list.get(indexBrack-1).getBenefitsToKm())){
            double distanceExtraBeneficio = distance - Double.parseDouble(list.get(indexBrack-1).getBenefitsToKm());


            double amountExtra =0;
            // ES VIAJE A EMPRESA //
            if(currentTravel.getIsTravelComany() == 1){// EMPRESA
                amountExtra = distanceExtraBeneficio * currentTravel.getPriceDitanceCompany();
            }else{
                amountExtra = distanceExtraBeneficio * PARAM_1;

            }

            value = amountExtra + value;
        }

        return  value;
    }

    public  void  checkVersion()
    {


             HttpConexion.setBase(HttpConexion.instance);
            if (this.daoTravel == null) {
                this.apiService = HttpConexion.getUri().create(ServicesLoguin.class);
            }


            try {

                Call<Boolean> call = this.apiService.checkVersion(MainActivity.version);
                Log.d("Call request", call.request().toString());
                Log.d("Call request header", call.request().headers().toString());

                call.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                        Log.d("Response request", call.request().toString());
                        Log.d("Response request header", call.request().headers().toString());
                        Log.d("Response raw header", response.headers().toString());
                        Log.d("Response raw", String.valueOf(response.raw().body()));
                        Log.d("Response code", String.valueOf(response.code()));


                        if (response.code() == 200) {
                            boolean rs = (boolean) response.body();


                            if (!rs) {
                            } else {
                                AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                                alertDialog.setTitle("Existe Una Nueva version!, Debe Atualizar para poder Disfrutar de los Nuevos Beneficios!");
                                alertDialog.setCancelable(false);
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Actualizar",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                // Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                                //       Uri.parse("http://as-nube.com/apk.apk"));
                                                //startActivity(browserIntent);

                                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                try {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                } catch (android.content.ActivityNotFoundException anfe) {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                }
                                            }
                                        });
                                alertDialog.show();


                            }

                        } else {

                            AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                            alertDialog.setTitle("ERROR" + "(" + response.code() + ")");
                            alertDialog.setMessage(response.errorBody().source().toString());
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();

                        }


                    }

                    public void onFailure(Call<Boolean> call, Throwable t) {
                        Snackbar.make(findViewById(android.R.id.content),
                                "ERROR (" + t.getMessage() + ")", Snackbar.LENGTH_LONG).show();
                    }
                });

            } finally {
                this.apiService = null;

            }


    }

    public void getParam(){
        if (this.daoTravel == null) { this.daoTravel = HttpConexion.getUri().create(ServicesTravel.class); }

        Log.d("PARAM_69", "GET PARAM");



        try {

        Call<List<paramEntity>> call = this.daoTravel.getparam();

            Log.d("PARAM_69", call.request().toString());
            Log.d("PARAM_69", call.request().headers().toString());


            call.enqueue(new Callback<List<paramEntity>>() {
            @Override
            public void onResponse(Call<List<paramEntity>> call, Response<List<paramEntity>> response) {

                Log.d("PARAM_69", response.headers().toString());
                Log.d("PARAM_69", String.valueOf(response.raw().body()));
                Log.d("PARAM_69", String.valueOf(response.code()));



                if (response.code() == 200) {

                    //the response-body is already parseable to your ResponseBody object
                    List<paramEntity> listParam = (List<paramEntity>) response.body();
                    gloval.setGv_param(listParam);
                    setParamLocal();

                    refreshButomPermision();


                }


            }

            @Override
            public void onFailure(Call<List<paramEntity>> call, Throwable t) {
                Snackbar.make(findViewById(android.R.id.content),
                        "ERROR ("+t.getMessage()+")", Snackbar.LENGTH_LONG).show();
            }
        });

    } finally {
        this.daoTravel = null;

    }
    }

    public void  refreshButomPermision(){
        Log.d("PARAM_79", String.valueOf(PARAM_69.isEmpty()));
        Log.d("PARAM_79", String.valueOf(PARAM_79.isEmpty()));


        if(PARAM_69.isEmpty()  && PARAM_79.isEmpty()){
            Snackbar.make(findViewById(android.R.id.content),
                    "MERCADO PAGO NO CONFIGURADO, LA AGENCIA DEBE CONFIGURAR EL MOTOR DE PAGO!",
                    Snackbar.LENGTH_LONG)
                    .setDuration(9000).show();
        }

        if(PARAM_68 == 1 && PARAM_69.length() > 0){
            btnFinishCar.setEnabled(true);
            btnFinishCar.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click
                    idPaymentFormKf = 3;
                    finishTravelCreditCar();
                }
            });
        }else {
            btnFinishCar.setEnabled(false);
        }
    }




}
