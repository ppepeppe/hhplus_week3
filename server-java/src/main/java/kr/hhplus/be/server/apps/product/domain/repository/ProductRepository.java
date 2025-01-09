package kr.hhplus.be.server.apps.product.domain.repository;

import kr.hhplus.be.server.apps.product.domain.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository {
    Product findProductByProductId(long productId);
    Product save(Product product);
    Page<Product> findAll(Pageable pageable);
    List<Product> findAllById(Iterable<Long> ids);
    Product findByIdWithLock(Long productId);
}
