package com.event.domain.coupon.facade;

import com.event.domain.coupon.service.OptimisticLockCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OptimisticLockCouponFacade {

    private final OptimisticLockCouponService optimisticLockCouponService;

    public void decrease(Long couponId) throws InterruptedException {
        while (true) {
            try {
                optimisticLockCouponService.decrease(couponId);
                break;
            } catch (Exception e){
                System.out.println("대기중");
                Thread.sleep(50);
            }
        }
    }

}
