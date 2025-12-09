package com.foodbooking.mobile.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodbooking.mobile.R;
import com.foodbooking.mobile.api.ApiClient;
import com.foodbooking.mobile.models.ApiResponse;
import com.foodbooking.mobile.models.Order;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private RecyclerView ordersRecyclerView;
    private Button refreshButton;
    private Button logoutButton;

    private int userId;
    private String userName;

    private List<Order> orders = new ArrayList<>();
    private DriverOrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);

        userId = getIntent().getIntExtra("userId", 0);
        userName = getIntent().getStringExtra("userName");

        welcomeTextView = findViewById(R.id.welcomeTextView);
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        refreshButton = findViewById(R.id.refreshButton);
        logoutButton = findViewById(R.id.logoutButton);

        welcomeTextView.setText("Sveiki, " + userName + "!");

        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DriverOrderAdapter(orders);
        ordersRecyclerView.setAdapter(adapter);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadOrders();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadOrders();
    }

    private void loadOrders() {
        refreshButton.setEnabled(false);
        refreshButton.setText("Kraunama...");

        ApiClient.getApiService().getDriverOrders(userId).enqueue(new Callback<ApiResponse<List<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Order>>> call,
                                   Response<ApiResponse<List<Order>>> response) {
                refreshButton.setEnabled(true);
                refreshButton.setText("Atnaujinti");

                if (response.isSuccessful() && response.body() != null) {
                    orders.clear();
                    orders.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                    Toast.makeText(DriverActivity.this,
                            "Rasta " + orders.size() + " užsakymų",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {
                refreshButton.setEnabled(true);
                refreshButton.setText("Atnaujinti");
                Toast.makeText(DriverActivity.this,
                        "Nepavyko užkrauti užsakymų: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    class DriverOrderAdapter extends RecyclerView.Adapter<DriverOrderViewHolder> {
        private List<Order> items;

        DriverOrderAdapter(List<Order> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public DriverOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order, parent, false);
            return new DriverOrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DriverOrderViewHolder holder, int position) {
            Order order = items.get(position);
            holder.orderIdText.setText("Užsakymas #" + order.getId());
            holder.restaurantText.setText("Restoranas: " + order.getRestaurantName());
            holder.addressText.setText("Adresas: " + order.getDeliveryAddress());
            holder.statusText.setText("Būsena: " + order.getStatus());
            holder.totalText.setText("Suma: €" + order.getTotalAmount());

            holder.updateButton.setVisibility(View.VISIBLE);
            holder.updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showStatusUpdateDialog(order);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    static class DriverOrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdText, restaurantText, addressText, statusText, totalText;
        Button updateButton;

        DriverOrderViewHolder(View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.orderIdText);
            restaurantText = itemView.findViewById(R.id.restaurantText);
            addressText = itemView.findViewById(R.id.addressText);
            statusText = itemView.findViewById(R.id.statusText);
            totalText = itemView.findViewById(R.id.totalText);
            updateButton = itemView.findViewById(R.id.updateStatusButton);
        }
    }

    private void showStatusUpdateDialog(Order order) {
        String[] statuses = {"PICKED_UP", "DELIVERING", "DELIVERED"};
        String[] displayNames = {"Paimta", "Vežama", "Pristatyta"};

        new AlertDialog.Builder(this)
                .setTitle("Atnaujinti užsakymo būseną")
                .setItems(displayNames, (dialog, which) -> {
                    updateOrderStatus(order.getId(), statuses[which]);
                })
                .setNegativeButton("Atšaukti", null)
                .show();
    }

    private void updateOrderStatus(int orderId, String newStatus) {
        ApiClient.getApiService().updateOrderStatus(orderId, newStatus)
                .enqueue(new Callback<ApiResponse<String>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<String>> call,
                                           Response<ApiResponse<String>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(DriverActivity.this,
                                    "Būsena atnaujinta!",
                                    Toast.LENGTH_SHORT).show();
                            loadOrders();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                        Toast.makeText(DriverActivity.this,
                                "Nepavyko atnaujinti būsenos: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
