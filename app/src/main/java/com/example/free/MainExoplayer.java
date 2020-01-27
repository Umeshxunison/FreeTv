package com.example.free;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.free.ui.epg.Channel_Programm;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainExoplayer extends AppCompatActivity implements ExoPlayer.EventListener{

    private SimpleExoPlayerView simpleExoPlayerView;
    private String hlsVideoUri;
    private SimpleExoPlayer player;
    private ProgressBar progressBar;
    //private ImageView channel_info;
    String channelid;
    ProgramlistAdapter adapter;
    ArrayList<Channel_Programm> Allchannelprogram;
    GridView channel_info;
    RelativeLayout bargridview;
    ImageView showgridview;
    TextView txtchannel_name;
    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_exoplayer);
        channel_info = (GridView) findViewById(R.id.txt_channel_info);
        Allchannelprogram = new ArrayList<>();
        //channel_info = findViewById(R.id.channel_info);
        bargridview = findViewById(R.id.bargridview);
        showgridview = findViewById(R.id.showgridview);
        txtchannel_name = findViewById(R.id.txtchannel_name);

        //internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.CONNECTED && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            Intent intent = new Intent(getApplicationContext(),no_connection.class);
            startActivity(intent);
        }
        //internet connection

        Intent intent = getIntent();
        hlsVideoUri = intent.getStringExtra("m3u8url");
        channelid = intent.getStringExtra("channel_id");
        txtchannel_name.setText(intent.getStringExtra("channel_name"));

        showgridview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(i == 0) {
                    showgridview.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    bargridview.setVisibility(View.VISIBLE);
                    i = 1;
                }
                else {
                    showgridview.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    bargridview.setVisibility(View.GONE);
                    i = 0;
                }
            }
        });

//        channel_info.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(), Channel_Guide.class);
//                i.putExtra("channelid",channelid);
//                startActivity(i);
//            }
//        });

        //call adapter class for bind data
        HashMap<String, String> recordData = new HashMap<String, String>();
        recordData.put("channel_id",channelid);
        ChannelprogramdatafromAPIRequest request = new ChannelprogramdatafromAPIRequest(Api.URL_READ_CHANNEL_PROGRAM, recordData, Api.CODE_POST_REQUEST);
        request.execute();
        //call adapter class for bind data

        // 1. Create a default TrackSelector
        Handler mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();

        // 3. Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);
        simpleExoPlayerView.setPlayer(player);

        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Exo2"), defaultBandwidthMeter);
        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        // This is the MediaSource representing the media to be played.
        HlsMediaSource hlsMediaSource = new HlsMediaSource(Uri.parse(hlsVideoUri), dataSourceFactory, mainHandler, new AdaptiveMediaSourceEventListener() {
            @Override
            public void onLoadStarted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs) {

            }

            @Override
            public void onLoadCompleted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {

            }

            @Override
            public void onLoadCanceled(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {

            }

            @Override
            public void onLoadError(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded, IOException error, boolean wasCanceled) {

            }

            @Override
            public void onUpstreamDiscarded(int trackType, long mediaStartTimeMs, long mediaEndTimeMs) {

            }

            @Override
            public void onDownstreamFormatChanged(int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaTimeMs) {

            }
        });

        player.addListener(this);
        player.prepare(hlsMediaSource);
        simpleExoPlayerView.requestFocus();
        player.setPlayWhenReady(true);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        switch (playbackState) {
            case Player.STATE_BUFFERING:
                //You can use progress dialog to show user that video is preparing or buffering so please wait
                progressBar.setVisibility(View.VISIBLE);
                break;
            case Player.STATE_IDLE:
                //idle state
                break;
            case Player.STATE_READY:
                // dismiss your dialog here because our video is ready to play now
                progressBar.setVisibility(View.GONE);
                break;
            case Player.STATE_ENDED:
                // do your processing after ending of video
                break;
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Could not able to stream video");
        adb.setMessage("It seems that something is going wrong.\nPlease try again.");
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish(); // take out user from this activity. you can skip this
            }
        });
        AlertDialog ad = adb.create();
        ad.show();
    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false); //to pause a video because now our video player is not in focus
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }

    private class Factory implements TrackSelection.Factory {
        public Factory(BandwidthMeter bandwidthMeter) {
        }

        @Override
        public TrackSelection createTrackSelection(TrackGroup group, int... tracks) {
            return null;
        }
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

        public ProgramlistAdapter(Context context,ArrayList<Channel_Programm> allchannellist) {
            super(context, R.layout.channel_program_list, allchannellist);
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
