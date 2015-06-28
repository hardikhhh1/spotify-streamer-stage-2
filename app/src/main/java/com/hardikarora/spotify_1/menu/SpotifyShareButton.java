package com.hardikarora.spotify_1.menu;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

import com.hardikarora.spotify_1.R;

/**
 * Created by hardikarora on 6/27/15.
 */
public class SpotifyShareButton {

    private ShareActionProvider mShareActionProvider;
    public static final String TRACK_SUBJECT_MESSAGE = "A great song indeed !!";

    private String externalTrackUrl;

    public SpotifyShareButton(String externalTrackUrl){
        this.externalTrackUrl = externalTrackUrl;
    }

    public void showShareButton(Menu menu){
        MenuItem item = menu.findItem(R.id.menu_item_share);
        if(item == null) return;
        item.setVisible(true);
        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        if(mShareActionProvider == null) return;
        //TODO: add a spotify as extra text.
        Intent shareButtonIntent = new Intent(Intent.ACTION_SEND);
        shareButtonIntent.setType("text/plain");
        shareButtonIntent.putExtra(Intent.EXTRA_SUBJECT, TRACK_SUBJECT_MESSAGE);
        shareButtonIntent.putExtra(Intent.EXTRA_TEXT,this.externalTrackUrl);
        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(shareButtonIntent);
        }

    }

}
