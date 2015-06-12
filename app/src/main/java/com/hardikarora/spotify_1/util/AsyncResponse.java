package com.hardikarora.spotify_1.util;

import com.hardikarora.spotify_1.model.SpotifyTrackComponent;

import java.util.List;

/**
 * Created by harora on 6/5/15.
 *
 * Interface which can be implemented by the activity.
 */
public interface AsyncResponse {

        // This method is called when an async task is completed.
        void afterExecution(List<SpotifyTrackComponent> input);
}
