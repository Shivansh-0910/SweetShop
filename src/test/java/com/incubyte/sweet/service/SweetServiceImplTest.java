package com.incubyte.sweet.service;

import com.incubyte.sweet.dto.PurchaseRequest;
import com.incubyte.sweet.dto.RestockRequest;
import com.incubyte.sweet.dto.SweetRequest;
import com.incubyte.sweet.dto.SweetResponse;
import com.incubyte.sweet.entity.Sweet;
import com.incubyte.sweet.exception.InsufficientStockException;
import com.incubyte.sweet.exception.ResourceNotFoundException;
import com.incubyte.sweet.repository.SweetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SweetService Unit Tests")
class SweetServiceImplTest {

    @Mock
    private SweetRepository sweetRepository;

    @InjectMocks
    private SweetServiceImpl sweetService;

    private SweetRequest validSweetRequest;
    private Sweet testSweet;

    @BeforeEach
    void setUp() {
        validSweetRequest = new SweetRequest(
                "Chocolate Bar",
                "Chocolate",
                new BigDecimal("2.99"),
                100
        );

        testSweet = new Sweet();
        testSweet.setId(1L);
        testSweet.setName("Chocolate Bar");
        testSweet.setCategory("Chocolate");
        testSweet.setPrice(new BigDecimal("2.99"));
        testSweet.setQuantity(100);
    }

    @Test
    @DisplayName("Should create sweet with valid data")
    void createSweet_WithValidData_ShouldSaveAndReturnSweet() {
        // Arrange
        when(sweetRepository.save(any(Sweet.class))).thenReturn(testSweet);

        // Act
        SweetResponse response = sweetService.createSweet(validSweetRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Chocolate Bar");
        assertThat(response.getCategory()).isEqualTo("Chocolate");
        assertThat(response.getPrice()).isEqualByComparingTo(new BigDecimal("2.99"));
        assertThat(response.getQuantity()).isEqualTo(100);

        verify(sweetRepository).save(any(Sweet.class));
    }

    @Test
    @DisplayName("Should get all sweets")
    void getAllSweets_ShouldReturnAllSweets() {
        // Arrange
        Sweet sweet2 = new Sweet();
        sweet2.setId(2L);
        sweet2.setName("Gummy Bears");
        sweet2.setCategory("Gummy");
        sweet2.setPrice(new BigDecimal("1.99"));
        sweet2.setQuantity(50);

        when(sweetRepository.findAll()).thenReturn(Arrays.asList(testSweet, sweet2));

        // Act
        List<SweetResponse> responses = sweetService.getAllSweets();

        // Assert
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getName()).isEqualTo("Chocolate Bar");
        assertThat(responses.get(1).getName()).isEqualTo("Gummy Bears");
    }

    @Test
    @DisplayName("Should get sweet by ID when exists")
    void getSweetById_WhenExists_ShouldReturnSweet() {
        // Arrange
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(testSweet));

        // Act
        SweetResponse response = sweetService.getSweetById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Chocolate Bar");
    }

    @Test
    @DisplayName("Should throw exception when sweet not found")
    void getSweetById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(sweetRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sweetService.getSweetById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Sweet not found with id: 999");
    }

    @Test
    @DisplayName("Should search sweets with filters")
    void searchSweets_WithFilters_ShouldReturnFilteredResults() {
        // Arrange
        when(sweetRepository.searchSweets("Chocolate", "Chocolate", 
                new BigDecimal("1.00"), new BigDecimal("5.00")))
                .thenReturn(Arrays.asList(testSweet));

        // Act
        List<SweetResponse> responses = sweetService.searchSweets(
                "Chocolate", "Chocolate", new BigDecimal("1.00"), new BigDecimal("5.00"));

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo("Chocolate Bar");
    }

    @Test
    @DisplayName("Should update sweet when exists")
    void updateSweet_WhenExists_ShouldUpdateAndReturn() {
        // Arrange
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(testSweet));
        when(sweetRepository.save(any(Sweet.class))).thenReturn(testSweet);

        SweetRequest updateRequest = new SweetRequest(
                "Updated Chocolate",
                "Premium Chocolate",
                new BigDecimal("3.99"),
                150
        );

        // Act
        SweetResponse response = sweetService.updateSweet(1L, updateRequest);

        // Assert
        ArgumentCaptor<Sweet> sweetCaptor = ArgumentCaptor.forClass(Sweet.class);
        verify(sweetRepository).save(sweetCaptor.capture());
        
        Sweet updatedSweet = sweetCaptor.getValue();
        assertThat(updatedSweet.getName()).isEqualTo("Updated Chocolate");
        assertThat(updatedSweet.getCategory()).isEqualTo("Premium Chocolate");
        assertThat(updatedSweet.getPrice()).isEqualByComparingTo(new BigDecimal("3.99"));
        assertThat(updatedSweet.getQuantity()).isEqualTo(150);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent sweet")
    void updateSweet_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(sweetRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sweetService.updateSweet(999L, validSweetRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Sweet not found with id: 999");
    }

    @Test
    @DisplayName("Should delete sweet when exists")
    void deleteSweet_WhenExists_ShouldDelete() {
        // Arrange
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(testSweet));

        // Act
        sweetService.deleteSweet(1L);

        // Assert
        verify(sweetRepository).delete(testSweet);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent sweet")
    void deleteSweet_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(sweetRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sweetService.deleteSweet(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should purchase sweet with sufficient quantity")
    void purchaseSweet_WithSufficientQuantity_ShouldReduceQuantity() {
        // Arrange
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(testSweet));
        when(sweetRepository.save(any(Sweet.class))).thenReturn(testSweet);

        PurchaseRequest purchaseRequest = new PurchaseRequest(10);

        // Act
        sweetService.purchaseSweet(1L, purchaseRequest);

        // Assert
        ArgumentCaptor<Sweet> sweetCaptor = ArgumentCaptor.forClass(Sweet.class);
        verify(sweetRepository).save(sweetCaptor.capture());
        
        Sweet updatedSweet = sweetCaptor.getValue();
        assertThat(updatedSweet.getQuantity()).isEqualTo(90); // 100 - 10
    }

    @Test
    @DisplayName("Should throw exception when purchasing with insufficient quantity")
    void purchaseSweet_WithInsufficientQuantity_ShouldThrowException() {
        // Arrange
        testSweet.setQuantity(5);
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(testSweet));

        PurchaseRequest purchaseRequest = new PurchaseRequest(10);

        // Act & Assert
        assertThatThrownBy(() -> sweetService.purchaseSweet(1L, purchaseRequest))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Insufficient stock");

        verify(sweetRepository, never()).save(any(Sweet.class));
    }

    @Test
    @DisplayName("Should restock sweet with valid quantity")
    void restockSweet_WithValidQuantity_ShouldIncreaseQuantity() {
        // Arrange
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(testSweet));
        when(sweetRepository.save(any(Sweet.class))).thenReturn(testSweet);

        RestockRequest restockRequest = new RestockRequest(50);

        // Act
        sweetService.restockSweet(1L, restockRequest);

        // Assert
        ArgumentCaptor<Sweet> sweetCaptor = ArgumentCaptor.forClass(Sweet.class);
        verify(sweetRepository).save(sweetCaptor.capture());
        
        Sweet updatedSweet = sweetCaptor.getValue();
        assertThat(updatedSweet.getQuantity()).isEqualTo(150); // 100 + 50
    }

    @Test
    @DisplayName("Should throw exception when restocking non-existent sweet")
    void restockSweet_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(sweetRepository.findById(999L)).thenReturn(Optional.empty());

        RestockRequest restockRequest = new RestockRequest(50);

        // Act & Assert
        assertThatThrownBy(() -> sweetService.restockSweet(999L, restockRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should handle purchase reducing quantity to zero")
    void purchaseSweet_ReducingToZero_ShouldWork() {
        // Arrange
        testSweet.setQuantity(10);
        when(sweetRepository.findById(1L)).thenReturn(Optional.of(testSweet));
        when(sweetRepository.save(any(Sweet.class))).thenReturn(testSweet);

        PurchaseRequest purchaseRequest = new PurchaseRequest(10);

        // Act
        sweetService.purchaseSweet(1L, purchaseRequest);

        // Assert
        ArgumentCaptor<Sweet> sweetCaptor = ArgumentCaptor.forClass(Sweet.class);
        verify(sweetRepository).save(sweetCaptor.capture());
        
        Sweet updatedSweet = sweetCaptor.getValue();
        assertThat(updatedSweet.getQuantity()).isEqualTo(0);
    }
}
