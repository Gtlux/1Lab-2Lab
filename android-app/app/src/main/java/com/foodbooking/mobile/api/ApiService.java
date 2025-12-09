package com.foodbooking.mobile.api;

import com.foodbooking.mobile.models.*;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("api/client/restaurants")
    Call<ApiResponse<List<Restaurant>>> getRestaurants();

    @GET("api/client/menu/restaurant/{restaurantId}")
    Call<ApiResponse<List<MenuItem>>> getMenuByRestaurant(@Path("restaurantId") int restaurantId);

    @GET("api/client/orders/{clientId}")
    Call<ApiResponse<List<Order>>> getClientOrders(@Path("clientId") int clientId);

    @GET("api/driver/orders/{driverId}")
    Call<ApiResponse<List<Order>>> getDriverOrders(@Path("driverId") int driverId);

    @PUT("api/driver/orders/{orderId}/status")
    Call<ApiResponse<String>> updateOrderStatus(
            @Path("orderId") int orderId,
            @Query("status") String status
    );

    @POST("api/driver/orders/{orderId}/accept")
    Call<ApiResponse<String>> acceptOrder(
            @Path("orderId") int orderId,
            @Query("driverId") int driverId
    );
}
