package com.hardikarora.spotify_1.service;

import android.view.View;

import com.hardikarora.spotify_1.model.SpotifyTrackComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hardikarora on 6/23/15.
 */
public interface ServiceSubscriber {

    public void stateChanged(SpotifyTrackComponent component);

}
