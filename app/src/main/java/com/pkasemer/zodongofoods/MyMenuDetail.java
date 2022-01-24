package com.pkasemer.zodongofoods;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.pkasemer.zodongofoods.Models.SectionedMenuItem;
import com.pkasemer.zodongofoods.Models.SelectedCategoryMenuItemResult;

public class MyMenuDetail extends AppCompatActivity {

    private int modeltype;
    SectionedMenuItem sectionedMenuItem;
    SelectedCategoryMenuItemResult selectedCategoryMenuItemResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_menu_detail);

        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        actionBar.setTitle("Menu Detail");

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.receiveData();

    }

    private void receiveData()
    {
        //RECEIVE DATA VIA INTENT
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            System.out.println("category_selected_key "+bundle.getParcelable("sectionedMenuItem"));
            System.out.println("modeltype"+ bundle.getString("modeltype"));
            modeltype = Integer.parseInt(bundle.getString("modeltype"));
            if(modeltype == 0){
                sectionedMenuItem = bundle.getParcelable("sectionedMenuItem"); // Key
            } else {
                selectedCategoryMenuItemResult = bundle.getParcelable("sectionedMenuItem");
            }

        }


    }
}