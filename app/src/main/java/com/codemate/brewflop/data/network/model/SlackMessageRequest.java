package com.codemate.brewflop.data.network.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by iiro on 4.10.2016.
 */
public class SlackMessageRequest {
    final String text;
    List<Attachment> attachments = new ArrayList<>();

    public SlackMessageRequest(String text, Attachment... attachments) {
        this.text = text;
        this.attachments.addAll(Arrays.asList(attachments));
    }
}
