package com.example.free;

public class Allchannel
{
    public int id;
    public String channel_name,channel_icon,channel_stream;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public String getChannel_icon() {
        return channel_icon;
    }

    public String getChannel_stream(){
        return channel_stream;
    }

    public Allchannel(int id, String channel_name, String channel_icon,String channel_stream) {
        this.id = id;
        this.channel_name = channel_name;
        this.channel_icon = channel_icon;
        this.channel_stream = channel_stream;
    }
}
