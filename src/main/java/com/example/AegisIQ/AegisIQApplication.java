package com.example.AegisIQ;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AegisIQApplication {

	public static void main(String[] args) {
		SpringApplication.run(AegisIQApplication.class, args);
		System.out.println("\n==============================================");
		System.out.println("🚀 AegisIQ System Started Successfully!");
		System.out.println("🌐 Server running at: http://localhost:8080");
		System.out.println("📊 API Endpoints available at: /api/*");
		System.out.println("==============================================\n");
	}

}
