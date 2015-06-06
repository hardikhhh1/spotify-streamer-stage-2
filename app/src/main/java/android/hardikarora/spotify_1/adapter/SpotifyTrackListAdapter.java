package android.hardikarora.spotify_1.adapter;

import android.content.Context;
import android.hardikarora.spotify_1.R;
import android.hardikarora.spotify_1.model.SpotifyTrackComponent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by hardikarora on 6/3/15.
 */
public class SpotifyTrackListAdapter extends ArrayAdapter<SpotifyTrackComponent> {

    private List<SpotifyTrackComponent> artistTracks;

    public SpotifyTrackListAdapter(Context context, int resource, int textViewResourceId,
                                   List<SpotifyTrackComponent> artistTracks) {
        super(context, resource, textViewResourceId, artistTracks);
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

        SpotifyTrackComponent track = artistTracks.get(position);


        if(track != null){
            TextView trackTextView = (TextView) view.findViewById(R.id.spotify_item_textview);
            trackTextView.setText(track.getAlbumName());
            String imageUrl = track.getImageUrl();

            ImageView imageView = (ImageView) view.findViewById(R.id.spotify_album_image);
            if(imageUrl != null) {
                Picasso.with(getContext()).load(imageUrl).into(imageView);
            }
        }
        return view;
    }
}
