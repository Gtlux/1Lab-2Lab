package com.foodbooking.mobile.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodbooking.mobile.R;
import com.foodbooking.mobile.api.ApiClient;
import com.foodbooking.mobile.models.*;
import com.foodbooking.mobile.util.ShoppingCart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingCartActivity extends AppCompatActivity {

    private TextView restaurantNameText, itemCountText, totalText;
    private RecyclerView cartRecyclerView;
    private EditText deliveryAddressEditText, notesEditText;
    private Button clearCartButton, checkoutButton;

    private ShoppingCart cart;
    private CartAdapter adapter;
    private int clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        clientId = getIntent().getIntExtra("clientId", 0);
        cart = ShoppingCart.getInstance();

        restaurantNameText = findViewById(R.id.restaurantNameText);
        itemCountText = findViewById(R.id.itemCountText);
        totalText = findViewById(R.id.totalText);
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        deliveryAddressEditText = findViewById(R.id.deliveryAddressEditText);
        notesEditText = findViewById(R.id.notesEditText);
        clearCartButton = findViewById(R.id.clearCartButton);
        checkoutButton = findViewById(R.id.checkoutButton);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(cart.getItems());
        cartRecyclerView.setAdapter(adapter);

        clearCartButton.setOnClickListener(v -> clearCart());
        checkoutButton.setOnClickListener(v -> checkout());

        updateUI();
    }

    private void updateUI() {
        if (cart.getRestaurant() != null) {
            restaurantNameText.setText("Restoranas: " + cart.getRestaurant().getName());
        } else {
            restaurantNameText.setText("Krepšelis tuščias");
        }

        itemCountText.setText(String.valueOf(cart.getItemCount()));
        totalText.setText("€" + cart.getTotal().toString());

        adapter.notifyDataSetChanged();

        checkoutButton.setEnabled(!cart.isEmpty());
    }

    private void clearCart() {
        cart.clear();
        updateUI();
        Toast.makeText(this, "Krepšelis išvalytas", Toast.LENGTH_SHORT).show();
    }

    private void checkout() {
        String deliveryAddress = deliveryAddressEditText.getText().toString().trim();
        String notes = notesEditText.getText().toString().trim();

        if (deliveryAddress.isEmpty()) {
            Toast.makeText(this, "Įveskite pristatymo adresą", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cart.isEmpty()) {
            Toast.makeText(this, "Krepšelis tuščias", Toast.LENGTH_SHORT).show();
            return;
        }

        CreateOrderRequest request = new CreateOrderRequest();
        request.setClientId(clientId);
        request.setRestaurantId(cart.getRestaurant().getId());
        request.setDeliveryAddress(deliveryAddress);
        request.setNotes(notes);

        List<CreateOrderRequest.OrderItemRequest> items = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            CreateOrderRequest.OrderItemRequest item = new CreateOrderRequest.OrderItemRequest();
            item.setMenuItemId(cartItem.getMenuItem().getId());
            item.setQuantity(cartItem.getQuantity());
            items.add(item);
        }
        request.setItems(items);

        // Debug logging
        android.util.Log.d("ShoppingCart", "Creating order - ClientId: " + clientId +
                ", RestaurantId: " + cart.getRestaurant().getId() +
                ", Items: " + items.size());
        Toast.makeText(this, "Siunčiamas užsakymas su Client ID: " + clientId, Toast.LENGTH_SHORT).show();

        checkoutButton.setEnabled(false);
        checkoutButton.setText("Kuriamas užsakymas...");

        ApiClient.getApiService().createOrder(request).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                checkoutButton.setEnabled(true);
                checkoutButton.setText("Užsakyti");

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(ShoppingCartActivity.this,
                            "Užsakymas sukurtas sėkmingai! #" + response.body().getData().getId(),
                            Toast.LENGTH_LONG).show();
                    cart.clear();
                    finish();
                } else {
                    Toast.makeText(ShoppingCartActivity.this,
                            "Nepavyko sukurti užsakymo",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                checkoutButton.setEnabled(true);
                checkoutButton.setText("Užsakyti");
                Toast.makeText(ShoppingCartActivity.this,
                        "Klaida: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {
        private List<CartItem> items;

        CartAdapter(List<CartItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cart, parent, false);
            return new CartViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
            CartItem item = items.get(position);
            holder.itemNameText.setText(item.getMenuItem().getName());
            holder.itemPriceText.setText("€" + item.getMenuItem().getPrice().toString());
            holder.quantityText.setText(String.valueOf(item.getQuantity()));
            holder.subtotalText.setText("€" + item.getSubtotal().toString());

            holder.increaseButton.setOnClickListener(v -> {
                cart.updateQuantity(item, item.getQuantity() + 1);
                updateUI();
            });

            holder.decreaseButton.setOnClickListener(v -> {
                if (item.getQuantity() > 1) {
                    cart.updateQuantity(item, item.getQuantity() - 1);
                } else {
                    cart.removeItem(item);
                }
                updateUI();
            });

            holder.removeButton.setOnClickListener(v -> {
                cart.removeItem(item);
                updateUI();
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameText, itemPriceText, quantityText, subtotalText;
        Button increaseButton, decreaseButton, removeButton;

        CartViewHolder(View itemView) {
            super(itemView);
            itemNameText = itemView.findViewById(R.id.itemNameText);
            itemPriceText = itemView.findViewById(R.id.itemPriceText);
            quantityText = itemView.findViewById(R.id.quantityText);
            subtotalText = itemView.findViewById(R.id.subtotalText);
            increaseButton = itemView.findViewById(R.id.increaseButton);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
            removeButton = itemView.findViewById(R.id.removeButton);
        }
    }
}
