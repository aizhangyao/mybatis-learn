package com.aiz.mybatis.mapper;

import com.aiz.mybatis.annotations.Param;
import com.aiz.mybatis.annotations.Select;
import com.aiz.mybatis.pojo.User;

import java.util.List;

/**
 * @ClassName UserMapper
 * @Description UserMapper
 * @Author ZhangYao
 * @Date Create in 11:50 2023/3/23
 * @Version 1.0
 */
public interface UserMapper {

    @Select("select * from t_user where sex = #{sex}")
    List<User> getUserBySex(@Param("sex") String sex);

    @Select("select * from t_user where id = #{id}")
    User getUserById(@Param("id") Integer id);
}
