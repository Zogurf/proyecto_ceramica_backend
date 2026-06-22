package com.example.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "favorites", uniqueConstraints = @UniqueConstraint(columnNames = {"id_user", "id_product"}))
public class Favorite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_favorite")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_user", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "id_product", nullable = false)
    private Product product;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
