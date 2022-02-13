package com.lhstack.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lhstack.mybatis.entity.User;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    List<User> findAll();
}

