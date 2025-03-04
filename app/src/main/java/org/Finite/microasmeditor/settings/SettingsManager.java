package org.Finite.microasmeditor.settings;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

public class SettingsManager {
    private static SettingsManager instance;
    private SharedPreferences prefs;

    private SettingsManager(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static synchronized SettingsManager getInstance(Context context) {
        if (instance == null) {
            instance = new SettingsManager(context.getApplicationContext());
        }
        return instance;
    }

    public boolean showLineNumbers() {
        return prefs.getBoolean("line_numbers", true);
    }

    public int getFontSize() {
        return Integer.parseInt(prefs.getString("font_size", "14"));
    }

    public boolean isDarkMode() {
        return prefs.getBoolean("dark_mode", false);
    }

    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
