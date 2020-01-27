package com.example.free;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class ChannellistAdapter extends ArrayAdapter<Allchannel> {
    ArrayList<Allchannel> allchannellist, tempCustomer, suggestions;

    public ChannellistAdapter(Context context, ArrayList<Allchannel> objects) {
        super(context, R.layout.channel_list, R.id.txt_channel_name, objects);
        this.allchannellist = objects;
        this.tempCustomer = new ArrayList<Allchannel>(objects);
        this.suggestions = new ArrayList<Allchannel>(objects);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        Allchannel customer = getItem(position);
        if (convertView == null) {
            if (parent == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.channel_list, null);
            else
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.channel_list, parent, false);
        }
        TextView txtCustomer = (TextView) convertView.findViewById(R.id.txt_channel_name);
        ImageView ivCustomerImage = (ImageView) convertView.findViewById(R.id.img_channel_icon);
        if (txtCustomer != null)
            txtCustomer.setText(customer.getChannel_name());

        Picasso.get().load(Api.GET_IMAGE_URL+customer.getChannel_icon()).into(ivCustomerImage);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return myFilter;
    }
    Filter myFilter =new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            Allchannel customer =(Allchannel)resultValue ;
            return customer.getChannel_name();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Allchannel cust : tempCustomer) {
                    if (cust.getChannel_name().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(cust);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            }
            else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<Allchannel> c =  (ArrayList<Allchannel> )results.values ;
            if (results != null && results.count > 0) {
                clear();
                for (Allchannel cust : c) {
                    add(cust);
                    notifyDataSetChanged();
                }
            }
            else{
                clear();
                notifyDataSetChanged();
            }
        }
    };
}
