package com.module.project;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.module.project.model.Role;
import com.module.project.repository.RoleRepository;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class Main implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public void run(String... args) throws Exception {
		try {
			Role[] roles = new Role[] {
					new Role(1, "admin"),
					new Role(2, "manager"),
					new Role(3, "leader"),
					new Role(4, "cleaner"),
					new Role(5, "customer"),
			};
			roleRepository.saveAll(Arrays.asList(roles));
		} catch (Exception e) {
		}
	}

}
