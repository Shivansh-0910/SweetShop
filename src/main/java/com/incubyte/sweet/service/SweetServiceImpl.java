package com.incubyte.sweet.service;

import com.incubyte.sweet.dto.PurchaseRequest;
import com.incubyte.sweet.dto.RestockRequest;
import com.incubyte.sweet.dto.SweetRequest;
import com.incubyte.sweet.dto.SweetResponse;
import com.incubyte.sweet.entity.Sweet;
import com.incubyte.sweet.exception.InsufficientStockException;
import com.incubyte.sweet.exception.ResourceNotFoundException;
import com.incubyte.sweet.repository.SweetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SweetServiceImpl implements SweetService {
    
    private final SweetRepository sweetRepository;
    
    @Override
    @Transactional
    public SweetResponse createSweet(SweetRequest request) {
        log.debug("Creating sweet: {}", request.getName());
        
        Sweet sweet = new Sweet();
        sweet.setName(request.getName());
        sweet.setCategory(request.getCategory());
        sweet.setPrice(request.getPrice());
        sweet.setQuantity(request.getQuantity());
        
        Sweet savedSweet = sweetRepository.save(sweet);
        log.info("Sweet created successfully: {}", savedSweet.getId());
        
        return mapToResponse(savedSweet);
    }
    
    @Override
    public List<SweetResponse> getAllSweets() {
        log.debug("Fetching all sweets");
        return sweetRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public SweetResponse getSweetById(Long id) {
        log.debug("Fetching sweet by id: {}", id);
        Sweet sweet = sweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sweet not found with id: " + id));
        return mapToResponse(sweet);
    }
    
    @Override
    public List<SweetResponse> searchSweets(String name, String category, BigDecimal minPrice, BigDecimal maxPrice) {
        log.debug("Searching sweets with filters - name: {}, category: {}, minPrice: {}, maxPrice: {}",
                name, category, minPrice, maxPrice);
        
        return sweetRepository.searchSweets(name, category, minPrice, maxPrice).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public SweetResponse updateSweet(Long id, SweetRequest request) {
        log.debug("Updating sweet: {}", id);
        
        Sweet sweet = sweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sweet not found with id: " + id));
        
        sweet.setName(request.getName());
        sweet.setCategory(request.getCategory());
        sweet.setPrice(request.getPrice());
        sweet.setQuantity(request.getQuantity());
        
        Sweet updatedSweet = sweetRepository.save(sweet);
        log.info("Sweet updated successfully: {}", id);
        
        return mapToResponse(updatedSweet);
    }
    
    @Override
    @Transactional
    public void deleteSweet(Long id) {
        log.debug("Deleting sweet: {}", id);
        
        Sweet sweet = sweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sweet not found with id: " + id));
        
        sweetRepository.delete(sweet);
        log.info("Sweet deleted successfully: {}", id);
    }
    
    @Override
    @Transactional
    public void purchaseSweet(Long id, PurchaseRequest request) {
        log.debug("Processing purchase for sweet: {}, quantity: {}", id, request.getQuantity());
        
        Sweet sweet = sweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sweet not found with id: " + id));
        
        if (sweet.getQuantity() < request.getQuantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock. Available: " + sweet.getQuantity() + ", Requested: " + request.getQuantity());
        }
        
        sweet.setQuantity(sweet.getQuantity() - request.getQuantity());
        sweetRepository.save(sweet);
        
        log.info("Purchase completed for sweet: {}, quantity: {}", id, request.getQuantity());
    }
    
    @Override
    @Transactional
    public void restockSweet(Long id, RestockRequest request) {
        log.debug("Restocking sweet: {}, quantity: {}", id, request.getQuantity());
        
        Sweet sweet = sweetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sweet not found with id: " + id));
        
        sweet.setQuantity(sweet.getQuantity() + request.getQuantity());
        sweetRepository.save(sweet);
        
        log.info("Restock completed for sweet: {}, quantity: {}", id, request.getQuantity());
    }
    
    private SweetResponse mapToResponse(Sweet sweet) {
        return new SweetResponse(
                sweet.getId(),
                sweet.getName(),
                sweet.getCategory(),
                sweet.getPrice(),
                sweet.getQuantity()
        );
    }
}
