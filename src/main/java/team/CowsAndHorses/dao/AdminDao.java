package team.CowsAndHorses.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import team.CowsAndHorses.domain.Admin;

@Mapper
public interface AdminDao extends BaseMapper<Admin> {
}
