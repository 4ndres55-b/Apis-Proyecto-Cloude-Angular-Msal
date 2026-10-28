package com.pedidos360.ms_pedidos360_bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsPedidos360BffApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsPedidos360BffApplication.class, args);
	}

}
