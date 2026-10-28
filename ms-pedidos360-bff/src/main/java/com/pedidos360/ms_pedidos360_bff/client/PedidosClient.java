package com.pedidos360.ms_pedidos360_bff.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;

@FeignClient(name = "ms-pedidos", url = "http://localhost:8083")
public interface PedidosClient {

    @GetMapping("/api/orders")
    List<Map<String, Object>> obtenerPedidos();
}