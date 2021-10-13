package top.hyx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.hyx.exception.MyException;
import top.hyx.mapper.UserMapper;
import top.hyx.pojo.User;
import top.hyx.pojo.bo.UserBo;
import top.hyx.pojo.UserNoPassword;
import top.hyx.service.UserService;

/**
 * @author: HanYuXing
 * @date: 2021-10-13 9:29
 **/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public void register(UserBo userBo) throws MyException {
        if (userBo == null) {
            throw new MyException(10000, "参数错误");
        }
        User user = new User();
        BeanUtils.copyProperties(userBo, user);
        int count = baseMapper.insert(user);
        if (count != 1) {
            throw new MyException(10001, "新增失败");
        }
    }

    @Override
    public UserNoPassword login(String username, String password) throws MyException {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new MyException(10000, "参数错误");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(!StringUtils.isEmpty(username), "username", username);
        User user = baseMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new MyException(10002, "该用户不存在!");
        }
        UserNoPassword userNoPassword = new UserNoPassword();
        BeanUtils.copyProperties(user, userNoPassword);
        return userNoPassword;
    }
}
