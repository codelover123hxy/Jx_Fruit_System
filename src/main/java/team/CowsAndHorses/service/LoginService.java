package team.CowsAndHorses.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;
import team.CowsAndHorses.domain.User;

@Transactional
public interface LoginService extends IService<User> {
    User login(String username, String password, Integer role);

    User loginByWechat(String openId);
    User registerByWechat(String openId);
    Integer addBrowseNum(Integer userId);
}
