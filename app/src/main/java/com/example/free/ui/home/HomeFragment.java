package com.example.free.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.Transliterator;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.free.Allchannel;
import com.example.free.Api;
import com.example.free.Image_Slider;
import com.example.free.Languagewise_Channel_list;
import com.example.free.MainExoplayer;
import com.example.free.MainVideoplayer;
import com.example.free.R;
import com.example.free.RequestHandlers;
import com.example.free.no_connection;
import com.example.free.ui.country.CountryFragment;
import com.example.free.ui.language.Language;
import com.example.free.ui.language.LanguageFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    RecyclerView listView;
    public  List<Allchannel> channel_lists;
    ImageView img_view_new_channel;
    TextView txt_new_channel_name;

    RecyclerView.LayoutManager RecyclerViewLayoutManager;
    RecyclerAdapterHomeChannnel recyclerAdapterHomeChannnel;
    LinearLayoutManager HorizontalLayout ;

    public static ViewPager img_viewpager;
    public static int currentPage = 0;
    public static int NUM_PAGES = 0;
    public ArrayList<Image_Slider> imageModelArrayList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        img_viewpager = root.findViewById(R.id.img_viewpager);
        imageModelArrayList = new ArrayList<>();
        //videoModelArrayList = new ArrayList<>();

        listView =  root.findViewById(R.id.listnewchannels);
        img_view_new_channel = root.findViewById(R.id.img_view_new_channel);
        txt_new_channel_name = root.findViewById(R.id.txt_new_channel_name);

        channel_lists = new ArrayList<>();
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.CONNECTED && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            Intent intent = new Intent(getContext(), no_connection.class);
            startActivity(intent);
        }
        return root;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PerformNetworkRequest_image_slider request_image = new PerformNetworkRequest_image_slider(Api.URL_READ_IMAGE_SLIDER, null, Api.CODE_GET_REQUEST);
        request_image.execute();

        PerformNetworkRequestchannel request1 = new PerformNetworkRequestchannel(Api.URL_READ_NEW_CHANNEL, null, Api.CODE_GET_REQUEST);
        request1.execute();

    }

    private class PerformNetworkRequest_image_slider  extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkRequest_image_slider(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
            //Toast.makeText(getActivity(), url+"\n"+params+"\n"+requestCode, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Toast.makeText(getActivity(), "s : "+s, Toast.LENGTH_SHORT).show();
            try {
                JSONObject object = new JSONObject(s);
                if (object.getBoolean("task")) {
                    JSONArray data = object.getJSONArray("data");
                    for (int i =0 ; i < data.length(); i++) {
                        JSONObject obj = data.getJSONObject(i);

                        String image = obj.getString("image");
                        //image = image.substring(0,image.length()-1);
                        String[] split_image = image.split(",");
                        //Toast.makeText(getActivity(), "s : "+"\n"+split_image[0]+"\n"+split_image[1]+"\n"+split_image[2], Toast.LENGTH_LONG).show();
                        for(int j=0 ; j < split_image.length ; j++)
                        {
                            Image_Slider image_slider = new Image_Slider();
                            image_slider.setSlider_id(obj.getInt("slider_id"));
                            image_slider.setSlider_title(obj.getString("title"));
                            image_slider.setSlider_image(split_image[j]);
                            image_slider.setSlider_second(obj.getString("second"));
                            imageModelArrayList.add(image_slider);
                        }
                    }
                }
                img_viewpager.setAdapter(new SlidingImage_Adapter(HomeFragment.this.getActivity(),imageModelArrayList));
                NUM_PAGES =imageModelArrayList.size();
                final Handler handler = new Handler();
                final Runnable Update = new Runnable() {
                    public void run() {
                        if (currentPage == NUM_PAGES) {
                            currentPage = 0;
                        }
                        img_viewpager.setCurrentItem(currentPage++, true);
                    }
                };
                Timer swipeTimer = new Timer();
                swipeTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(Update);
                    }
                }, 4000, 4000);

            } catch (JSONException e) {
                Toast.makeText(getActivity(), "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private class PerformNetworkRequestchannel  extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkRequestchannel(String url, HashMap<String, String> params, int requestCode) {
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
            //Toast.makeText(getActivity(), "s : "+s, Toast.LENGTH_SHORT).show();
            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if (object.getBoolean("task")) {

                    JSONArray data = object.getJSONArray("data");
                    for (int i =0 ; i < data.length(); i++) {
                        JSONObject obj = data.getJSONObject(i);
                        channel_lists.add(new Allchannel(
                                obj.getInt("channel_id"),
                                obj.getString("name"),
                                obj.getString("icon"),
                                obj.getString("streamurl")
                        ));
                    }


                    RecyclerViewLayoutManager = new LinearLayoutManager(getActivity());
                    listView.setLayoutManager(RecyclerViewLayoutManager);
                    recyclerAdapterHomeChannnel = new RecyclerAdapterHomeChannnel(HomeFragment.this.getActivity(),channel_lists);
                    HorizontalLayout = new LinearLayoutManager(HomeFragment.this.getActivity(),LinearLayoutManager.HORIZONTAL,false);
                    listView.setLayoutManager(HorizontalLayout);
                    listView.setAdapter(recyclerAdapterHomeChannnel);
                }
            } catch (JSONException e) {
                Toast.makeText(getActivity(), "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
}

class SlidingImage_Adapter extends PagerAdapter {


    private ArrayList<Image_Slider> imageModelArrayList;
    private LayoutInflater inflater;
    private Context context;

    private final String KEY_GLOBAL_PREFS = "global";
    public SlidingImage_Adapter(Context context, ArrayList<Image_Slider> imageModelArrayList) {
        this.context = context;
        this.imageModelArrayList = imageModelArrayList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return imageModelArrayList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.image_slider_list, view, false);
//        VideoView videoView = imageLayout.findViewById(R.id.video_slider);
//        videoView.setVisibility(View.GONE);
        assert imageLayout != null;

        TextView txt_slider_title = imageLayout.findViewById(R.id.txt_slider_title);
        txt_slider_title.setText(imageModelArrayList.get(position).getSlider_title());
        ImageView imageView = imageLayout.findViewById(R.id.img_slider);
        Picasso.get().load(Api.GET_SLIDER_IMAGE_URL + imageModelArrayList.get(position).getSlider_image()).into(imageView);
        view.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}

class RecyclerAdapterHomeChannnel extends RecyclerView.Adapter<RecyclerAdapterHomeChannnel.Myviewholder>{

    private Context context;
    public RecyclerAdapterHomeChannnel(Context context, List<Allchannel> channel_lists) {
        this.context = context;
        this.channel_lists = channel_lists;
    }
    private List<Allchannel> channel_lists;

    @NonNull
    @Override
    public RecyclerAdapterHomeChannnel.Myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.new_channel_list,parent,false);
        return new RecyclerAdapterHomeChannnel.Myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapterHomeChannnel.Myviewholder holder, final int position) {
        holder.textView.setText(channel_lists.get(position).getChannel_name());
        Picasso.get().load(Api.GET_IMAGE_URL + channel_lists.get(position).getChannel_icon()).into(holder.img_view_new_channel);
        holder.new_channel_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(channel_lists.get(position).getChannel_stream().indexOf(".m3u8") != -1) {
                    Intent intent = new Intent(context, MainExoplayer.class);
                    intent.putExtra("m3u8url", channel_lists.get(position).getChannel_stream());
                    intent.putExtra("channel_id", String.valueOf(channel_lists.get(position).getId()));
                    context.startActivity(intent);
                    //Toast.makeText(getApplicationContext(),AllchannelList.get(i).getChannel_stream()+"\n"+AllchannelList.get(i).getId(),Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(context, MainVideoplayer.class);
                    intent.putExtra("youtuburl",channel_lists.get(position).getChannel_stream());
                    intent.putExtra("channel_id", String.valueOf(channel_lists.get(position).getId()));
                    context.startActivity(intent);
                    //Toast.makeText(getApplicationContext(),"invalid",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return channel_lists.size();
    }

    public static class Myviewholder extends RecyclerView.ViewHolder
    {
        TextView textView;
        CardView new_channel_linearlayout;
        ImageView img_view_new_channel;
        public Myviewholder(View view)
        {
            super(view);
            textView =  view.findViewById(R.id.txt_new_channel_name);
            new_channel_linearlayout =  view.findViewById(R.id.new_channel_linearlayout);
            img_view_new_channel = view.findViewById(R.id.img_view_new_channel);
        }
    }
}