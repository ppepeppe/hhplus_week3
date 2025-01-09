package kr.hhplus.be.server.apps.stats.domain.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statsId;
    private Long productId;
    private Integer soldQuantity;
    private LocalDate soldDate;
}
