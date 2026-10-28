
package com.pedidos360.ms_catalogo.repository;

import com.pedidos360.ms_catalogo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}