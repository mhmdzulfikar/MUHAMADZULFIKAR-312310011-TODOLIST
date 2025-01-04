package com.example.uastugas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class login extends AppCompatActivity {

    private Button loginButton;
    private EditText emailEditText, passwordEditText;
    private TextView toRegisterTextView;

    // SharedPreferences untuk menyimpan data pengguna
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Menghubungkan UI dengan elemen di XML
        loginButton = findViewById(R.id.login_button);
        emailEditText = findViewById(R.id.email_field);
        passwordEditText = findViewById(R.id.password_field);
        toRegisterTextView = findViewById(R.id.to_register);

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);

        // Listener untuk tombol login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Validasi input
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(login.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6) {
                    Toast.makeText(login.this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                } else {
                    // Ambil data dari SharedPreferences
                    String savedEmail = sharedPreferences.getString("email", null);
                    String savedPassword = sharedPreferences.getString("password", null);

                    // Periksa apakah data cocok
                    if (savedEmail != null && savedPassword != null) {
                        if (email.equals(savedEmail) && password.equals(savedPassword)) {
                            // Login berhasil
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("userEmail", email);  // Mengirimkan email ke MainActivity
                            setResult(RESULT_OK, resultIntent);
                            finish(); // Kembali ke MainActivity
                            Toast.makeText(getApplicationContext(), "Login was successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(login.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(login.this, "You need to register first", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(login.this, RegisterActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        // Listener untuk TextView "Don't have an account? Sign up"
        toRegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
