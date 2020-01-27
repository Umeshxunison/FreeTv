
package com.example.free.ui.country;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.free.Api;
import com.example.free.Categorywise_Channel_list;
import com.example.free.Countrywise_Channel_list;
import com.example.free.R;
import com.example.free.RequestHandlers;
import com.example.free.no_connection;
import com.example.free.ui.category.Category;
import com.example.free.ui.category.CategoryFragment;
import com.example.free.ui.country.RecyclerAdapterCountry;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CountryFragment extends Fragment {

    RecyclerView listView;
    List<Country> countryList;
    TextView textView;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_country, container, false);
        listView =  root.findViewById(R.id.listViewCountry);
        textView =  root.findViewById(R.id.txt_country);
        countryList = new ArrayList<>();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        buildList();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id =  item.getItemId();
        if(id == R.id.txt_channel_name);
        {
            Intent i = new Intent(CountryFragment.this.getActivity(), Countrywise_Channel_list.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
    private void parseJson(String jsonstr)
    {
        try{
            JSONArray resultArray = new JSONArray(jsonstr);
            ArrayList<HashMap<String, String>> list_data = new ArrayList<HashMap<String, String>>();
            for(int i=0; i<resultArray.length(); i++)
            {
                JSONObject recordObj = resultArray.getJSONObject(i);

                int channel_id = recordObj.getInt("channel_id");
                String channel_name = recordObj.getString("name");
                String icon = recordObj.getString("icon");

                HashMap<String, String> recordData = new HashMap<String, String>();
                recordData.put("name", channel_name);
                recordData.put("icon", icon);
                list_data.add(recordData);
            }
            String[] from = new String[]{"name", "icon"};
            int[] to = new int[]{android.R.id.text1, android.R.id.text2};
            SimpleAdapter adapter = new SimpleAdapter(CountryFragment.this.getActivity(), list_data, android.R.layout.simple_list_item_2, from, to);
        }
        catch (Exception e)
        { Toast.makeText(CountryFragment.this.getActivity(), "JSON Format Error"+e, Toast.LENGTH_LONG).show(); }
    }

    private void buildList()
    {
        getPostTask task = new getPostTask();
        task.execute();
    }
    private class getPostTask extends AsyncTask<Void, Void, String>
    {
        String url;
        HashMap<String, String> params;
        int requestCode;
        getPostTask() {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s!=null){
                parseJson(s);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.CONNECTED && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            Intent intent = new Intent(getContext(), no_connection.class);
            startActivity(intent);
        }
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_COUNTRY, null, Api.CODE_GET_REQUEST);
        request.execute();
    }

    private class PerformNetworkRequest  extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

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
            try {
                JSONObject object = new JSONObject(s);
                if (object.getBoolean("task")) {

                    JSONArray data = object.getJSONArray("data");
                    for (int i =0 ; i < data.length(); i++) {
                        JSONObject obj = data.getJSONObject(i);
                        countryList.add(new Country(
                                obj.getInt("country_id"),
                                obj.getString("country_name")
                        ));
                    }
                    RecyclerAdapterCountry recyclerAdapter = new RecyclerAdapterCountry(CountryFragment.this.getActivity(),countryList);
                    DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
                    float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
                    float columnWidthDp = 180;
                    int noOfColumns = (int) (screenWidthDp / columnWidthDp + 0.5);
                    if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                        listView.setLayoutManager(new GridLayoutManager(CountryFragment.this.getActivity(),noOfColumns));
                    }
                    else{
                        listView.setLayoutManager(new GridLayoutManager(CountryFragment.this.getActivity(),noOfColumns));
                    }
                    listView.setAdapter(recyclerAdapter);
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
class RecyclerAdapterCountry extends RecyclerView.Adapter<RecyclerAdapterCountry.Myviewholder>{

    private Context context;
    public RecyclerAdapterCountry(Context context, List<Country> categories) {
        this.context = context;
        this.categories = categories;
    }
    private List<Country> categories;

    @NonNull
    @Override
    public RecyclerAdapterCountry.Myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.country_list,parent,false);
        return new RecyclerAdapterCountry.Myviewholder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull final RecyclerAdapterCountry.Myviewholder holder, final int position) {
        holder.textView.setText(categories.get(position).getCountry_name());
        holder.country_linearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Countrywise_Channel_list.class);
                Bundle bundle = new Bundle();
                bundle.putString("position", String.valueOf(categories.get(position).getCountry_id()));
                bundle.putString("country_name", String.valueOf(categories.get(position).getCountry_name()));
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class Myviewholder extends RecyclerView.ViewHolder
    {
        TextView textView;
        CardView country_linearlayout;
        public Myviewholder(View view)
        {
            super(view);
            textView = (TextView) view.findViewById(R.id.txt_country);
            country_linearlayout = (CardView) view.findViewById(R.id.country_linearlayout);
        }
    }
}

