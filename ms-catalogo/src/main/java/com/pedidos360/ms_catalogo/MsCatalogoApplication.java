package com.pedidos360.ms_catalogo;

import com.pedidos360.ms_catalogo.model.Producto;
import com.pedidos360.ms_catalogo.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MsCatalogoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsCatalogoApplication.class, args);
	}

	// Este código se ejecuta automáticamente al iniciar el servidor
	@Bean
	CommandLineRunner initData(ProductoRepository repository) {
		return args -> {
			// Solo inserta si la tabla está vacía
			if (repository.count() == 0) {
				Producto p1 = new Producto();
				p1.setNombre("Laptop Pro 16");
				p1.setPrecio(1500.0);
				repository.save(p1);

				Producto p2 = new Producto();
				p2.setNombre("Monitor 4K");
				p2.setPrecio(350.0);
				repository.save(p2);

				System.out.println("=> Datos de prueba insertados en AWS MySQL exitosamente.");
			} else {
				System.out.println("=> La base de datos ya tiene productos.");
			}
		};
	}
}