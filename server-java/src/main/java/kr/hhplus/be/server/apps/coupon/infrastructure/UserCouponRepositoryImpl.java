package kr.hhplus.be.server.apps.coupon.infrastructure;

import kr.hhplus.be.server.apps.coupon.domain.models.UserCoupon;
import kr.hhplus.be.server.apps.coupon.domain.repository.UserCouponRepository;
import kr.hhplus.be.server.apps.user.infrastructure.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserCouponRepositoryImpl implements UserCouponRepository {
    private final UserCouponJpaRepository userCouponJpaRepository;
    @Override
    public UserCoupon save(UserCoupon userCoupon) {

        return userCouponJpaRepository.save(userCoupon);
    }

    @Override
    public UserCoupon findUserCouponByUserId(Long userId) {
        return userCouponJpaRepository.findByUserId(userId);
    }
}
