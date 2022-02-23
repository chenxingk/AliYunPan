package xin.xingk.www.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;
import xin.xingk.www.entity.User;

public interface UserMapper extends BaseMapper<User> {

    /**
     * 退出登录
     * @return
     */
    @Update("update user set token = null")
    int logout();

}

