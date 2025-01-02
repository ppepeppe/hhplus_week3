package kr.hhplus.be.server.domain.stats.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SalesStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statsId;

    private Long productId;

    private Integer soldQuantity;
}
