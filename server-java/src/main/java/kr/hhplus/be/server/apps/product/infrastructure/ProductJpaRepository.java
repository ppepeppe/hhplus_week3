package kr.hhplus.be.server.apps.product.infrastructure;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.apps.product.domain.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface ProductJpaRepository extends JpaRepository<Product, Long> {
    Optional<Product> findProductByProductId(long productId);
    // 페이지네이션 지원 메서드
    Page<Product> findAll(Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.productId = :productId")
    Product findByIdWithLock(Long productId);
}
