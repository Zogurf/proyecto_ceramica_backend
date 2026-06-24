package com.example.backend.controllers;

import com.example.backend.config.JwtAuthenticationFilter;
import com.example.backend.services.JwtService;
import com.example.backend.services.PurchaseIntentService;
import com.example.backend.services.UserDetailsServiceImpl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PurchaseIntentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PurchaseIntentControllerBlackBoxTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PurchaseIntentService purchaseIntentService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;


    @Test
    void registerIntent_WhenRequestIsValid_ReturnsNoContent() throws Exception {
        mockMvc.perform(post("/api/purchase-intentions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "productId": 10,
                                  "interactionType": "VIEW"
                                }
                                """))
                .andExpect(status().isNoContent());
    }
}
