package com.pedidos360.ms_catalogo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "productos")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable =false)
    private String nombre;

    @Column (nullable = false)
    private Double precio;

    @Column (nullable = false)
    private Number stock;

    @Column (length = 1000)
    private String descripcion;

    @Column (nullable = false)
    private String categoria;

    @Column(name ="url_img")
    private String urlImg;
}