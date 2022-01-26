package com.pkasemer.zodongofoods.Utils;

import com.pkasemer.zodongofoods.Models.FoodDBModel;

public interface MenuDetailListener {
    void retryPageLoad();
    void incrementqtn(int qty, FoodDBModel foodDBModel);
    void decrementqtn(int qty, FoodDBModel foodDBModel);

    void addToCartbtn(int qty, FoodDBModel foodDBModel);
    void orderNowMenuBtn(FoodDBModel foodDBModel);

}
