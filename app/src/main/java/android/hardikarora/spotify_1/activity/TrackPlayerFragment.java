package android.hardikarora.spotify_1.activity;

import android.app.Fragment;
import android.content.DialogInterface;
import android.hardikarora.spotify_1.R;
import android.hardikarora.spotify_1.model.SpotifyTrack;
import android.hardikarora.spotify_1.model.SpotifyTrackComponent;
import android.hardikarora.spotify_1.util.AsyncResponse;
import android.hardikarora.spotify_1.util.SpotifyApiUtility;
import android.hardikarora.spotify_1.util.SpotifyAsyncTask;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class TrackPlayerFragment extends Fragment implements AsyncResponse, View.OnClickListener {

    public static final String TAG = TrackPlayerFragment.class.getSimpleName();
    public static final String LOG_TAG = TrackPlayerFragment.class.getSimpleName();
    SpotifyTrackComponent spotifyTrack;

    public TrackPlayerFragment() {
    }

    public static TrackPlayerFragment newInstance(){
        return new TrackPlayerFragment();
    }

    @Override
    public void afterExecution(List<SpotifyTrackComponent> input) {
        spotifyTrack = input.get(0);
        View view = getView();
        if(view == null) {
            Log.e(LOG_TAG, "View is null");
            return ;
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.player_album_image);
        if(imageView != null){
            // Load image with picasso.
            Log.d(LOG_TAG, "Loading the background image with picasso.");
            String imageUrl = spotifyTrack.getImageUrl();
            Picasso.with(view.getContext()).load(imageUrl).into(imageView);
        } else{
            Log.e(LOG_TAG, "Image view is null");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.player_play_btn_img:
                Log.d(LOG_TAG, "Playing the track.");
                try {
                    String url = spotifyTrack.getTrackUrl();
                    MediaPlayer player = new MediaPlayer();
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.setDataSource(url);
                    player.prepare();
                    player.start();
                } catch (IOException e){
                    Log.e(LOG_TAG, "Error while playing track" + e.getMessage());
                }

                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Creating the fragment for media player.");
        View rootView = inflater.inflate(R.layout.fragment_song_play, container, false);

        //Setting itself as an on click listerner.
        ImageView playButtonImage = (ImageView) rootView.findViewById(R.id.player_play_btn_img);
        if(playButtonImage != null)
            playButtonImage.setOnClickListener(this);


        // If no track id has been set we return the root view.
        String trackId = getArguments().getString(TrackListFragment.SPOTIFY_TRACK_ID_TAG);
        if(trackId == null) return rootView;
        if(trackId.isEmpty()) return rootView;

        GetTrackDataTask getTrackDataTask = new GetTrackDataTask(this);
        try {
            getTrackDataTask.execute(trackId);
        } catch (Exception e){
            Log.e(LOG_TAG, "Error while getting track details from spotify" + e.getMessage());
        }
        return rootView;
    }




    private class GetTrackDataTask extends SpotifyAsyncTask{

        private GetTrackDataTask(AsyncResponse callback) {
            super(callback);
        }

        @Override
        protected List<SpotifyTrackComponent> doInBackground(Object[] objects) {
            String trackId = (String) objects[0];
            List<SpotifyTrackComponent> spotifyTrackComponentList = new ArrayList<>();
            if(trackId == null) return null;
            if(trackId.isEmpty()) return null;

            SpotifyTrackComponent trackComponent = SpotifyApiUtility.getTrackFromSpotify(trackId,
                    1000);
            spotifyTrackComponentList.add(trackComponent);
            return spotifyTrackComponentList;
        }
    }
}
