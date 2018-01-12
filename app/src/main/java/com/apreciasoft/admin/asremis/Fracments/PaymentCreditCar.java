package com.apreciasoft.admin.asremis.Fracments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.apreciasoft.admin.asremis.Activity.HomeActivity;
import com.apreciasoft.admin.asremis.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mercadopago.constants.Sites;
import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.model.Item;
import com.mercadopago.model.PaymentData;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.util.JsonUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.apreciasoft.admin.asremis.Activity.HomeActivity.round;

/**
 * Created by usario on 1/7/2017.
 */

public class PaymentCreditCar extends AppCompatActivity {



    // Set the supported payment method types
    protected List<String> mSupportedPaymentTypes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_credit_car);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Pago Con Tarjeta");




        mSupportedPaymentTypes =  new ArrayList<String>();
        mSupportedPaymentTypes.add("credit_card");
        mSupportedPaymentTypes.add("debit_card");
        mSupportedPaymentTypes.add("prepaid_card");

        final TextView txt_mount2 = (TextView) findViewById(R.id.txt_mount2);

        txt_mount2.setText("$"+Double.toString(round(HomeActivity.totalFinal,2)));




    }



    private void startMercadoPagoCheckout(CheckoutPreference checkoutPreference) {
        new MercadoPagoCheckout.Builder()
                .setActivity(this)
                .setPublicKey(String.valueOf(HomeActivity.PARAM_69))
                .setCheckoutPreference(checkoutPreference)
                .startForPaymentData();
    }

    // Método ejecutado al hacer clic en el botón
    public void submit(View view) {



        CheckoutPreference checkoutPreference = new CheckoutPreference.Builder()
                .addItem(new Item("PAGO REMIS ASREMIS", new BigDecimal(HomeActivity.totalFinal)))
                .setSite(Sites.ARGENTINA)
                .setMaxInstallments(1) //Limit the amount of installments
                .build();
        startMercadoPagoCheckout(checkoutPreference);




    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {

        if (requestCode == MercadoPagoCheckout.CHECKOUT_REQUEST_CODE) {
            if (resultCode == MercadoPagoCheckout.PAYMENT_DATA_RESULT_CODE) {
                PaymentData paymentData = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentData"), PaymentData.class);
                //Done!
               // if(paymentData.getStatus().contains("approved")){
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    Log.d("paymentData",gson.toJson(paymentData));


                    HomeActivity.mp_jsonPaymentCard = gson.toJson(paymentData);
                    HomeActivity.mp_paymentMethodId = paymentData.getPaymentMethod().getId();
                    HomeActivity.mp_paymentTypeId = paymentData.getPaymentMethod().getPaymentTypeId();
                    HomeActivity.mp_paymentstatus = paymentData.getTransactionAmount().toEngineeringString();
                    HomeActivity._PAYCREDITCAR_OK = true;
                    this.finish();



               // }
            } else if (resultCode == RESULT_CANCELED) {
                if (data != null && data.getStringExtra("mercadoPagoError") != null) {
                    //Resolve error in checkout
                   Log.d("ERROR"," PROCESANDO PAGO");
                } else {
                    //Resolve canceled checkout
                }
            }
        }




    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                HomeActivity._PAYCREDITCAR_OK = false;
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

        Log.d("va","va");
       // finish();
    }


}


