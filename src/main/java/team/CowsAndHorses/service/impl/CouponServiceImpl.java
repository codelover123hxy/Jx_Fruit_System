package team.CowsAndHorses.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import team.CowsAndHorses.dao.CouponDao;
import team.CowsAndHorses.domain.Coupon;
import team.CowsAndHorses.service.CouponService;

@Service
public class CouponServiceImpl extends ServiceImpl<CouponDao, Coupon> implements CouponService {
}
