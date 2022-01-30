package com.pkasemer.zodongofoods;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.pkasemer.zodongofoods.Apis.MovieApi;
import com.pkasemer.zodongofoods.Apis.MovieService;
import com.pkasemer.zodongofoods.Dialogs.ChangeLocation;
import com.pkasemer.zodongofoods.Models.FoodDBModel;
import com.pkasemer.zodongofoods.Models.OrderRequest;
import com.pkasemer.zodongofoods.localDatabase.SenseDBHelper;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceOrder extends AppCompatActivity implements ChangeLocation.NoticeDialogListener {

    private MovieService movieService;
    private SenseDBHelper db;
    boolean food_db_itemchecker;
    List<FoodDBModel> cartitemlist;

    ProgressBar placeorder_main_progress;
    OrderRequest orderRequest = new OrderRequest();
    TextView btn_change_location, grandsubvalue, grandshipvalue,grandtotalvalue;

    Button btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        getSupportActionBar().setTitle("Zodongo Foods"); // set the top title
        String title = actionBar.getTitle().toString(); // get the title
        actionBar.hide();

        movieService = MovieApi.getClient(PlaceOrder.this).create(MovieService.class);
        db = new SenseDBHelper(PlaceOrder.this);
        cartitemlist = db.listTweetsBD();

        btn_change_location = findViewById(R.id.btn_change_location);
        btnCheckout = findViewById(R.id.btnCheckout);
        grandsubvalue = findViewById(R.id.grandsubvalue);
        grandshipvalue = findViewById(R.id.grandshipvalue);
        grandtotalvalue = findViewById(R.id.grandtotalvalue);
        placeorder_main_progress = (ProgressBar) findViewById(R.id.placeorder_main_progress);
        placeorder_main_progress.setVisibility(View.GONE);

        OrderTotalling();


        orderRequest.setOrderAddress("MK45678901098");
        orderRequest.setCustomerId("2");
        orderRequest.setTotalAmount("23000");
        orderRequest.setOrderStatus("1");
        orderRequest.setProcessedBy("1");
        orderRequest.setOrderItemList(cartitemlist);

        btn_change_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeLocation changeLocation = new ChangeLocation();
                changeLocation.show(getSupportFragmentManager(),"change Location");
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                placeorder_main_progress.setVisibility(View.VISIBLE);

                postAllCartItems().enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        Log.i("Ress", "onResponse: " + (response.code()));
                        placeorder_main_progress.setVisibility(View.GONE);

                        if (response.code() == 201) {
                            Log.i("Order Success", "Order Created: ");
                            db.clearCart();

                            Intent i = new Intent(PlaceOrder.this, RootActivity.class);
                            startActivity(i);

                        } else {
                            Log.i("Order Failed", "Order Failed Try Again: ");
                        }

                        // Got data. Send it to adapter

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        placeorder_main_progress.setVisibility(View.GONE);

                        t.printStackTrace();
                        Log.i("Order Failed", "Order Failed Try Again: " + t);
                    }
                });
            }
        });


    }


    private Call<ResponseBody> postAllCartItems() {
        return movieService.postCartOrder(orderRequest);
    }


    public void OrderTotalling() {
        grandsubvalue.setText("" + NumberFormat.getNumberInstance(Locale.US).format(db.sumPriceCartItems()));
        grandshipvalue.setText("2000");
        grandtotalvalue.setText("" + NumberFormat.getNumberInstance(Locale.US).format(db.sumPriceCartItems() + 2000));
    }



    @Override
    public void onDialogPositiveClick(DialogFragment dialog, TextInputEditText inputUserNewLocation) {

        String name = inputUserNewLocation.getText().toString();
        Log.i("dialog", "Positive Method2: " + name);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.i("dialog", "Negative: ");
    }
}