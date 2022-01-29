package com.pkasemer.zodongofoods;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pkasemer.zodongofoods.Adapters.CartAdapter;
import com.pkasemer.zodongofoods.Apis.MovieApi;
import com.pkasemer.zodongofoods.Apis.MovieService;
import com.pkasemer.zodongofoods.Models.FoodDBModel;
import com.pkasemer.zodongofoods.Models.Pk;
import com.pkasemer.zodongofoods.Models.SelectedCategoryMenuItem;
import com.pkasemer.zodongofoods.Models.SelectedCategoryMenuItemResult;
import com.pkasemer.zodongofoods.localDatabase.SenseDBHelper;

import java.util.List;
import java.util.concurrent.TimeoutException;

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
    
    Button btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        getSupportActionBar().setTitle("Zodongo Foods"); // set the top title
        String title = actionBar.getTitle().toString(); // get the title
        actionBar.hide();

        db = new SenseDBHelper(PlaceOrder.this);
        cartitemlist = db.listTweetsBD();

        btnCheckout = findViewById(R.id.btnCheckout);
        placeorder_main_progress = (ProgressBar)findViewById(R.id.placeorder_main_progress);
        placeorder_main_progress.setVisibility(View.GONE);

        //init service and load data
        movieService = MovieApi.getClient(PlaceOrder.this).create(MovieService.class);
        
        
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cartitemlist.size() > 0) {
                    placeorder_main_progress.setVisibility(View.VISIBLE);
                    loadFirstPage();
                } else {
//                    emptycartwarning();
                }


            }
        });
    }
    
    

    
    private void loadFirstPage() {

        // To ensure list is visible when retry button in error view is clicked
        hideErrorView();

        postPlaceOrderItems().enqueue(new Callback<FoodDBModel>() {
            @Override
            public void onResponse(Call<FoodDBModel> call, Response<FoodDBModel> response) {
                hideErrorView();
                placeorder_main_progress.setVisibility(View.GONE);
                Log.v("SUCCESS", response.body() + " " + cartitemlist);
                
            }

            @Override
            public void onFailure(Call<FoodDBModel> call, Throwable t) {
                t.printStackTrace();
//                showErrorView(t);
                placeorder_main_progress.setVisibility(View.GONE);
                Toast.makeText(PlaceOrder.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Call<FoodDBModel> postPlaceOrderItems(){
        return  movieService.postOrderItems(
                cartitemlist
        );
    }


    /**
     * @param throwable required for {@link #fetchErrorMessage(Throwable)}
     * @return
     */
    private void showErrorView(Throwable throwable) {

//        if (errorLayout.getVisibility() == View.GONE) {
//            errorLayout.setVisibility(View.VISIBLE);
//            progressBar.setVisibility(View.GONE);
//
//            txtError.setText(fetchErrorMessage(throwable));
//        }
    }

    private void showCategoryErrorView() {

//        progressBar.setVisibility(View.GONE);

        AlertDialog.Builder android = new AlertDialog.Builder(PlaceOrder.this);
        android.setTitle("Coming Soon");
        android.setIcon(R.drawable.africanwoman);
        android.setMessage("This Menu Category will be updated with great tastes soon, Stay Alert for Updates.")
                .setCancelable(false)

                .setPositiveButton("Home", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //go to activity
                        Intent intent = new Intent(PlaceOrder.this, RootActivity.class);
                        startActivity(intent);
                    }
                });
        android.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //go to activity
                Intent intent = new Intent(PlaceOrder.this, RootActivity.class);
                startActivity(intent);
            }
        });
        android.create().show();

    }



    /**
     * @param throwable to identify the type of error
     * @return appropriate error message
     */
    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (!isNetworkConnected()) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }

        return errorMsg;
    }

    // Helpers -------------------------------------------------------------------------------------


    private void hideErrorView() {
//        if (errorLayout.getVisibility() == View.VISIBLE) {
//            errorLayout.setVisibility(View.GONE);
//            progressBar.setVisibility(View.VISIBLE);
//        }
    }

    /**
     * Remember to add android.permission.ACCESS_NETWORK_STATE permission.
     *
     * @return
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) PlaceOrder.this.getSystemService(PlaceOrder.this.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}