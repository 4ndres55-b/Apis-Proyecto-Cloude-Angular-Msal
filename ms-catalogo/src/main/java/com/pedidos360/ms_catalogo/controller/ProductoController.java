package com.pedidos360.ms_catalogo.controller;

import com.pedidos360.ms_catalogo.model.Producto;
import com.pedidos360.ms_catalogo.service.ProductoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;



@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private  ProductoService productoService;

    @GetMapping   
    public  List<Producto> listarProductos(){
        return productoService.todosLosProductos();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.productoPorId(id));
    }

    @PostMapping(value = "/guardar", consumes= {"multipart/form-data"})
    public ResponseEntity<Producto> guardarProducto(@RequestBody Producto productoIngr, @RequestParam(value="imagen", required = false) MultipartFile image) {
        Producto nuevoProducto = productoService.guardarProducto(productoIngr, image);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }
    
    
}