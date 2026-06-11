package com.example.backend.services;

import com.example.backend.dto.*;
import com.example.backend.models.Category;
import com.example.backend.models.Product;
import com.example.backend.repositories.CategoryRepository;
import com.example.backend.repositories.ProductRepository;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Logger logger =
            LoggerFactory.getLogger(ProductService.class);

    private final CategoryRepository categoryRepository;

    private final ProductRepository productRepository;

    // ======================
    // DETALLE DEL PRODUCTO
    // ======================

    public ProductDetailResponse getProductDetailsById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Producto no encontrado"));

        // Obtener tamaños del producto
        List<SizeDTO> sizes = product.getSizes()
                .stream()
                .map(size -> new SizeDTO(
                        size.getId(),
                        size.getName(),
                        size.getDimension()
                ))
                .toList();

        // Obtener productos relacionados
        List<RelatedProductDTO> relatedProducts =
                productRepository.findRecommendations(
                                product.getCategory().getId(),
                                product.getId()
                        )
                        .stream()
                        .limit(4)
                        .map(RelatedProductDTO::new)
                        .toList();

        return ProductDetailResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .status(product.isStatus())
                .categoryName(product.getCategory().getName())
                .description(product.getCategory().getDescription())
                .sizes(sizes)
                .relatedProducts(relatedProducts)
                .build();
        }

    public List<AdminProductResponse> getAllAdminProducts() {

        return productRepository.findByStatusTrue()
                .stream()
                .map(this::mapToAdminProductResponse)
                .toList();
    }

    public AdminProductResponse createProduct(
            ProductRequest request
    ) {

        logger.info("Creando producto...");

        Category category =
                categoryRepository.findById(
                                request.getCategoryId()
                        )
                        .orElseThrow(() -> {

                            logger.error(
                                    "Categoria no encontrada: id {}",
                                    request.getCategoryId()
                            );

                            return new RuntimeException(
                                    "Categoria no encontrada"
                            );
                        });

        Product product = new Product();

        product.setName(request.getName());

        product.setPrice(request.getPrice());

        product.setStock(request.getStock());

        product.setImageUrl(request.getImageUrl());

        product.setCategory(category);

        product.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : request.getStock() > 0
        );

        Product saved = productRepository.save(product);

        logger.info(
                "Producto creado con id: {}",
                saved.getId()
        );

        return mapToAdminProductResponse(saved);
    }

    // ======================
    // ADMIN - ACTUALIZAR
    // ======================

    public AdminProductResponse updateProduct(
            Long id,
            ProductRequest request
    ) {

        logger.info(
                "Buscando producto con id {}",
                id
        );

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() -> {

                            logger.error(
                                    "Producto no encontrado. Id: {}",
                                    id
                            );

                            return new RuntimeException(
                                    "Producto no encontrado"
                            );
                        });

        Category category =
                categoryRepository.findById(
                                request.getCategoryId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Categoria no encontrada"
                                ));

        product.setName(request.getName());

        product.setPrice(request.getPrice());

        product.setStock(request.getStock());

        product.setImageUrl(request.getImageUrl());

        product.setCategory(category);

        product.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : request.getStock() > 0
        );

        Product updated =
                productRepository.save(product);

        logger.info(
                "Producto actualizado con id: {}",
                updated.getId()
        );

        return mapToAdminProductResponse(updated);
    }

    // ======================
    // ADMIN - ELIMINAR
    // ======================

    public void deleteProduct(Long id) {

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Producto no encontrado"
                                ));

        product.setStatus(false);

        productRepository.save(product);

        logger.info(
                "Producto inactivo: {}",
                id
        );
    }

    // ======================
    // HOME - DESTACADOS
    // ======================

    public List<AdminProductResponse> getFeaturedProducts() {

        return getAllAdminProducts()
                .stream()
                .collect(Collectors.toMap(
                        AdminProductResponse::getCategoryId,
                        product -> product,
                        (existing, replacement) -> existing
                ))
                .values()
                .stream()
                .limit(4)
                .toList();
    }

    // ======================
    // MAPPER
    // ======================

    private AdminProductResponse mapToAdminProductResponse(
            Product product
    ) {

        return new AdminProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getImageUrl(),
                product.isStatus(),
                product.getCategory().getId(),
                product.getCategory().getName()
        );
    }
}