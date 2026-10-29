package com.pedidos360.ms_pedidos360_bff.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@FeignClient(name = "ms-catalogo", url = "${catalogo.service.url}")
public interface CatalogoClient {

    @GetMapping("/api/productos")
    List<Map<String, Object>> obtenerProductos();

    
    @PostMapping(value = "/api/productos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Map<String, Object> guardarProducto(
            @RequestPart("nombre") String nombre,
            @RequestPart("stock") Integer stock,
            @RequestPart("precio") Double precio,
            @RequestPart("descripcion") String descripcion,
            @RequestPart("categoria") String categoria,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen
    );
    @GetMapping("/uploads/{filename}")
    ResponseEntity<byte[]> obtenerImagen(@PathVariable("filename") String filename);
}
