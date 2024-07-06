package com.example.myapplication2;

import android.app.AlertDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication2.api.ApiConfig;
import com.example.myapplication2.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    EditText username, password;
    Button btnlogin;
    String keynama, keypass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.editekuser);
        password = findViewById(R.id.editekpassword);
        btnlogin = findViewById(R.id.tombollogin);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keynama = username.getText().toString();
                keypass = password.getText().toString();

                ApiConfig.getRetrofitClient().getAllUser().enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            boolean loginSuccessful = false;
                            for (User user : response.body()) {
                                if (keynama.equals(user.getUsername()) && keypass.equals("password")) {
                                    loginSuccessful = true;
                                    break;
                                }
                            }
                            if (loginSuccessful) {
                                // jika login berhasil
                                Toast.makeText(getApplicationContext(), "LOGIN BERHASIL", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(Login.this, Home.class);
                                intent.putExtra("username", keynama);
                                Login.this.startActivity(intent);
                                finish();
                            } else {
                                // jika login gagal
                                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                                builder.setMessage("Username atau password salah")
                                        .setNegativeButton("Ulangi", null)
                                        .create().show();

                                username.setText("");
                                password.setText("");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<User>> call, Throwable throwable) {
                        // Handle failure
                        Toast.makeText(getApplicationContext(), "Gagal terhubung ke server", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
