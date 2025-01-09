package kr.hhplus.be.server.unit;

import kr.hhplus.be.server.apps.product.domain.models.Product;
import kr.hhplus.be.server.apps.product.domain.repository.ProductRepository;
import kr.hhplus.be.server.apps.product.domain.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    private static final long PRODUCT_ID = 1L;
    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;

    /**
     * 사용자 포인트 조회의 성공 케이스를 테스트합니다.
     */
    @Test
    @DisplayName("상품ID로 해당 상품을 조회한다.")
    void shouldRetrieveProductByUserIdSuccessfully() {
        // given
        when(productRepository.findProductByProductId(PRODUCT_ID)).thenReturn(new Product(PRODUCT_ID, "패딩", 100000, 10, 0));
        // when
        Product product = productService.getProductByProductId(PRODUCT_ID);

        // then
        assertThat(product.getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(product.getName()).isEqualTo("패딩");
        assertThat(product.getPrice()).isEqualTo(100000);
        assertThat(product.getQuantity()).isEqualTo(10);

    }
    /**
     * TODO 상품ID 없는 경우 테스트
     */
    /**
     * 상품 ID로 상품 재고 차감의 성공 케이스를 테스트 합니다.
     */
    @Test
    @DisplayName("상품ID로 해당 상품의 재고를 차감한다.")
    void shouldReduceProductStockSuccessfully() {
        // given
        when(productRepository.findByIdWithLock(PRODUCT_ID)).thenReturn(new Product(PRODUCT_ID, "패딩", 100000, 10, 0));
        when(productRepository.save(any(Product.class))).thenReturn(new Product(PRODUCT_ID, "패딩", 100000, 9, 0));
        // when
        Product product = productService.orderProduct(PRODUCT_ID, 1);

        // then
        assertThat(product.getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(product.getName()).isEqualTo("패딩");
        assertThat(product.getPrice()).isEqualTo(100000);
        assertThat(product.getQuantity()).isEqualTo(9);

    }
    /**
     * 상품 ID로 판매량 증가의 성공 케이스를 테스트 합니다.
     */
    @Test
    @DisplayName("상품ID로 해당 상품의 판매량을 증가시킨다.")
    void shouldIncreaseProductStockSuccessfully() {
        // given
        when(productRepository.findByIdWithLock(PRODUCT_ID)).thenReturn(new Product(PRODUCT_ID, "패딩", 100000, 10, 0));
        when(productRepository.save(new Product(PRODUCT_ID, "패딩", 100000, 9, 1))).thenReturn(new Product(PRODUCT_ID, "패딩", 100000, 9, 1));
        // when
        Product product = productService.orderProduct(PRODUCT_ID, 1);

        // then
        assertThat(product.getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(product.getName()).isEqualTo("패딩");
        assertThat(product.getPrice()).isEqualTo(100000);
        assertThat(product.getQuantity()).isEqualTo(9);
        assertThat(product.getSales()).isEqualTo(1);
    }

    /**
     * 상품의 목록을 조회하는 성공 케이스를 테스트합니다.
     */
    @Test
    @DisplayName("상품목록을 조회한다.")
    void shouldRetrieveProductListSuccessfully() {
        Product product1 = Product.builder()
                .productId(1L)
                .name("패딩")
                .price(200000)
                .quantity(10)
                .sales(0)
                .build();
        Product product2 = Product.builder()
                .productId(2L)
                .name("신발")
                .price(100000)
                .quantity(10)
                .sales(0)
                .build();
        List<Product> productList = List.of(product1, product2);

        Pageable pageable = PageRequest.of(0, 2, Sort.by("productId").ascending()); // page: 0, size: 2
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());

        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // when
        Page<Product> result = productService.getProductList(0, 2);
        // then
        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.getContent().get(0)).isEqualTo(product1);
        assertThat(result.getContent().get(1)).isEqualTo(product2);

    }

}
