package top.hyx.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import top.hyx.exception.MyException;
import top.hyx.pojo.UserNoPassword;
import top.hyx.pojo.vo.UserVO;
import top.hyx.service.UserService;
import top.hyx.utils.JsonUtils;
import top.hyx.utils.RedisOperator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author: HanYuXing
 * @date: 2021-10-13 14:45
 **/
@Controller
public class SSOController {

    final
    UserService userService;

    final
    RedisOperator redisOperator;

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    @Autowired
    public SSOController(UserService userService, RedisOperator redisOperator) {
        this.userService = userService;
        this.redisOperator = redisOperator;
    }


    @GetMapping("/login")
    public String login(String returnUrl,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        model.addAttribute("returnUrl", returnUrl);
        //TODO 后续完善登录校验
        return "login";
    }

    @PostMapping("/doLogin")
    public String doLogin(String username,
                          String password,
                          String returnUrl,
                          Model model,
                          HttpServletRequest request,
                          HttpServletResponse response) {
        model.addAttribute("returnUrl", returnUrl);
        //0.判断用户名密码不能为空
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            model.addAttribute("errmsg", "用户名或密码不能为空");
            return "login";
        }
        //1.实现登录
        UserNoPassword userNoPassword = null;
        try {
            userNoPassword = userService.login(username, password);
        } catch (MyException e) {
            model.addAttribute("errmsg", "用户名或密码不正确");
            return "login";
        }
        //2.实现用户的redis会话
        String uniqueToken = UUID.randomUUID().toString().trim();
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userNoPassword,userVO);
        userVO.setUserUniqueToken(uniqueToken);
        redisOperator.set(REDIS_USER_TOKEN+":"+ userNoPassword.getId(), JsonUtils.objectToJson(userVO));

        return "login";
    }
}
