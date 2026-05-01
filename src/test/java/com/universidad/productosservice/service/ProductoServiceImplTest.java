package com.universidad.productosservice.service;

import com.universidad.productosservice.domain.Producto;
import com.universidad.productosservice.repository.ProductoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    @Captor
    private ArgumentCaptor<Producto> productoCaptor;

    // ─────────────────────────────────────────────
    // Happy Path
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("crear: datos válidos → retorna el producto guardado")
    void crear_datosValidos_retornaProductoGuardado() {
        Producto productoGuardado = Producto.builder()
                .id(1L)
                .nombre("Laptop Pro")
                .precio(new BigDecimal("1500.00"))
                .stock(10)
                .build();

        when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);

        Producto resultado = productoService.crear("Laptop Pro", new BigDecimal("1500.00"), 10);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Laptop Pro");
        assertThat(resultado.getPrecio()).isEqualByComparingTo("1500.00");
        assertThat(resultado.getStock()).isEqualTo(10);

        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("buscarPorId: producto existente → retorna Optional con el producto")
    void buscarPorId_existente_retornaProducto() {
        Producto producto = Producto.builder()
                .id(5L)
                .nombre("Monitor 4K")
                .precio(new BigDecimal("800.00"))
                .stock(3)
                .build();

        when(productoRepository.findById(5L)).thenReturn(Optional.of(producto));

        Optional<Producto> resultado = productoService.buscarPorId(5L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Monitor 4K");
        verify(productoRepository).findById(5L);
    }

    @Test
    @DisplayName("buscarPorId: id inexistente → retorna Optional vacío")
    void buscarPorId_inexistente_retornaOptionalVacio() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Producto> resultado = productoService.buscarPorId(99L);

        assertThat(resultado).isEmpty();
        verify(productoRepository).findById(99L);
    }

    // ─────────────────────────────────────────────
    // Pruebas negativas – nombre inválido
    // ─────────────────────────────────────────────

    @ParameterizedTest(name = "[{index}] nombre=''{0}'' → IllegalArgumentException")
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("crear: nombre nulo, vacío o en blanco → lanza IllegalArgumentException")
    void crear_nombreInvalido_lanzaIllegalArgumentException(String nombre) {
        assertThatThrownBy(() ->
                productoService.crear(nombre, new BigDecimal("100.00"), 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre");

        verifyNoInteractions(productoRepository);
    }

    // ─────────────────────────────────────────────
    // Pruebas negativas – precio inválido
    // ─────────────────────────────────────────────

    @ParameterizedTest(name = "[{index}] precio={0} → IllegalArgumentException")
    @ValueSource(doubles = {0.0, -1.0, -999.99})
    @DisplayName("crear: precio cero o negativo → lanza IllegalArgumentException")
    void crear_precioInvalido_lanzaIllegalArgumentException(double precio) {
        assertThatThrownBy(() ->
                productoService.crear("Teclado", BigDecimal.valueOf(precio), 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("precio");

        verifyNoInteractions(productoRepository);
    }

    // ─────────────────────────────────────────────
    // Prueba negativa – stock inválido
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("crear: stock negativo → lanza IllegalArgumentException")
    void crear_stockNegativo_lanzaIllegalArgumentException() {
        assertThatThrownBy(() ->
                productoService.crear("Mouse", new BigDecimal("25.00"), -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock");

        verifyNoInteractions(productoRepository);
    }

    // ─────────────────────────────────────────────
    // ArgumentCaptor – normalización con strip()
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("crear: nombre con espacios → persiste el nombre normalizado con strip()")
    void crear_nombreConEspacios_guardaNombreNormalizado() {
        Producto productoGuardado = Producto.builder()
                .id(2L)
                .nombre("Auriculares")
                .precio(new BigDecimal("50.00"))
                .stock(20)
                .build();

        when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);

        productoService.crear("   Auriculares   ", new BigDecimal("50.00"), 20);

        verify(productoRepository).save(productoCaptor.capture());
        Producto capturado = productoCaptor.getValue();

        assertThat(capturado.getNombre())
                .isEqualTo("Auriculares")
                .doesNotStartWith(" ")
                .doesNotEndWith(" ");
    }

    // ─────────────────────────────────────────────
    // actualizarStock
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("actualizarStock: producto existente y stock válido → retorna producto actualizado")
    void actualizarStock_productoExistente_retornaProductoActualizado() {
        Producto existente = Producto.builder()
                .id(1L).nombre("Silla").precio(new BigDecimal("200.00")).stock(5).build();
        Producto actualizado = Producto.builder()
                .id(1L).nombre("Silla").precio(new BigDecimal("200.00")).stock(15).build();

        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoRepository.save(any(Producto.class))).thenReturn(actualizado);

        Producto resultado = productoService.actualizarStock(1L, 15);

        assertThat(resultado.getStock()).isEqualTo(15);
        verify(productoRepository).findById(1L);
        verify(productoRepository).save(existente);
    }

    @Test
    @DisplayName("actualizarStock: producto inexistente → lanza IllegalArgumentException")
    void actualizarStock_productoInexistente_lanzaIllegalArgumentException() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.actualizarStock(99L, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no encontrado");
    }

    @Test
    @DisplayName("actualizarStock: stock negativo → lanza IllegalArgumentException")
    void actualizarStock_stockNegativo_lanzaIllegalArgumentException() {
        assertThatThrownBy(() -> productoService.actualizarStock(1L, -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negativo");

        verifyNoInteractions(productoRepository);
    }

    // ─────────────────────────────────────────────
    // eliminar
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("eliminar: producto existente → invoca deleteById sin lanzar excepción")
    void eliminar_productoExistente_eliminaSinExcepcion() {
        when(productoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(1L);

        assertThatCode(() -> productoService.eliminar(1L))
                .doesNotThrowAnyException();

        verify(productoRepository).existsById(1L);
        verify(productoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("eliminar: producto inexistente → lanza IllegalArgumentException")
    void eliminar_productoInexistente_lanzaIllegalArgumentException() {
        when(productoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> productoService.eliminar(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no encontrado");

        verify(productoRepository, never()).deleteById(any());
    }
}
