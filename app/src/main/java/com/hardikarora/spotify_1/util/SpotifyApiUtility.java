package com.hardikarora.spotify_1.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hardikarora.spotify_1.model.SpotifyArtist;
import com.hardikarora.spotify_1.model.SpotifyTrack;
import com.hardikarora.spotify_1.model.SpotifyTrackComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by hardikarora on 6/5/15.
 *
 * Class which has methods that wrap arround the spotify api.
 */
public class SpotifyApiUtility{

    public static final String LOG_TAG = SpotifyApiUtility.class.getSimpleName();

    private static final String TYPE_TOKEN = "type";
    private static final String ARTIST_TOKEN = "artist";
    private static final String COUNTRY_TOKEN = "country";
    //TODO: check to see where and how to use market token.
    private static final String MARKET_TOKEN = "market";
    private static final String USA_SYMBOL = "US";
    private static final String EMPTY_PIC_LINK =
            "http://www.cloudcomputing-news.net/media/cloud_question_mark.jpg.600x600_q96.png";

    private Context mContext;

    public SpotifyApiUtility(Context context){
        this.mContext = context;
    }

    /**
     * Function to search artists with the artist name.
     * @param artistName Name of the artist.
     * @return A list of {@link com.hardikarora.spotify_1.model.SpotifyArtist }
     */
    public List<SpotifyTrackComponent> searchArtists(String artistName){
        SpotifyApi spotifyApi = new SpotifyApi();
        SpotifyService spotifyService = spotifyApi.getService();
        Map<String, Object> apiOptions = new HashMap<>();
        apiOptions.put(COUNTRY_TOKEN, getCountryCode());
        apiOptions.put(TYPE_TOKEN, ARTIST_TOKEN);

        ArtistsPager artists = spotifyService.searchArtists(artistName, apiOptions);
        List<Artist> artistList  = artists.artists.items;

        List<SpotifyTrackComponent> spotifyTrackComponents = new ArrayList<>();
        SpotifyTrackComponent spotifyArtist;
        for(Artist artist: artistList){
            // For each artist recieved we initialize a spotify artist
            // and append it to the list.
            spotifyArtist = new SpotifyArtist(artist.id, artist.name);
            String imageUrl = findImageUrl(artist.images, 200);
            spotifyArtist.setImageUrl(imageUrl);
            spotifyTrackComponents.add(spotifyArtist);
        }

        return spotifyTrackComponents;

    }

    /**
     * Function to get the top tracks for an artist
     * @param artistID : String representing the id of the artist.
     * @return List of {@link com.hardikarora.spotify_1.model.SpotifyTrack}
     */
    public List<SpotifyTrackComponent> getArtistsTopTracks(String artistID){
        SpotifyApi spotifyApi = new SpotifyApi();
        SpotifyService service = spotifyApi.getService();
        Map<String, Object> apiOptions = new HashMap<>();
        apiOptions.put(COUNTRY_TOKEN, getCountryCode());
        Tracks tracks = service.getArtistTopTrack(artistID, apiOptions);
        List<SpotifyTrackComponent> spotifyTracks = new ArrayList<>();
        if(tracks == null)
            return spotifyTracks;

        SpotifyTrack spotifyTrack;
        String artistName = "";
        if(tracks.tracks.size() > 0){
            for(Track track : tracks.tracks){
                if(artistName.equals("")){
                    List<ArtistSimple> artistList = track.artists;
                    for(ArtistSimple artist : artistList){
                        if(artistID.equals(artist.id)){
                            artistName = artist.name;
                        }
                    }
                }
                spotifyTrack = new SpotifyTrack(track, artistName);
                spotifyTracks.add(spotifyTrack);
            }
        }
        return spotifyTracks;
    }


    public String findImageUrl(List<Image> imagesList, int size){
        if(imagesList == null)
            return EMPTY_PIC_LINK;
        if(imagesList.size() == 0)
            return EMPTY_PIC_LINK;

        for(Image image : imagesList){
            if(image.width == size){
                Log.d(LOG_TAG, "The image size is width : " + image.width);
                return image.url;
            }
        }
        Log.d(LOG_TAG, "The image size is width : " + imagesList.get(0).width);
        return imagesList.get(0).url;
    }

    private String getCountryCode(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String countryToken = preferences.getString("countryCode", USA_SYMBOL);
        return USA_SYMBOL;
    }


}
