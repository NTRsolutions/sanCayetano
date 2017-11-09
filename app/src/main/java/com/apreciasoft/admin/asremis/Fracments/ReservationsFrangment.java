package com.apreciasoft.admin.asremis.Fracments;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.apreciasoft.admin.asremis.Adapter.ReservationsAdapter;
import com.apreciasoft.admin.asremis.Entity.InfoTravelEntity;
import com.apreciasoft.admin.asremis.Http.HttpConexion;
import com.apreciasoft.admin.asremis.R;
import com.apreciasoft.admin.asremis.Services.ServicesTravel;
import com.apreciasoft.admin.asremis.Util.GlovalVar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by JORGE GUTTIERREZ on 25/4/2017.
 */

public class ReservationsFrangment extends Fragment  {


    public static final int INFO_ACTIVITY = 1;
    public static final int RESULT_OK = 2;
    ServicesTravel apiService = null;
    View myView;
    ReservationsAdapter adapter = null;
    RecyclerView rv = null;
    List<InfoTravelEntity> list;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.apiService = HttpConexion.getUri().create(ServicesTravel.class);

        serviceAllNotification();

        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.notification_reservations_driver, container, false);
        return myView;
    }


    private  void refreshContent(){

        rv = (RecyclerView) myView.findViewById(R.id.rv_recycler_view);
        rv.setHasFixedSize(true);

        adapter = new ReservationsAdapter(list, ReservationsFrangment.this,myView.getContext(),
                new ReservationsAdapter.OnItemClickListener() {
            @Override public void onItemClick(InfoTravelEntity item) {
              //  Toast.makeText(myView.getContext(), "Item Clicked", Toast.LENGTH_LONG).show();
                Intent intent = new Intent( myView.getContext(), InfoDetailTravelAc.class);
                intent.putExtra("TRAVEL",item);
                startActivityForResult(intent, INFO_ACTIVITY);

            }
        });
        rv.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

    }

    public void serviceAllNotification() {


        final GlovalVar gloval = ((GlovalVar)getActivity().getApplicationContext());
        Call<List<InfoTravelEntity>> call = this.apiService.getReservations(gloval.getGv_id_driver());

        // Log.d("***",call.request().body().toString());

        call.enqueue(new Callback<List<InfoTravelEntity>>() {
            @Override
            public void onResponse(Call<List<InfoTravelEntity>> call, Response<List<InfoTravelEntity>> response) {

                Log.d("Call request", call.request().toString());
                Log.d("Call request header", call.request().headers().toString());
                Log.d("Response raw header", response.headers().toString());
                Log.d("Response raw", String.valueOf(response.raw().body()));
                Log.d("Response code", String.valueOf(response.code()));


                if (response.code() == 200) {

                    //the response-body is already parseable to your ResponseBody object
                    list = (List<InfoTravelEntity>) response.body();
                    gloval.setGv_lisReservations(list);

                    refreshContent();

                    //
                } else if (response.code() == 404) {

                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
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
            public void onFailure(Call<List<InfoTravelEntity>> call, Throwable t) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

               this.serviceAllNotification();

    }



}
