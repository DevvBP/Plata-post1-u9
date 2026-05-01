package com.universidad.productosservice.service;

import com.universidad.productosservice.domain.Producto;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProductoService {

    Producto crear(String nombre, BigDecimal precio, int stock);

    Optional<Producto> buscarPorId(Long id);

    Producto actualizarStock(Long id, int nuevoStock);

    void eliminar(Long id);
}
