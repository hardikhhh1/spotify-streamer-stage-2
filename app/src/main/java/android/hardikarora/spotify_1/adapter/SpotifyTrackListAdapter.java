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
    private int resource;
    private final int ALBUM_NAME_TEXTVIEW_ID = R.id.spotify_album_textview ;
    private final int ALBUM_IMAGE_ID = R.id.spotify_album_image ;
    private final int TRACK_NAME_TEXTVIEW_ID = R.id.spotify_trackname_textview;


    public SpotifyTrackListAdapter(Context context, int resource, int textViewResourceId,
                                   List<SpotifyTrackComponent> artistTracks) {
        super(context, resource, textViewResourceId, artistTracks);
        this.artistTracks = artistTracks;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        TrackViewHolder trackViewHolder;
        LayoutInflater inflater = (LayoutInflater)getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(view == null){
            view = inflater.inflate(resource, null);
            trackViewHolder = new TrackViewHolder();
            trackViewHolder.albumTextView = (TextView) view.findViewById(ALBUM_NAME_TEXTVIEW_ID);
            trackViewHolder.trackTextView = (TextView) view.findViewById(TRACK_NAME_TEXTVIEW_ID);
            trackViewHolder.imageView = (ImageView) view.findViewById(ALBUM_IMAGE_ID);
            view.setTag(trackViewHolder);
        } else {
            trackViewHolder = (TrackViewHolder) view.getTag();
        }

        SpotifyTrackComponent track = artistTracks.get(position);
        if(track == null) return view;

        trackViewHolder.albumTextView.setText(track.getAlbumName());
        trackViewHolder.trackTextView.setText(track.getTrackName());
        String imageUrl = track.getImageUrl();
        if(trackViewHolder != null && trackViewHolder.imageView != null) {
            Picasso.with(getContext()).load(imageUrl)
                    .into(trackViewHolder.imageView);
        }

        return view;
    }

    private class TrackViewHolder{

        TextView albumTextView;
        TextView trackTextView;
        ImageView imageView;

    }
}
