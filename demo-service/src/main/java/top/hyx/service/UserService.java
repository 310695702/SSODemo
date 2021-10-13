package top.hyx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.hyx.exception.MyException;
import top.hyx.pojo.User;
import top.hyx.pojo.bo.UserBo;
import top.hyx.pojo.vo.UserVO;

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

    UserVO login(String username, String password) throws MyException;
}
