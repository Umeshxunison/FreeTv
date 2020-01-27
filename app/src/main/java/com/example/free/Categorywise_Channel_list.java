package com.example.free;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class Categorywise_Channel_list extends AppCompatActivity {

    ChannellistAdapter adapter;
    ArrayList<Allchannel> AllchannelList;
    ListView listView;
    SearchView searchdata;
    String channel_id = null;
    String page_name = "Channel List";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorywise__channel_list);
        searchdata = findViewById(R.id.txt_search_category);
        listView = findViewById(R.id.listView_categorywise_Channel);
        AllchannelList = new ArrayList<>();

        //ads
        AdView adView = (AdView) findViewById(R.id.adView3);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequest);
        //ads

        Bundle bundle = getIntent().getExtras();
        channel_id = bundle.getString("position");
        page_name = bundle.getString("category_name");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(page_name);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.CONNECTED && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED) {
            Intent intent = new Intent(getApplicationContext(),no_connection.class);
            startActivity(intent);
        }
        //internet connection

        //call adapter class for bind data
        HashMap<String, String> recordData = new HashMap<String, String>();
        recordData.put("select_category","");
        recordData.put("id", channel_id);
        CategoryChanneldatafromAPIRequest request = new CategoryChanneldatafromAPIRequest(Api.URL_READ_CATEGORY_WISE_CHANNEL, recordData, Api.CODE_POST_REQUEST);
        request.execute();
        //call adapter class for bind data

        //search channel
        searchdata.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(adapter != null) {
                    adapter.getFilter().filter(s);
                }
                return false;
            }
        });
        //search channel

        //play video (channel click listener)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(AllchannelList.get(i).getChannel_stream().indexOf(".m3u8") != -1) {
                    Intent intent = new Intent(getApplicationContext(), MainExoplayer.class);
                    intent.putExtra("m3u8url", AllchannelList.get(i).getChannel_stream());
                    intent.putExtra("channel_id", String.valueOf(AllchannelList.get(i).getId()));
                    intent.putExtra("channel_name", String.valueOf(AllchannelList.get(i).getChannel_name()));
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), MainVideoplayer.class);
                    intent.putExtra("youtuburl", AllchannelList.get(i).getChannel_stream());
                    intent.putExtra("channel_id", String.valueOf(AllchannelList.get(i).getId()));
                    intent.putExtra("channel_name", String.valueOf(AllchannelList.get(i).getChannel_name()));
                    startActivity(intent);
                }
            }
        });
        //play video (channel click listener)
    }

    //diaplay back button in action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //diaplay back button in action bar

    //get data from api and store in adapter and adapter bind in list view
    private class CategoryChanneldatafromAPIRequest  extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        CategoryChanneldatafromAPIRequest(String url, HashMap<String, String> params, int requestCode) {
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
                JSONObject object = new JSONObject(s);
                if (object.getBoolean("task")) {
                    JSONArray data = object.getJSONArray("data");
                    for (int i =0 ; i < data.length(); i++) {
                        JSONObject obj = data.getJSONObject(i);
                        AllchannelList.add(new Allchannel(
                                obj.getInt("channel_id"),
                                obj.getString("name"),
                                obj.getString("icon"),
                                obj.getString("streamurl")
                        ));
                        adapter = new ChannellistAdapter(getApplicationContext(),AllchannelList);
                        listView.setAdapter(adapter);
                    }
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandlers requestHandler = new RequestHandlers();
            if (requestCode == Api.CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);
            if (requestCode == Api.CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);
            return null;
        }
    }
    //get data from api and store in adapter and adapter bind in list view
}
