package com.apreciasoft.admin.asremis.Activity;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.apreciasoft.admin.asremis.Entity.DestinationEntity;
import com.apreciasoft.admin.asremis.Entity.InfoTravelEntity;
import com.apreciasoft.admin.asremis.Entity.OriginEntity;
import com.apreciasoft.admin.asremis.Entity.TravelBodyEntity;
import com.apreciasoft.admin.asremis.Entity.TravelEntity;
import com.apreciasoft.admin.asremis.Entity.reason;
import com.apreciasoft.admin.asremis.Entity.reporte;
import com.apreciasoft.admin.asremis.Entity.resp;
import com.apreciasoft.admin.asremis.Entity.token;
import com.apreciasoft.admin.asremis.Entity.tokenFull;
import com.apreciasoft.admin.asremis.Fracments.HistoryTravelDriver;
import com.apreciasoft.admin.asremis.Fracments.HomeClientFragment;
import com.apreciasoft.admin.asremis.Fracments.ListTypeCarLayout;
import com.apreciasoft.admin.asremis.Fracments.NotificationsFrangment;
import com.apreciasoft.admin.asremis.Fracments.PaymentFormClient;
import com.apreciasoft.admin.asremis.Fracments.ProfileClientFr;
import com.apreciasoft.admin.asremis.Fracments.ReservationsFrangment;
import com.apreciasoft.admin.asremis.Http.HttpConexion;
import com.apreciasoft.admin.asremis.R;
import com.apreciasoft.admin.asremis.Services.ServicesLoguin;
import com.apreciasoft.admin.asremis.Services.ServicesTravel;
import com.apreciasoft.admin.asremis.Util.GlovalVar;
import com.apreciasoft.admin.asremis.Util.GooglePlacesAutocompleteAdapter;
import com.apreciasoft.admin.asremis.Util.WsTravel;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jorge gutierrez on 07/03/2017.
 */

public class HomeClientActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AdapterView.OnItemClickListener,View.OnClickListener ,AdapterView.OnItemSelectedListener {

    private static String ReservationName;
    ServicesTravel apiService = null;
    List<reason> list = null;

    Integer motivo = 0;


    protected PowerManager.WakeLock wakelock;
    public static GlovalVar gloval;
    public ServicesTravel daoTravel = null;
    public static InfoTravelEntity currentTravel;
    public ServicesLoguin daoLoguin = null;
    public WsTravel ws = null;
    public ListTypeCarLayout dialogFragment = null;
    public static final int PROFILE_DRIVER_ACTIVITY = 2;
    public static  ArrayList resultList = null;
    public static  ArrayList resultListPlaceID = null;
    public List<Integer> listCatgoryId = new ArrayList<Integer>();
    public static String location = "";
    public String lat = "";
    public String lon = "";

    public static String destination = "";
    public static String latDestination = "";
    public static String lonDestination = "";


    public Spinner spinner;
    public Spinner spinner2;
    public String dateTravel= "";

    public boolean isReervation = false;

    /* GOOGLE PALCE   */
    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAIL = "/details";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyApZ1embZ2bhcI4Ir8NepmyjTfNvGvjUas";

    /*DATE*/
    //UI References
    private EditText fromDateEtxt;
    private EditText fromTimeEtxt;
    private DatePickerDialog fromDatePickerDialog;
    private TimePickerDialog fromTimePickerDialog;
    private SimpleDateFormat dateFormatter;

    private static String[] VEHYCLETYPE = new String[0];

    public FloatingActionMenu materialDesignFAM;
    public FloatingActionButton floatingActionButton1, floatingActionButton2;



    public ProgressDialog loading;
    private Integer idTypeVehicle;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        //evitar que la pantalla se apague
        final PowerManager pm=(PowerManager)getSystemService(Context.POWER_SERVICE);
        this.wakelock=pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "etiqueta");
        wakelock.acquire();


        //this.apiService = HttpConexion.getUri().create(ServicesTravel.class);

        //Prueba de funcion para traer data de motivos
        serviceAllTravel();


        //Toast.makeText(getApplicationContext(), gloval.getGv_id_cliet() , Toast.LENGTH_SHORT).show();


        Log.d("TAG","Leandro esta es la prueba despues de la funcion");

        setContentView(R.layout.activity_client_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // variable global //
        gloval = ((GlovalVar)getApplicationContext());



        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token: ", token);
        enviarTokenAlServidor(token,gloval.getGv_user_id());


        Button btnrequertReser = (Button) findViewById(R.id.btnrequertReser);
        btnrequertReser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    requestTravel();

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }


        });

        Button btnrequetTravelNow = (Button) findViewById(R.id.btn_requetTravelNow);
        btnrequetTravelNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    requestTravel();

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }


        });



        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item2);


        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked

                //activeGifMotivos(true,"");

                if(isReervation)
                {
                    LinearLayout contentInfoReervation = (LinearLayout) findViewById(R.id.contentInfoReervation);
                    contentInfoReervation.setVisibility(LinearLayout.INVISIBLE);
                    isReervation = false;

                }
                else
                {
                    LinearLayout contentInfoReervation = (LinearLayout) findViewById(R.id.contentInfoReervation);
                    contentInfoReervation.setVisibility(LinearLayout.VISIBLE);
                    isReervation = true;

                }

                materialDesignFAM.close(true);


            }
        });


        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked
                contetRequestTravelVisible(true);
                materialDesignFAM.close(true);
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        FragmentManager fr =  getFragmentManager();
        fr.beginTransaction().replace(R.id.content_frame_client, new HomeClientFragment()).commit();

        // HEADER MENU //
        View header = navigationView.getHeaderView(0);
        TextView name = (TextView)header.findViewById(R.id.username);
        TextView email = (TextView)header.findViewById(R.id.email);
        name.setText(gloval.getGv_user_name());
        email.setText(gloval.getGv_user_mail());


        currentTravel = gloval.getGv_travel_current();
        controlViewTravel();

        // WEB SOCKET //
        ws = new WsTravel();
        ws.connectWebSocket(gloval.getGv_user_id());



        /*GOOGLE PACE*/
        AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));

        autoCompView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView < ? > adapter, View view, final int position, long arg){
                // TODO Auto-generated method stub



                Thread thread = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        placeDetail(resultListPlaceID.get(position).toString());
                    }
                });

                thread.start();


                String str = (String) adapter.getItemAtPosition(position);

                HomeClientActivity.ReservationName = String.valueOf(str);
                Log.d("ORIGEN - 0",str);

                }

            });


        //autoCompView.setOnItemClickListener(this);

        AutoCompleteTextView autoCompView2 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView2);
        autoCompView2.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_item));

        autoCompView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView < ? > adapter, View view, final int position, long arg){
                // TODO Auto-generated method stub



                // this.location = String.valueOf(GooglePlacesAutocompleteAdapter.getItemByIndex(position));
                // this.location = String.valueOf(adapterView.getAdapter().getClass().getName();


                Thread thread = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        placeDetail(resultListPlaceID.get(position).toString());
                    }
                });

                thread.start();

                String str = (String) adapter.getItemAtPosition(position);

                HomeClientActivity.destination = str;
                Log.d("ORIGEN - 1",str);

            }

        });

        //autoCompView2.setOnItemClickListener(this);




        contetRequestTravelVisible(false);


        /*EDIT TEX DATE */
        dateFormatter = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        findViewsById();
        setDateTimeField();

        /*SETEAR CATEGORIAS */
        // Spinner element
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        _setCategory();
        _setCategory2();
        /*------------------*/


        _setEditPlaceHolder();


        getPick(gloval.getGv_user_id());



        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);


        int PARAM_35 =  Integer.parseInt(gloval.getGv_param().get(34).getValue());// PUEDE SOLICITAR  RESERVA
        int PARAM_36 =  Integer.parseInt(gloval.getGv_param().get(35).getValue());// PUEDE SOLICITAR  VIAJES

        if(PARAM_35 != 1){

            floatingActionButton1.setEnabled(false);
        }

        if(PARAM_36 != 1){
            floatingActionButton2.setEnabled(false);
        }



    }


    public void  _setEditPlaceHolder()
    {

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setHint("A Donde Vas?");

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();
        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("Place: ", "Place: " + place.getName());//get place details here

                HomeClientActivity.destination = (String) place.getName();
                HomeClientActivity.latDestination = String.valueOf(place.getLatLng().latitude);
                HomeClientActivity.lonDestination = String.valueOf(place.getLatLng().longitude);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("Place: ", "An error occurred: " + status);
            }
        });
    }


    private void findViewsById() {
        fromDateEtxt = (EditText) findViewById(R.id.txtdateReervation);
        fromDateEtxt.setInputType(InputType.TYPE_NULL);

        fromTimeEtxt = (EditText) findViewById(R.id.txtTimeReervation);
        fromTimeEtxt.setInputType(InputType.TYPE_NULL);

    }

    private void setDateTimeField() {
        fromDateEtxt.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fromDateEtxt.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));



        fromTimeEtxt.setOnClickListener(this);

        fromTimePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        fromTimeEtxt.setText(hourOfDay + ":" + minute);

                    }
                },newCalendar.get(Calendar.HOUR), newCalendar.get(Calendar.MINUTE), false);


    }

    @Override
    public void onClick(View view) {
        if(view == fromDateEtxt) {
            fromDatePickerDialog.show();
        }else
        if(view == fromTimeEtxt) {
            fromTimePickerDialog.show();

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setInfoTravel() {

        //Log.d("fatal", String.valueOf(currentTravel));


        if (currentTravel != null) {

            HomeClientFragment.setInfoTravel(currentTravel);


        } else {
            HomeClientFragment.clearInfo();
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverLoadTodays, new IntentFilter("update-message"));
        super.onResume();

        Log.d("NOT1","pasoooo");

        if(currentTravel !=  null) {
            Log.d("NOT","pasoooo");
            currentTravel = gloval.getGv_travel_current();
            controlViewTravel();
        }
    }


    public void _setCategory2()
    {

        List<String> list = new ArrayList<String>();

        VEHYCLETYPE = new String[gloval.getGv_listvehicleType().size()];

        for (int i =0 ;i< gloval.getGv_listvehicleType().size();i++)
        {
            list.add("Tipo De Vehiculo: "+gloval.getGv_listvehicleType().get(i).getVehiclenType());
            listCatgoryId.add(gloval.getGv_listvehicleType().get(i).getIdVehicleType());
        }


        list.toArray(VEHYCLETYPE);

        spinner2.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner2.setAdapter(dataAdapter);

          /*MULTI AUTO COMPLETE*/
        /* ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                 android.R.layout.simple_dropdown_item_1line, VEHYCLETYPE);
         AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.txtCatVehicleReervation);
         textView.setAdapter(adapter);
         textView.setOnItemClickListener(this);*/


    }


    public void _setCategory()
    {

        List<String> list = new ArrayList<String>();

        VEHYCLETYPE = new String[gloval.getGv_listvehicleType().size()];

        for (int i =0 ;i< gloval.getGv_listvehicleType().size();i++)
        {
            list.add("Tipo De Vehiculo: "+gloval.getGv_listvehicleType().get(i).getVehiclenType());
            listCatgoryId.add(gloval.getGv_listvehicleType().get(i).getIdVehicleType());
        }


        list.toArray(VEHYCLETYPE);



        // Spinner click listener
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

          /*MULTI AUTO COMPLETE*/
        /* ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                 android.R.layout.simple_dropdown_item_1line, VEHYCLETYPE);
         AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.txtCatVehicleReervation);
         textView.setAdapter(adapter);
         textView.setOnItemClickListener(this);*/


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        // Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

        this.idTypeVehicle =  listCatgoryId.get(position);

    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }





    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
/*
        String str = (String) adapterView.getItemAtPosition(position);
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();



        Log.d("lopo",String.valueOf(adapterView.getAdapter().getClass().getName()));
        Log.d("lopo",String.valueOf(resultListPlaceID.get(position).toString()));
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                placeDetail(resultListPlaceID.get(position).toString());
            }
        });

        thread.start();*/


       // if(adapterView.getAdapter().getClass().getName()
       //         == "com.apreciasoft.admin.asremis.Util.GooglePlacesAutocompleteAdapter")// GOOGLE PLACE
        //{

       /* Log.d("form", String.valueOf(view.getId()));

        switch (itemSearch) {

            case 1:
                this.location = String.valueOf(adapterView.getAdapter().getClass().getName());
                break;
            case 2:
                this.destination = String.valueOf(adapterView.getAdapter().getClass().getName());
                break;
        }*/



            //  Log.d("lopo",GooglePlacesAutocompleteAdapter.getItemByIndex(position).toString());
            // this.location = String.valueOf(GooglePlacesAutocompleteAdapter.getItemByIndex(position));
            // this.location = String.valueOf(adapterView.getAdapter().getClass().getName();
       // }


    }

    public  ArrayList<Double> placeDetail(String input) {

        ArrayList<Double> resultList1 = null;

        HttpURLConnection conn1 = null;
        StringBuilder jsonResults1 = new StringBuilder();
        try {
            StringBuilder sb1 = new StringBuilder(PLACES_API_BASE + TYPE_DETAIL + OUT_JSON);
            sb1.append("?placeid=" + URLEncoder.encode(input, "utf8"));
            sb1.append("&key=" + API_KEY);

            URL url1 = new URL(sb1.toString());
            //Log.e("url", url.toString());
            System.out.println("URL: "+url1);
            System.out.println("******************************* connexion au serveur *****************************************");

            conn1 = (HttpURLConnection) url1.openConnection();
            InputStreamReader in = new InputStreamReader(conn1.getInputStream());

            // Load the results into a StringBuilder
            int read1;
            char[] buff1 = new char[1024];
            while ((read1 = in.read(buff1)) != -1) {
                jsonResults1.append(buff1, 0, read1);
            }
        } catch (MalformedURLException e) {
            Log.d("Error processing Places API URL", String.valueOf(e));
            return resultList1;
        } catch (IOException e) {
            Log.d("Error connecting to Places API", String.valueOf(e));
            return resultList1;
        } finally {
            if (conn1 != null) {
//                conn1.disconnect();
            }
        }

        try {

            // Create a JSON object hierarchy from the results
            //Log.e("creation du fichier Json", "creation du fichier Json");
            System.out.println("fabrication du Json Objet");
            JSONObject jsonObj1 = new JSONObject(jsonResults1.toString());
            //JSONArray predsJsonArray = jsonObj.getJSONArray("html_attributions");
            JSONObject result1 = jsonObj1.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
            System.out.println("la chaine Json "+result1);
            Double longitude1  = result1.getDouble("lng");
            Double latitude1 =  result1.getDouble("lat");


            this.lat = String.valueOf(latitude1);
            this.lon = String.valueOf(longitude1);
            this.location = jsonObj1.getJSONObject("result").getString("name");

            System.out.println("longitude et latitude "+ longitude1+latitude1);
            resultList1 = new ArrayList<Double>(result1.length());
            resultList1.add(result1.getDouble("lng"));
            resultList1 .add(result1.getDouble("lat"));
            System.out.println("les latitude dans le table"+resultList1);

        } catch (JSONException e) {
            ///Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList1;
    }

    public static ArrayList autocomplete(String input) {


        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            // sb.append("&components=country:gr");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.d("Error processing Places API URL", String.valueOf(e));
            return resultList;
        } catch (IOException e) {
            Log.d("Error connecting to Places API", String.valueOf(e));
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            resultListPlaceID = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                resultListPlaceID.add(predsJsonArray.getJSONObject(i).getString("place_id"));

            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    public void activeGif(boolean active,String sms)
    {
        // GifImageView GIF =   (GifImageView) findViewById(R.id.gif_auto);
        //  ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if(active)
        {


            loading = new ProgressDialog(this);
            loading.setMessage(sms);
            loading.show();


            loading.setContentView(R.layout.custom_progressdialog);


            TextView text = (TextView) loading.findViewById(R.id.smsDialog);
            text.setText("Presiona para Cancelar!");


            loading.setCancelable(false);

            CardView car = (CardView) loading.findViewById(R.id.car_notifications_from_client);
            car.getBackground().setAlpha(200);

            Button btnCnacel = (Button) loading.findViewById(R.id.car_notifications_cancel_client);
            btnCnacel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {

                        activeGif(false,"");
                        activeGifMotivos(true,"");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }


            });

        }
        else
        {

            if(loading != null)
            {
                loading.dismiss();
            }



            //progressBar.setIndeterminate(false);
            //  GIF.setVisibility(View.INVISIBLE);
            //btnTravelNew.setVisibility(View.VISIBLE);
        }
    }

    public void activeGifMotivos(boolean active,String sms)
    {

        if(active)
        {

            loading = new ProgressDialog(this);
            loading.setMessage(sms);
            loading.show();

            loading.setContentView(R.layout.custom_modal);

            loading.setCancelable(false);

            CardView car = (CardView) loading.findViewById(R.id.car_notifications_from_client_cancelar);
            car.getBackground().setAlpha(200);

            RadioGroup rg = (RadioGroup) loading.findViewById(R.id.radio_group);

            for(int i=0;i<list.size();i++){
                RadioButton rb=new RadioButton(this); // dynamically creating RadioButton and adding to RadioGroup.
                rb.setText(list.get(i).getReason().toString());
                rg.addView(rb);
            }

            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    int childCount = group.getChildCount();
                    for (int x = 0; x < childCount; x++) {
                        RadioButton btn = (RadioButton) group.getChildAt(x);
                        if (btn.getId() == checkedId) {
                            //Log.e("selected RadioButton->",btn.getText().toString());

                            motivo = Integer.valueOf((checkedId));
                            //Toast.makeText(getApplicationContext(), String.valueOf(checkedId) , Toast.LENGTH_LONG).show();

                        }
                    }
                }
            });

            Button btnCnacel = (Button) loading.findViewById(R.id.btn_motivo);
            btnCnacel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {

                        //activeGifMotivos(false,"");
                        if (motivo == 0){
                            Toast.makeText(getApplicationContext(), "Debe seleccionar algun motivo" , Toast.LENGTH_LONG).show();
                        }
                        else{
                            cancelTravelByCliet();
                        }

                        //Toast.makeText(getApplicationContext(), mot.toString(), Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


            });

        }
        else
        {

            if(loading != null)
            {
                loading.dismiss();
            }

        }
    }

    public void activeGifMotivosFalla(boolean active,String sms)
    {

        if(active)
        {

            loading = new ProgressDialog(this);
            loading.setMessage(sms);
            loading.show();
            loading.setContentView(R.layout.custom_modal_reporte);
            loading.setCancelable(false);

            CardView car = (CardView) loading.findViewById(R.id.car_notifications_from_client_cancelar);
            car.getBackground().setAlpha(200);

            final EditText razon = (EditText) loading.findViewById(R.id.et_motivo);
            final EditText descripcion = (EditText) loading.findViewById(R.id.et_descripcion);
            final EditText email = (EditText) loading.findViewById(R.id.et_email);

            Button btnCnacel = (Button) loading.findViewById(R.id.btn_motivo);
            btnCnacel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {

                            ReportarByCliet();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


            });

        }
        else
        {

            if(loading != null)
            {
                loading.dismiss();
            }

        }
    }

    //Esta es la funcion para enviar el reporte de falla MUESTRAME EL ERRO
    public void ReportarByCliet()
    {

        if (this.daoLoguin == null) { this.daoLoguin = HttpConexion.getUri().create(ServicesLoguin.class); }

        try {

            int id = 0;
            String nombre = "Leandro";
            String correo = "Leandro";
            String correo2 = "Leandro";
            String razon = "Leandro";
            String company = "Leandro";
            String mensaje = "Leandro";
            int istravel = 1;

            reporte datos = new reporte(id,nombre,correo,correo2,razon,company,mensaje,istravel);

            /*esto imprime el json quue se va a amandar prueba*/
             GsonBuilder builder = new GsonBuilder();
             Gson gson = builder.create();
             System.out.println(gson.toJson(datos));

            Call<reporte> call = this.daoLoguin.reporteFalla(datos);

            Log.d("fatal", call.request().toString());
            Log.d("fatal", call.request().headers().toString());

            call.enqueue(new Callback<reporte>() {
                @Override
                public void onResponse(Call<reporte> call, Response<reporte> response) {

                        reporte rs = response.body();

                        Toast.makeText(getApplicationContext(), "Datos Enviados", Toast.LENGTH_SHORT).show();

                        loading.dismiss();

                }

                public void onFailure(Call<reporte> call, Throwable t) {
                    AlertDialog alertDialog = new AlertDialog.Builder(HomeClientActivity.this).create();
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

    public void cancelTravelByCliet()
    {

        if (this.daoTravel == null) { this.daoTravel = HttpConexion.getUri().create(ServicesTravel.class); }

        try {

            motivo = motivo - 1;
            Call<Boolean> call = this.daoTravel.cancelByClient(gloval.getGv_id_cliet(), motivo);

            Log.d("fatal", call.request().toString());
            Log.d("fatal", call.request().headers().toString());

            call.enqueue(new Callback<Boolean>() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                    Toast.makeText(getApplicationContext(), "VIAJE CANCELADO!", Toast.LENGTH_LONG).show();

                    // cerramo el dialog //
                    activeGifMotivos(false,"");

                    materialDesignFAM.close(true);

                    LinearLayout contentInfoReervation = (LinearLayout) findViewById(R.id.contentInfoReervation);
                    contentInfoReervation.setVisibility(LinearLayout.INVISIBLE);

                    isReervation =  false;
                    contetRequestTravelVisible(false);

                }

                public void onFailure(Call<Boolean> call, Throwable t) {
                    AlertDialog alertDialog = new AlertDialog.Builder(HomeClientActivity.this).create();
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

    // Mostramos listado de detalles de el CATEGORIAS //
    public  void showModalCategory() {
        try {

            FragmentManager fm = getFragmentManager();
            dialogFragment = new ListTypeCarLayout();
            dialogFragment.show(fm, "Sample Fragment");
            // this.requestTravel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*METODO CUANDO SELECIONAMOS UNA CATEGORYA */
   /* public void savePreferences(int idTipe,String name) throws JSONException {
        Log.d("fatal", String.valueOf(idTipe));
        dialogFragment.dismiss();

        this.mIdTypeVehicle = idTipe;

        EditText txt_cateogi =  (EditText) findViewById(R.id.txtCatVehicleReervation);
        txt_cateogi.setText(name);
        //this.requestTravel();
    }*/

    // Mostramos listado de detalles de el viajes //
    public  void preRequestTravel()
    {
       /* try {

            FragmentManager fm = getFragmentManager();
             dialogFragment = new ListTypeCarLayout ();
            dialogFragment.show(fm, "Sample Fragment");
            // this.requestTravel();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


    // Enviar token al servidor //
    private void enviarTokenAlServidor(String str_token,int idUser) {


        if (this.daoLoguin == null) { this.daoLoguin = HttpConexion.getUri().create(ServicesLoguin.class); }

        try {
            token T = new token();
            T.setToken(new tokenFull(str_token, idUser,gloval.getGv_id_driver(),MainActivity.version));

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();

            Log.d("Response",gson.toJson(T));

            Call<Boolean> call = this.daoLoguin.token(T);

            call.enqueue(new Callback<Boolean>() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {

                }

                public void onFailure(Call<Boolean> call, Throwable t) {


                    Log.d("ERROR",t.getMessage());
                }
            });

        } finally {
            this.daoTravel = null;
        }


    }

    @Override
    public void onBackPressed() {



        LinearLayout contentInfoReervation = (LinearLayout) findViewById(R.id.contentInfoReervation);
        contentInfoReervation.setVisibility(LinearLayout.INVISIBLE);

        contetRequestTravelVisible(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        FragmentManager fm = getFragmentManager();


        if (id == R.id.nav_camera) {
            controlViewTravel();
            fm.beginTransaction().replace(R.id.content_frame_client,new HomeClientFragment()).commit();

        } else if (id == R.id.nav_gallery) {

            HistoryTravelDriver verifi = new HistoryTravelDriver();
            verifi.ver = 1;
            fm.beginTransaction().replace(R.id.content_frame_client,new HistoryTravelDriver()).commit();
            btnFlotingVisible(false);

        } else if (id == R.id.nav_slideshow) {
            btnFlotingVisible(false);

        }else if (id == R.id.nav_pay_form_client) {


            FloatingActionMenu btnTravelNew = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
            btnTravelNew.setVisibility(View.INVISIBLE);
            getFragmentManager().beginTransaction().replace(R.id.content_frame_client,new PaymentFormClient()).commit();
            btnFlotingVisible(false);


        }else if (id == R.id.nav_manage) {

            fm.beginTransaction().replace(R.id.content_frame_client,new NotificationsFrangment()).commit();
            btnFlotingVisible(false);

        } else if (id == R.id.nav_reservations) {
            btnFlotingVisible(false);

        } else if (id == R.id.nav_send) {

            activeGifMotivosFalla(true,"");
            btnFlotingVisible(false);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // CONONTRO BOTON FLOTANTE //
    public  void  btnFlotingVisible(boolean isVisible)
    {
        FloatingActionMenu btnService = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);

        if(!isVisible)
        {
            btnService.setVisibility(View.INVISIBLE);
        }else
        {
            btnService.setVisibility(View.VISIBLE);
        }

    }

    // Recibios notificacion //
    private BroadcastReceiver broadcastReceiverLoadTodays = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {

            currentTravel = gloval.getGv_travel_current();
            controlViewTravel();

        }
    };


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void controlViewTravel()
    {

        cliaerNotificationAndoid();

        if(currentTravel != null)
        {
            activeGif(false,"");

            materialDesignFAM.setVisibility(View.INVISIBLE);


            if(currentTravel.getIdSatatusTravel() == 0
                    || currentTravel.getIdSatatusTravel() == 4
                    || currentTravel.getIdSatatusTravel() == 5
                    || currentTravel.getIdSatatusTravel() == 6
                    || currentTravel.getIdSatatusTravel() == 7)
            {

                if(currentTravel.getIdSatatusTravel() == 4) {
                    Toast.makeText(getApplicationContext(), "Viaje Aceptado, Chofer en camino!", Toast.LENGTH_SHORT).show();
                    //  HomeClientFragment.panelTopIsVisible(true);

                }
                else if(currentTravel.getIdSatatusTravel() == 5) {
                    Toast.makeText(getApplicationContext(), "Viaje En Curso, Feliz Viaje!", Toast.LENGTH_SHORT).show();
                    //HomeClientFragment.panelTopIsVisible(true);

                }
                else if(currentTravel.getIdSatatusTravel() == 6) {
                    HomeClientFragment.txtStatus.setText("SERVICIO ACTIVO");
                    Toast.makeText(getApplicationContext(), "Viaje Finalizado, Gracias por Preferirnos!", Toast.LENGTH_SHORT).show();
                    currentTravel = null;
                    materialDesignFAM.setVisibility(View.VISIBLE);
                    gloval.setGv_travel_current(null);
                    HomeClientFragment.clearInfo();
                    // HomeClientFragment.panelTopIsVisible(false);
                }
                else if(currentTravel.getIdSatatusTravel() == 7) {
                    Toast.makeText(getApplicationContext(), "Viaje Reachazado!", Toast.LENGTH_SHORT).show();
                    HomeClientFragment.txtStatus.setText("Viaje Reachazado por el Chofer!");
                    currentTravel = null;
                    materialDesignFAM.setVisibility(View.VISIBLE);
                    gloval.setGv_travel_current(null);
                    HomeClientFragment.clearInfo();
                    // HomeClientFragment.panelTopIsVisible(false);
                }
                else if(currentTravel.getIdSatatusTravel() == 0) {
                    Toast.makeText(getApplicationContext(), "Viaje Reachazado!, "+currentTravel.getReason(), Toast.LENGTH_LONG).show();
                   // HomeClientFragment.txtStatus.setText(currentTravel.getReason());
                    currentTravel = null;
                    materialDesignFAM.setVisibility(View.VISIBLE);
                    gloval.setGv_travel_current(null);
                    HomeClientFragment.clearInfo();
                    // HomeClientFragment.panelTopIsVisible(false);
                }


                setInfoTravel();

            }

        }else
        {

            materialDesignFAM.setVisibility(View.VISIBLE);
            // setInfoTravel();
        }
    }

    /// LLAMAMOS A EL SERVICIO DE SOLICITAR REMIS //
    public void requestTravel() throws JSONException {

        if (this.daoTravel == null) { this.daoTravel = HttpConexion.getUri().create(ServicesTravel.class); }

        try {

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            Date date = new Date();

            String currentDate = dateFormat.format(date).toString();

            TravelEntity travel = new TravelEntity();




            if(this.location != "" &&
                    this.lat != "" &&
                    this.lon != "")
            {}else {
                this.location = HomeClientFragment.nameLocation;
                this.lat =   String.valueOf( HomeClientFragment.getmLastLocation().getLatitude());
                this.lon =   String.valueOf(HomeClientFragment.getmLastLocation().getLongitude());
            }



            if(!fromTimeEtxt.getText().toString().matches("")  &&  !fromDateEtxt.getText().toString().matches("") )
            {
                this.dateTravel =  fromDateEtxt.getText().toString()+" "+fromTimeEtxt.getText().toString();
            }
            else {
                this.dateTravel =  currentDate;
            }


            boolean isTravelComany = false;
            if(gloval.getGv_id_profile() == 5)
            {
                isTravelComany = true;
            }

            if(HomeClientActivity.ReservationName != null)
            {
                if(HomeClientActivity.ReservationName.length() > 0)
                {
                    Log.d("ORIGEN -3",HomeClientActivity.location.toString());
                    this.location = HomeClientActivity.ReservationName.toString();
                }
            }




            travel.setTravelBody(
                    new TravelBodyEntity(
                            gloval.getGv_id_cliet(),
                            isTravelComany,
                            new OriginEntity(
                                    this.lat,
                                    this.lon,
                                    this.location),
                            new DestinationEntity(
                                    this.latDestination,
                                    this.lonDestination,
                                    this.destination
                            )
                            ,this.dateTravel,idTypeVehicle,true,gloval.getGv_user_id()
                    )
            );


            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            System.out.println(gson.toJson(travel));

            Call<resp> call = this.daoTravel.addTravel(travel);

            Log.d("Call request",call.request().body().toString());
            Log.d("Call request", call.request().toString());
            Log.d("Call request header", call.request().headers().toString());

            call.enqueue(new Callback<resp>() {
                @Override
                public void onResponse(Call<resp> call, Response<resp> response) {


                    Log.d("Response raw header", response.headers().toString());
                    Log.d("Response raw", String.valueOf(response.raw().body()));
                    Log.d("Response code", String.valueOf(response.code()));


                    if (response.code() == 200)
                    {

                        //the response-body is already parseable to your ResponseBody object
                        resp responseBody = (resp) response.body();
                        //you can do whatever with the response body now...
                        String responseBodyString= responseBody.getResponse().toString();



                        if(isReervation)
                        {
                            Toast.makeText(getApplicationContext(), "Reserva Solicitada!", Toast.LENGTH_SHORT).show();
                            activeGif(false,"");
                        }else
                        {
                            Toast.makeText(getApplicationContext(), "Viaje Solicitado!", Toast.LENGTH_SHORT).show();
                            activeGif(true,"Buscando Chofer...");
                        }

                        materialDesignFAM.close(true);

                        LinearLayout contentInfoReervation = (LinearLayout) findViewById(R.id.contentInfoReervation);
                        contentInfoReervation.setVisibility(LinearLayout.INVISIBLE);
                        isReervation =  false;
                        contetRequestTravelVisible(false);


                    }
                    else {

                        AlertDialog alertDialog = new AlertDialog.Builder(HomeClientActivity.this).create();
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
                public void onFailure(Call<resp> call, Throwable t) {
                    AlertDialog alertDialog = new AlertDialog.Builder(HomeClientActivity.this).create();
                    alertDialog.setTitle("ERROR");
                    alertDialog.setMessage(t.getMessage());

                    Log.d("**",t.getMessage());

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


    public  void  contetRequestTravelVisible(boolean visible)
    {
        if(visible)
        {
            RelativeLayout btnTravelNew = (RelativeLayout) findViewById(R.id.contetRequestTravel);
            btnTravelNew.setVisibility(View.VISIBLE);
        }
        else
        {
            RelativeLayout btnTravelNew = (RelativeLayout) findViewById(R.id.contetRequestTravel);
            btnTravelNew.setVisibility(View.INVISIBLE);
        }

    }

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
        }else if (id == R.id.action_notifications) {

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

        try
        {
            FragmentManager fm = getFragmentManager();

            FloatingActionMenu btnTravelNew = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
            btnTravelNew.setVisibility(View.INVISIBLE);
            fm.beginTransaction().replace(R.id.content_frame_client,new ProfileClientFr()).commit();


        }catch (Exception e)
        {
            Log.d("e",e.getMessage());
        }
    }

    // SALIR DE ACTIVITY //
    public void fn_exit() throws IOException {
        // LAMAMOS A EL MAIN ACTIVITY//
        Intent main = new Intent(HomeClientActivity.this, MainActivity.class);
        startActivity(main);

        currentTravel = null;
        gloval.setGv_logeed(false);
        new GlovalVar();

        // enviarTokenAlServidor("",gloval.getGv_user_id());
        ws.coseWebSocket();

        finish();


    }


    // FIRMAAAA //
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode) {

            case PROFILE_DRIVER_ACTIVITY:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
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




    public void cliaerNotificationAndoid()
    {
        NotificationManager notifManager= (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();
    }

    //Leandro funcion para traer data de motivos

    public void serviceAllTravel() {

        this.apiService = HttpConexion.getUri().create(ServicesTravel.class);
        Call<List<reason>> call = this.apiService.obtIdMotivo();

        call.enqueue(new Callback<List<reason>>() {
            @Override
            public void onResponse(Call<List<reason>> call, Response<List<reason>> response) {

                Log.d("Call request", call.request().toString());
                Log.d("Call request header", call.request().headers().toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", String.valueOf(response.raw().body()));
                Log.d("Response code", String.valueOf(response.code()));

                if (response.code() == 200) {

                    list = (List<reason>) response.body();

                    //Toast.makeText(getApplicationContext(), list.get(0).getReason().toString(), Toast.LENGTH_SHORT).show();




                } else if (response.code() == 404) {

                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(HomeClientActivity.this).create();
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
            public void onFailure(Call<List<reason>> call, Throwable t) {
                AlertDialog alertDialog = new AlertDialog.Builder(HomeClientActivity.this).create();
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


    }

    private void refreshContent(){
/*
        rv = (RecyclerView) myView.findViewById(R.id.rv_recycler_view);
        rv.setHasFixedSize(true);
        adapter = new MyAdapter(list.);
        rv.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(new HomeClientActivity());
        rv.setLayoutManager(llm);
*/
    }

    //Notificacioens


    public void fn_gotonotification()
    {

        //btnFlotingVisible(false);
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame_client,new NotificationsFrangment()).commit();
    }

    public void fn_gotoreservation()
    {

        //btnFlotingVisible(false);
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction().replace(R.id.content_frame_client,new ReservationsFrangment()).commit();
    }

    // radio buton list

    private void showRadioButtonDialog() {

        // custom dialog




    }

}
