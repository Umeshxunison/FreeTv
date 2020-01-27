package com.example.free.ui.epg;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.free.Api;
import com.example.free.R;
import com.example.free.RequestHandlers;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class EpgFragment extends Fragment {

    RecyclerView channel_time_line,channel_list,channel_program_list;

    ArrayList<String> timelineList;
    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    Channel_timeAdapter timeAdapter;
    LinearLayoutManager HorizontalLayout ;

    ArrayList<Channel_Programm> channel_channel;
    Channel_epgAdapter channel_epgAdapter;

    ArrayList<Channel_Programm> channel_programms;
    Channel_programepgAdapter channel_programepgadapter;

    ArrayList<Channel_Programm> horizantal;
    ArrayList<ArrayList<Channel_Programm>> vertical;

    ArrayList<String> ch_name = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_epg, container, false);

        //Toast.makeText(getContext(),String.valueOf(ctime),Toast.LENGTH_LONG).show();
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        channel_channel = new ArrayList<>();
        channel_programms = new ArrayList<>();

        vertical = new ArrayList<>();
        horizantal = new ArrayList<>();

        channel_time_line = getActivity().findViewById(R.id.channel_time_line);
        channel_list = getActivity().findViewById(R.id.channel_list);
        channel_program_list = getActivity().findViewById(R.id.channel_program_list);

        //set timeline
        timelineList = new ArrayList<>();
        gettimeline();
        RecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        channel_time_line.setLayoutManager(RecyclerViewLayoutManager);
        timeAdapter = new Channel_timeAdapter(getContext(),timelineList);
        HorizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
        channel_time_line.setLayoutManager(HorizontalLayout);
        channel_time_line.setAdapter(timeAdapter);

        HashMap<String, String> recordData = new HashMap<String, String>();
        recordData.put("channel_program_list"," ");
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_CHANNEL_PROGRAM, recordData, Api.CODE_POST_REQUEST);
        request.execute();

    }
    //timebar
    public void gettimeline (){
        timelineList.add("01:00\nAm");timelineList.add("01:30\nAm");
        timelineList.add("02:00\nAm");timelineList.add("02:30\nAm");
        timelineList.add("03:00\nAm");timelineList.add("03:30\nAm");
        timelineList.add("04:00\nAm");timelineList.add("04:30\nAm");
        timelineList.add("05:00\nAm");timelineList.add("05:30\nAm");
        timelineList.add("06:00\nAm");timelineList.add("06:30\nAm");
        timelineList.add("07:00\nAm");timelineList.add("07:30\nAm");
        timelineList.add("08:00\nAm");timelineList.add("08:30\nAm");
        timelineList.add("09:00\nAm");timelineList.add("09:30\nAm");
        timelineList.add("10:00\nAm");timelineList.add("10:30\nAm");
        timelineList.add("11:00\nAm");timelineList.add("11:30\nAm");
        timelineList.add("12:00\nPm");timelineList.add("12:30\nPm");
        timelineList.add("13:00\nPm");timelineList.add("13:30\nPm");
        timelineList.add("14:00\nPm");timelineList.add("14:30\nPm");
        timelineList.add("15:00\nPm");timelineList.add("15:30\nPm");
        timelineList.add("16:00\nPm");timelineList.add("16:30\nPm");
        timelineList.add("17:00\nPm");timelineList.add("17:30\nPm");
        timelineList.add("18:00\nPm");timelineList.add("18:30\nPm");
        timelineList.add("19:00\nPm");timelineList.add("19:30\nPm");
        timelineList.add("20:00\nPm");timelineList.add("20:30\nPm");
        timelineList.add("21:00\nPm");timelineList.add("21:30\nPm");
        timelineList.add("22:00\nPm");timelineList.add("22:30\nPm");
        timelineList.add("23:00\nPm");timelineList.add("23:30\nPm");
        timelineList.add("24:00\nAm");timelineList.add("24:30\nAm");
    }
    class Channel_timeAdapter extends RecyclerView.Adapter<Channel_timeAdapter.Myviewholder>{

        private ArrayList<String> channel_lists;
        private Context context;
        public Channel_timeAdapter(Context context, ArrayList<String> channel_lists) {
            this.context = context;
            this.channel_lists = channel_lists;
        }

        @NonNull
        @Override
        public Channel_timeAdapter.Myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.epg_timeline,parent,false);
            return new Channel_timeAdapter.Myviewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final Channel_timeAdapter.Myviewholder holder, final int position) {
            holder.textView.setText(channel_lists.get(position));

        }

        @Override
        public int getItemCount() {
            return channel_lists.size();
        }

        public class Myviewholder extends RecyclerView.ViewHolder
        {
            TextView textView;
            public Myviewholder(View view)
            {
                super(view);
                textView =  view.findViewById(R.id.text_timeline);
            }
        }
    }
    //timebar

    //channel list
    private class PerformNetworkRequest  extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;
        int a = 0;

        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode) {
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
            //Log.e("data ",s);
            try {
                JSONObject object = new JSONObject(s);
                if (object.getBoolean("task")) {
                    JSONArray data = object.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject obj = data.getJSONObject(i);
                        if (!ch_name.contains(obj.getString("channel_name"))) {
                            ch_name.add(obj.getString("channel_name"));
                            if(i > 0) {
                                vertical.add(horizantal);
                                Log.e(obj.getString("channel_name"), String.valueOf(horizantal.size()));
                                horizantal = new ArrayList<>();
                            }
                            channel_channel.add(new Channel_Programm(
                                    obj.getInt("channel_id"),
                                    obj.getString("channel_name"),
                                    obj.getString("channel_icon")
                            ));
                        }
                        Log.e("chanel name", obj.getString("channel_name")+","+obj.getString("title"));
                        horizantal.add(new Channel_Programm(
                                obj.getInt("channel_id"),
                                obj.getString("title"),
                                obj.getString("dates"),
                                obj.getString("start_time"),
                                obj.getString("end_time")
                        ));
                    }
                    vertical.add(horizantal);
                    Log.e("last", String.valueOf(horizantal.size()));
                    Log.e("vartical", String.valueOf(vertical.size()));

                    RecyclerViewLayoutManager = new LinearLayoutManager(getContext());
                    channel_list.setLayoutManager(RecyclerViewLayoutManager);
                    channel_list.setHasFixedSize(true);
                    channel_epgAdapter = new Channel_epgAdapter(getContext(),channel_channel);
                    channel_list.setAdapter(channel_epgAdapter);

                    RecyclerViewLayoutManager = new LinearLayoutManager(getContext());
                    channel_program_list.setLayoutManager(RecyclerViewLayoutManager);
                    channel_program_list.setHasFixedSize(true);
                    channel_programepgadapter = new Channel_programepgAdapter(getContext(),vertical);
                    channel_program_list.setAdapter(channel_programepgadapter);
                }
            } catch (JSONException e) {
                Toast.makeText(getContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
    class Channel_epgAdapter extends RecyclerView.Adapter<Channel_epgAdapter.Myviewholder>{

        private ArrayList<Channel_Programm> channel_channel;
        private Context context;
        public Channel_epgAdapter(Context context, ArrayList<Channel_Programm> channel_lists) {
            this.context = context;
            this.channel_channel = channel_lists;
        }

        @NonNull
        @Override
        public Channel_epgAdapter.Myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.epg_channel,parent,false);
            return new Channel_epgAdapter.Myviewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final Channel_epgAdapter.Myviewholder holder, final int position) {
            holder.chanel_name.setText(channel_channel.get(position).getChannel_name());
            Picasso.get().load(Api.GET_IMAGE_URL + channel_channel.get(position).getChannel_icon()).into(holder.channel_img);
        }

        @Override
        public int getItemCount() {
            return channel_channel.size();
        }

        public class Myviewholder extends RecyclerView.ViewHolder
        {
            TextView chanel_name;
            ImageView channel_img;
            public Myviewholder(View view)
            {
                super(view);
                chanel_name =  view.findViewById(R.id.chanel_name);
                channel_img = view.findViewById(R.id.channel_img);
            }
        }
    }
    //channel list

    //channel epg list
    class Channel_programepgAdapter extends RecyclerView.Adapter<Channel_programepgAdapter.Myviewholder>{

        private ArrayList<ArrayList<Channel_Programm>> channel_epg;
        private Context context;
        public Channel_programepgAdapter(Context context, ArrayList<ArrayList<Channel_Programm>> channel_epg) {
            this.context = context;
            this.channel_epg = channel_epg;
        }

        @NonNull
        @Override
        public Channel_programepgAdapter.Myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.epg_programlist,parent,false);
            return new Channel_programepgAdapter.Myviewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final Channel_programepgAdapter.Myviewholder holder, final int position) {

            for(int g = 0; g < channel_epg.get(position).size(); g++){
                holder.channel_programepghorizontalAdapter = new Channel_programepghorizontalAdapter(getContext(),channel_epg.get(position));
                holder.RecyclerViewLayoutManager = new LinearLayoutManager(getContext());
                holder.txt_epg_name.setLayoutManager(holder.RecyclerViewLayoutManager);
                holder.txt_epg_name.setHasFixedSize(true);
                holder.HorizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
                holder.txt_epg_name.setLayoutManager(holder.HorizontalLayout);
                holder.txt_epg_name.setAdapter(holder.channel_programepghorizontalAdapter);
                //holder.txt_epg_name.append(channel_epg.get(position).get(g).getTitle()+" Time :"+channel_epg.get(position).get(g).getStart_time()+" To "+channel_epg.get(position).get(g).getEnd_time()+"\n");
            }
            //holder.txt_epg_name.setText(String.valueOf(channel_epg.get(position).size()));
        }

        @Override
        public int getItemCount() {
            return channel_epg.size();
        }

        public class Myviewholder extends RecyclerView.ViewHolder
        {
            Channel_programepghorizontalAdapter channel_programepghorizontalAdapter;
            RecyclerView.LayoutManager RecyclerViewLayoutManager;
            LinearLayoutManager HorizontalLayout ;
            RecyclerView txt_epg_name;
            public Myviewholder(View view)
            {
                super(view);
                txt_epg_name =  view.findViewById(R.id.txt_epg_name);
            }
        }
    }
    //channel epg list

    //channel epg horizantel data list
    class Channel_programepghorizontalAdapter extends RecyclerView.Adapter<Channel_programepghorizontalAdapter.Myviewholder>{
        private Date channel_time = Calendar.getInstance().getTime();
        private DateFormat date = new SimpleDateFormat("HHmm");
        private int ctime = Integer.parseInt(date.format(channel_time));
        private ArrayList<Channel_Programm> channel_epg_data;
        private Context context;

        public Channel_programepghorizontalAdapter(Context context, ArrayList<Channel_Programm> channel_epg) {
            this.context = context;
            this.channel_epg_data = channel_epg;
        }

        @NonNull
        @Override
        public Channel_programepghorizontalAdapter.Myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.epg_data_layout,parent,false);
            return new Channel_programepghorizontalAdapter.Myviewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final Channel_programepghorizontalAdapter.Myviewholder holder, final int position) {
            int stime = Integer.parseInt(channel_epg_data.get(position).getStart_time().replace(":",""));
            int etime = Integer.parseInt(channel_epg_data.get(position).getEnd_time().replace(":",""));

            if(ctime > stime && ctime < etime) {
                holder.txt_epg_name_data.setBackgroundColor(R.color.black);
                holder.txt_epg_name_data.setTextColor(R.color.white);
                holder.txt_epg_name_data.setText(channel_epg_data.get(position).getTitle() + " \n " + channel_epg_data.get(position).getStart_time() + " To " + channel_epg_data.get(position).getEnd_time());
            }
            else {
                holder.txt_epg_name_data.setText(channel_epg_data.get(position).getTitle() + " \n " + channel_epg_data.get(position).getStart_time() + " To " + channel_epg_data.get(position).getEnd_time());
            }
            //holder.txt_epg_name_data.setText(channel_epg_data.get(position).getTitle() + " \n " + channel_epg_data.get(position).getStart_time() + " To " + channel_epg_data.get(position).getEnd_time());
        }

        @Override
        public int getItemCount() {
            return channel_epg_data.size();
        }

        public class Myviewholder extends RecyclerView.ViewHolder
        {

            TextView txt_epg_name_data;
            public Myviewholder(View view)
            {
                super(view);
                txt_epg_name_data =  view.findViewById(R.id.txt_epg_name_data);
            }
        }
    }
    //channel epg horizantel data list
}