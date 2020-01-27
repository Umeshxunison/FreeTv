package com.example.free;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class registration extends AppCompatActivity {

    Button btnlogin,btnregister;
    EditText edtname,edtemail,edtpassword,edtcontact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        btnlogin = findViewById(R.id.btnlogin);
        edtname = findViewById(R.id.edtname);
        edtemail = findViewById(R.id.edtemail);
        edtpassword = findViewById(R.id.edtpassword);
        edtcontact = findViewById(R.id.edtcontact);
        btnregister = findViewById(R.id.btnregister);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.CONNECTED && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            Intent intent = new Intent(getApplicationContext(),no_connection.class);
            startActivity(intent);
        }

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtname.getText().toString().trim();
                String email = edtemail.getText().toString().trim();
                String password = edtpassword.getText().toString().trim();
                String contact = edtcontact.getText().toString().trim();
                String p = "[a-zA-Z0-9._-]+@[a-z]+\\\\.+[a-z]+";

                if (TextUtils.isEmpty(name)) {
                    edtname.setError("Please Enter Name!!!");
                    edtname.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    edtemail.setError("Please Enter Email");
                    edtemail.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    edtpassword.setError("Please Enter Password");
                    edtpassword.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(contact)) {
                    edtcontact.setError("Please Enter Contact Number");
                    edtcontact.requestFocus();
                    return;
                }
                if (email.matches(p)) {
                    edtemail.setError("Please Enter Validate Email Id");
                    edtemail.requestFocus();
                    return;
                }

                HashMap<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("contact", contact);
                params.put("name", name);
                params.put("password", password);
                PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_RGISTER, params, Api.CODE_POST_REQUEST);
                request.execute();
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
            }
        });
    }

    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        int requestCode;
        String url;
        HashMap<String, String> params;
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.getBoolean("task"))
                {
                    Toast.makeText(getApplicationContext(), "Insert Successfull", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), Login.class);
                    i.putExtra("username",params.getOrDefault("email",""));
                    i.putExtra("password",params.getOrDefault("password",""));
                    startActivity(i);

                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Already Exists Email", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage().toString() , Toast.LENGTH_LONG).show();
            }


        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();
            if (requestCode == Api.CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);
            if(requestCode == Api.CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);
            return null;
        }
    }
}

class RequestHandler{
    public String sendPostRequest(String requestURL, HashMap<String, String> postDataParams) {
        URL url;
        StringBuilder sb = new StringBuilder();

        try {
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                sb = new StringBuilder();
                String response;
                //Reading server response
                while ((response = br.readLine()) != null) {
                    sb.append(response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public String sendGetRequest(String requestURL) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(requestURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String s;
            while ((s = bufferedReader.readLine()) != null) {
                sb.append(s + "\n");
            }
        } catch (Exception e) {
        }
        return sb.toString();
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}