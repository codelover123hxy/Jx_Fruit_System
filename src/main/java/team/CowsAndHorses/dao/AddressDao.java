package team.CowsAndHorses.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import team.CowsAndHorses.domain.Address;
@Mapper
public interface AddressDao extends BaseMapper<Address> {
}
