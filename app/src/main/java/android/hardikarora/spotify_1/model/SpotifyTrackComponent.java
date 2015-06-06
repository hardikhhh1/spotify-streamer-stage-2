package android.hardikarora.spotify_1.model;

/**
 * Created by hardikarora on 6/5/15.
 *
 * Class representing the spotify track component, implementing the strategy design pattern here.
 * Each spotify component should implement this class and override the methods accordingly.
 *
 */
public abstract class SpotifyTrackComponent {

    public String getArtistId() { throw new UnsupportedOperationException();}

    public void setArtistId(String artistId) { throw new UnsupportedOperationException(); }

    public String getTrackName() { throw new UnsupportedOperationException(); }

    public void setTrackName(String trackName) { throw new UnsupportedOperationException(); }

    public String getAlbumName() { throw new UnsupportedOperationException(); }

    public void setAlbumName(String albumName) { throw new UnsupportedOperationException(); }

    public String getArtistName() { throw new UnsupportedOperationException(); }

    public void setArtistName(String artistName) { throw new UnsupportedOperationException(); }

    public String getImageUrl() { throw new UnsupportedOperationException(); }

    public void setImageUrl(String imageUrl) { throw new UnsupportedOperationException(); }
}
