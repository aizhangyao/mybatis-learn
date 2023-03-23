package com.aiz.mybatis;

import com.aiz.mybatis.mapper.UserMapper;
import com.aiz.mybatis.pojo.User;
import org.junit.Test;

import java.util.List;

/**
 * @ClassName TestMyBatis
 * @Description 测试使用手写的MyBatis
 * @Author ZhangYao
 * @Date Create in 11:46 2023/3/23
 * @Version 1.0
 */
public class TestMyBatis {

    @Test
    public void testMybatis() {
        UserMapper mapper = MapperProxyFactory.getMapper(UserMapper.class);
        User user = mapper.getUserById(1);
        System.out.println(user);

        List<User> userList = mapper.getUserBySex("女");
        userList.stream().forEach(System.out::println);
    }

}
