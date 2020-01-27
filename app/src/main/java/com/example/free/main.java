package com.example.free;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

public class main extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    NavigationView navigationView;
    TextView txt_username,txt_useremail;
    View mheaderview;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set username and email in drawer
        navigationView = findViewById(R.id.nav_view);
        mheaderview = navigationView.getHeaderView(0);
        txt_username = mheaderview.findViewById(R.id.txt_username);
        txt_useremail = mheaderview.findViewById(R.id.txt_useremail);
        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        //check status and get navigation drawer value
        if(sharedPreferences.getString("status","").equals("") || sharedPreferences.getString("status","").equals("3"))
        {
            txt_username.setVisibility(View.GONE);
            txt_useremail.setVisibility(View.GONE);
            NavigationView navigationView;
            navigationView = findViewById(R.id.nav_view);
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_logout).setVisible(false);
            menu.findItem(R.id.nav_login).setVisible(true);
        }
        else {
            txt_username.setVisibility(View.VISIBLE);
            txt_useremail.setVisibility(View.VISIBLE);
            txt_username.setText(sharedPreferences.getString("user_name",""));
            txt_useremail.setText(sharedPreferences.getString("user_email",""));
            NavigationView navigationView;
            navigationView = findViewById(R.id.nav_view);
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.nav_logout).setVisible(true);
            menu.findItem(R.id.nav_login).setVisible(false);
        }
        //check status and get navigation drawer value
        //set username and email in drawer

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_category, R.id.nav_country,
                R.id.nav_language, R.id.nav_share, R.id.nav_logout,R.id.nav_epg)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
