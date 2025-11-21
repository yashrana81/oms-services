package com.oms.inventory.service;

import com.oms.inventory.entity.Product;
import com.oms.inventory.exception.ProductServiceException;
import com.oms.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getAllProducts(boolean inStockOnly) {
        if (inStockOnly) {
            return productRepository.findByStockAvailableGreaterThan(0);
        }
        return productRepository.findAll();
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    // Validates stock availability and reserves it by reducing the quantity with pessimistic locking
    @Transactional
    public void reserveStock(Long productId, Integer quantity) {
        Product product;
        try {
            product = productRepository.findByIdForUpdate(productId)
                    .orElseThrow(() -> new ProductServiceException("Product not found with id: " + productId, HttpStatus.NOT_FOUND));
        } catch (ProductServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Database error while fetching product for reserve: {}", productId, ex);
            throw new ProductServiceException("Unable to check stock. Please try again later", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (product.getStockAvailable() < quantity) {
            log.error("Place order request failed. Insufficient stock available. Only {} items in stock", product.getStockAvailable());
            throw new ProductServiceException("Insufficient stock available. Only " + product.getStockAvailable() + " items in stock", HttpStatus.BAD_REQUEST);
        }

        product.setStockAvailable(product.getStockAvailable() - quantity);
        product.setUpdatedAt(LocalDateTime.now());

        try {
            productRepository.save(product);
            log.info("Reduced stock for productId: {}. New stock: {}", productId, product.getStockAvailable());
        } catch (Exception ex) {
            log.error("Database error while reserving stock for productId: {}", productId, ex);
            throw new ProductServiceException("Unable to reserve stock. Please try again later", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Restores stock when an order is cancelled with pessimistic locking
    @Transactional
    public void restoreStock(Long productId, Integer quantity) {
        Product product;
        try {
            product = productRepository.findByIdForUpdate(productId)
                    .orElseThrow(() -> new ProductServiceException("Product not found with id: " + productId, HttpStatus.NOT_FOUND));
        } catch (ProductServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Database error while fetching product for restore: {}", productId, ex);
            throw new ProductServiceException("Unable to fetch product. Please try again later", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        int currentStock = product.getStockAvailable();
        int newStock = currentStock + quantity;

        product.setStockAvailable(newStock);
        product.setUpdatedAt(LocalDateTime.now());

        try {
            productRepository.save(product);
            log.info("Restored stock for productId: {}. Old: {}, New: {}", productId, currentStock, newStock);
        } catch (Exception ex) {
            log.error("Database error while restoring stock for productId: {}", productId, ex);
            throw new ProductServiceException("Unable to restore stock. Please try again later", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}