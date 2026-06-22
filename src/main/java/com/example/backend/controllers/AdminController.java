package com.example.backend.controllers;

import com.example.backend.dto.AdminProductResponse;
import com.example.backend.dto.AdminUserResponse;
import com.example.backend.dto.CampaignRequest;
import com.example.backend.dto.CampaignResponse;
import com.example.backend.dto.OrderResponse;
import com.example.backend.dto.ProductRequest;
import com.example.backend.dto.PurchaseIntentResponse;
import com.example.backend.dto.UpdateFulfillmentStatusRequest;
import com.example.backend.dto.DashboardResponse;
import com.example.backend.dto.CategoryIntentAnalyticsResponse;
import com.example.backend.repositories.UserRepository;
import com.example.backend.services.CheckoutService;
import com.example.backend.services.GeminiCampaignService;
import com.example.backend.services.ProductService;
import com.example.backend.services.PurchaseIntentService;
import com.example.backend.services.AnalyticsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final ProductService productService;
    private final UserRepository userRepository;
    private final CheckoutService checkoutService;
    private final PurchaseIntentService purchaseIntentService;
    private final GeminiCampaignService geminiCampaignService;
    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return analyticsService.dashboard();
    }

    @GetMapping("/purchase-intentions/categories")
    public List<CategoryIntentAnalyticsResponse> getCategoryIntentions(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        return analyticsService.categoryIntentions(startDate, endDate);
    }

    @GetMapping("/test")
    public String test() {
        return "Admin funcionando";
    }

    @GetMapping("/users")
    public List<AdminUserResponse> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new AdminUserResponse(
                        user.getId(),
                        user.getPersona().getName(),
                        user.getEmail(),
                        user.getRole().getName()
                )).toList();
    }

    @GetMapping("/orders")
    public List<OrderResponse> getOrders() {
        return checkoutService.getAdminOrders();
    }

    @PutMapping("/orders/{orderId}/fulfillment")
    public OrderResponse updateOrderFulfillment(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateFulfillmentStatusRequest request
    ) {
        return checkoutService.updateFulfillmentStatus(orderId, request);
    }

    @GetMapping("/purchase-intentions")
    public List<PurchaseIntentResponse> getPurchaseIntentions(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        return purchaseIntentService.getAdminIntentions(startDate, endDate);
    }

    @PostMapping("/campaigns/preview")
    public CampaignResponse previewCampaign(@Valid @RequestBody CampaignRequest request) {
        return geminiCampaignService.previewCampaign(request);
    }

    @PostMapping("/campaigns")
    public CampaignResponse sendCampaign(@Valid @RequestBody CampaignRequest request) {
        return geminiCampaignService.sendCampaign(request);
    }

    // Rutas de Producto
    @GetMapping("/products")
    public List<AdminProductResponse> getProducts() {
        return productService.getAllAdminProducts();
    }

    @PostMapping("/products")
    public AdminProductResponse createProduct(
            @RequestBody ProductRequest request
    ) {
        return productService.createProduct(request);
    }

    @PutMapping("/products/{id}")
    public AdminProductResponse updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequest request
    ) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/products/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
