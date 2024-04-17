package team.CowsAndHorses.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;
import team.CowsAndHorses.domain.Address;

import java.util.List;

@Transactional
public interface AddressService extends IService<Address> {
    Integer addAddress(Address addressInfo);
    Integer removeAddress(Integer id);
    Integer updateAddress(Address newInfo);
    List<Address> getAddressList(Integer userId);
    List<Address> getAllAddress();
    Address getAddressById(Integer id);
    Address getDefaultAddress(Integer userId);
}
