package com.hardikarora.spotify_1.service;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hardikarora on 6/23/15.
 */
public interface ServicePublisher {


    public void updateStateChanged();

    public void subscribe(ServiceSubscriber subscriber);

    public void unsubscribe(ServiceSubscriber subscriber);

}
