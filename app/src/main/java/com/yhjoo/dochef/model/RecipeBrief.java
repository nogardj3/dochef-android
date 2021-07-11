package com.yhjoo.dochef.model;

public class RecipeBrief {
    private final int thumbnail_type;
    private final String imageUrl;
    private final int isNew;

    public RecipeBrief(int type, String imageUrl, int isNew) {
        this.thumbnail_type = type;
        this.imageUrl = imageUrl;
        this.isNew = isNew;
    }

    public int getThumbnail_type() {
        return thumbnail_type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getIsNew() {
        return isNew;
    }
}
