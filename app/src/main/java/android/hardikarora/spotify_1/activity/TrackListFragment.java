package android.hardikarora.spotify_1.activity;

import android.app.Fragment;
import android.content.Intent;
import android.hardikarora.spotify_1.util.AsyncResponse;
import android.hardikarora.spotify_1.R;
import android.hardikarora.spotify_1.util.SpotifyApiUtility;
import android.hardikarora.spotify_1.util.SpotifyAsyncTask;
import android.hardikarora.spotify_1.model.SpotifyTrackComponent;
import android.hardikarora.spotify_1.adapter.SpotifyTrackListAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

    SpotifyTrackListAdapter spotifyTrackListAdapter;
    List<SpotifyTrackComponent> topTracks = new ArrayList<>();
    FetchArtistTopTrackData fetchArtistTopTrackData = new FetchArtistTopTrackData(this);

    public TrackListFragment() {

    }



    @Override
    public void afterExecution(List<SpotifyTrackComponent> input) {
        topTracks = input;
        fetchArtistTopTrackData = new FetchArtistTopTrackData(this);
        spotifyTrackListAdapter = new SpotifyTrackListAdapter(
                getActivity(), R.layout.list_item_spotify,
                R.id.spotify_item_textview, input);

        ListView spotifyListView = (ListView) getActivity().findViewById(R.id.list_view_spotify2);
        spotifyListView.setAdapter(spotifyTrackListAdapter);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Parcelable> parcelableList = new ArrayList<>();
        if(topTracks != null){
            if(topTracks.size() > 0 ){
                parcelableList =(ArrayList<Parcelable>) (ArrayList<?>) topTracks;
            }
        }
        outState.putParcelableArrayList("TopTracks", parcelableList);

    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        fetchArtistTopTrackData.callback = this;
        if(savedInstanceState == null){
            return;
        }
        List<Parcelable> parcelableList;
        parcelableList = savedInstanceState.getParcelableArrayList("TopTracks");
        if(parcelableList == null){
            return;
        }
        if(!(parcelableList.size() > 0)){
            return;
        }


        List<SpotifyTrackComponent> componentList =
                (List<SpotifyTrackComponent>)(List<?>) parcelableList;
        if(parcelableList.size() > 0){
            //TODO : send it to the adapter list.
            spotifyTrackListAdapter = new SpotifyTrackListAdapter(
                    getActivity(), R.layout.list_item_spotify,
                    R.id.spotify_item_textview, componentList);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist_details, container, false);
        String artistId ;
        artistId = getArguments().getString(Intent.EXTRA_TEXT);
        try {
            fetchArtistTopTrackData.execute(artistId);

        } catch(Exception e){
            Log.e(LOG_TAG, "Error occured while getting top tracks : " + e.getMessage());
        }

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
                spotifyTracks = SpotifyApiUtility.getArtistsTopTracks(artistName);
            } catch (Exception e){
                Log.e(LOG_TAG, "Error occured while getting top tracks : " + e.getMessage());
                return new ArrayList<>();
            }
            return spotifyTracks;
        }
    }
}