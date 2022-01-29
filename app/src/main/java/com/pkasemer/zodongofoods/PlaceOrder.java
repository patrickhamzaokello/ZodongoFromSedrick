package com.pkasemer.zodongofoods;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.pkasemer.zodongofoods.Apis.MovieService;
import com.pkasemer.zodongofoods.HttpRequests.RequestHandler;
import com.pkasemer.zodongofoods.HttpRequests.URLs;
import com.pkasemer.zodongofoods.Models.FoodDBModel;
import com.pkasemer.zodongofoods.localDatabase.SenseDBHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

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
        placeorder_main_progress = (ProgressBar) findViewById(R.id.placeorder_main_progress);
        placeorder_main_progress.setVisibility(View.GONE);

        //init service and load data


        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cartitemlist.size() > 0) {

                    Cursor cursor = db.getUnsyncedMenuItem();
                    if (cursor.moveToFirst()) {
                        do {
                            //calling the method to save the unsynced name to MySQL
                            saveName(
                                    cursor.getInt(cursor.getColumnIndex(SenseDBHelper.COLUMN_id)),
                                    cursor.getString(cursor.getColumnIndex(SenseDBHelper.COLUMN_menuName))
                            );
                        } while (cursor.moveToNext());
                    }


                }


            }
        });
    }


    private void saveName(final int id, final String name) {


        class NamePostRequest extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                placeorder_main_progress.setVisibility(View.VISIBLE);

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if (s.isEmpty()) {
                    return;
                }

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        placeorder_main_progress.setVisibility(View.GONE);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("name", name);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_SENDNAME, params);
            }
        }

        NamePostRequest ul = new NamePostRequest();
        ul.execute();
    }


}