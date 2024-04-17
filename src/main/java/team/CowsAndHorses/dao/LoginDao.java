package team.CowsAndHorses.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import team.CowsAndHorses.domain.Admin;
import team.CowsAndHorses.domain.User;
@Mapper
public interface LoginDao extends BaseMapper<User> {
    @Select("select * from tbl_admin where username = #{username} and password = #{password}")
    public User loginByAdmin(@Param("username") String username, @Param("password") String password);

    @Select("select * from tbl_user where username = #{username} and password = #{password}")
    public User login(@Param("username") String username, @Param("password") String password);
}
