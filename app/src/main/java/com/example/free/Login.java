package com.example.free;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

public class Login extends AppCompatActivity {
    Button btnlogin,btn_skip,btn_signup;
    EditText edtemail,edtpassword;
    CheckBox chk_rememberme;
    SharedPreferences sharedpreferences;
    Boolean savelogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnlogin = findViewById(R.id.btnlogin);
        btn_skip = findViewById(R.id.btn_skip);
        btn_signup = findViewById(R.id.btn_signup);
        edtemail = findViewById(R.id.edtemail);
        edtpassword = findViewById(R.id.edtpassword);
        chk_rememberme = findViewById(R.id.chk_rememberme);

        //internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.CONNECTED && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED) {
            Intent intent = new Intent(getApplicationContext(),no_connection.class);
            startActivity(intent);
        }
        //internet connection

        Intent i = getIntent();
        edtemail.setText(i.getStringExtra("username"));
        edtpassword.setText(i.getStringExtra("password"));

        //check data in sharedpreference
        sharedpreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        if(!sharedpreferences.getString("user_password", "").equals("") && !sharedpreferences.getString("user_email", "").equals("") )
        {
            edtemail.setText(sharedpreferences.getString("user_email", ""));
            edtpassword.setText(sharedpreferences.getString("user_password", ""));
            chk_rememberme.setChecked(true);
        }
        //check data in sharedpreference

        //sign up button click
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this,registration.class);
                startActivity(i);
            }
        });
        //sign up button click

        //login button click
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chk_rememberme.isChecked())
                    savelogin = true;
                else
                    savelogin = false;

                //validation check
                String email = edtemail.getText().toString().trim();
                String password = edtpassword.getText().toString().trim();
                String p = "[a-zA-Z0-9._-]+@[a-z]+\\\\.+[a-z]+";
                if (TextUtils.isEmpty(email)) {
                    edtemail.setError("Please Enter Email Id");
                    edtemail.requestFocus();
                    return;
                }
                if (email.matches(p)) {
                    edtemail.setError("Please Enter Validate Email Id");
                    edtemail.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    edtpassword.setError("Please Enter Password");
                    edtpassword.requestFocus();
                    return;
                }
                //validation check

                //call adapter class for bind data
                HashMap<String, String> params = new HashMap<>();
                params.put("username", email);
                params.put("password", password);
                LogindatafromAPIRequest request = new LogindatafromAPIRequest(Api.URL_CREATE_LOGIN, params, Api.CODE_POST_REQUEST);
                request.execute();
                //call adapter class for bind data
            }
        });
        //login button click

        //skip button click
        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("status","3");
                editor.commit();
                Intent i = new Intent(Login.this,main.class);
                startActivity(i);
            }
        });
        //skip button click
    }

    //get data from api and store in adapter and adapter bind in list view
    private class LogindatafromAPIRequest extends AsyncTask<Void, Void, String> {
        int requestCode;
        String url;
        HashMap<String, String> params;
        LogindatafromAPIRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.getBoolean("task"))
                {
                    JSONArray data = jsonObject.getJSONArray("data");
                    JSONObject obj = data.getJSONObject(0);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    Intent i = new Intent(getApplicationContext(), main.class);
                    if(savelogin)
                    {
                        editor.putString("user_id", obj.getString("user_id"));
                        editor.putString("user_name",obj.getString("name") );
                        editor.putString("user_email", edtemail.getText().toString());
                        editor.putString("user_password", edtpassword.getText().toString());
                        editor.putString("status","1");
                        editor.commit();
                    }
                    else {
                        editor.putString("user_name",obj.getString("name") );
                        editor.putString("user_email", edtemail.getText().toString());
                        editor.putString("status","0");
                        editor.commit();
                    }
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Email & Password Not Found", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(),e.getMessage().toString() , Toast.LENGTH_LONG).show();
            }
        }
        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();
            if (requestCode == Api.CODE_POST_REQUEST) {
                return requestHandler.sendPostRequest(url, params);
            }
            if(requestCode == Api.CODE_GET_REQUEST) {
                return requestHandler.sendGetRequest(url);
            }
            return null;
        }
    }
    //get data from api and store in adapter and adapter bind in list view
}









