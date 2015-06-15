package com.hardikarora.spotify_1.activity;

import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import com.hardikarora.spotify_1.R;
import com.hardikarora.spotify_1.model.SpotifyTrack;
import com.hardikarora.spotify_1.model.SpotifyTrackComponent;
import com.hardikarora.spotify_1.util.SpotifyPlayerService;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A placeholder fragment containing a simple view.
 */
public class TrackPlayerFragment extends Fragment implements View.OnClickListener,
        MediaPlayer.OnCompletionListener{

    public static final String TAG = TrackPlayerFragment.class.getSimpleName();
    public static final String LOG_TAG = TrackPlayerFragment.class.getSimpleName();
    List<SpotifyTrackComponent> spotifyTrackList;
    SpotifyTrackComponent spotifyTrack;
    int trackIndex;
    private PlayerViewHolder playerViewHolder;
    private ShareActionProvider mShareActionProvider;


    private SpotifyPlayerService spotifyPlayerService;

    // A service connection to connect to the spotify api service.
    public ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            spotifyPlayerService = ((SpotifyPlayerService.SpotifyBinder)service).getService();
            spotifyPlayerService.spotifyTrackList = spotifyTrackList;
            spotifyPlayerService.trackIndex = trackIndex;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            spotifyPlayerService = null;
        }
    };

    public static TrackPlayerFragment newInstance(){
        return new TrackPlayerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initiate the service.
        Intent intent = new Intent(getActivity(), SpotifyPlayerService.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        getActivity().startService(intent);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_song_play, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);

        mShareActionProvider = (ShareActionProvider) item.getActionProvider();


        //TODO: add a spotify as extra text.
        Intent shareButtonIntent = new Intent(Intent.ACTION_SEND);
        shareButtonIntent.setType("text/plain");
        String shareString = spotifyTrack.getTrackUrl();
        shareButtonIntent.putExtra(Intent.EXTRA_SUBJECT, "A great song indeed !!");
        shareButtonIntent.putExtra(Intent.EXTRA_TEXT, shareString);
        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(shareButtonIntent);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }



    @Override
    public void onPause() {
        super.onPause();

        try {
            // Initiating the intents for playback controls.
            Intent playIntent = new Intent(getActivity(), SpotifyPlayerService.class).setAction("Play");
            Intent previousIntent = new Intent(getActivity(), SpotifyPlayerService.class).
                    setAction("Previous");
            Intent nextIntent = new Intent(getActivity(), SpotifyPlayerService.class).setAction("Next");

            // Initiating the pending intents for playback controls.
            PendingIntent playPendingIntent = PendingIntent.getService(getActivity(), 0,
                    playIntent, 0);
            PendingIntent previousPendingIntent = PendingIntent.getService(getActivity(), 0,
                    previousIntent, 0);
            PendingIntent nextPendingIntent = PendingIntent.getService(getActivity(), 0,
                    nextIntent, 0);


            NotificationManager nm = (NotificationManager) getActivity()
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            Notification.Builder builder = new Notification.Builder(getActivity());

            RemoteViews notificationView =
                    new RemoteViews(getActivity().getPackageName(), R.layout.notification_player);

            notificationView.setOnClickPendingIntent(R.id.notification_play_btn_img,
                     playPendingIntent);
            notificationView.setOnClickPendingIntent(R.id.notification_prev_btn_img,
                    previousPendingIntent);
            notificationView.setOnClickPendingIntent(R.id.notification_next_btn_img,
                    nextPendingIntent);

            builder.setContent(notificationView)
                    .setSmallIcon(R.drawable.ic_skip_next_black_24dp)
                    .setOngoing(true)
                    .setTicker("Spotify Streamer");

            Notification n = builder.build();
            nm.notify(1, n);
        }
        catch (Exception e){
            Log.e(LOG_TAG,  "Error while getting log for the player." + e.getMessage() + e.getStackTrace());
        }

    }




    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        // If the fragment is clicked we check for the click we check for the click and
        // take action accordingly.
        int trackIndex = -1;
        switch (v.getId()) {
            case R.id.player_play_btn_img:
                // If the play button is clicked.
                Log.d(LOG_TAG, "Play button has been clicked.");
                trackIndex = spotifyPlayerService.playButtonPressed();
                break;

            case R.id.player_next_btn_img:
                // If the next button is clicked.
                Log.d(LOG_TAG, "Next song button pressed.");
                spotifyPlayerService.nextTrack();
                trackIndex = spotifyPlayerService.nextTrack();
                break;

            case R.id.player_prev_btn_img:
                Log.d(LOG_TAG, "Previous song button pressed.");
                spotifyPlayerService.previousTrack();
                trackIndex = spotifyPlayerService.previousTrack();
                break;
        }

        if(trackIndex != -1){
            // If any button was clicked and track has chaned, we have to update the
            // view objects accordingly.
            playerViewHolder.setViewObjects(getView(),(SpotifyTrack) spotifyTrackList.get(trackIndex));
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(LOG_TAG, "The track is over, playing next track.");
        mp.reset();
        int trackIndex = spotifyPlayerService.nextTrack();
        playerViewHolder.setViewObjects(getView(),(SpotifyTrack) spotifyTrackList.get(trackIndex));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Creating the fragment for media player.");
        View rootView = inflater.inflate(R.layout.fragment_song_play, container, false);

        // If root view itself is null, we return null.
        if(rootView == null) return null;


        playerViewHolder = new PlayerViewHolder(rootView);
        rootView.setTag(playerViewHolder);

        //Setting itself as an on click listener for various play back controls..
        if(playerViewHolder.playButtonImage != null)
            playerViewHolder.playButtonImage.setOnClickListener(this);

        if(playerViewHolder.previousButtonImage != null)
            playerViewHolder.previousButtonImage.setOnClickListener(this);

        if(playerViewHolder.nextButtonImage != null)
            playerViewHolder.nextButtonImage.setOnClickListener(this);

        // If no track id has been set we return the root view.
        spotifyTrackList = (ArrayList<SpotifyTrackComponent>)(ArrayList<?>)
                getArguments().getParcelableArrayList("TrackList");
        trackIndex = getArguments().getInt("TrackIndex");
        spotifyTrack = spotifyTrackList.get(trackIndex);

        // Setting the view objects with respect to the track.
        playerViewHolder.setViewObjects(rootView, (SpotifyTrack)spotifyTrack);



        // Setting the share intent for share button.
        setHasOptionsMenu(true);

        return rootView;
    }



    static class PlayerViewHolder{

        @InjectView(R.id.player_play_btn_img) ImageView playButtonImage;
        @InjectView(R.id.player_next_btn_img) ImageView nextButtonImage;
        @InjectView(R.id.player_prev_btn_img) ImageView previousButtonImage;
        @InjectView(R.id.player_album_image) ImageView albumImageView;
        @InjectView(R.id.player_album_name_textview) TextView albumNameTextView;
        @InjectView(R.id.player_artist_name_textview) TextView artistNameTextView;

        public PlayerViewHolder(View view){
            ButterKnife.inject(this, view);
        }

        public void setViewObjects(View view, SpotifyTrack spotifyTrack)
        {
            if(albumNameTextView != null) albumNameTextView.setText(spotifyTrack.getAlbumName());
            if(artistNameTextView != null) artistNameTextView.setText(spotifyTrack.getTrackName());
            if(albumImageView != null){
                // Load image with picasso.
                Log.d(LOG_TAG, "Loading the background image with picasso.");
                Picasso.with(view.getContext()).load(spotifyTrack.getImageUrl()).into(albumImageView);
            } else{
                Log.e(LOG_TAG, "Image view is null");
            }
        }
    }
}
