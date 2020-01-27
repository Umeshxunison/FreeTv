package com.example.free;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.free.ui.epg.Channel_Programm;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainVideoplayer extends AppCompatActivity {

    //WebView mWebview;
    private String hlsVideoUri;
    String s= "https://www.youtube.com/embed/";
    String channelid;
    ProgramlistAdapter adapter;
    ArrayList<Channel_Programm> Allchannelprogram;
    GridView channel_info;
    ImageView open_gridview;
    TextView txtchannel_name;
    RelativeLayout infobar,viewcontent,video_programm;
    int i = 0;
    int j = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_videoplayer);
        channel_info = (GridView) findViewById(R.id.txt_channel_info);
        open_gridview = findViewById(R.id.open_gridview);
        txtchannel_name = findViewById(R.id.txtchannel_name);
        infobar = findViewById(R.id.gridvideingo);
        viewcontent = findViewById(R.id.viewcontent);
        video_programm = findViewById(R.id.video_programm);


        Allchannelprogram = new ArrayList<>();
        Intent intent = getIntent();
        hlsVideoUri = intent.getStringExtra("youtuburl");
        channelid = intent.getStringExtra("channel_id");
        txtchannel_name.setText(intent.getStringExtra("channel_name"));


        //youtub player
        hlsVideoUri = hlsVideoUri.substring(hlsVideoUri.indexOf("=")+1);
        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = hlsVideoUri;
                youTubePlayer.loadVideo(videoId,0f);
            }
        });

        //youtub player

        open_gridview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(i == 0) {
                    open_gridview.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    infobar.setVisibility(View.VISIBLE);
                    i = 1;
                }
                else {
                    open_gridview.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    infobar.setVisibility(View.GONE);
                    i = 0;
                }
            }
        });
        //call adapter class for bind data
        HashMap<String, String> recordData = new HashMap<String, String>();
        recordData.put("channel_id",channelid);
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
                        adapter = new ProgramlistAdapter(getApplicationContext(),Allchannelprogram);
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

        public ProgramlistAdapter(Context context, ArrayList<Channel_Programm> allchannellist) {
            super(context , R.layout.channel_program_list, allchannellist);
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

    //hide and show notification bar
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
    //hide and show notification bar
}
