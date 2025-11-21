package com.oms.inventory.controller;

import com.oms.inventory.dto.ProductResponse;
import com.oms.inventory.dto.StockRequest;
import com.oms.inventory.entity.Product;
import com.oms.inventory.mapper.ProductMapper;
import com.oms.inventory.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductsController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    // Fetch product details by product ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(productMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Fetch all products with optional filter for in-stock items only
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(@RequestParam(required = false, defaultValue = "false") boolean inStockOnly) {
        List<Product> products = productService.getAllProducts(inStockOnly);
        return ResponseEntity.ok(productMapper.toResponseList(products));
    }

    // Fetch all products by category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(productMapper.toResponseList(products));
    }

    // Internal endpoint to validate and reserve stock for order placement
    @PostMapping("/reserve-stock")
    public ResponseEntity<Void> reserveStock(@RequestBody StockRequest request) {
        productService.reserveStock(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok().build();
    }
}