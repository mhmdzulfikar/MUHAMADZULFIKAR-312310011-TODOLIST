package com.example.uastugas;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    private Switch themeSwitch;
    private Spinner languageSpinner;

    private static final String PREFS_NAME = "settings_prefs";
    private static final String DARK_MODE_KEY = "dark_mode";
    private static final String LANGUAGE_KEY = "language";

    private boolean isSpinnerInitialized = false; // Flag untuk mencegah looping saat spinner diinisialisasi

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Inisialisasi komponen
        themeSwitch = view.findViewById(R.id.theme_switch);
        languageSpinner = view.findViewById(R.id.language_spinner);

        // Muat SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Konfigurasi tema
        boolean isDarkMode = prefs.getBoolean(DARK_MODE_KEY, false);
        themeSwitch.setChecked(isDarkMode);
        AppCompatDelegate.setDefaultNightMode(isDarkMode
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);

        // Listener untuk Switch Tema
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(DARK_MODE_KEY, isChecked);
            editor.apply();

            AppCompatDelegate.setDefaultNightMode(isChecked
                    ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_NO);

            Toast.makeText(requireContext(), isChecked ? "Dark Mode Enabled" : "Light Mode Enabled", Toast.LENGTH_SHORT).show();
        });

        // Konfigurasi Spinner Bahasa
        String[] languages = {"English", "Bahasa Indonesia"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Set Spinner ke bahasa yang tersimpan
        String savedLanguage = prefs.getString(LANGUAGE_KEY, "en");
        languageSpinner.setSelection(savedLanguage.equals("id") ? 1 : 0);

        // Listener untuk Spinner Bahasa
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (!isSpinnerInitialized) {
                    isSpinnerInitialized = true;
                    return; // Abaikan event pertama kali (saat spinner diinisialisasi)
                }

                String selectedLanguage = position == 0 ? "en" : "id";

                // Cek apakah bahasa dipilih ulang
                if (!selectedLanguage.equals(prefs.getString(LANGUAGE_KEY, "en"))) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(LANGUAGE_KEY, selectedLanguage);
                    editor.apply();

                    changeLanguage(selectedLanguage);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Tidak ada tindakan
            }
        });

        return view;
    }

    private void changeLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);

        requireContext().getResources().updateConfiguration(config, requireContext().getResources().getDisplayMetrics());
        Toast.makeText(requireContext(), "Language Changed to " + (languageCode.equals("id") ? "Bahasa Indonesia" : "English"), Toast.LENGTH_SHORT).show();

        // Restart activity untuk menerapkan perubahan bahasa
        getActivity().recreate();
    }
}
