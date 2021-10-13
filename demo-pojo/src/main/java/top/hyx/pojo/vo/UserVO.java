package top.hyx.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.hyx.model.BaseEntity;

/**
 * @author: HanYuXing
 * @date: 2021-10-13 13:35
 **/
@Getter
@Setter
@ToString
public class UserVO extends BaseEntity<UserVO> {

    private String username;

    private String nickName;
}
