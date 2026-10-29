package com.pedidos360.ms_catalogo.controller;

import com.pedidos360.ms_catalogo.model.Producto;
import com.pedidos360.ms_catalogo.service.ProductoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Producto> guardarProducto(
            @RequestParam("nombre") String nombre,
            @RequestParam("stock") Integer stock,
            @RequestParam("precio") Double precio,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("categoria") String categoria,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {

        Producto productoIngr = new Producto();
        productoIngr.setNombre(nombre);
        productoIngr.setStock(stock);
        productoIngr.setPrecio(precio);
        productoIngr.setDescripcion(descripcion);
        productoIngr.setCategoria(categoria);

        Producto nuevoProducto = productoService.guardarProducto(productoIngr, imagen);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }
    
    
}