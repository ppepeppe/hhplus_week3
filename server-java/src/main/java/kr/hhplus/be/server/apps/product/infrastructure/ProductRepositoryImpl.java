package kr.hhplus.be.server.apps.product.infrastructure;

import kr.hhplus.be.server.apps.product.domain.models.Product;
import kr.hhplus.be.server.apps.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    public final ProductJpaRepository productJpaRepository;
    @Override
    public Product findProductByProductId(long productId) {
        return productJpaRepository.findProductByProductId(productId);
    }

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(product);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productJpaRepository.findAll(pageable);
    }

    @Override
    public List<Product> findAllById(Iterable<Long> ids) {
        return productJpaRepository.findAllById(ids);
    }

    @Override
    public Product findByIdWithLock(Long productId) {
        return productJpaRepository.findByIdWithLock(productId);
    }


}
