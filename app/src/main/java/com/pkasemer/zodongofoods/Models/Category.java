
package com.pkasemer.zodongofoods.Models;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Category {

    @SerializedName("sliderBanners")
    @Expose
    private List<SliderBanner> sliderBanners = null;
    @SerializedName("featuredCategories")
    @Expose
    private List<FeaturedCategory> featuredCategories = null;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("imageCover")
    @Expose
    private String imageCover;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("sectioned_menuItems")
    @Expose
    private List<SectionedMenuItem> sectionedMenuItems = null;

    public List<SliderBanner> getSliderBanners() {
        return sliderBanners;
    }

    public void setSliderBanners(List<SliderBanner> sliderBanners) {
        this.sliderBanners = sliderBanners;
    }

    public List<FeaturedCategory> getFeaturedCategories() {
        return featuredCategories;
    }

    public void setFeaturedCategories(List<FeaturedCategory> featuredCategories) {
        this.featuredCategories = featuredCategories;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageCover() {
        return imageCover;
    }

    public void setImageCover(String imageCover) {
        this.imageCover = imageCover;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public List<SectionedMenuItem> getSectionedMenuItems() {
        return sectionedMenuItems;
    }

    public void setSectionedMenuItems(List<SectionedMenuItem> sectionedMenuItems) {
        this.sectionedMenuItems = sectionedMenuItems;
    }

}
