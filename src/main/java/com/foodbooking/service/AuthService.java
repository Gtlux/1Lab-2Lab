package com.foodbooking.service;

import com.foodbooking.dao.UserDAO;
import com.foodbooking.dto.LoginRequest;
import com.foodbooking.dto.LoginResponse;
import com.foodbooking.dto.RegisterRequest;
import com.foodbooking.dto.UserDTO;
import com.foodbooking.model.User;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public LoginResponse login(LoginRequest request) {
        User user = userDAO.authenticate(request.getUsername(), request.getPassword());

        if (user == null) {
            return new LoginResponse(false, "Neteisingas vartotojo vardas arba slaptažodis");
        }

        if (!user.isActive()) {
            return new LoginResponse(false, "Vartotojas neaktyvus");
        }

        UserDTO userDTO = UserDTO.fromUser(user);
        return new LoginResponse(true, "Prisijungimas sėkmingas", userDTO);
    }

    public LoginResponse register(RegisterRequest request) {
        if (userDAO.getUserByUsername(request.getUsername()) != null) {
            return new LoginResponse(false, "Vartotojo vardas jau užimtas");
        }

        if (userDAO.getUserByEmail(request.getEmail()) != null) {
            return new LoginResponse(false, "El. paštas jau užimtas");
        }

        User user = new User(
                request.getUsername(),
                request.getPassword(),
                request.getEmail(),
                request.getFullName(),
                request.getPhoneNumber(),
                request.getRole()
        );

        int userId = userDAO.createUser(user);
        if (userId > 0) {
            user.setId(userId);
            UserDTO userDTO = UserDTO.fromUser(user);
            return new LoginResponse(true, "Registracija sėkminga", userDTO);
        }

        return new LoginResponse(false, "Registracija nepavyko");
    }
}
