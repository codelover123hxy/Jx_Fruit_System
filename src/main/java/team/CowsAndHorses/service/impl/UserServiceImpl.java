package team.CowsAndHorses.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import team.CowsAndHorses.dao.LoginDao;
import team.CowsAndHorses.dao.UserDao;
import team.CowsAndHorses.domain.Profile;
import team.CowsAndHorses.domain.User;
import team.CowsAndHorses.dto.PageQueryDto;
import team.CowsAndHorses.service.UserService;

import java.util.List;

@RequiredArgsConstructor
@CacheConfig(cacheNames = "ExpireOneMin")
@Service
public class UserServiceImpl extends ServiceImpl<LoginDao, User> implements UserService {
    final UserDao userDao;
    final LoginDao loginDao;
    @Override
    public Profile getProfileInfo(Integer userId){
        QueryWrapper<Profile> wrapper = new QueryWrapper<>();
        wrapper.eq("id", userId);
        wrapper.select("nick_name", "username", "stu_no", "campus", "gender", "avatar_url");
        return userDao.selectOne(wrapper);
    }
    @Override
    public Integer updateProfileInfo(Integer userId, Profile profile){
        QueryWrapper<Profile> wrapper = new QueryWrapper<>();
        wrapper.eq("id", userId);
        return userDao.update(profile, wrapper);
    }
    @Override
    public Integer updateProfile(Integer userId, String avatarUrl){
        QueryWrapper<Profile> wrapper = new QueryWrapper<>();
        wrapper.eq("id", userId);
        wrapper.select("nick_name", "username", "stu_no", "campus", "gender", "avatar_url");
        Profile profile = userDao.selectOne(wrapper);
        profile.setAvatarUrl(avatarUrl);
        return userDao.update(profile, wrapper);
    }

    @Override
    public User getUserInfo(Integer id) {
        return loginDao.selectById(id);
    }

    @Override
    public IPage<User> getAllUsers(PageQueryDto pageQuery) {
        Page<User> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        return loginDao.selectPage(page, null);
    }
    @Override
    public Integer deleteUserByIds(List<Integer> ids) {
        return loginDao.deleteBatchIds(ids);
    }
    @Override
    public Integer updateUser(User newUser) {
        return loginDao.updateById(newUser);
    }
    @Override
    public Integer addUser(User user) {
        String username = user.getUsername();
        System.out.println(username);
        User res =  loginDao.selectOne(new QueryWrapper<User>().eq("username", username));
        if (res == null) {
            return loginDao.insert(user);
        }
        else {
            return 0;
        }
    }

    @Override
    public Integer excelImport(List<User> list) {
        try {
            for (User user : list) {
                user.setId(null);
                loginDao.insert(user);
            }
            return 1;
        }
        catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }
}
