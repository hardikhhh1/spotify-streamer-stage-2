package android.hardikarora.spotify_1.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hardikarora on 6/5/15.
 *
 * Class representing the spotify track model.
 */
public class SpotifyTrack extends SpotifyTrackComponent implements Parcelable {


    private String trackName;
    private String albumName;
    private String imageUrl;

    public SpotifyTrack(String albumName, String trackName, String imageUrl) {
        this.setAlbumName(albumName);
        this.setTrackName(trackName);
        this.setImageUrl(imageUrl);
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.albumName);
        dest.writeString(this.trackName);
        dest.writeString(this.imageUrl);
    }

    private static Creator TrackCreator = new Creator() {
        @Override
        public SpotifyTrack createFromParcel(Parcel source) {
            return new SpotifyTrack(source.readString(), source.readString(),
                    source.readString());
        }

        @Override
        public SpotifyTrack[] newArray(int size) { return new SpotifyTrack[size]; }
    };

    public String getAlbumName() { return albumName; }

    public void setAlbumName(String albumName) { this.albumName = albumName; }

    public String getTrackName() { return trackName; }

    public void setTrackName(String trackName) { this.trackName = trackName; }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

}
