package com.example.free;

import android.content.Context;
import android.net.ConnectivityManager;

public class Api
{
    public static final int CODE_GET_REQUEST = 1024;
    public static final int CODE_POST_REQUEST = 1025;

    //private static final String ROOT_URL = "http://192.168.1.119/free_tv_hub/api/";
    private static final String ROOT_URL = "http://dev.purplesmarttv.com/free_tv_hub/api/";

    public static final String URL_CREATE_RGISTER = ROOT_URL + "register.php";

    public static final String URL_CREATE_LOGIN = ROOT_URL + "login.php";

    //GET SLIDER URL
    //public static final String GET_SLIDER_IMAGE_URL = "http://192.168.1.119/free_tv_hub/assets/img/channel/slider/image/";
    public static final String GET_SLIDER_IMAGE_URL = "http://dev.purplesmarttv.com/free_tv_hub/assets/img/channel/slider/image/";

    //GET CHANNEL URL
    //public static final String GET_IMAGE_URL="http://192.168.1.119/free_tv_hub/assets/img/channel/icon/";
    public static final String GET_IMAGE_URL="http://dev.purplesmarttv.com/free_tv_hub/assets/img/channel/icon/";

    //READ ALL CATEGORY
    public static final String URL_READ_CATEGORY = ROOT_URL + "category.php?select";

    //READ ALL COUNTRY
    public static final String URL_READ_COUNTRY = ROOT_URL + "country.php?select";

    //READ ALL LANGUAGE
    public static final String URL_READ_LANGUAGE = ROOT_URL + "language.php?select";

    //READ COUNTRY WISE CHANNEL
    public static final String URL_READ_COUNTRY_WISE_CHANNEL = ROOT_URL + "channel.php";

    //READ LANGUAGE WISE CHANNEL
    public static final String URL_READ_LANGUAGE_WISE_CHANNEL = ROOT_URL + "channel.php";

    //READ CATEGORY WISE CHANNEL
    public static final String URL_READ_CATEGORY_WISE_CHANNEL = ROOT_URL + "channel.php";

    //READ LEATEST 10 CHANNEL ON HOME ACTIVITY
    public static final String URL_READ_NEW_CHANNEL = ROOT_URL + "channel.php?view_new_channel";

    //READ CHANNEL PROGRAMM
    public static final String URL_READ_CHANNEL_PROGRAM = ROOT_URL + "channel_programs.php";

    //READ SLIDER ON HOME ACTIVITY
    public static final String URL_READ_IMAGE_SLIDER = ROOT_URL + "image_slider.php?image_slider";
}
