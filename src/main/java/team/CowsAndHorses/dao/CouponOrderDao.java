package team.CowsAndHorses.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import team.CowsAndHorses.dto.CouponOrderDto;
@Mapper
public interface CouponOrderDao extends BaseMapper<CouponOrderDto> {
}
