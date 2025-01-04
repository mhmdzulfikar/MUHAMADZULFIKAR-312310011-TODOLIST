package com.example.uastugas;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private TextView emailTextView;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi SharedPreferences
        preferences = getSharedPreferences("userPrefs", MODE_PRIVATE);

        // Setup Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Remove title from Toolbar/ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        // Setup DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Mendapatkan emailTextView dari header NavigationView
        View headerView = navigationView.getHeaderView(0);
        emailTextView = headerView.findViewById(R.id.emailTextView);

        // Setup Drawer Toggle (for the menu button in the Toolbar)
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set email to the TextView if user is logged in
        String userEmail = preferences.getString("userEmail", null);
        if (userEmail != null) {
            emailTextView.setText(userEmail);
        } else {
            emailTextView.setText("Login to save data");
        }

        // Load the first fragment if no saved instance exists
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.home_page);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_page:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
                break;

            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SettingsFragment())
                        .commit();
                break;

            case R.id.nav_about:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AboutFragment())
                        .commit();
                break;

            case R.id.to_login:
                Intent intent = new Intent(this, login.class);
                startActivityForResult(intent, 1);  // Memulai login activity untuk menerima hasil login
                break;

            case R.id.nav_logout:
                // Menampilkan dialog konfirmasi logout
                new AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to logout?")
                        .setCancelable(false)  // Tidak bisa dibatalkan dengan mengetuk di luar dialog
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Jika klik OK, logout
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.remove("userEmail");  // Menghapus email dari SharedPreferences saat logout
                                editor.apply();

                                Toast.makeText(getApplicationContext(), "logout was successful", Toast.LENGTH_SHORT).show();
                                emailTextView.setText("Login to save data");  // Reset TextView ke default
                            }
                        })
                        .setNegativeButton("Cancel", null)  // Jika klik Cancel, tidak ada aksi
                        .show();
                break;

        }

        drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
            drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String userEmail = data.getStringExtra("userEmail");

            if (userEmail != null) {
                emailTextView.setText(userEmail);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("userEmail", userEmail);  // Simpan email yang baru saja login
                editor.apply();
            }
        }
    }
}
