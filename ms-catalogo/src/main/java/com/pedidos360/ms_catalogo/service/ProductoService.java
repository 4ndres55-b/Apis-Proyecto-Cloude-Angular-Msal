package com.pedidos360.ms_catalogo.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    private final String carpeta_img = System.getProperty("user.dir") + "/uploads/";

    public List<Producto> todosLosProductos (){
        return productoRepository.findAll();
    }

    public Producto productoPorId(Long id){
        return productoRepository.findById(id).orElseThrow(()-> new RuntimeException("Producto No Encontrado"));
    }

    public Producto guardarProducto(@RequestBody Producto productoIngr, MultipartFile imagen){
        String imagenUrl = null;
        if(imagen != null && !imagen.isEmpty()){
            System.out.println(">>> RECIBIENDO IMAGEN: " + imagen.getOriginalFilename());
            try{
                Path direccionSubidaImg = Paths.get(carpeta_img);
                if(!Files.exists(direccionSubidaImg)){
                    Files.createDirectories(direccionSubidaImg);
                }
                
                // 1. Obtener nombre original y sanitizar espacios
                String nombreOriginal = imagen.getOriginalFilename();
                String nombreBase = "";
                String extension = "";
                
                if (nombreOriginal != null) {
                    nombreOriginal = nombreOriginal.replaceAll("\\s+", "_");
                    int indicePunto = nombreOriginal.lastIndexOf(".");
                    if (indicePunto != -1) {
                        extension = nombreOriginal.substring(indicePunto);
                        nombreBase = nombreOriginal.substring(0, indicePunto);
                    } else {
                        nombreBase = nombreOriginal;
                    }
                    if (nombreBase.length() > 20) {
                        nombreBase = nombreBase.substring(0, 20);
                    }
                }
                String nombreImg = UUID.randomUUID().toString() + "_" + nombreBase + extension;
                
                Path direccionArchivo = direccionSubidaImg.resolve(nombreImg);
                Files.copy(imagen.getInputStream(), direccionArchivo, StandardCopyOption.REPLACE_EXISTING);
                imagenUrl = "/uploads/" + nombreImg;
                System.out.println(">>> IMAGEN GUARDADA EN: " + direccionArchivo.toAbsolutePath());
            } catch(IOException e) {
                throw new RuntimeException("Fallo al guardar la imagen", e);
            } 
        } else {
            System.out.println(">>> ADVERTENCIA: La imagen llegó nula o vacía.");
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
