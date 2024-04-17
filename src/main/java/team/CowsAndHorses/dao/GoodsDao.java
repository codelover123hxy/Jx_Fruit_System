package team.CowsAndHorses.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import team.CowsAndHorses.domain.Goods;
import team.CowsAndHorses.dto.GoodsDetail;

import java.util.List;

@Mapper
public interface GoodsDao extends BaseMapper<Goods> {
    @Select("select * from goods_detail")
    public List<GoodsDetail> selectGoodsDetail();


    @Update("update tbl_goods set is_deleted=1 where id= #{id} and is_deleted=0")
    public Integer deleteLogicById(@Param("id") Integer id);

}
