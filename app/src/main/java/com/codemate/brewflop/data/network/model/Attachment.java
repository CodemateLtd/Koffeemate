package com.codemate.brewflop.data.network.model;

/**
 * Created by iiro on 4.10.2016.
 */
public class Attachment {
    public String fallback;
    public String color;
    public String image_url;

    private Attachment(Builder builder) {
        fallback = builder.fallback;
        color = builder.color;
        image_url = builder.imageUrl;
    }

    public static final class Builder {
        private String fallback;
        private String color;
        private String imageUrl;

        public Builder() {
        }

        public Builder fallback(String val) {
            fallback = val;
            return this;
        }

        public Builder color(String val) {
            color = val;
            return this;
        }

        public Builder imageUrl(String val) {
            imageUrl = val;
            return this;
        }

        public Attachment build() {
            return new Attachment(this);
        }
    }
}
