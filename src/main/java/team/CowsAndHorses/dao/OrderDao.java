package team.CowsAndHorses.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import team.CowsAndHorses.domain.Order;
import team.CowsAndHorses.dto.OrderDetail;

import java.util.List;

@Mapper
public interface OrderDao extends BaseMapper<Order> {
//    @Select("select * from order_detail where user_id = #{userId}")
//    public List<OrderDetail> getOrderDetail(@Param("userId") Integer userId);
    @Select("select * from order_detail where order_id = #{id}")
    OrderDetail getOrderDetailById(@Param("id") Integer id);

    @Delete("UPDATE tbl_order SET is_deleted = 1 WHERE id = #{id} AND is_deleted = 0")
    Integer deleteOrderById(@Param("id") Integer id);


    @Select("select * from tbl_order where id = #{id}")
    Order getOrderById(@Param("id") Integer id);
    IPage<OrderDetail> selectMyPage(IPage<OrderDetail> page, @Param(Constants.WRAPPER) Wrapper<OrderDetail> queryWrapper);
}