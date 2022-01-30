package com.pkasemer.zodongofoods.HttpRequests;
/**
 * Created by Belal on 9/5/2017.
 */

public class URLs {

//    private static final String ROOT_URL = "http://pk.kakebe.com/ZodongoFoods/";
    private static final String ROOT_URL = "https://8f2c-41-75-189-25.ngrok.io/ZodongoFoodsAPI/";
    public static final String URL_REGISTER = ROOT_URL + "Api.php?apicall=signup";
    public static final String URL_LOGIN= ROOT_URL + "Api.php?apicall=login";

    public static final String URL_SEND_ORDER = ROOT_URL + "orders/create_order.php";

}
