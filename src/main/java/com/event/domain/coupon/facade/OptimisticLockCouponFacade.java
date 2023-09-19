package com.event.domain.coupon.facade;

import com.event.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OptimisticLockCouponFacade {

    private final CouponService couponService;

    public void decrease(Long couponId) throws InterruptedException {
        while (true) {
            try {
                couponService.decrease(couponId);
                break;
            } catch (Exception e){
                System.out.println("대기중");
                Thread.sleep(50);
            }
        }
    }

}
