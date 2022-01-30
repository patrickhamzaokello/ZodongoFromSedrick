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
import com.pkasemer.zodongofoods.Dialogs.ChangePaymentMethod;
import com.pkasemer.zodongofoods.Dialogs.OrderConfirmationDialog;
import com.pkasemer.zodongofoods.HelperClasses.SharedPrefManager;
import com.pkasemer.zodongofoods.Models.FoodDBModel;
import com.pkasemer.zodongofoods.Models.OrderRequest;
import com.pkasemer.zodongofoods.Models.UserModel;
import com.pkasemer.zodongofoods.localDatabase.SenseDBHelper;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceOrder extends AppCompatActivity implements ChangeLocation.NoticeDialogListener, OrderConfirmationDialog.OrderConfirmLister {

    private MovieService movieService;
    private SenseDBHelper db;
    boolean food_db_itemchecker;
    List<FoodDBModel> cartitemlist;

    ProgressBar placeorder_main_progress;
    OrderRequest orderRequest = new OrderRequest();
    TextView btn_change_location, moneyChangeButton, grandsubvalue, grandshipvalue, grandtotalvalue, location_address_view, order_page_fullname, order_page_phoneno, order_page_username;

    Button btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        if (!SharedPrefManager.getInstance(PlaceOrder.this).isLoggedIn()) {
//            finish();
            startActivity(new Intent(PlaceOrder.this, LoginMaterial.class));
        }

        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        getSupportActionBar().setTitle("Zodongo Foods"); // set the top title
        String title = actionBar.getTitle().toString(); // get the title
        actionBar.hide();

        movieService = MovieApi.getClient(PlaceOrder.this).create(MovieService.class);
        db = new SenseDBHelper(PlaceOrder.this);
        cartitemlist = db.listTweetsBD();

        btn_change_location = findViewById(R.id.btn_change_location);
        moneyChangeButton = findViewById(R.id.moneyChangeButton);
        btnCheckout = findViewById(R.id.btnCheckout);
        grandsubvalue = findViewById(R.id.grandsubvalue);
        grandshipvalue = findViewById(R.id.grandshipvalue);
        grandtotalvalue = findViewById(R.id.grandtotalvalue);
        location_address_view = findViewById(R.id.location_address_view);

        order_page_fullname = findViewById(R.id.order_page_fullname);
        order_page_phoneno = findViewById(R.id.order_page_phoneno);
        order_page_username = findViewById(R.id.order_page_username);

        placeorder_main_progress = (ProgressBar) findViewById(R.id.placeorder_main_progress);
        placeorder_main_progress.setVisibility(View.GONE);

        OrderTotalling();

        UserModel userModel = SharedPrefManager.getInstance(PlaceOrder.this).getUser();

        order_page_fullname.setText(userModel.getFullname());
        order_page_username.setText(userModel.getUsername());
        order_page_phoneno.setText(userModel.getPhone());
        location_address_view.setText(userModel.getAddress() + " - " + userModel.getPhone());


        orderRequest.setOrderAddress(userModel.getAddress() + " - " + userModel.getPhone());
        orderRequest.setCustomerId(String.valueOf(userModel.getId()));
        orderRequest.setTotalAmount(String.valueOf(db.sumPriceCartItems()));
        orderRequest.setOrderStatus("1");
        orderRequest.setProcessedBy("1");
        orderRequest.setOrderItemList(cartitemlist);

        btn_change_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeLocation changeLocation = new ChangeLocation();
                changeLocation.show(getSupportFragmentManager(), "change Location");
            }
        });

        moneyChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangePaymentMethod changePaymentMethod = new ChangePaymentMethod();
                changePaymentMethod.show(getSupportFragmentManager(), "change payment method");
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

                        if (response.code() == 200) {
                            Log.i("Order Success", "Order Created: ");
                            db.clearCart();
                            updatecartCount();
                            OrderTotalling();

                            OrderConfirmationDialog orderConfirmationDialog = new OrderConfirmationDialog();
                            orderConfirmationDialog.show(getSupportFragmentManager(), "Order Confirmation Dialog");


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

    private void updatecartCount() {
        String mycartcount = String.valueOf(db.countCart());
        Intent intent = new Intent(getApplicationContext().getResources().getString(R.string.cartcoutAction));
        intent.putExtra(getApplicationContext().getResources().getString(R.string.cartCount), mycartcount);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void updatelocationView(String location) {
        location_address_view.setText(location);
        orderRequest.setOrderAddress(location);
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog, TextInputEditText inputUserNewLocation, TextInputEditText inputUserNewPhone) {

        String location_name = inputUserNewLocation.getText().toString();
        String phone = inputUserNewPhone.getText().toString();

        Log.i("dialog", "Positive Method2: " + location_name + " - " + phone);

        order_page_phoneno.setText(phone);
        updatelocationView(location_name + "-" + phone);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.i("dialog", "Negative: ");
    }

    @Override
    public void onOrderDialogPositiveClick(DialogFragment dialog) {
        Intent i = new Intent(PlaceOrder.this, RootActivity.class);
        startActivity(i);
    }
}