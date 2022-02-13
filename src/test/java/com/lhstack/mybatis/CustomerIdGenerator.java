package com.lhstack.mybatis;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;

/**
 * @author: Mr.chen
 * @date: 2022/2/13 19:03
 * @description:
 */
public class CustomerIdGenerator implements IdentifierGenerator {
    @Override
    public Number nextId(Object entity) {
        long nextId = IdUtil.getSnowflakeNextId();
        System.out.println(nextId);
        return nextId;
    }

    @Override
    public String nextUUID(Object entity) {
        String uuid = IdUtil.simpleUUID();
        System.out.println(uuid);
        return uuid;
    }
}
