package top.hyx.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.hyx.pojo.UserNoPassword;

/**
 * @author: HanYuXing
 * @date: 2021-10-13 16:38
 **/
@Getter
@Setter
@ToString
public class UserVO extends UserNoPassword {

    private String userUniqueToken;

}
