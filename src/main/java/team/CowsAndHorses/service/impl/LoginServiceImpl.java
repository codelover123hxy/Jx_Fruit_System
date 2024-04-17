package team.CowsAndHorses.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import team.CowsAndHorses.dao.LoginDao;
import team.CowsAndHorses.domain.User;
import team.CowsAndHorses.service.LoginService;
import java.util.UUID;

@RequiredArgsConstructor
@CacheConfig(cacheNames = "ExpireOneMin")
@Service
public class LoginServiceImpl extends ServiceImpl<LoginDao, User> implements LoginService {
    final LoginDao loginDao;
    @Override
    public User login(String username, String password, Integer role){
        if (role == 1) {
            return loginDao.login(username, password);
        }
        else if (role == 2) {
            return loginDao.loginByAdmin(username, password);
        }
        else {
            return null;
        }
    }

    @Override
    public User loginByWechat(String openId) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("open_id", openId);
        return loginDao.selectOne(wrapper);
    }

    @Override
    public User registerByWechat(String openId) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("open_id", openId);
        User user = new User();
        user.setOpenId(openId);
        String randomCode = UUID.randomUUID().toString().substring(0,10);
        user.setUsername("jxxs" + randomCode);
        loginDao.insert(user);
        return loginDao.selectOne(wrapper);
    }
    @Override
    public Integer addBrowseNum(Integer userId) {
        User user = loginDao.selectById(userId);
        user.setBrowseNum(user.getBrowseNum() + 1);
        return loginDao.updateById(user);
    }
}
