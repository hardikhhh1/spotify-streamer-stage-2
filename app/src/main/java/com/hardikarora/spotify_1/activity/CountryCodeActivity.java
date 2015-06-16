package com.hardikarora.spotify_1.activity;

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
public class CountryCodeActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (hasHeaders()) {
            Button countryPreferenceButton = new Button(this);
            countryPreferenceButton.setText("Select country code");
            setListFooter(countryPreferenceButton);
        }

        PreferenceFragment preferenceFragment = new CountryCodeFragment();

        getFragmentManager().beginTransaction().replace(android.R.id.content, preferenceFragment)
                .commit();
    }

    public static class CountryCodeFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle saveBundleInstance) {
            super.onCreate(saveBundleInstance);
            PreferenceManager.setDefaultValues(getActivity(),
                    R.xml.advanced_preferences, false);
            addPreferencesFromResource(R.xml.advanced_preferences);
        }

    }
}