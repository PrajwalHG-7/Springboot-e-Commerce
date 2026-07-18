package com.app.ecom.controllers;

import java.util.List;
import java.util.Optional;

import com.app.ecom.models.Product;
import lombok.RequiredArgsConstructor;
import com.app.ecom.dto.ProductRequest;
import com.app.ecom.dto.ProductResponse;
import org.springframework.http.HttpStatus;
import com.app.ecom.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest) {
        return new ResponseEntity<ProductResponse>(productService.createProduct(productRequest), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllUsers() {
        return new ResponseEntity<>(productService.fetchAllProducts(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getUser(@PathVariable Long id) {
        return productService.fetchProduct(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@RequestBody ProductRequest updatedProductRequest, @PathVariable Long id) {
        return new ResponseEntity<>(productService.updateProduct(updatedProductRequest, id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return productService.removeProduct(id) != null ? new ResponseEntity<>(productService.removeProduct(id), HttpStatus.OK) : new ResponseEntity<>("Product: "+id+" not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUser(@RequestParam String keyword) {
        return new ResponseEntity<>(productService.searchProducts(keyword), HttpStatus.OK);
    }
}