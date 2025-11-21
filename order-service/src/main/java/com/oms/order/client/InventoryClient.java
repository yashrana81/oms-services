package com.oms.order.client;

import com.oms.order.dto.StockRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryClient {

    private final RestTemplate restTemplate;

    @Value("${inventory.service.url:http://localhost:8081}")
    private String inventoryServiceUrl;

    //This method calls the inventory service to check if the stock is available and reserve it for the user
    public void reserveStock(Long productId, Integer quantity) {
        String url = inventoryServiceUrl + "/api/products/reserve-stock";
        StockRequest request = StockRequest.builder()
                .productId(productId)
                .quantity(quantity)
                .build();
        
        restTemplate.postForObject(url, request, Void.class);
    }
}