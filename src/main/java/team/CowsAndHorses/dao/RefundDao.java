package team.CowsAndHorses.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import team.CowsAndHorses.domain.CartGoods;
import team.CowsAndHorses.domain.Refund;

import java.util.List;

@Mapper
public interface RefundDao extends BaseMapper<Refund> {

    @Insert("insert into tbl_refund_sku(refund_id, sku_id) value (#{refundId}, #{skuId})")
    void addRefundSku(@Param("refundId") Integer refundId, @Param("skuId") Integer skuId);

    @Select("select sku_id from tbl_refund_sku where refund_id = #{refundId}")
    List<Integer> getSkuIds(@Param("refundId") Integer refundId);

}
