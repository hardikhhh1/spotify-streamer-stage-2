package com.hardikarora.spotify_1.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

import com.hardikarora.spotify_1.R;
import com.hardikarora.spotify_1.menu.SpotifyShareButton;
import com.hardikarora.spotify_1.service.SpotifyPlayerService;

public class SpotifyBaseFragment extends Fragment {

    MenuItem nowPlayingMenuItem;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        nowPlayingMenuItem = menu.findItem(R.id.now_playing_button);
        nowPlayingMenuItem.setVisible(false);
        SpotifyPlayerService.PlayerState state = SpotifyPlayerService.spotifyPlayerState;

        if(state == SpotifyPlayerService.PlayerState.Play){
            nowPlayingMenuItem.setVisible(true);

            SpotifyShareButton button =
                    new SpotifyShareButton(SpotifyPlayerService.nowPlayingSpotifyTrack.getExternalTrackUrl());
            button.showShareButton(menu);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check if now playing button is needed.
        if(nowPlayingMenuItem == null) return;
        SpotifyPlayerService.PlayerState state = SpotifyPlayerService.spotifyPlayerState;
        if(state == SpotifyPlayerService.PlayerState.Play){
            nowPlayingMenuItem.setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
