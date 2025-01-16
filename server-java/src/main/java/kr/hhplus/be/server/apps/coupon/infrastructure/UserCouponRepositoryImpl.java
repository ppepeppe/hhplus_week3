package kr.hhplus.be.server.apps.coupon.infrastructure;

import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.apps.user.infrastructure.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserCouponRepositoryImpl implements UserCouponRepository {
    private final UserCouponJpaRepository userCouponJpaRepository;
    @Override
    public UserCoupon save(UserCoupon userCoupon) {

        return userCouponJpaRepository.save(userCoupon);
    }

    @Override
    public List<UserCoupon> findAllByUserId(Long userId) {
        return userCouponJpaRepository.findAllByUserId(userId);
    }

    @Override
    public Optional<UserCoupon> findUserCouponByUserIdAndCouponId(Long userId, Long couponId) {
        return userCouponJpaRepository.findByUserIdAndCouponId(userId, couponId);
    }
}
