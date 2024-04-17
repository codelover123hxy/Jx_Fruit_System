package team.CowsAndHorses.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;
import team.CowsAndHorses.domain.Coupon;
@Transactional
public interface CouponService extends IService<Coupon> {
}
