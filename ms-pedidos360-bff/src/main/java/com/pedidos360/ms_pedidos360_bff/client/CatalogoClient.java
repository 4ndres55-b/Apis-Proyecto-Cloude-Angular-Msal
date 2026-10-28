package com.pedidos360.ms_pedidos360_bff.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;

@FeignClient(name = "ms-catalogo", url = "http://localhost:8081")
public interface CatalogoClient {

    @GetMapping("/api/productos")
    List<Map<String, Object>> obtenerProductos();
}
