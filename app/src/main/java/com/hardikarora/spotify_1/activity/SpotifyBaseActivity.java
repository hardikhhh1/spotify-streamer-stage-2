package com.hardikarora.spotify_1.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.hardikarora.spotify_1.R;
import com.hardikarora.spotify_1.menu.PreferenceMenuActivity;
import com.hardikarora.spotify_1.model.SpotifyTrack;
import com.hardikarora.spotify_1.model.SpotifyTrackComponent;
import com.hardikarora.spotify_1.service.SpotifyPlayerService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hardikarora on 6/24/15.
 */
public class SpotifyBaseActivity extends Activity {


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.menu_country_code:
                Intent intent = new Intent(this, PreferenceMenuActivity.class);
                startActivity(intent);
                return true;

            case R.id.now_playing_button:

                List<SpotifyTrackComponent> trackList = SpotifyPlayerService.spotifyTrackList;
                if(trackList == null) return true;
                if(trackList.size() == 0) return true;

                int trackIndex = SpotifyPlayerService.trackIndex;


                // TODO : Show as an dialog.
                Bundle arguments = new Bundle();
                arguments.putParcelableArrayList(TrackListFragment.TRACK_LIST_TAG,
                        (ArrayList<SpotifyTrack>)
                                (ArrayList<?>)trackList);
                arguments.putInt(TrackListFragment.TRACK_INDEX_TAG,
                        trackIndex);
                arguments.putString(TrackListFragment.SPOTIFY_TRACK_ID_TAG,
                        trackList.get(trackIndex).getTrackId());

                TrackPlayerDialogFragment fragment = TrackPlayerDialogFragment.newInstance();
                fragment.setArguments(arguments);

                getFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment, TrackPlayerDialogFragment.TAG)
                        .commit();


                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
