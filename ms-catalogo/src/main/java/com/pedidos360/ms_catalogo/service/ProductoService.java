package com.pedidos360.ms_catalogo.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import com.pedidos360.ms_catalogo.model.Producto;
import com.pedidos360.ms_catalogo.repository.ProductoRepository;

@Service 
public class ProductoService {
    @Autowired 
    private ProductoRepository productoRepository;

    private final String carpeta_img =  "uploads/";

    public List<Producto> todosLosProductos (){
        return productoRepository.findAll();
    }

    public Producto productoPorId(Long id){
        return productoRepository.findById(id).orElseThrow(()-> new RuntimeException("Producto No Encontrado"));
    }

    public Producto guardarProducto(@RequestBody Producto productoIngr, MultipartFile imagen){
        String imagenUrl = null;
        if(imagen != null && !imagen.isEmpty()){
            try{
                Path direccionSubidaImg =Paths.get(carpeta_img);
                if(!Files.exists(direccionSubidaImg)){
                        Files.createDirectories(direccionSubidaImg);
                }
                String nombreImg= UUID.randomUUID().toString()+ "_" + imagen.getOriginalFilename();
                Path direccionArchivo = direccionSubidaImg.resolve(nombreImg);
                imagenUrl = "/uploads/" + nombreImg;
            }
            catch(IOException e)
            {
                throw new  RuntimeException("Fallo al guardar la imagen",e);
            } 
        }
        Producto producto = new Producto();
        producto.setNombre(productoIngr.getNombre());
        producto.setPrecio(productoIngr.getPrecio());
        producto.setDescripcion(productoIngr.getDescripcion());
        producto.setCategoria(productoIngr.getCategoria());
        producto.setStock(productoIngr.getStock());
        producto.setUrlImg(imagenUrl);
        return productoRepository.save(producto);
    }
}
