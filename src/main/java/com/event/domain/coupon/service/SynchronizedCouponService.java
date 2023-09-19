package com.event.domain.coupon.service;

import com.event.db.entity.Coupon;
import com.event.domain.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
//@Transactional
@RequiredArgsConstructor
public class SynchronizedCouponService {

    private final CouponRepository couponRepository;

    public synchronized void decrease(Long couponId) {
        // 쿠폰 ID 기준으로 쿠폰 조회
        Coupon coupon = couponRepository.findById(couponId).orElseThrow();

        // 재고 감소 시키기
        coupon.decrease();
        couponRepository.saveAndFlush(coupon);
    }

}
