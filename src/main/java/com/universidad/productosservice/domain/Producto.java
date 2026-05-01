package com.universidad.productosservice.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock;

    public Producto() {}

    private Producto(Builder builder) {
        this.id = builder.id;
        this.nombre = builder.nombre;
        this.precio = builder.precio;
        this.stock = builder.stock;
    }

    // ── Getters ──────────────────────────────────
    public Long getId()          { return id; }
    public String getNombre()    { return nombre; }
    public BigDecimal getPrecio(){ return precio; }
    public Integer getStock()    { return stock; }

    // ── Setters ──────────────────────────────────
    public void setId(Long id)            { this.id = id; }
    public void setNombre(String nombre)  { this.nombre = nombre; }
    public void setPrecio(BigDecimal p)   { this.precio = p; }
    public void setStock(Integer stock)   { this.stock = stock; }

    // ── Builder ──────────────────────────────────
    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private Long id;
        private String nombre;
        private BigDecimal precio;
        private Integer stock;

        private Builder() {}

        public Builder id(Long id)            { this.id = id;       return this; }
        public Builder nombre(String nombre)  { this.nombre = nombre; return this; }
        public Builder precio(BigDecimal p)   { this.precio = p;    return this; }
        public Builder stock(Integer stock)   { this.stock = stock; return this; }
        public Producto build()               { return new Producto(this); }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Producto that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Producto{id=" + id + ", nombre='" + nombre + "', precio=" + precio + ", stock=" + stock + '}';
    }
}
