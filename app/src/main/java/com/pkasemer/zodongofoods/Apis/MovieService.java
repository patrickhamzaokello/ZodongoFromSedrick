package com.pkasemer.zodongofoods.Apis;

import com.pkasemer.zodongofoods.Models.FoodDBModel;
import com.pkasemer.zodongofoods.Models.HomeBannerModel;
import com.pkasemer.zodongofoods.Models.HomeFeed;
import com.pkasemer.zodongofoods.Models.HomeMenuCategoryModel;
import com.pkasemer.zodongofoods.Models.OrderRequest;
import com.pkasemer.zodongofoods.Models.OrderResponse;
import com.pkasemer.zodongofoods.Models.SectionedCategoryMenu;
import com.pkasemer.zodongofoods.Models.SelectedCategoryMenuItem;
import com.pkasemer.zodongofoods.Models.UserOrders;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface MovieService {

    //selected category and its products end point
//    https://zodongofoods.com/mobile/api/v1/menus/menupages.php?category=1&page=1
    @GET("menus/menupages.php")
    Call<SelectedCategoryMenuItem> getTopRatedMovies(
            @Query("category") int menu_category_id,
            @Query("page") int pageIndex
    );

    //search end
//    https://zodongofoods.com/mobile/api/v1/menus/topmenuitems.php?page=1
    @GET("menus/topmenuitems.php")
    Call<SelectedCategoryMenuItem> getTopMenuItems(
            @Query("page") int pageIndex
    );

    //    http://192.168.0.199:8080/projects/ZodongoFoodsAPI/home/homefeed.php?page=1
    @GET("home/homefeed.php")
    Call<HomeFeed> getHomeFeed(
            @Query("page") int pageIndex
    );

    //    menus/menudetails.php?menuId=9&category=3&page=1
    @GET("menus/menudetails.php")
    Call<SelectedCategoryMenuItem> getMenuDetails(
            @Query("menuId") int menu_id,
            @Query("category") int menu_category_id,
            @Query("page") int pageIndex
    );

    @POST("orders/create_order.php")
    Call<OrderResponse> postCartOrder(
            @Body OrderRequest orderRequest
    );

    //fetch past orders
    @GET("orders/userOrders.php")
    Call<UserOrders> getUserOrders(
            @Query("customerId") int customerID,
            @Query("page") int pageIndex
    );


}