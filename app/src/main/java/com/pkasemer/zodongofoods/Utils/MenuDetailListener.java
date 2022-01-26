package com.pkasemer.zodongofoods.Utils;

import com.pkasemer.zodongofoods.Adapters.OnlineMenuDetailAdapter;
import com.pkasemer.zodongofoods.Models.FoodDBModel;
import com.pkasemer.zodongofoods.Models.SelectedCategoryMenuItemResult;

public interface MenuDetailListener {
    void retryPageLoad();
    void incrementqtn(int qty, FoodDBModel foodDBModel);
    void decrementqtn(int qty, FoodDBModel foodDBModel);

    void addToCartbtn(SelectedCategoryMenuItemResult selectedCategoryMenuItemResult);
    void orderNowMenuBtn(SelectedCategoryMenuItemResult selectedCategoryMenuItemResult);

}
