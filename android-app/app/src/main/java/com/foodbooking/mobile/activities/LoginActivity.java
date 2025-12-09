package com.foodbooking.mobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.foodbooking.mobile.R;
import com.foodbooking.mobile.api.ApiClient;
import com.foodbooking.mobile.models.LoginRequest;
import com.foodbooking.mobile.models.LoginResponse;
import com.foodbooking.mobile.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        errorTextView = findViewById(R.id.errorTextView);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Užpildykite visus laukus");
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Jungiamasi...");

        LoginRequest request = new LoginRequest(username, password);

        ApiClient.getApiService().login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                loginButton.setEnabled(true);
                loginButton.setText("Prisijungti");

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse.isSuccess()) {
                        User user = loginResponse.getUser();
                        Toast.makeText(LoginActivity.this,
                                "Sveiki, " + user.getFullName() + "!",
                                Toast.LENGTH_SHORT).show();

                        Intent intent;
                        if ("CLIENT".equals(user.getRole())) {
                            intent = new Intent(LoginActivity.this, ClientActivity.class);
                        } else if ("DRIVER".equals(user.getRole())) {
                            intent = new Intent(LoginActivity.this, DriverActivity.class);
                        } else {
                            showError("Tik klientai ir vairuotojai gali naudotis šia programa");
                            return;
                        }

                        intent.putExtra("userId", user.getId());
                        intent.putExtra("userName", user.getFullName());
                        intent.putExtra("userRole", user.getRole());
                        startActivity(intent);
                        finish();
                    } else {
                        showError(loginResponse.getMessage());
                    }
                } else {
                    showError("Prisijungimas nepavyko");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginButton.setEnabled(true);
                loginButton.setText("Prisijungti");
                showError("Klaida: " + t.getMessage());
                Toast.makeText(LoginActivity.this,
                        "Patikrinkite ar serveris veikia (http://10.0.2.2:8080)",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showError(String message) {
        errorTextView.setText(message);
        errorTextView.setVisibility(View.VISIBLE);
    }
}
