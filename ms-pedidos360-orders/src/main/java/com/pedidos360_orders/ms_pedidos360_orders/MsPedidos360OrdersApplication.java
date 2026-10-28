package com.pedidos360_orders.ms_pedidos360_orders;

import com.pedidos360_orders.ms_pedidos360_orders.model.DetallePedido;
import com.pedidos360_orders.ms_pedidos360_orders.model.Pedido;
import com.pedidos360_orders.ms_pedidos360_orders.repository.PedidoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.util.List;

@SpringBootApplication
public class MsPedidos360OrdersApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsPedidos360OrdersApplication.class, args);
	}

	@Bean
	CommandLineRunner initData(PedidoRepository repository) {
		return args -> {
			if (repository.count() == 0) {
				Pedido pedido = new Pedido();
				pedido.setUsername("nicolas.cliente");
				pedido.setEstado("CREADO");
				pedido.setTotal(1500.0);

				DetallePedido detalle = new DetallePedido();
				detalle.setProductoId(1L); // Referencia a la Laptop Pro 16
				detalle.setCantidad(1);
				detalle.setPrecioUnitario(1500.0);

				// Agregamos el detalle al pedido
				pedido.setDetalles(List.of(detalle));

				repository.save(pedido);
				System.out.println("=> Pedido de prueba insertado exitosamente.");
			}
		};
	}
}