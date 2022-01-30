package com.pkasemer.zodongofoods;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.pkasemer.zodongofoods.Apis.MovieApi;
import com.pkasemer.zodongofoods.Apis.MovieService;
import com.pkasemer.zodongofoods.Models.FoodDBModel;
import com.pkasemer.zodongofoods.Models.OrderRequest;
import com.pkasemer.zodongofoods.localDatabase.SenseDBHelper;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceOrder extends AppCompatActivity {

    private MovieService movieService;
    private SenseDBHelper db;
    boolean food_db_itemchecker;
    List<FoodDBModel> cartitemlist;

    ProgressBar placeorder_main_progress;
    OrderRequest orderRequest = new OrderRequest();

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

        btnCheckout = findViewById(R.id.btnCheckout);
        placeorder_main_progress = (ProgressBar) findViewById(R.id.placeorder_main_progress);
        placeorder_main_progress.setVisibility(View.GONE);


        orderRequest.setOrderAddress("MK45678901098");
        orderRequest.setCustomerId("zd1246528");
        orderRequest.setTotalAmount("23000");
        orderRequest.setOrderStatus("1");
        orderRequest.setProcessedBy("1");
        orderRequest.setOrderItemList(cartitemlist);

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                placeorder_main_progress.setVisibility(View.VISIBLE);

                postAllCartItems().enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                Log.i("Ress", "onResponse: " + (response.body()));

                        // Got data. Send it to adapter
                        placeorder_main_progress.setVisibility(View.GONE);

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });


    }


    private Call<ResponseBody> postAllCartItems() {
        return movieService.postCartOrder(orderRequest);
    }


}