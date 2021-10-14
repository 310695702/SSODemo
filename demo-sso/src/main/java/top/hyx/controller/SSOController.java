package top.hyx.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.hyx.exception.MyException;
import top.hyx.pojo.UserNoPassword;
import top.hyx.pojo.vo.UserVO;
import top.hyx.service.UserService;
import top.hyx.utils.JSONResult;
import top.hyx.utils.JsonUtils;
import top.hyx.utils.MD5Utils;
import top.hyx.utils.RedisOperator;

import javax.servlet.http.Cookie;
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

    public static final String REDIS_USER_TICKET = "redis_user_ticket";

    public static final String REDIS_TMP_TICKET = "redis_tmp_ticket";

    public static final String COOKIE_USER_TICKET = "cookie_user_ticket";

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
        //1.获取userTicket门票
        String userTicket = getCookie(request,COOKIE_USER_TICKET);
        boolean isVerified = verifyUserTicket(userTicket);
        if (isVerified){
            String tmpTicket = getTmpTicket();
            return "redirect:" + returnUrl + "?tmpTicket="+tmpTicket;
        }
        return "login";
    }

    /**
     * 校验CAS全局用户门票
     * @param userTicket
     * @return
     */
    private boolean verifyUserTicket(String userTicket){
        if (StringUtils.isEmpty(userTicket)){
            return false;
        }
        String userId = redisOperator.get(REDIS_USER_TICKET+":"+userTicket);
        if (StringUtils.isEmpty(userId)){
            return false;
        }
        String userRedis = redisOperator.get(REDIS_USER_TOKEN+":"+userId);
        if (StringUtils.isEmpty(userRedis)){
            return false;
        }
        return true;
    }

    /**
     * CAS的统一登录接口
     *      目的：
     *          1.登录后创建用户的全局会话                  ->  uniqueToken
     *          2.创建用户全局门票，用以表示在CAS端是否登录    ->  userTicket
     *          3.创建用户的临时票据，用于回跳回传            ->  tmpTicket
     * @param username
     * @param password
     * @param returnUrl
     * @param model
     * @param request
     * @param response
     * @return
     */
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

        //3.生成Ticket全局门票，代表用户在CAS端登录过
        String userTicket = UUID.randomUUID().toString().trim();
        //3.1用户全局门票需要放入CAS端的cookie中
        setCookie(COOKIE_USER_TICKET,userTicket,response);
        //4.userTicket关联用户id，并且放入redis，代表该用户有门票了
        redisOperator.set(REDIS_USER_TICKET+":"+userTicket,userNoPassword.getId().toString());
        //5.生成临时票据，回跳到调用端网址，由CAS端所签发的一个一次性的临时ticket
        String tmpTicket = getTmpTicket();
        return "redirect:" + returnUrl + "?tmpTicket="+tmpTicket;
    }

    @PostMapping("/verifyTmpTicket")
    @ResponseBody
    public JSONResult verifyTmpTicket(String tmpTicket,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        //使用一次性临时票据来验证是否登录，如果登录过把用户会话信息返回给站点
        //使用完成后需要销毁
        String tmpTicketValue = redisOperator.get(REDIS_TMP_TICKET+":"+tmpTicket);
        if (StringUtils.isEmpty(tmpTicketValue)){
            return JSONResult.errorUserTicket("用户票据异常");
        }
        //如果票据OK则需要销毁，并且拿到CAS端cookie中的全局userTicket，再获取用户数据
        if (!tmpTicketValue.equals(MD5Utils.getMD5Str(tmpTicket))){
            return JSONResult.errorUserTicket("用户票据异常");
        }else {
            //销毁临时票据
            redisOperator.del(REDIS_TMP_TICKET+":"+tmpTicket);
        }

        //1.验证并获取用户ticket
        String userTicket = getCookie(request,COOKIE_USER_TICKET);

        String userId = redisOperator.get(REDIS_USER_TICKET+":"+userTicket);
        if (StringUtils.isEmpty(userId)){
            return JSONResult.errorUserTicket("用户票据异常");
        }

        //2.验证门票对应的user会话是否存在
        String userRedis = redisOperator.get(REDIS_USER_TOKEN+":"+userId);
        if (StringUtils.isEmpty(userRedis)){
            return JSONResult.errorUserTicket("用户票据异常");
        }
        //验证成功，返回OK，携带用户会话
        return JSONResult.ok(JsonUtils.jsonToPojo(userRedis,UserVO.class));
    }

    @PostMapping("/logout")
    @ResponseBody
    public JSONResult logout(String userId,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        //0.获取CAS中的用户门票
        String userTicket = getCookie(request, COOKIE_USER_TICKET);

        //1.清除userTicket票据,redis/cookie
        deleteCookie(COOKIE_USER_TICKET, response);
        redisOperator.del(REDIS_USER_TICKET + ":" + userTicket);

        //2.清除用户全局会话（分布式会话）
        redisOperator.del(REDIS_USER_TOKEN+":"+userId);

        return JSONResult.ok();
    }

    /**
     * 创建临时票据
     */
    private String getTmpTicket(){
        String tmpTicket = UUID.randomUUID().toString().trim();
        try {
            redisOperator.set(REDIS_TMP_TICKET+":"+tmpTicket, MD5Utils.getMD5Str(tmpTicket)
                    ,600);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmpTicket;
    }

    private void setCookie(String key,
                           String val,
                           HttpServletResponse response){
        Cookie cookie = new Cookie(key,val);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private String getCookie(HttpServletRequest request,
                             String key){
        Cookie[] cookies = request.getCookies();
        if (cookies == null || StringUtils.isEmpty(key)){
            return null;
        }
        String cookieValue = null;
        for (int i=0;i<cookies.length;i++){
            if (cookies[i].getName().equals(key)){
                cookieValue = cookies[i].getValue();
                break;
            }
        }
        return cookieValue;
    }

    private void deleteCookie(String key,
                              HttpServletResponse response){
        Cookie cookie = new Cookie(key,null);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }
}
