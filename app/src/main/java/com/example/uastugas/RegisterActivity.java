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

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button registerButton;
    private TextView toLoginTextView;

    // SharedPreferences untuk menyimpan data pengguna
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Menghubungkan UI dengan elemen di XML
        emailEditText = findViewById(R.id.email_field);
        passwordEditText = findViewById(R.id.password_field);
        registerButton = findViewById(R.id.register_button);
        toLoginTextView = findViewById(R.id.to_login);

        // Inisialisasi SharedPreferences untuk menyimpan data pengguna
        sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);

        // Set listener untuk tombol register
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Validasi input
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6) { // Panjang password minimal
                    Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                } else {
                    // Menyimpan data akun ke SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.apply(); // Simpan data

                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                    // Pindah ke halaman login setelah registrasi berhasil
                    Intent intent = new Intent(RegisterActivity.this, login.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        // Menambahkan listener untuk TextView yang digunakan untuk berpindah ke halaman login
        toLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke halaman LoginActivity ketika TextView diklik
                Intent intent = new Intent(RegisterActivity.this, login.class);
                startActivity(intent);
            }
        });
    }
}
