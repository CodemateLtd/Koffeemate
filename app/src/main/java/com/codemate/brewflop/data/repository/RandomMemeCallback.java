package com.codemate.brewflop.data.repository;

import com.codemate.brewflop.data.network.model.Meme;

/**
 * Created by ironman on 11/10/16.
 */
public interface RandomMemeCallback {
    void gotRandomMeme(Meme randomMeme);
}