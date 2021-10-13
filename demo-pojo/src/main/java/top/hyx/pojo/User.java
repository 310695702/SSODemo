package top.hyx.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import top.hyx.model.BaseEntity;

import javax.validation.constraints.NotBlank;

/**
 * @author: HanYuXing
 * @date: 2021-10-13 9:19
 **/
@Getter
@Setter
@TableName(autoResultMap = true)
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity<User> {

    @NotBlank(message = "用户名不能为空")
    @Length(max = 64,message = "用户名最长64位")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "昵称不能为空")
    @Length(max = 64,message = "昵称最长64位")
    private String nickName;

}
