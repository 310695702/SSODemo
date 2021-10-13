package top.hyx.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.hyx.model.BaseEntity;

/**
 * 无密码的User
 * @author: HanYuXing
 * @date: 2021-10-13 13:35
 **/
@Getter
@Setter
@ToString
public class UserNoPassword extends BaseEntity<UserNoPassword> {

    private String username;

    private String nickName;
}
