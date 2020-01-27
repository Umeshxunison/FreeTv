package com.example.free.ui.epg;

public class Channel_Programm {

    public int id;
    public String channel_name,channel_icon,title,dates,start_time,end_time;

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public String getChannel_icon() {
        return channel_icon;
    }

    public void setChannel_icon(String channel_icon) {
        this.channel_icon = channel_icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

//    public Channel_Programm(int id, String title, String dates, String start_time, String end_time, String channel_name, String channel_icon) {
//        this.id = id;
//        this.title = title;
//        this.dates = dates;
//        this.start_time = start_time;
//        this.end_time = end_time;
//        this.channel_name = channel_name;
//        this.channel_icon = channel_icon;
//    }

    public Channel_Programm(int id, String title, String dates, String start_time, String end_time) {
        this.id = id;
        this.title = title;
        this.dates = dates;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public Channel_Programm(int id, String channel_name, String channel_icon) {
        this.id = id;
        this.channel_name = channel_name;
        this.channel_icon = channel_icon;
    }
}