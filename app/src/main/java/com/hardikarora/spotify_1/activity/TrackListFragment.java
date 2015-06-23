package com.hardikarora.spotify_1.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
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

import com.hardikarora.spotify_1.R;
import com.hardikarora.spotify_1.adapter.SpotifyTrackListAdapter;
import com.hardikarora.spotify_1.model.SpotifyTrack;
import com.hardikarora.spotify_1.model.SpotifyTrackComponent;
import com.hardikarora.spotify_1.util.AsyncResponse;
import com.hardikarora.spotify_1.util.SpotifyApiUtility;
import com.hardikarora.spotify_1.util.SpotifyAsyncTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by hardikarora on 6/6/15.
 */
/**
 * A placeholder fragment containing a simple view.
 */
public class TrackListFragment extends Fragment implements AsyncResponse {

    private static final String LOG_TAG =  TrackListFragment.class.getSimpleName();

    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private static final String NO_TOP_TRACKS =  "Found no top tracks for the artist";

    public static final String TAG =  TrackListFragment.class.getSimpleName();
    public static final String TOP_TRACKS_TAG = "TopTracks";
    public static final String SPOTIFY_TRACK_ID_TAG = "SpotifyTrackId";
    public static final String TRACK_INDEX_TAG = "TrackIndex";
    public static final String TRACK_LIST_TAG = "TrackList";

    /**
     * Current activated item position, only used in tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    List<SpotifyTrackComponent> topTracks;
    SpotifyTrackListAdapter spotifyTrackListAdapter;
    FetchArtistTopTrackData fetchArtistTopTrackData;
    SpotifyApiUtility utility;
    String artistId;
    String artistImageUrl;

    @InjectView(R.id.album_list_view) ListView topTracksListView;
    @InjectView(R.id.artist_main_image) ImageView artistImageView;

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

        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }

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

        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
//            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }

        View rootView = inflater.inflate(R.layout.fragment_artist_details, container, false);

        ButterKnife.inject(this, rootView);

        // Initializing the adapter and the list view.
        spotifyTrackListAdapter = new SpotifyTrackListAdapter(
                getActivity(), R.layout.album_details_item,
                R.id.spotify_album_textview, topTracks);

        topTracksListView.setAdapter(spotifyTrackListAdapter);

        // Setting the on item click listener to open the Spotify play activity on click.

        topTracksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                SpotifyTrackComponent selectedTrack = topTracks.get(position);
                bundle.putParcelableArrayList(TRACK_LIST_TAG, (ArrayList<SpotifyTrack>)
                        (ArrayList<?>)topTracks);
                bundle.putInt(TRACK_INDEX_TAG, position);
                bundle.putString(SPOTIFY_TRACK_ID_TAG, selectedTrack.getTrackId());
                FragmentManager manager = getFragmentManager();
                TrackPlayerDialogFragment trackPlayerDialogFragment = new TrackPlayerDialogFragment();
                trackPlayerDialogFragment.setArguments(bundle);
                if (getActivity().findViewById(R.id.top_tracks_container) != null
                        && getActivity().findViewById(R.id.container) != null){
                    trackPlayerDialogFragment.show(manager, "Spotify player");
                    } else {

                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.replace(R.id.container, trackPlayerDialogFragment,
                            TrackPlayerDialogFragment.TAG)
                            .commit();

                    }

            }
        });

        artistId = getArguments().getString(Intent.EXTRA_TEXT);
        artistImageUrl = getArguments().getString(ArtistListFragment.ARTIST_IMAGE_TEXT);

        artistImageView = (ImageView) rootView.findViewById(R.id.artist_main_image);
        if(artistImageUrl != null) {
            Picasso.with(rootView.getContext()).load(artistImageUrl).into(artistImageView);
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