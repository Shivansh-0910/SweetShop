package com.incubyte.sweet.controller;

import com.incubyte.sweet.dto.PurchaseRequest;
import com.incubyte.sweet.dto.RestockRequest;
import com.incubyte.sweet.dto.SweetRequest;
import com.incubyte.sweet.dto.SweetResponse;
import com.incubyte.sweet.service.SweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/sweets")
@RequiredArgsConstructor
public class SweetController {
    
    private final SweetService sweetService;
    
    @PostMapping
    public ResponseEntity<SweetResponse> createSweet(@Valid @RequestBody SweetRequest request) {
        SweetResponse response = sweetService.createSweet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<SweetResponse>> getAllSweets() {
        List<SweetResponse> sweets = sweetService.getAllSweets();
        return ResponseEntity.ok(sweets);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SweetResponse> getSweetById(@PathVariable Long id) {
        SweetResponse sweet = sweetService.getSweetById(id);
        return ResponseEntity.ok(sweet);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<SweetResponse>> searchSweets(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        
        List<SweetResponse> sweets = sweetService.searchSweets(name, category, minPrice, maxPrice);
        return ResponseEntity.ok(sweets);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<SweetResponse> updateSweet(
            @PathVariable Long id,
            @Valid @RequestBody SweetRequest request) {
        
        SweetResponse response = sweetService.updateSweet(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSweet(@PathVariable Long id) {
        sweetService.deleteSweet(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/purchase")
    public ResponseEntity<Void> purchaseSweet(
            @PathVariable Long id,
            @Valid @RequestBody PurchaseRequest request) {
        
        sweetService.purchaseSweet(id, request);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/restock")
    public ResponseEntity<Void> restockSweet(
            @PathVariable Long id,
            @Valid @RequestBody RestockRequest request) {
        
        sweetService.restockSweet(id, request);
        return ResponseEntity.ok().build();
    }
}
