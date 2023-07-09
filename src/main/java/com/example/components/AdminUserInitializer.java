package com.example.components;

import com.example.auth.AuthenticationRequest;
import com.example.auth.RegisterRequest;
import com.example.entities.Role;
import com.example.entities.User;
import com.example.repositories.UserRepository;
import com.example.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    UserRepository userRepository;
    @Value("${adminUser.email}")
    private String adminEmail;
    @Value("${adminUser.password}")
    private String adminPassword;

    @Override
    public void run(String...args) throws Exception {

        String jwtToken;
        Optional<User> user = userRepository.findByEmail(adminEmail);
        if (user.isEmpty()) {

            // create new admin
            var newAdminRequest = RegisterRequest.builder()
                    .name("admin")
                    .email(adminEmail)
                    .password(adminPassword)
                    .build();
            jwtToken = authenticationService.register(newAdminRequest, Role.ROLE_ADMIN).getToken();
        } else {

            // get token for existing admin
            var adminRequest = AuthenticationRequest.builder()
                    .email(adminEmail)
                    .password(adminPassword)
                    .build();
            jwtToken = authenticationService.authenticate(adminRequest).getToken();
        }
        System.out.println("\nAdmin Email: " + adminEmail);
        System.out.println("Admin Password: " + adminPassword);
        System.out.println("Admin Token: " + jwtToken + "\n");

    }
}
