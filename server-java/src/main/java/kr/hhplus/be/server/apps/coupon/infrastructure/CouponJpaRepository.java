package kr.hhplus.be.server.apps.coupon.infrastructure;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.apps.coupon.domain.models.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Coupon c WHERE c.couponId = :couponId")
    Optional<Coupon> findCouponByCouponIdWithLock(@Param("couponId") Long couponId);

    Optional<Coupon> findCouponByCouponId(Long couponId);
}
