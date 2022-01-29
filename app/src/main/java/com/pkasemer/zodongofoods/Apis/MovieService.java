package com.pkasemer.zodongofoods.Apis;

import com.pkasemer.zodongofoods.Models.FoodDBModel;
import com.pkasemer.zodongofoods.Models.HomeBannerModel;
import com.pkasemer.zodongofoods.Models.HomeMenuCategoryModel;
import com.pkasemer.zodongofoods.Models.Pk;
import com.pkasemer.zodongofoods.Models.SectionedCategoryMenu;
import com.pkasemer.zodongofoods.Models.SelectedCategoryMenuItem;

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

    @GET("menus/menupages.php")
    Call<SelectedCategoryMenuItem> getTopRatedMovies(
            @Query("category") int menu_category_id,
            @Query("page") int pageIndex
    );

    @GET("menus/topmenuitems.php")
    Call<SelectedCategoryMenuItem> getTopMenuItems(
            @Query("page") int pageIndex
    );

    @GET("menucategory/readPaginated.php")
    Call<HomeMenuCategoryModel> getMenuCategories();

    @GET("menucategory/readSectionedMenu.php")
    Call<SectionedCategoryMenu> getMenuCategoriesSection();

    @GET("banner/read.php")
    Call<HomeBannerModel> getHomeBanners();

//    menus/menudetails.php?menuId=9&category=3&page=1
    @GET("menus/menudetails.php")
    Call<SelectedCategoryMenuItem> getMenuDetails(
            @Query("menuId") int menu_id,
            @Query("category") int menu_category_id,
            @Query("page") int pageIndex
    );



    @POST("menus/create.php")
    Call<ResponseBody> postCartItems(
            @Body List<FoodDBModel> foodDBModels
    );

    //on below line we are creating a method to post our data.

    @FormUrlEncoded
    @POST("index.php?action=item")
    Call<FoodDBModel> postOrderItems(
            @Field("items[]") List<FoodDBModel> listFooDBModel
    );


    //method 3
    @POST("endpoint")
    Call<Void> postPKArray(@Body Pk mypk);

}