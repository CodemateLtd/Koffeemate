package com.codemate.brewflop.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Profile {
    @SerializedName("first_name")
    @Expose
    public String firstName;

    @SerializedName("last_name")
    @Expose
    public String lastName;

    @SerializedName("avatar_hash")
    @Expose
    public String avatarHash;

    @SerializedName("real_name")
    @Expose
    public String realName;

    @SerializedName("real_name_normalized")
    @Expose
    public String realNameNormalized;

    @SerializedName("email")
    @Expose
    public String email;

    @SerializedName("image_24")
    @Expose
    public String image24;

    @SerializedName("image_32")
    @Expose
    public String image32;

    @SerializedName("image_48")
    @Expose
    public String image48;

    @SerializedName("image_72")
    @Expose
    public String image72;

    @SerializedName("image_192")
    @Expose
    public String image192;

    @SerializedName("image_512")
    @Expose
    public String image512;
}
