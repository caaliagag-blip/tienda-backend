package com.tienda_backend.controller;

import com.tienda_backend.model.Product;
import com.tienda_backend.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Productos", description = "Operaciones CRUD del catálogo Level-Up Gamer")
public class    ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    @Operation(summary = "Listar todos los productos")
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @GetMapping("/{code}")
    @Operation(summary = "Obtener producto por código")
    public ResponseEntity<Product> getById(@PathVariable String code) {
        return productRepository.findById(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear producto (Solo ADMIN)")
    public ResponseEntity<Product> create(@RequestBody Product product) {
        return ResponseEntity.ok(productRepository.save(product));
    }

    @PutMapping("/{code}")
    @Operation(summary = "Actualizar producto existente (Solo ADMIN)")
    public ResponseEntity<Product> update(@PathVariable String code, @RequestBody Product details) {
        return productRepository.findById(code).map(existing -> {
            existing.setCategory(details.getCategory());
            existing.setName(details.getName());
            existing.setPrice(details.getPrice());
            existing.setDesc(details.getDesc());
            return ResponseEntity.ok(productRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{code}")
    @Operation(summary = "Eliminar producto (Solo ADMIN)")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        if (!productRepository.existsById(code)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(code);
        return ResponseEntity.noContent().build();
    }
}