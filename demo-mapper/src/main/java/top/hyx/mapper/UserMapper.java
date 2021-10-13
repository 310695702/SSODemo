package top.hyx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.hyx.pojo.User;

/**
 * @author: HanYuXing
 * @date: 2021-10-13 9:32
 **/
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
