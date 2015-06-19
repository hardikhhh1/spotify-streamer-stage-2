package com.hardikarora.spotify_1.menu;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Button;

import com.hardikarora.spotify_1.R;


/**
 * Class representing activity to set the country code
 * for the user.
 */
public class PreferenceMenuActivity extends PreferenceActivity {

    public static final String PREFERENCE_BUTTON_TEXT = "Preference";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasHeaders()) {
            Button countryPreferenceButton = new Button(this);
            countryPreferenceButton.setText(PREFERENCE_BUTTON_TEXT);
            setListFooter(countryPreferenceButton);
        }

        PreferenceFragment preferenceFragment = new PreferenceMenuFragment();

        getFragmentManager().beginTransaction().replace(android.R.id.content, preferenceFragment)
                .commit();
    }

    public static class PreferenceMenuFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle saveBundleInstance) {
            super.onCreate(saveBundleInstance);
            PreferenceManager.setDefaultValues(getActivity(),
                    R.xml.advanced_preferences, false);
            addPreferencesFromResource(R.xml.advanced_preferences);
        }

    }
}