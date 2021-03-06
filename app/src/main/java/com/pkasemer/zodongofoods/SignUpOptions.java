package com.pkasemer.zodongofoods;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pkasemer.zodongofoods.HelperClasses.SharedPrefManager;

public class SignUpOptions extends AppCompatActivity {

    Button login_screen,register_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_options);

        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        getSupportActionBar().setTitle("Sign Up Options Foods"); // set the top title
        String title = actionBar.getTitle().toString(); // get the title
        actionBar.hide();

        //if the user is already logged in we will directly start the profile activity
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, RootActivity.class));
            return;
        }

        login_screen = findViewById(R.id.login_screen);
        register_btn = findViewById(R.id.register_btn);



        login_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpOptions.this, LoginMaterial.class);
                startActivity(intent);
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpOptions.this, RegisterMaterial.class);
                startActivity(intent);
            }
        });

    }
}