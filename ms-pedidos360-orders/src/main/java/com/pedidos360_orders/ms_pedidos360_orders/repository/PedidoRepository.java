package com.pedidos360_orders.ms_pedidos360_orders.repository;

import com.pedidos360_orders.ms_pedidos360_orders.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Este método nos servirá luego para que el Cliente vea solo sus pedidos
    List<Pedido> findByUsername(String username);
}
