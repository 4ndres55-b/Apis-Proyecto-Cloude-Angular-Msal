package com.pedidos360.ms_pedidos360_bff.controller;

import com.pedidos360.ms_pedidos360_bff.client.CatalogoClient;
import com.pedidos360.ms_pedidos360_bff.client.PedidosClient;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bff")
public class BffController {

    private final CatalogoClient catalogoClient;
    private final PedidosClient pedidosClient; // Nuevo cliente

    // Inyectamos ambos clientes en el constructor
    public BffController(CatalogoClient catalogoClient, PedidosClient pedidosClient) {
        this.catalogoClient = catalogoClient;
        this.pedidosClient = pedidosClient;
    }


    //  Estado del BFF
    @GetMapping("/estado")
    public Map<String, Object> estadoServidor(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "mensaje", "¡El BFF de Pedidos360 está funcionando y seguro!",
                "usuarioAutenticado",
                jwt.getClaimAsString("name") != null ? jwt.getClaimAsString("name") : "Usuario Desconocido");
    }


    //Endpoints para productos
    @GetMapping("/productos")
    public List<Map<String, Object>> obtenerProductos() {
        return catalogoClient.obtenerProductos();
    }


    @PostMapping(value = "/productos",consumes = {"multipart/form-data"})
    public ResponseEntity<Map<String, Object>> crearProductoBff(
            @RequestParam("nombre") String nombre,
            @RequestParam("stock") Integer stock,
            @RequestParam("precio") Double precio,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("categoria") String categoria,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {

        Map<String, Object> respuesta = catalogoClient.guardarProducto(
                nombre, stock, precio, descripcion, categoria, imagen
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
    @GetMapping("/productos/uploads/{filename}")
    public ResponseEntity<byte[]> verImagen(@PathVariable("filename") String filename) {
        try {
            // El BFF le pide la imagen al microservicio de catálogo
            return catalogoClient.obtenerImagen(filename);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    



    //Enpoints para pedidos
    @GetMapping("/pedidos")
    public List<Map<String, Object>> obtenerPedidos() {
        // El BFF llama al ms-pedidos (puerto 8083) y devuelve la lista
        return pedidosClient.obtenerPedidos();

    }

    @PostMapping("/pedidos")
    public ResponseEntity<Map<String, Object>> crearPedidoBff(@RequestBody Map<String, Object> pedidoRequest) {
        
        Map<String, Object> respuesta = pedidosClient.crearPedido(pedidoRequest);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
}