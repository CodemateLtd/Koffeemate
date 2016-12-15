package com.codemate.brewflop.data.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

public class User {
    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("team_id")
    @Expose
    public String teamId;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("deleted")
    @Expose
    public boolean deleted;

    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("color")
    @Expose
    public String color;

    @SerializedName("real_name")
    @Expose
    public String realName;

    @SerializedName("tz")
    @Expose
    public String tz;

    @SerializedName("tz_label")
    @Expose
    public String tzLabel;

    @SerializedName("tz_offset")
    @Expose
    public int tzOffset;

    @SerializedName("profile")
    @Expose
    public Profile profile;

    @SerializedName("is_admin")
    @Expose
    public boolean isAdmin;

    @SerializedName("is_owner")
    @Expose
    public boolean isOwner;

    @SerializedName("is_primary_owner")
    @Expose
    public boolean isPrimaryOwner;

    @SerializedName("is_restricted")
    @Expose
    public boolean isRestricted;

    @SerializedName("is_ultra_restricted")
    @Expose
    public boolean isUltraRestricted;

    @SerializedName("is_bot")
    @Expose
    public boolean isBot;

    @NotNull
    public String getLargestAvailableProfileImageUrl() {
        String imageUrl = profile.image512;

        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = profile.image192;
        }

        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = profile.image72;
        }

        return imageUrl;
    }
}
