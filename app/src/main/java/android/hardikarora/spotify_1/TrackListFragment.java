package android.hardikarora.spotify_1;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

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
    FetchArtistTopTrackData fetchArtistTopTrackData = new FetchArtistTopTrackData();

    public TrackListFragment() {
        fetchArtistTopTrackData.response = this;
    }



    @Override
    public void afterExecution(List<SpotifyTrackComponent> input) {
        topTracks = input;
        fetchArtistTopTrackData = new FetchArtistTopTrackData();
        fetchArtistTopTrackData.response = this;
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
        fetchArtistTopTrackData.response = this;
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

    public class FetchArtistTopTrackData extends SpotifyAsyncTask{
        @Override
        protected List<SpotifyTrackComponent> doInBackground(Object[] params) {
            Tracks tracks;
            try {
                tracks = SpotifyApiUtil.getArtistsTopTracks((String)params[0]);
            } catch (Exception e){
                Log.e(LOG_TAG, "Error occured while getting top tracks : " + e.getMessage());
                return new ArrayList<>();
            }
            List<SpotifyTrackComponent> spotifyTracks = new ArrayList<>();
            if(tracks == null)
                return spotifyTracks;
            if(tracks.tracks.size() > 0){
                for(Track track : tracks.tracks){
                    String albumName = track.album.name;
                    String trackName = track.name;
                    String imageUrl = track.album.images.get(0).url;
                    spotifyTracks.add(new SpotifyTrack(albumName, trackName,
                            imageUrl));
                }
            }
            return spotifyTracks;
        }
    }
}