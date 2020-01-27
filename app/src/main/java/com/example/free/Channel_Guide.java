package com.example.free;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.free.ui.epg.Channel_Programm;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class Channel_Guide extends AppCompatActivity {

    ProgramlistAdapter adapter;
    ArrayList<Channel_Programm> Allchannelprogram;
    GridView channel_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_guide);
        channel_info = (GridView) findViewById(R.id.txt_channel_info);
        Allchannelprogram = new ArrayList<>();

        Intent intent = getIntent();

        //call adapter class for bind data
        HashMap<String, String> recordData = new HashMap<String, String>();
        recordData.put("channel_id",intent.getStringExtra("channelid"));
        ChannelprogramdatafromAPIRequest request = new ChannelprogramdatafromAPIRequest(Api.URL_READ_CHANNEL_PROGRAM, recordData, Api.CODE_POST_REQUEST);
        request.execute();
        //call adapter class for bind data
    }

    //get data from api and store in adapter
    private class ChannelprogramdatafromAPIRequest  extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        ChannelprogramdatafromAPIRequest(String url, HashMap<String, String> params, int requestCode) {
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
            Log.e("Data : ",s);
            try {
                JSONObject object = new JSONObject(s);
                if (object.getBoolean("task")) {
                    JSONArray data = object.getJSONArray("data");
                    for (int i =0 ; i < data.length(); i++) {
                        JSONObject obj = data.getJSONObject(i);
                        Allchannelprogram.add(new Channel_Programm(
                            obj.getInt("guide_id"),
                            obj.getString("title"),
                            obj.getString("dates"),
                            obj.getString("start_time"),
                            obj.getString("end_time")
                        ));
                        adapter = new ProgramlistAdapter(Allchannelprogram);
                        channel_info.setAdapter(adapter);
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
    //get data from api and store in adapter

    //adapter data bind in view
    class ProgramlistAdapter extends ArrayAdapter<Channel_Programm> {
        ArrayList<Channel_Programm> allchannellist;

        public ProgramlistAdapter(ArrayList<Channel_Programm> allchannellist) {
            super(Channel_Guide.this , R.layout.channel_program_list, allchannellist);
            this.allchannellist = allchannellist;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.channel_program_list, null, true);
            TextView txt_channel_program_id = listViewItem.findViewById(R.id.txt_channel_program_id);
            TextView txt_progra_date = listViewItem.findViewById(R.id.txt_progra_date);
            TextView txt_progra_starttime = listViewItem.findViewById(R.id.txt_progra_starttime);
            TextView txt_progra_endtime = listViewItem.findViewById(R.id.txt_progra_endtime);

            final Channel_Programm c = allchannellist.get(position);
            txt_channel_program_id.setText(c.getTitle());
            txt_progra_date.setText(c.getDates());
            txt_progra_starttime.setText(c.getStart_time());
            txt_progra_endtime.setText(c.getEnd_time());
            return listViewItem;
        }
    }
    //adapter data bind in view
}
