package android.hardikarora.spotify_1.model;

import android.hardikarora.spotify_1.util.SpotifyApiUtility;
import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by hardikarora on 6/5/15.
 *
 * Class representing the spotify track model.
 */
public class SpotifyTrack extends SpotifyTrackComponent implements Parcelable {

    public static final int DEFAULT_IMAGE_SIZE = 200;

    private String trackName;
    private String albumName;
    private String imageUrl;
    private String trackId;
    private String trackUrl;

    public SpotifyTrack(String albumName, String trackName, String imageUrl,
                        String trackId, String trackUrl) {
        this.setAlbumName(albumName);
        this.setTrackName(trackName);
        this.setImageUrl(imageUrl);
        this.setTrackId(trackId);
        this.setTrackUrl(trackUrl);
    }


    public SpotifyTrack(Track spotifyTrack, int imageSize) {
        this(spotifyTrack.album.name, spotifyTrack.name,
                SpotifyApiUtility.findImageUrl(spotifyTrack.album.images, imageSize),
                spotifyTrack.id, spotifyTrack.preview_url);
    }


    public SpotifyTrack(Track spotifyTrack) {
        this(spotifyTrack, DEFAULT_IMAGE_SIZE);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.albumName);
        dest.writeString(this.trackName);
        dest.writeString(this.imageUrl);
        dest.writeString(this.trackId);
        dest.writeString(this.trackUrl);
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public SpotifyTrack createFromParcel(Parcel source) {
            return new SpotifyTrack(source.readString(), source.readString(),
                    source.readString(), source.readString(), source.readString());
        }

        @Override
        public SpotifyTrack[] newArray(int size) { return new SpotifyTrack[size]; }
    };

    public int describeContents() { return 0; }

    public String getTrackId() { return this.trackId; }

    public void setTrackId(String trackId) { this.trackId = trackId; }

    public String getTrackUrl() { return this.trackUrl; }

    public void setTrackUrl(String trackUrl) { this.trackUrl = trackUrl; }

    public String getAlbumName() { return albumName; }

    public void setAlbumName(String albumName) { this.albumName = albumName; }

    public String getTrackName() { return trackName; }

    public void setTrackName(String trackName) { this.trackName = trackName; }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

}
