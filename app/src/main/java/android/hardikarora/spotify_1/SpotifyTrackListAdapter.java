package android.hardikarora.spotify_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by hardikarora on 6/3/15.
 */
public class SpotifyTrackListAdapter extends ArrayAdapter<Track> {

    private List<Track> artistTracks;
    private int textViewResourceId;

    public SpotifyTrackListAdapter(Context context, int resource, int textViewResourceId,
                                   List<Track> artistTracks) {
        super(context, resource, textViewResourceId, artistTracks);
        this.textViewResourceId = textViewResourceId;
        this.artistTracks = artistTracks;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater)getContext().
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.list_item_spotify, null);

        }

        Track track = artistTracks.get(position);


        if(track != null){
            TextView trackTextView = (TextView) view.findViewById(R.id.spotify_item_textview);
            trackTextView.setText(track.name);
            Image image = track.album.images.get(0);
            String imageUrl = image.url;

            ImageView imageView = (ImageView) view.findViewById(R.id.spotify_album_image);
            if(imageUrl != null) {
                Picasso.with(getContext()).load(imageUrl).into(imageView);
            }
        }
        return view;
    }
}
