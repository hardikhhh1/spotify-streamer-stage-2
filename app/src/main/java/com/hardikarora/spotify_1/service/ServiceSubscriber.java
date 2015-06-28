package com.hardikarora.spotify_1.service;

import com.hardikarora.spotify_1.model.SpotifyTrackComponent;

/**
 * Created by hardikarora on 6/23/15.
 * Subscriber interface for PUB/SUB design pattern
 */
public interface ServiceSubscriber {

    public void stateChanged(SpotifyTrackComponent component);

}
