package xin.xingk.www.mybatis.config;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;

/**
 * author: Mr.chen
 * date: 2022/2/13 19:03
 * description:Id生成器
 */
public class CustomerIdGenerator implements IdentifierGenerator {
    @Override
    public Number nextId(Object entity) {
        long nextId = IdUtil.getSnowflakeNextId();
        return nextId;
    }

    @Override
    public String nextUUID(Object entity) {
        String uuid = IdUtil.simpleUUID();
        return uuid;
    }
}
