package com.hardikarora.spotify_1.menu;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.RemoteViews;

import com.hardikarora.spotify_1.R;
import com.hardikarora.spotify_1.model.SpotifyTrack;
import com.hardikarora.spotify_1.model.SpotifyTrackComponent;
import com.hardikarora.spotify_1.service.ServiceSubscriber;
import com.hardikarora.spotify_1.service.SpotifyPlayerService;
import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by hardikarora on 6/23/15.
 */
public class SpotifyNotification implements ServiceSubscriber {

    public static final int NOTIFICATION_ID = 1;
    SpotifyPlayerService service = new SpotifyPlayerService().getInstance();

    public static final String PLAY_ACTION = "Play";
    public static final String NEXT_ACTION = "Next";
    public static final String PREVIOUS_ACTION = "Previous";
    public static final String SPOTIFY_STREAMER_TICKER = "Spotify Streamer";

    Context mContext;
    SpotifyTrack track;
    static Notification notification;
    static RemoteViews notificationView;

    public SpotifyNotification(Context mContext,
                               SpotifyTrack track){
        this.track = track;
        this.mContext = mContext;
        if(service != null){
            service.subscribe(this);
        }
    }
    @Override
    public void stateChanged(SpotifyTrackComponent component) {
        if(notificationView != null){
            notification = buildNotification(
                    component.getAlbumName(), component.getImageUrl()
            );
            NotificationManager nm = (NotificationManager) mContext
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(1, notification);

        }

    }

    public void startNotification(){
        if(track == null) return;
        NotificationManager nm = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = buildNotification();
        nm.notify(NOTIFICATION_ID, notification);
    }

    public void cancelNotification(){
        NotificationManager nm = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_ID);
    }


    public Notification buildNotification(){
        if(track == null) return null;
        return buildNotification(track.getTrackName(), track.getImageUrl());
    }

    public Notification buildNotification(final String trackName,final String imageUrl){
        // Initiating the pending intents for playback controls.
        PendingIntent playPendingIntent = initiateSpotifyPendingIntent(mContext, PLAY_ACTION);
        PendingIntent previousPendingIntent = initiateSpotifyPendingIntent(mContext, PREVIOUS_ACTION);
        PendingIntent nextPendingIntent = initiateSpotifyPendingIntent(mContext, NEXT_ACTION);

        Notification.Builder builder = new Notification.Builder(mContext);


        notificationView =
                new RemoteViews(mContext.getPackageName(), R.layout.notification_player);

        notificationView.setOnClickPendingIntent(R.id.notification_play_btn_img,
                playPendingIntent);
        notificationView.setOnClickPendingIntent(R.id.notification_prev_btn_img,
                previousPendingIntent);
        notificationView.setOnClickPendingIntent(R.id.notification_next_btn_img,
                nextPendingIntent);

        notificationView.setTextViewText(R.id.notification_album_text, trackName);

        Bitmap image = null;

        try {
            image = new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... params) {
                    try {
                        return Picasso.with(mContext).load(imageUrl).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (image != null) {
            notificationView.setImageViewBitmap(R.id.notification_album_image, image);
            builder.setLargeIcon(image);
        }
        builder.setContent(notificationView)
                .setSmallIcon(R.drawable.ic_skip_next_black_24dp)
                .setOngoing(true)
                .setTicker(SPOTIFY_STREAMER_TICKER);



        Notification playerNotification = builder.build();
        playerNotification.bigContentView = notificationView;
        this.notification = playerNotification;
        return playerNotification;
    }

    private PendingIntent initiateSpotifyPendingIntent(Context context, String action){
        Intent intent = new Intent(context, SpotifyPlayerService.class).setAction(
                action);
        PendingIntent pendingIntent = PendingIntent.getService(mContext, 0,
                intent, 0);
        return pendingIntent;
    }
}
