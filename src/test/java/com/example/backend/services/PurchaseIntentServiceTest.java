package com.example.backend.services;

import com.example.backend.dto.PurchaseIntentRequest;
import com.example.backend.models.Product;
import com.example.backend.models.PurchaseIntent;
import com.example.backend.models.User;
import com.example.backend.repositories.ProductRepository;
import com.example.backend.repositories.PurchaseIntentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseIntentServiceTest {
    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PurchaseIntentRepository purchaseIntentRepository;

    @InjectMocks
    private PurchaseIntentService purchaseIntentService;

    @Test
    void registerIntent_WhenRecentIntentExists_DoesNotSaveDuplicate() {
        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(10L);

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(purchaseIntentRepository.existsByUser_IdAndProduct_IdAndViewedAtAfter(eq(1L), eq(10L), any()))
                .thenReturn(true);

        purchaseIntentService.registerIntent(new PurchaseIntentRequest(10L));

        verify(purchaseIntentRepository, never()).save(any(PurchaseIntent.class));
    }

    @Test
    void registerIntent_WhenNoRecentIntentExists_SavesIntent() {
        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(10L);

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(purchaseIntentRepository.existsByUser_IdAndProduct_IdAndViewedAtAfter(eq(1L), eq(10L), any()))
                .thenReturn(false);

        purchaseIntentService.registerIntent(new PurchaseIntentRequest(10L));

        ArgumentCaptor<PurchaseIntent> intentCaptor = ArgumentCaptor.forClass(PurchaseIntent.class);
        verify(purchaseIntentRepository).save(intentCaptor.capture());

        PurchaseIntent savedIntent = intentCaptor.getValue();
        assertEquals(user, savedIntent.getUser());
        assertEquals(product, savedIntent.getProduct());
        assertNotNull(savedIntent.getViewedAt());
    }
}
