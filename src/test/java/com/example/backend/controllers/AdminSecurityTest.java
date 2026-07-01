package com.example.backend.controllers;

import com.example.backend.config.JwtAuthenticationFilter;
import com.example.backend.config.SecurityConfig;
import com.example.backend.repositories.UserRepository;
import com.example.backend.services.AnalyticsService;
import com.example.backend.services.CheckoutService;
import com.example.backend.services.GeminiCampaignService;
import com.example.backend.services.JwtService;
import com.example.backend.services.ProductService;
import com.example.backend.services.PurchaseIntentService;
import com.example.backend.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class AdminSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private CheckoutService checkoutService;

    @MockitoBean
    private PurchaseIntentService purchaseIntentService;

    @MockitoBean
    private GeminiCampaignService geminiCampaignService;

    @MockitoBean
    private AnalyticsService analyticsService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminEndpoint_WhenUserHasAdminRole_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/admin/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Admin funcionando"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminEndpoint_WhenUserDoesNotHaveAdminRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/test"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error")
                        .value("No tienes permisos de administrador para acceder a este recurso"));
    }

    @Test
    void adminEndpoint_WhenUserIsNotAuthenticated_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/test"))
                .andExpect(status().isForbidden());
    }
}
