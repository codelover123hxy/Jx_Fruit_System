package team.CowsAndHorses.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import team.CowsAndHorses.dao.AddressDao;
import team.CowsAndHorses.domain.Address;
import team.CowsAndHorses.service.AddressService;

import java.util.List;
@RequiredArgsConstructor
@CacheConfig(cacheNames = "ExpireOneMin")
@Service
public class AddressServiceImpl extends ServiceImpl<AddressDao, Address> implements AddressService {
    final AddressDao addressDao;
    @Override
    public Integer addAddress(Address addressInfo){
        try {
            addressDao.insert(addressInfo);
            if (addressInfo.getIsDefault() == 1) {
                setDefaultAddress(addressInfo);
            }
            return 1;
        }
        catch (Exception e){
            return 0;
        }
    }
    @Override
    public Integer removeAddress(Integer id){
        return addressDao.deleteById(id);
    }




    @Override
    public Integer updateAddress(Address newAddressInfo){
        try {
            addressDao.updateById(newAddressInfo);
            if (newAddressInfo.getIsDefault() == 1){
                setDefaultAddress(newAddressInfo);
            }
            return 1;
        }
        catch (Exception e){
            return 0;
        }
    }

    public void setDefaultAddress(Address addressInfo){
        if (addressInfo.getIsDefault() == 1) {
            Integer currentId = addressInfo.getId();
            QueryWrapper<Address> wrapper = new QueryWrapper<>();
            wrapper.eq("is_default", 1);
            wrapper.ne("id", currentId);
            List<Address> myAddressList = addressDao.selectList(wrapper);
            for (Address address : myAddressList) {
                address.setIsDefault(0);
                addressDao.updateById(address);
            }
        }
    }

    @Override
    public List<Address> getAddressList(Integer userId){
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return addressDao.selectList(wrapper);
    }
    @Override
    public List<Address> getAllAddress(){
        return addressDao.selectList(null);
    }
    @Override
    public Address getAddressById(Integer id){
        return addressDao.selectById(id);
    }
    @Override
    public Address getDefaultAddress(Integer userId){
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("is_default", 1);
        return addressDao.selectOne(wrapper);
    }
}
