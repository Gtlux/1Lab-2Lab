package com.foodbooking.mobile.activities;

import android.content.Intent;
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
import com.foodbooking.mobile.models.Restaurant;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private Button logoutButton;
    private Button cartButton;

    private int userId;
    private String userName;

    private List<Restaurant> restaurants = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        // Restore from savedInstanceState if available (back button case)
        if (savedInstanceState != null) {
            userId = savedInstanceState.getInt("userId", 0);
            userName = savedInstanceState.getString("userName");
            android.util.Log.d("ClientActivity", "Restored from savedInstanceState - userId: " + userId);
        } else {
            // Get from Intent (first time)
            userId = getIntent().getIntExtra("userId", 0);
            userName = getIntent().getStringExtra("userName");
            android.util.Log.d("ClientActivity", "Got from Intent - userId: " + userId);
        }

        // Debug logging
        android.util.Log.d("ClientActivity", "Logged in with userId: " + userId + ", userName: " + userName);

        welcomeTextView = findViewById(R.id.welcomeTextView);
        tabLayout = findViewById(R.id.tabLayout);
        recyclerView = findViewById(R.id.recyclerView);
        logoutButton = findViewById(R.id.logoutButton);
        cartButton = findViewById(R.id.cartButton);

        welcomeTextView.setText("Sveiki, " + userName + "!");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tabLayout.addTab(tabLayout.newTab().setText("Restoranai"));
        tabLayout.addTab(tabLayout.newTab().setText("Mano užsakymai"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    loadRestaurants();
                } else {
                    loadOrders();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClientActivity.this, ShoppingCartActivity.class);
                intent.putExtra("clientId", userId);
                startActivity(intent);
            }
        });

        loadRestaurants();
    }

    private void loadRestaurants() {
        ApiClient.getApiService().getRestaurants().enqueue(new Callback<ApiResponse<List<Restaurant>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Restaurant>>> call,
                                   Response<ApiResponse<List<Restaurant>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    restaurants = response.body().getData();
                    if (restaurants != null && !restaurants.isEmpty()) {
                        recyclerView.setAdapter(new RestaurantAdapter(restaurants));
                        Toast.makeText(ClientActivity.this,
                                "Užkrauta " + restaurants.size() + " restoranų",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ClientActivity.this,
                                "Restoranų sąrašas tuščias",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ClientActivity.this,
                            "Klaida: " + response.code() + " " + response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Restaurant>>> call, Throwable t) {
                Toast.makeText(ClientActivity.this,
                        "Nepavyko užkrauti restoranų: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void loadOrders() {
        ApiClient.getApiService().getClientOrders(userId).enqueue(new Callback<ApiResponse<List<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Order>>> call,
                                   Response<ApiResponse<List<Order>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orders = response.body().getData();
                    if (orders != null && !orders.isEmpty()) {
                        recyclerView.setAdapter(new OrderAdapter(orders));
                        Toast.makeText(ClientActivity.this,
                                "Užkrauta " + orders.size() + " užsakymų",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        recyclerView.setAdapter(new OrderAdapter(new ArrayList<>()));
                        Toast.makeText(ClientActivity.this,
                                "Užsakymų sąrašas tuščias",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ClientActivity.this,
                            "Klaida: " + response.code() + " " + response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {
                Toast.makeText(ClientActivity.this,
                        "Nepavyko užkrauti užsakymų: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {
        private List<Restaurant> items;

        RestaurantAdapter(List<Restaurant> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_restaurant, parent, false);
            return new RestaurantViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
            Restaurant restaurant = items.get(position);
            holder.nameText.setText(restaurant.getName());
            holder.addressText.setText(restaurant.getAddress());
            holder.phoneText.setText(restaurant.getPhoneNumber());

            holder.viewMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ClientActivity.this, MenuActivity.class);
                    intent.putExtra("restaurantId", restaurant.getId());
                    intent.putExtra("restaurantName", restaurant.getName());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, addressText, phoneText;
        Button viewMenuButton;

        RestaurantViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.restaurantName);
            addressText = itemView.findViewById(R.id.restaurantAddress);
            phoneText = itemView.findViewById(R.id.restaurantPhone);
            viewMenuButton = itemView.findViewById(R.id.viewMenuButton);
        }
    }

    class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {
        private List<Order> items;

        OrderAdapter(List<Order> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order, parent, false);
            return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
            Order order = items.get(position);
            holder.orderIdText.setText("Užsakymas #" + order.getId());
            holder.restaurantText.setText("Restoranas: " + order.getRestaurantName());
            holder.addressText.setText("Adresas: " + order.getDeliveryAddress());
            holder.statusText.setText("Būsena: " + order.getStatus());
            holder.totalText.setText("Suma: €" + order.getTotalAmount());
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdText, restaurantText, addressText, statusText, totalText;

        OrderViewHolder(View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.orderIdText);
            restaurantText = itemView.findViewById(R.id.restaurantText);
            addressText = itemView.findViewById(R.id.addressText);
            statusText = itemView.findViewById(R.id.statusText);
            totalText = itemView.findViewById(R.id.totalText);
        }
    }
}
