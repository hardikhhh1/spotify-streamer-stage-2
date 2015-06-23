package com.hardikarora.spotify_1.activity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.hardikarora.spotify_1.R;
import com.hardikarora.spotify_1.model.SpotifyTrack;
import com.hardikarora.spotify_1.model.SpotifyTrackComponent;
import com.hardikarora.spotify_1.util.SpotifyPlayerService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A placeholder fragment containing a simple view.
 */
public class TrackPlayerDialogFragment extends DialogFragment implements View.OnClickListener{

    public static final String TAG = TrackPlayerDialogFragment.class.getSimpleName();
    public static final String LOG_TAG = TrackPlayerDialogFragment.class.getSimpleName();
    public static final String TRACK_SUBJECT_MESSAGE = "A great song indeed !!";
    public static final String PLAY_ACTION = "Play";
    public static final String NEXT_ACTION = "Next";
    public static final String PREVIOUS_ACTION = "Previous";
    public static final String SPOTIFY_STREAMER_TICKER = "Spotify Streamer";
    public static final String PLAYER_PREFERENCE = "playerPreference";
    public static final String PLAYER_PREFERENCE_KEY = PLAYER_PREFERENCE;

    private PlayerViewHolder playerViewHolder;
    private ShareActionProvider mShareActionProvider;
    private SpotifyPlayerService spotifyPlayerService;

    List<SpotifyTrackComponent> spotifyTrackList; // List of spotify tracks.
    int trackIndex;

    private BroadcastReceiver playerReciever;
    Notification playerNotification;

    // A service connection to connect to the spotify api service.
    public ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            spotifyPlayerService = ((SpotifyPlayerService.SpotifyBinder)service).getService();
            spotifyPlayerService.spotifyTrackList = spotifyTrackList;
            spotifyPlayerService.trackIndex = trackIndex;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            spotifyPlayerService = null;
        }
    };

    public static TrackPlayerDialogFragment newInstance(){
        return new TrackPlayerDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(spotifyPlayerService != null) return;
        // Initiate the service.
        Intent intent = new Intent(getActivity(), SpotifyPlayerService.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        getActivity().startService(intent);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item = menu.findItem(R.id.menu_item_share);
        if(item == null) return;
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        if(mShareActionProvider == null) return;
        //TODO: add a spotify as extra text.
        Intent shareButtonIntent = new Intent(Intent.ACTION_SEND);
        shareButtonIntent.setType("text/plain");
        String shareString = spotifyTrackList.get(trackIndex).getTrackUrl();
        shareButtonIntent.putExtra(Intent.EXTRA_SUBJECT, TRACK_SUBJECT_MESSAGE);
        shareButtonIntent.putExtra(Intent.EXTRA_TEXT, shareString);
        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(shareButtonIntent);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }



    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "On pause for player fragment called");

        // Check the user preference.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean notificationPreference = preferences.getBoolean(PLAYER_PREFERENCE_KEY, true);

        if(!notificationPreference) return;

        try {
            if(playerNotification == null){
                buildNotification();
            }
            NotificationManager nm = (NotificationManager) getActivity()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(1, playerNotification);
        }
        catch (Exception e){
            Log.e(LOG_TAG,  "Error while getting log for the player." + e.getMessage() +
                    e.getStackTrace());
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "On resume for player fragment called");
        if(playerNotification == null) return;

        // Cancelling the notification.
        NotificationManager nm = (NotificationManager) getActivity()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(1);
    }

    private void buildNotification(){
        // Initiating the pending intents for playback controls.
        PendingIntent playPendingIntent = initiateSpotifyPendingIntent(getActivity(), PLAY_ACTION);
        PendingIntent previousPendingIntent = initiateSpotifyPendingIntent(getActivity(), PREVIOUS_ACTION);
        PendingIntent nextPendingIntent = initiateSpotifyPendingIntent(getActivity(), NEXT_ACTION);

        Notification.Builder builder = new Notification.Builder(getActivity());
        RemoteViews notificationView =
                new RemoteViews(getActivity().getPackageName(), R.layout.notification_player);

        notificationView.setOnClickPendingIntent(R.id.notification_play_btn_img,
                playPendingIntent);
        notificationView.setOnClickPendingIntent(R.id.notification_prev_btn_img,
                previousPendingIntent);
        notificationView.setOnClickPendingIntent(R.id.notification_next_btn_img,
                nextPendingIntent);

        builder.setContent(notificationView)
                .setSmallIcon(R.drawable.ic_skip_next_black_24dp)
                .setOngoing(true)
                .setTicker(SPOTIFY_STREAMER_TICKER);

        playerNotification = builder.build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "On destroy for player fragment called");

        // Canceling the notification.
        if(playerNotification != null) {
            NotificationManager nm = (NotificationManager) getActivity()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(1);
        }

        // Unregistering the reciever.
        getActivity().unregisterReceiver(this.playerReciever);

        // Unbinding and clearing the service.
        getActivity().unbindService(connection);
        Intent intent = new Intent(getActivity(), SpotifyPlayerService.class);
        getActivity().stopService(intent);

    }

    private PendingIntent initiateSpotifyPendingIntent(Context context, String action){
        Intent intent = new Intent(context, SpotifyPlayerService.class).setAction(
                action);
        PendingIntent pendingIntent = PendingIntent.getService(getActivity(), 0,
                intent, 0);
        return pendingIntent;
    }

    @Override
    public void onClick(View v) {
        // If the fragment is clicked we check for the click we check for the click and
        // take action accordingly.
        int trackIndex = -1;
        switch (v.getId()) {
            case R.id.player_play_btn_img:
                // If the play button is clicked.
                Log.d(LOG_TAG, "Play button has been clicked.");
                trackIndex = spotifyPlayerService.playButtonPressed();
                break;

            case R.id.player_next_btn_img:
                // If the next button is clicked.
                Log.d(LOG_TAG, "Next song button pressed.");
                spotifyPlayerService.nextTrack();
                trackIndex = spotifyPlayerService.nextTrack();
                break;

            case R.id.player_prev_btn_img:
                Log.d(LOG_TAG, "Previous song button pressed.");
                spotifyPlayerService.previousTrack();
                trackIndex = spotifyPlayerService.previousTrack();
                break;
        }

        if(trackIndex != -1){
            // If any button was clicked and track has chaned, we have to update the
            // view objects accordingly.
            playerViewHolder.setViewObjects(getView(),(SpotifyTrack) spotifyTrackList.get(trackIndex));
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Creating the fragment for media player.");
        View rootView = inflater.inflate(R.layout.fragment_song_play, container, false);

        // If root view itself is null, we return null.
        if(rootView == null) return null;


        playerViewHolder = new PlayerViewHolder(rootView);
        rootView.setTag(playerViewHolder);

        //Setting itself as an on click listener for various play back controls.
        setOnClickForImage(playerViewHolder.playButtonImage);
        setOnClickForImage(playerViewHolder.previousButtonImage);
        setOnClickForImage( playerViewHolder.nextButtonImage);

        // If no track id has been set we return the root view.
        spotifyTrackList = (ArrayList<SpotifyTrackComponent>)(ArrayList<?>)
                getArguments().getParcelableArrayList(TrackListFragment.TRACK_LIST_TAG);
        trackIndex = getArguments().getInt(TrackListFragment.TRACK_INDEX_TAG);

        // Setting the view objects with respect to the track.
        playerViewHolder.setViewObjects(rootView, (SpotifyTrack) spotifyTrackList.get(trackIndex));

        // Setting the share intent for share button.
        setHasOptionsMenu(true);

        initiateReciever();

        getActivity().registerReceiver(this.playerReciever, new IntentFilter(SpotifyPlayerService.SONG_FINISHED_EVENT));

        return rootView;
    }

    private void initiateReciever(){
        playerReciever = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(LOG_TAG, "The track is over, playing next track.");
                String action = intent.getAction();
                if(action == null) return;

                if(!action.equals(SpotifyPlayerService.SONG_FINISHED_EVENT )) return;

                int serviceTrackIndex = intent.getIntExtra(TrackListFragment.TRACK_INDEX_TAG, -1);

                if(serviceTrackIndex == -1) return;

                trackIndex = serviceTrackIndex;
                playerViewHolder.setViewObjects(getView(), (SpotifyTrack) spotifyTrackList.get(trackIndex));
            }

        };
    }

    private void setOnClickForImage(ImageView image){
        if(image != null)
            image.setOnClickListener(this);
    }



    static class PlayerViewHolder{

        @InjectView(R.id.player_play_btn_img) ImageView playButtonImage;
        @InjectView(R.id.player_next_btn_img) ImageView nextButtonImage;
        @InjectView(R.id.player_prev_btn_img) ImageView previousButtonImage;
        @InjectView(R.id.player_album_image) ImageView albumImageView;
        @InjectView(R.id.player_album_name_textview) TextView albumNameTextView;
        @InjectView(R.id.player_artist_name_textview) TextView artistNameTextView;
        @InjectView(R.id.player_track_name_textview) TextView trackNameTextView;


        public PlayerViewHolder(View view){
            ButterKnife.inject(this, view);
        }

        public void setViewObjects(View view, SpotifyTrack spotifyTrack)
        {
            if(albumNameTextView != null) albumNameTextView.setText(spotifyTrack.getAlbumName());
            if(artistNameTextView != null) artistNameTextView.setText(spotifyTrack.getArtistName());
            if(trackNameTextView != null) trackNameTextView.setText(spotifyTrack.getTrackName());
            if(albumImageView != null){
                // Load image with picasso.
                Log.d(LOG_TAG, "Loading the background image with picasso.");
                Picasso.with(view.getContext()).load(spotifyTrack.getImageUrl()).into(albumImageView);
            } else{
                Log.e(LOG_TAG, "Image view is null");
            }
        }
    }
}
