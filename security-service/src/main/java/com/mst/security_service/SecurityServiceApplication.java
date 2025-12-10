package com.mst.security_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.mst.security_service.client")
public class SecurityServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(SecurityServiceApplication.class, args);
	}
}
