package android.hardikarora.spotify_1.util;

import android.hardikarora.spotify_1.model.SpotifyArtist;
import android.hardikarora.spotify_1.model.SpotifyTrack;
import android.hardikarora.spotify_1.model.SpotifyTrackComponent;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by hardikarora on 6/5/15.
 *
 * Class which has methods that wrap arround the spotify api.
 */
public class SpotifyApiUtility {

    public static final String TYPE_TOKEN = "type";
    public static final String ARTIST_TOKEN = "artist";
    public static final String COUNTRY_TOKEN = "country";
    public static final String USA_SYMBOL = "US";

    /**
     * Function to search artists with the artist name.
     * @param artistName Name of the artist.
     * @return A list of {@link android.hardikarora.spotify_1.model.SpotifyArtist }
     */
    public static List<SpotifyTrackComponent> searchArtists(String artistName){
        SpotifyApi spotifyApi = new SpotifyApi();
        SpotifyService spotifyService = spotifyApi.getService();
        Map<String, Object> apiOptions = new HashMap<>();
        apiOptions.put(TYPE_TOKEN, ARTIST_TOKEN);
        ArtistsPager artists = spotifyService.searchArtists(artistName, apiOptions);
        List<Artist> artistList  = artists.artists.items;

        List<SpotifyTrackComponent> spotifyTrackComponents = new ArrayList<>();
        SpotifyTrackComponent spotifyArtist;
        for(Artist artist: artistList){
            // For each artist recieved we initialize a spotify artist
            // and append it to the list.
            spotifyArtist = new SpotifyArtist(artist.id, artist.name);
            String imageUrl = null;
            if(artist.images.size() > 0){
                imageUrl = artist.images.get(0).url;
            }
            spotifyArtist.setImageUrl(imageUrl);
//                    for(Image image : artist.images){
//                    TODO : get the smallest sized image.
//                    }
            spotifyTrackComponents.add(spotifyArtist);
        }

        return spotifyTrackComponents;

    }

    /**
     * Function to get the top tracks for an artist
     * @param artistID : String representing the id of the artist.
     * @return List of {@link android.hardikarora.spotify_1.model.SpotifyTrack}
     */
    public static List<SpotifyTrackComponent> getArtistsTopTracks(String artistID){
        SpotifyApi spotifyApi = new SpotifyApi();
        SpotifyService service = spotifyApi.getService();
        Map<String, Object> apiOptions = new HashMap<>();
        apiOptions.put(COUNTRY_TOKEN, USA_SYMBOL);
        Tracks tracks = service.getArtistTopTrack(artistID, apiOptions);
        List<SpotifyTrackComponent> spotifyTracks = new ArrayList<>();
        if(tracks == null)
            return spotifyTracks;

        if(tracks.tracks.size() > 0){
            for(Track track : tracks.tracks){
                String albumName = track.album.name;
                String trackName = track.name;

                //                    for(Image image : artist.images){
//                    TODO : get the smallest sized image.
//                    }


                String imageUrl = track.album.images.get(0).url;
                spotifyTracks.add(new SpotifyTrack(albumName, trackName,
                        imageUrl));
            }
        }


        return spotifyTracks;
    }



}
