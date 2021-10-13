package top.hyx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.hyx.exception.MyException;
import top.hyx.pojo.User;
import top.hyx.pojo.bo.UserBo;
import top.hyx.pojo.UserNoPassword;

/**
 * @author: HanYuXing
 * @date: 2021-10-13 9:29
 **/
public interface UserService extends IService<User> {

    /**
     * 注册
     * @param userBo
     * @throws MyException
     */
    void register(UserBo userBo) throws MyException;

    UserNoPassword login(String username, String password) throws MyException;
}
