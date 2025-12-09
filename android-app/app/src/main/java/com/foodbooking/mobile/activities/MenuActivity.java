package com.foodbooking.mobile.activities;

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
import com.foodbooking.mobile.models.MenuItem;
import com.foodbooking.mobile.util.ShoppingCart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity {

    private TextView restaurantNameText;
    private RecyclerView menuRecyclerView;
    private Button backButton;

    private int restaurantId;
    private String restaurantName;
    private List<MenuItem> menuItems = new ArrayList<>();
    private ShoppingCart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        restaurantId = getIntent().getIntExtra("restaurantId", 0);
        restaurantName = getIntent().getStringExtra("restaurantName");
        cart = ShoppingCart.getInstance();

        restaurantNameText = findViewById(R.id.restaurantNameText);
        menuRecyclerView = findViewById(R.id.menuRecyclerView);
        backButton = findViewById(R.id.backButton);

        restaurantNameText.setText(restaurantName);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadMenu();
    }

    private void loadMenu() {
        ApiClient.getApiService().getMenuByRestaurant(restaurantId)
                .enqueue(new Callback<ApiResponse<List<MenuItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<MenuItem>>> call,
                                   Response<ApiResponse<List<MenuItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    menuItems = response.body().getData();
                    for (MenuItem item : menuItems) {
                        item.setRestaurantId(restaurantId);
                        item.setRestaurantName(restaurantName);
                    }
                    menuRecyclerView.setAdapter(new MenuAdapter(menuItems));
                } else {
                    Toast.makeText(MenuActivity.this,
                            "Nepavyko užkrauti meniu",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<MenuItem>>> call, Throwable t) {
                Toast.makeText(MenuActivity.this,
                        "Klaida: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    class MenuAdapter extends RecyclerView.Adapter<MenuViewHolder> {
        private List<MenuItem> items;
        private Map<Integer, Integer> quantities = new HashMap<>();

        MenuAdapter(List<MenuItem> items) {
            this.items = items;
            for (MenuItem item : items) {
                quantities.put(item.getId(), 1);
            }
        }

        @NonNull
        @Override
        public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_menu_item, parent, false);
            return new MenuViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
            MenuItem menuItem = items.get(position);
            int quantity = quantities.get(menuItem.getId());

            holder.nameText.setText(menuItem.getName());
            holder.descriptionText.setText(menuItem.getDescription());
            holder.priceText.setText("€" + menuItem.getPrice());
            holder.quantityText.setText(String.valueOf(quantity));

            holder.incrementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentQty = quantities.get(menuItem.getId());
                    quantities.put(menuItem.getId(), currentQty + 1);
                    notifyItemChanged(holder.getAdapterPosition());
                }
            });

            holder.decrementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentQty = quantities.get(menuItem.getId());
                    if (currentQty > 1) {
                        quantities.put(menuItem.getId(), currentQty - 1);
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                }
            });

            holder.addToCartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int qty = quantities.get(menuItem.getId());
                        cart.addItem(menuItem, qty);
                        Toast.makeText(MenuActivity.this,
                                menuItem.getName() + " pridėtas į krepšelį (" + qty + " vnt.)",
                                Toast.LENGTH_SHORT).show();
                        quantities.put(menuItem.getId(), 1);
                        notifyItemChanged(holder.getAdapterPosition());
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(MenuActivity.this,
                                e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, descriptionText, priceText, quantityText;
        Button incrementButton, decrementButton, addToCartButton;

        MenuViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.menuItemName);
            descriptionText = itemView.findViewById(R.id.menuItemDescription);
            priceText = itemView.findViewById(R.id.menuItemPrice);
            quantityText = itemView.findViewById(R.id.quantityText);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }
    }
}
