package team.CowsAndHorses.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import team.CowsAndHorses.domain.Sku;
import team.CowsAndHorses.dto.SkuInfoDto;

import java.util.List;

@Mapper
public interface SkuDao extends BaseMapper<Sku> {
    @Select("select * from sku_info where order_id = #{orderId}")
    public List<SkuInfoDto> selectSkuList(@Param("orderId") Integer orderId);
}
