package kr.hhplus.be.server.apps.product.domain.repository;

import kr.hhplus.be.server.apps.product.domain.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository {
    Optional<Product> findProductByProductId(long productId);
    Product save(Product product);
    Page<Product> findAll(Pageable pageable);
    List<Product> findAllById(Iterable<Long> ids);
    Optional<Product> findByIdWithLock(Long productId);
    Optional<Product> findById(Long id);
    void saveAll(List<Product> products);
}
