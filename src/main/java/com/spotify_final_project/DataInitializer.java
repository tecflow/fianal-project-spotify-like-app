package com.spotify_final_project;

import com.spotify_final_project.enums.AccountStatus;
import com.spotify_final_project.enums.Role;
import com.spotify_final_project.model.User;
import com.spotify_final_project.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDefaultUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Create default artist user
            if (userRepository.findByUsername("string").isEmpty()) {
                User artistUser = new User();
                artistUser.setUsername("string")
                        .setEmail("string@example.com")
                        .setFirstName("Default")
                        .setLastName("Artist")
                        .setBirthDate(java.time.LocalDate.of(1990, 1, 1))
                        .setPassword(passwordEncoder.encode("string"))
                        .setRole(Role.ARTIST)
                        .setVerified(true)
                        .setStatus(AccountStatus.ACTIVE);

                userRepository.save(artistUser);
                System.out.println("Default artist user created: string / string");
            }

            // Create default listener user
            if (userRepository.findByUsername("string1").isEmpty()) {
                User listenerUser = new User();
                listenerUser.setUsername("string1")
                        .setEmail("string1@example.com")
                        .setFirstName("Default")
                        .setLastName("Listener")
                        .setBirthDate(java.time.LocalDate.of(1995, 1, 1))
                        .setPassword(passwordEncoder.encode("string1"))
                        .setRole(Role.LISTENER)
                        .setVerified(true)
                        .setStatus(AccountStatus.ACTIVE);

                userRepository.save(listenerUser);
                System.out.println("Default listener user created: string1 / string1");
            }

            // Create default admin user
            if (userRepository.findByUsername("admin").isEmpty()) {
                User adminUser = new User();
                adminUser.setUsername("admin")
                        .setEmail("admin@example.com")
                        .setFirstName("Default")
                        .setLastName("Admin")
                        .setBirthDate(java.time.LocalDate.of(1985, 1, 1))
                        .setPassword(passwordEncoder.encode("admin")) // use "admin" as password
                        .setRole(Role.ADMIN)
                        .setVerified(true)
                        .setStatus(AccountStatus.ACTIVE);

                userRepository.save(adminUser);
                System.out.println("Default admin user created: admin / admin");
            }
        };
    }
}

