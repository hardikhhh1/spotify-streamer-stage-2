package com.hardikarora.spotify_1.service;

/**
 * Created by hardikarora on 6/23/15.
 * Publisher interface for PUB/SUB design pattern.
 */
public interface ServicePublisher {


    public void updateStateChanged();

    public void subscribe(ServiceSubscriber subscriber);

    public void unsubscribe(ServiceSubscriber subscriber);

}
