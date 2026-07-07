package com.app.ecom.services;

import com.app.ecom.dto.*;
import com.app.ecom.dto.ProductResponse;
import com.app.ecom.models.Product;
import com.app.ecom.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;

    private ProductResponse mapToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(String.valueOf(product.getId()));
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setCategory(product.getCategory());
        response.setImageUrl(product.getImageUrl());
        response.setActive(product.getActive());

        return response;
    }

    private void updateProductFromRequest(Product product, ProductRequest productRequest) {
        product.setName(productRequest.getName() != null ? productRequest.getName() : product.getName());
        product.setDescription(productRequest.getDescription() != null ? productRequest.getDescription() : product.getDescription());
        product.setPrice(productRequest.getPrice() != null ? productRequest.getPrice() : product.getPrice());
        product.setStockQuantity(productRequest.getStockQuantity() != null ? productRequest.getStockQuantity() : product.getStockQuantity());
        product.setCategory(productRequest.getCategory() != null ? productRequest.getCategory() : product.getCategory());
        product.setImageUrl(productRequest.getImageUrl() != null ? productRequest.getImageUrl() : product.getImageUrl());
        product.setActive(productRequest.getActive() != null ? productRequest.getActive() : product.getActive());
    }
    
    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = new Product();
        updateProductFromRequest(product, productRequest);
        return mapToProductResponse(productRepository.save(product));
    }

    public List<ProductResponse> fetchAllProducts() {
        return productRepository.findByActiveTrue().stream().map(this::mapToProductResponse).collect(Collectors.toList());
    }

    public Optional<ProductResponse> fetchProduct(Long id) {
        return productRepository.findById(id).map(this::mapToProductResponse);
    }

    public ProductResponse updateProduct(ProductRequest updatedProductRequest, Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    updateProductFromRequest(product, updatedProductRequest);
                    productRepository.save(product);
                    return mapToProductResponse(product);
                }).orElseThrow(() -> new RuntimeException("Product: "+id+" not found"));
    }

    public Product removeProduct(Long id) {
        Product delProduct = productRepository.findById(id).orElse(null);
        delProduct.setActive(false);
        productRepository.save(delProduct);
        return delProduct;
    }

    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchProduct(keyword).stream().map(this::mapToProductResponse).collect(Collectors.toList());
    }
}