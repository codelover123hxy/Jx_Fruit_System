package team.CowsAndHorses.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ImageDao {
    @Select("select img_url from tbl_comment_image where comment_id = #{commentId}")
    public List<String> getImagesByCommentId(@Param("commentId") Integer commentId);

    @Select("select img_url from tbl_goods_image where goods_id = #{goodsId}")
    public List<String> getImagesByGoodsId(@Param("goodsId") Integer goodsId);
}
