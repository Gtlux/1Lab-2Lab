package com.foodbooking.mobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.foodbooking.mobile.R;
import com.foodbooking.mobile.api.ApiClient;
import com.foodbooking.mobile.models.LoginResponse;
import com.foodbooking.mobile.models.RegisterRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, fullNameEditText;
    private EditText phoneEditText, passwordEditText, confirmPasswordEditText;
    private RadioGroup roleRadioGroup;
    private RadioButton clientRadio, driverRadio;
    private Button registerButton;
    private TextView errorTextView, loginLinkText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        fullNameEditText = findViewById(R.id.fullNameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        roleRadioGroup = findViewById(R.id.roleRadioGroup);
        clientRadio = findViewById(R.id.clientRadio);
        driverRadio = findViewById(R.id.driverRadio);
        registerButton = findViewById(R.id.registerButton);
        errorTextView = findViewById(R.id.errorTextView);
        loginLinkText = findViewById(R.id.loginLinkText);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRegistration();
            }
        });

        loginLinkText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void performRegistration() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String fullName = fullNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if (username.isEmpty() || email.isEmpty() || fullName.isEmpty() ||
                phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Prašome užpildyti visus laukus");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Slaptažodžiai nesutampa");
            return;
        }

        if (password.length() < 6) {
            showError("Slaptažodis turi būti bent 6 simbolių");
            return;
        }

        String role = clientRadio.isChecked() ? "CLIENT" : "DRIVER";

        registerButton.setEnabled(false);
        registerButton.setText("Registruojamasi...");

        RegisterRequest request = new RegisterRequest(
                username, password, email, fullName, phone, role
        );

        ApiClient.getApiService().register(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                registerButton.setEnabled(true);
                registerButton.setText("Registruotis");

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse registerResponse = response.body();

                    if (registerResponse.isSuccess()) {
                        Toast.makeText(RegisterActivity.this,
                                "Registracija sėkminga! Galite prisijungti.",
                                Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        showError(registerResponse.getMessage());
                    }
                } else {
                    showError("Registracija nepavyko");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                registerButton.setEnabled(true);
                registerButton.setText("Registruotis");
                showError("Klaida: " + t.getMessage());
                Toast.makeText(RegisterActivity.this,
                        "Patikrinkite ar serveris veikia",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showError(String message) {
        errorTextView.setText(message);
        errorTextView.setVisibility(View.VISIBLE);
    }
}
