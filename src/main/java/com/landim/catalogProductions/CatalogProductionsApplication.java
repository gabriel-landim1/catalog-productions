package com.landim.catalogProductions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class CatalogProductionsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatalogProductionsApplication.class, args);
	}
}
