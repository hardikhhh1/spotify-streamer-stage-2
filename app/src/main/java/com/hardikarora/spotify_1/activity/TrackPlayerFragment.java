package com.hardikarora.spotify_1.activity;

import android.app.Fragment;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
