package com.asset.car_rental;

import com.asset.car_rental.entity.User;
import com.asset.car_rental.repository.UserRepository;
import com.asset.car_rental.model.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class CarRentalApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarRentalApplication.class, args);
	}

	@Bean
	CommandLineRunner initDb(UserRepository userRepository) {
		return args -> {
			// чтобы не создавать дубликаты при каждом запуске
			if (userRepository.count() == 0) {
				User admin = new User();
				admin.setUsername("test_admin");
				admin.setPassword("password123"); // временно, потом заменим на BCrypt
				admin.setRole(UserRole.ADMIN);
				admin.setActive(true);
				admin.setCreatedAt(LocalDateTime.now());
				admin.setUpdatedAt(LocalDateTime.now());

				userRepository.save(admin);
			}

			System.out.println("=== USERS IN DB ===");
			userRepository.findAll().forEach(System.out::println);
		};
	}
}
