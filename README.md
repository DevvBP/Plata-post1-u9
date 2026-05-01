# productos-service

Laboratorio de Pruebas Unitarias e Integración — Unidad 9, Post-Contenido 1  
**Asignatura:** Desarrollo de Software Avanzado  
**Autor:** DevvBP

---

## Stack tecnológico

| Tecnología | Versión |
|---|---|
| Java | 21 (compilado con JDK 25) |
| Spring Boot | 3.3.5 |
| Spring Data JPA + H2 | incluida en Boot |
| JUnit 5 | incluida en spring-boot-starter-test |
| Mockito | incluida en spring-boot-starter-test |
| AssertJ | incluida en spring-boot-starter-test |
| JaCoCo | 0.8.12 |
| Maven | 3.9.14 |

---

## Estructura del proyecto

```
src/
├── main/java/com/universidad/productosservice/
│   ├── ProductosServiceApplication.java
│   ├── domain/
│   │   └── Producto.java              ← Entidad JPA (builder manual)
│   ├── repository/
│   │   └── ProductoRepository.java    ← JpaRepository<Producto, Long>
│   └── service/
│       ├── ProductoService.java        ← Interfaz de contrato
│       └── ProductoServiceImpl.java    ← Implementación con validaciones
└── test/java/com/universidad/productosservice/service/
    └── ProductoServiceImplTest.java   ← 18 pruebas unitarias
```

---

## Reglas de negocio validadas

- El nombre no puede ser `null`, vacío ni en blanco.
- El precio debe ser estrictamente mayor a `0`.
- El stock no puede ser negativo.
- El nombre se normaliza con `.strip()` antes de persistir.

---

## Ejecución de tests y cobertura

```bash
mvn clean test jacoco:report
```

**Resultado:** `Tests run: 18, Failures: 0, Errors: 0, Skipped: 0` — BUILD SUCCESS ✅

El reporte HTML de cobertura se genera en:
```
target/site/jacoco/index.html
```

Las capturas de evidencia se encuentran en la carpeta `/docs`:
- `terminal.png` — BUILD SUCCESS en terminal
- `cobertura.png` — Reporte JaCoCo mostrando cobertura del servicio

---

## Suite de pruebas implementada

| Prueba | Tipo | Descripción |
|---|---|---|
| `crear_datosValidos_retornaProductoGuardado` | Happy Path | Verifica creación correcta con mock del repositorio |
| `buscarPorId_existente_retornaProducto` | Happy Path | Verifica retorno de Optional con producto |
| `buscarPorId_inexistente_retornaOptionalVacio` | Borde | Verifica Optional vacío cuando no existe el id |
| `crear_nombreInvalido_lanzaIllegalArgumentException` | @ParameterizedTest | null, "", espacios → excepción, sin tocar el repo |
| `crear_precioInvalido_lanzaIllegalArgumentException` | @ParameterizedTest | 0.0, -1.0, -999.99 → excepción |
| `crear_stockNegativo_lanzaIllegalArgumentException` | Negativo | stock < 0 → excepción |
| `crear_nombreConEspacios_guardaNombreNormalizado` | ArgumentCaptor | Verifica que `.strip()` funciona antes de persistir |
| `actualizarStock_productoExistente_retornaProductoActualizado` | Happy Path | Verifica actualización y llamada al repositorio |
| `actualizarStock_productoInexistente_lanzaIllegalArgumentException` | Negativo | id inexistente → excepción |
| `actualizarStock_stockNegativo_lanzaIllegalArgumentException` | Negativo | stock negativo → excepción sin tocar repo |
| `eliminar_productoExistente_eliminaSinExcepcion` | Happy Path | Verifica deleteById invocado |
| `eliminar_productoInexistente_lanzaIllegalArgumentException` | Negativo | id inexistente → excepción, deleteById nunca llamado |

> Los `@ParameterizedTest` con `@NullAndEmptySource` + `@ValueSource` generan múltiples casos de prueba automáticamente, sumando los 18 en total.

---

## Historial de commits

```
chore: setup inicial del proyecto y configuracion de jacoco
feat: implementar Producto, repositorio y ProductoServiceImpl con validaciones
test: implementar suite completa de pruebas unitarias con mockito y assertions
docs: agregar README y evidencias de pruebas unitarias
```
