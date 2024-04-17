package team.CowsAndHorses.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;
import team.CowsAndHorses.domain.Profile;
import team.CowsAndHorses.domain.User;
import team.CowsAndHorses.dto.PageQueryDto;

import java.util.List;

@Transactional
public interface UserService extends IService<User> {
    Profile getProfileInfo(Integer userId);
    Integer updateProfileInfo(Integer userId, Profile profile);
    Integer updateProfile(Integer userId, String avatarUrl);
    User getUserInfo(Integer id);
    IPage<User> getAllUsers(PageQueryDto pageQuery);
    Integer deleteUserByIds(List<Integer> ids);
    Integer updateUser(User newUser);
    Integer addUser(User user);
    Integer excelImport(List<User> list);
}
