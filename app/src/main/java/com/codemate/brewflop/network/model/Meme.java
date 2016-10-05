package com.codemate.brewflop.network.model;

import com.codemate.brewflop.BuildConfig;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by iiro on 4.10.2016.
 */
public class Meme {
    public String img = "";
    public String top_text = "";
    public String bottom_text = "";

    public Meme() {
    }

    public String getDescription() {
        return top_text + " " + bottom_text;
    }

    public String getMemeApiUrl() {
        try {
            return String.format(
                    BuildConfig.MEME_API_BASE_URL,
                    img,
                    URLEncoder.encode(top_text, "UTF-8"),
                    URLEncoder.encode(bottom_text, "UTF-8")
            );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
