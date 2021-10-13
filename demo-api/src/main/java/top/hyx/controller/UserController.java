package top.hyx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.hyx.exception.MyException;
import top.hyx.pojo.bo.UserBo;
import top.hyx.pojo.vo.UserVO;
import top.hyx.service.UserService;

/**
 * @author: HanYuXing
 * @date: 2021-10-13 9:19
 **/
@Controller
public class UserController {

    final
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseBody
    public String register(UserBo userBo) throws MyException {
        userService.register(userBo);
        return "success";
    }

    @PostMapping("/login")
    @ResponseBody
    public UserVO login(String username, String password) throws MyException {
        UserVO userVO = userService.login(username,password);
        return userVO;
    }

}
