package com.apreciasoft.admin.asremis.Dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.apreciasoft.admin.asremis.Activity.HomeActivity;
import com.apreciasoft.admin.asremis.Entity.InfoTravelEntity;
import com.apreciasoft.admin.asremis.R;
import com.apreciasoft.admin.asremis.Util.GlovalVar;


/**
 * Created by usario on 26/4/2017.
 */

public class TravelDialog extends DialogFragment {


    public static InfoTravelEntity currentTravel;
    public GlovalVar gloval;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.travel_dialog, container, false);
        //Quitamos barra de titulo de la aplicacion
        getDialog().setTitle("Nuevo Viaje!");
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);

        // variable global //
        gloval = ((GlovalVar)getActivity().getApplicationContext());

        currentTravel = gloval.getGv_travel_current();

        final TextView title1 = (TextView) rootView.findViewById(R.id.txt_title);
        final TextView title2 = (TextView) rootView.findViewById(R.id.txt_title2);
        final TextView title3 = (TextView) rootView.findViewById(R.id.txt_pasajeros);
       // final TextView title4 = (TextView) rootView.findViewById(R.id.txt_title4);
        //final TextView telefono = (TextView) rootView.findViewById(R.id.txt_title1);

        final TextView titleOrigin = (TextView) rootView.findViewById(R.id.txt_origin);
        final TextView titleDestination = (TextView) rootView.findViewById(R.id.txt_detination);

        final TextView txt_monto = (TextView) rootView.findViewById(R.id.txt_monto);
        final TextView txt_distancia = (TextView) rootView.findViewById(R.id.txt_distancia);

      //  final TextView txt_title5 = (TextView) rootView.findViewById(R.id.txt_title5);//is multi origen
     //   final TextView txt_title6 = (TextView) rootView.findViewById(R.id.txt_title6);//is multi destination

        final TextView txt_date_info = (TextView) rootView.findViewById(R.id.txt_date_info);
        final TextView txt_mutiOrignes = (TextView) rootView.findViewById(R.id.txt_mutiOrignes);//is multi origen
        final TextView txt_multiDestinos = (TextView) rootView.findViewById(R.id.txt_multiDestinos);//is multi destination
        final TextView txt_observation = (TextView) rootView.findViewById(R.id.txt_observation);//is multi destination



       // telefono.setText(currentTravel.getPhoneNumber());
        title1.setText(currentTravel.getCodTravel());
        title2.setText(currentTravel.getClient());
        title3.setText(currentTravel.getPasajero());
        titleOrigin.setText(currentTravel.getNameOrigin());
        titleDestination.setText(currentTravel.getNameDestination());

        int param25 = Integer.parseInt(gloval.getGv_param().get(25).getValue());
        if(param25 == 1){
            txt_monto.setText(currentTravel.getAmountCalculate());
        }
        else {
            txt_monto.setText("---");
        }


        txt_distancia.setText(currentTravel.getDistanceLabel());

        txt_date_info.setText(currentTravel.getMdate());
        //txt_title5.setText(currentTravel.getIsTravelMultiOrigin());
       // txt_title6.setText(currentTravel.getIsMultiDestination());

        txt_mutiOrignes.setText(
                       " 1) "+currentTravel.getOriginMultipleDesc1()+" - "+
                       " 2) "+currentTravel.getOriginMultipleDesc2()+" - "+
                       " 3) "+currentTravel.getOriginMultipleDesc3()+" - "+
                       " 4) "+ currentTravel.getOriginMultipleDesc4());

       // txt_mutiOrignes.setMovementMethod(new ScrollingMovementMethod());


        /*if(currentTravel.getMultiDestination() != null)
        {
            txt_destinations.setText(this.getDestinations());
        }*/

       txt_multiDestinos.setText(currentTravel.getMultiDestination());

        txt_observation.setText(currentTravel.getObservationFromDriver());
      //  txt_multiDestinos.setMovementMethod(new ScrollingMovementMethod());

        final Button btnRefut = (Button) rootView.findViewById(R.id.btn_refut);

        btnRefut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // RECHAZAR VIAJE
                ((HomeActivity)getActivity()).cancelTravel(currentTravel.getIdTravel());
            }
        });

        if(HomeActivity.PARAM_66 == 1){
            btnRefut.setEnabled(true);
        }else {
            btnRefut.setEnabled(false);
        }


        final Button btnAcep = (Button) rootView.findViewById(R.id.btn_acept);

        btnAcep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // ACEPTAR VIAJE
                ((HomeActivity)getActivity()).aceptTravel(currentTravel.getIdTravel());
            }
        });




        return rootView;
    }





}
