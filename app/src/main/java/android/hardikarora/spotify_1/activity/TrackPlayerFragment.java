package android.hardikarora.spotify_1.activity;

import android.app.Fragment;
import android.content.Intent;
import android.hardikarora.spotify_1.R;
import android.hardikarora.spotify_1.model.SpotifyTrackComponent;
import android.hardikarora.spotify_1.util.AsyncResponse;
import android.hardikarora.spotify_1.util.SpotifyPlayerService;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class TrackPlayerFragment extends Fragment implements AsyncResponse, View.OnClickListener {

    public static final String TAG = TrackPlayerFragment.class.getSimpleName();
    public static final String LOG_TAG = TrackPlayerFragment.class.getSimpleName();
    List<SpotifyTrackComponent> spotifyTrackList;
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
                String url = spotifyTrack.getTrackUrl();
                Intent intent = new Intent(getActivity(), SpotifyPlayerService.class);
                intent.putExtra("SpotifyTrackUrl", url);
                getActivity().startService(intent);
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
        spotifyTrackList = (ArrayList<SpotifyTrackComponent>)(ArrayList<?>)
                getArguments().getParcelableArrayList("TrackList");
        int trackIndex = getArguments().getInt("TrackIndex");
        spotifyTrack = spotifyTrackList.get(trackIndex);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.player_album_image);
        if(imageView != null){
            // Load image with picasso.
            Log.d(LOG_TAG, "Loading the background image with picasso.");
            String imageUrl = spotifyTrack.getImageUrl();
            Picasso.with(rootView.getContext()).load(imageUrl).into(imageView);
        } else{
            Log.e(LOG_TAG, "Image view is null");
        }
        return rootView;
    }
}
