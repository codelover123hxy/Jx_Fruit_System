package team.CowsAndHorses.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import team.CowsAndHorses.domain.Cart;
import team.CowsAndHorses.domain.CartGoods;
import team.CowsAndHorses.dto.GoodsDetail;

import java.util.List;

@Mapper
public interface CartDao extends BaseMapper<Cart> {


    @Select("select * from cart_detail where user_id = #{userId} and goods_id = #{goodsId}")
    List<CartGoods> getCartDetailByGoods(@Param("userId") Integer userId,
                                         @Param("goodsId") Integer goodsId);
    @Select("select * from cart_detail where user_id = #{userId} and goods_id = #{goodsId} and selected = 1")
    List<CartGoods> getPreOrderDetailByGoods(@Param("userId") Integer userId,
                                         @Param("goodsId") Integer goodsId);


    @Select("select * from cart_detail where user_id = #{userId}")
    List<CartGoods> getCartDetail(@Param("userId") Integer userId);
    @Select("select * from cart_detail where user_id = #{userId} and selected = 1")
    List<CartGoods> getPreOrderDetail(@Param("userId") Integer userId);


    @Select("select * from goods_detail where sku_id = #{skuId}")
    List<GoodsDetail> getCartDetailByScaleId(@Param("skuId") Integer skuId);

}