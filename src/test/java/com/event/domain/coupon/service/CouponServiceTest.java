package com.event.domain.coupon.service;

import com.event.db.entity.Coupon;
import com.event.domain.coupon.facade.OptimisticLockCouponFacade;
import com.event.domain.coupon.repository.CouponRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CouponServiceTest {

    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private CouponService couponService;
    @Autowired
    private PessimisticLockCouponService pessimisticLockCouponService;
    @Autowired
    private OptimisticLockCouponFacade optimisticLockCouponFacade;

    @BeforeEach
    void setUp() {
        Coupon coupon = Coupon.create(1L, 100L);
        couponRepository.saveAndFlush(coupon);
    }

    @AfterEach
    void tearDown() {
//        couponRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("쿠폰 하나씩 감소 시키기")
    public void decreaseOneCoupon() {
        // when
        couponService.decrease(1L);
        Coupon coupon = couponRepository.findById(1L).orElseThrow();

        // then
        assertThat(coupon.getQuantity()).isEqualTo(99L);
    }

    @Test
    @DisplayName("쿠폰 100개 순차적으로 감소 시키기")
    public void decreaseOneHundredCoupon() {
        // when
        for (int i = 0; i < 100; i++) {
            couponService.decrease(1L);
        }
        Coupon coupon = couponRepository.findById(1L).orElseThrow();

        // then
        assertThat(coupon.getQuantity()).isEqualTo(0L);
    }

    @Test
    @DisplayName("동시에 쿠폰 100개 감소 시키기")
    public void decreaseAtTheSameTime() throws InterruptedException {
        // when
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(100);

        for (int i = 0; i < 100; ++i) {
            executorService.submit(() -> {
                try {
                    couponService.decrease(1L);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        Coupon coupon = couponRepository.findById(1L).orElseThrow();

        // then
        assertThat(coupon.getQuantity()).isEqualTo(0L);
    }

    @Test
    @DisplayName("PessimisticLock 사용")
    public void decreaseAtTheSameTimeWithPessimisticLock() throws InterruptedException {
        // when
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(100);

        for (int i = 0; i < 100; ++i) {
            executorService.submit(() -> {
                try {
                    pessimisticLockCouponService.decrease(1L);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        Coupon coupon = couponRepository.findById(1L).orElseThrow();

        // then
        assertThat(coupon.getQuantity()).isEqualTo(0L);
    }

    @Test
    @DisplayName("OptimisticLock 사용")
    public void decreaseAtTheSameTimeWithOptimisticLock() throws InterruptedException {
        // when
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch countDownLatch = new CountDownLatch(100);

        for (int i = 0; i < 100; ++i) {
            executorService.submit(() -> {
                try {
                    System.out.println(Thread.currentThread() + " : " + Thread.activeCount());
                    optimisticLockCouponFacade.decrease(1L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        Coupon coupon = couponRepository.findById(1L).orElseThrow();

        // then
        assertThat(coupon.getQuantity()).isEqualTo(0L);
    }


}