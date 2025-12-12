package com.incubyte.sweet.service;

import com.incubyte.sweet.dto.PurchaseRequest;
import com.incubyte.sweet.dto.RestockRequest;
import com.incubyte.sweet.dto.SweetRequest;
import com.incubyte.sweet.dto.SweetResponse;

import java.math.BigDecimal;
import java.util.List;

public interface SweetService {
    
    SweetResponse createSweet(SweetRequest request);
    
    List<SweetResponse> getAllSweets();
    
    SweetResponse getSweetById(Long id);
    
    List<SweetResponse> searchSweets(String name, String category, BigDecimal minPrice, BigDecimal maxPrice);
    
    SweetResponse updateSweet(Long id, SweetRequest request);
    
    void deleteSweet(Long id);
    
    void purchaseSweet(Long id, PurchaseRequest request);
    
    void restockSweet(Long id, RestockRequest request);
}
