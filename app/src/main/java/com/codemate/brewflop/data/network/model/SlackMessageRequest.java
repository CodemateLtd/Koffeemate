package com.codemate.brewflop.data.network.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by iiro on 4.10.2016.
 */
public class SlackMessageRequest {
    final String text;
    List<Attachment> attachments = new ArrayList<>();

    public SlackMessageRequest(String who, int incidentFreeDays, Meme meme) {
        this.text = String.format(Locale.ENGLISH, "_%s_ didn't know how to make coffee." +
                "\nTotal of *%d* incident free days were had.", who, incidentFreeDays);

        Attachment attachment = new Attachment.Builder()
                .fallback(meme.getDescription())
                .color("#6F4E37")
                .imageUrl(meme.getMemeApiUrl())
                .build();

        attachments.add(attachment);
    }
}
