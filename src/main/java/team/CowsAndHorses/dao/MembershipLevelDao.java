package team.CowsAndHorses.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import team.CowsAndHorses.domain.MembershipLevel;

@Mapper
public interface MembershipLevelDao extends BaseMapper<MembershipLevel> {
}
