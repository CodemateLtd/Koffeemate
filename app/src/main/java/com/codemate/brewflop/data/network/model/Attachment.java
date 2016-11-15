package com.codemate.brewflop.data.network.model;

/**
 * Created by iiro on 4.10.2016.
 */
public class Attachment {
    public String fallback;
    public String color;
    public String image_url;

    public Attachment(String fallback, String color, String imageUrl) {
        this.fallback = fallback;
        this.color = color;
        this.image_url = imageUrl;
    }
}
