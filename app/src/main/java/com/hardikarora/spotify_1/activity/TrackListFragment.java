package com.hardikarora.spotify_1.activity;

import android.app.Fragment;
import android.content.Intent;
import com.hardikarora.spotify_1.R;
import com.hardikarora.spotify_1.adapter.SpotifyTrackListAdapter;
import com.hardikarora.spotify_1.model.SpotifyTrack;
import com.hardikarora.spotify_1.model.SpotifyTrackComponent;
import com.hardikarora.spotify_1.util.AsyncResponse;
import com.hardikarora.spotify_1.util.SpotifyApiUtility;
import com.hardikarora.spotify_1.util.SpotifyAsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hardikarora on 6/6/15.
 */
/**
 * A placeholder fragment containing a simple view.
 */
public class TrackListFragment extends Fragment implements AsyncResponse {

    private static final String LOG_TAG =  TrackListFragment.class.getSimpleName();

    private static final String NO_TOP_TRACKS =  "Found no top tracks for the artist";

    public static final String TAG =  TrackListFragment.class.getSimpleName();
    public static final String TOP_TRACKS_TAG = "TopTracks";
    public static final String SPOTIFY_TRACK_ID_TAG = "SpotifyTrackId";

    List<SpotifyTrackComponent> topTracks;
    SpotifyTrackListAdapter spotifyTrackListAdapter;
    FetchArtistTopTrackData fetchArtistTopTrackData;
    SpotifyApiUtility utility;
    ListView topTracksListView;
    String artistId;
    String artistImageUrl;

    public TrackListFragment() {
        topTracks = new ArrayList<>();
        fetchArtistTopTrackData = new FetchArtistTopTrackData(this);

    }

    public static TrackListFragment newInstance(){
        return new TrackListFragment();
    }
    @Override
    public void afterExecution(List<SpotifyTrackComponent> input) {
        // Reinitialising the async task as it can be executed only once.
        fetchArtistTopTrackData = new FetchArtistTopTrackData(this);

        // Clearing the list adapter, so the new data can be added.
        spotifyTrackListAdapter.clear();
        topTracks.clear();

        if(input.size() == 0){
            // If there is not artist we show a toast.
            Toast toast = Toast.makeText(getActivity(), NO_TOP_TRACKS, Toast.LENGTH_SHORT);
            toast.show();
        }

        topTracks.addAll(input);
        spotifyTrackListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // To save the state we write all the artist list
        // in a parcelable list.
        ArrayList<Parcelable> parcelableList = new ArrayList<>();
        if(topTracks != null){
            if(topTracks.size() > 0 ){
                parcelableList =(ArrayList<Parcelable>) (ArrayList<?>) topTracks;
            }
        }
        outState.putParcelableArrayList(TOP_TRACKS_TAG, parcelableList);

    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        fetchArtistTopTrackData = new FetchArtistTopTrackData(this);

        // If the saved instance is null, function is exited.
        if(savedInstanceState == null) return;

        List<Parcelable> parcelableList;
        parcelableList = savedInstanceState.getParcelableArrayList("TopTracks");

        // If the list is empty, the function is exited.
        if(parcelableList == null) return;
        if(!(parcelableList.size() > 0)) return;


        List<SpotifyTrackComponent> componentList =
                (List<SpotifyTrackComponent>)(List<?>) parcelableList;
        spotifyTrackListAdapter.clear();
        topTracks.clear();
        topTracks.addAll(componentList);
        spotifyTrackListAdapter.notifyDataSetChanged();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist_details, container, false);

        // Initializing the adapter and the list view.
        spotifyTrackListAdapter = new SpotifyTrackListAdapter(
                getActivity(), R.layout.album_details_item,
                R.id.spotify_album_textview, topTracks);
        topTracksListView = (ListView) rootView.findViewById(R.id.album_list_view);
        topTracksListView.setAdapter(spotifyTrackListAdapter);
        topTracksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SpotifyTrackComponent selectedTrack = topTracks.get(position);
                Intent intent = new Intent(getActivity(), SongPlayActivity.class);
                intent.putParcelableArrayListExtra("TrackList", (ArrayList<SpotifyTrack>)
                        (ArrayList<?>)topTracks);
                intent.putExtra("TrackIndex", position);
                intent.putExtra(SPOTIFY_TRACK_ID_TAG, selectedTrack.getTrackId());
                startActivity(intent);

            }
        });

        artistId = getArguments().getString(Intent.EXTRA_TEXT);
        artistImageUrl = getArguments().getString(ArtistListFragment.ARTIST_IMAGE_TEXT);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.artist_main_image);
        if(artistImageUrl != null) {
            Picasso.with(rootView.getContext()).load(artistImageUrl).into(imageView);
        }
        try {
            fetchArtistTopTrackData.execute(artistId);

        } catch(Exception e){
            Log.e(LOG_TAG, "Error occured while getting top tracks : " + e.getMessage());
        }
        utility = new SpotifyApiUtility(getActivity());
        return rootView;
    }


    public class FetchArtistTopTrackData extends SpotifyAsyncTask {

        public FetchArtistTopTrackData(AsyncResponse response) {
            super(response);
        }

        @Override
        protected List<SpotifyTrackComponent> doInBackground(Object[] params) {
            List<SpotifyTrackComponent> spotifyTracks;
            String artistName = (String) params[0];
            try {
                spotifyTracks = utility.getArtistsTopTracks(artistName);
            } catch (Exception e){
                Log.e(LOG_TAG, "Error occured while getting top tracks : " + e.toString()
                        + e.getMessage());
                return new ArrayList<>();
            }
            return spotifyTracks;
        }
    }
}