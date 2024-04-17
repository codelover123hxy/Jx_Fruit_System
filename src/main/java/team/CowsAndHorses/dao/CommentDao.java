package team.CowsAndHorses.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import team.CowsAndHorses.domain.Comment;
@Mapper
public interface CommentDao extends BaseMapper<Comment> {
    @Select("select publisher_id from comment where comment_id = #{commentId}")
    public Integer selectId(@Param("commentId") Integer commentId);
}