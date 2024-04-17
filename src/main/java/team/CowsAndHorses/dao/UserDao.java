package team.CowsAndHorses.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import team.CowsAndHorses.domain.Profile;
import team.CowsAndHorses.domain.User;

@Mapper
public interface UserDao extends BaseMapper<Profile> {
}
