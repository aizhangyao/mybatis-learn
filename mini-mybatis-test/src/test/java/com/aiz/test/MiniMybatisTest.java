package com.aiz.test;


import com.aiz.dao.IUserDao;
import com.aiz.io.Resources;
import com.aiz.pojo.User;
import com.aiz.session.SqlSession;
import com.aiz.session.SqlSessionFactory;
import com.aiz.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class MiniMybatisTest {

    /**
     * 传统方式（不使用mapper代理）测试
     */
    @Test
    public void test1() throws Exception {

        // 1.根据配置文件的路径，加载成字节输入流，存到内存中 注意：配置文件还未解析
        InputStream resourceAsStream = Resources.getResourceAsStream("mybatis-config.xml");

        // 2.解析了配置文件，封装了Configuration对象  2.创建sqlSessionFactory工厂对象
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);

        // 3.生产sqlSession 创建了执行器对象
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 4.调用sqlSession方法
        User user = new User();
        user.setId(1);
        user.setUsername("赵六");
        /*User user2 = sqlSession.selectOne("com.aiz.dao.IUserDao.findByCondition", user);
        System.out.println(user2);*/
        List<User> list = sqlSession.selectList("com.aiz.dao.IUserDao.findAll", null);
        for (User user1 : list) {
            System.out.println(user1);
        }

        // 5.释放资源
        sqlSession.close();
    }

    /**
     * mapper代理测试
     */
    @Test
    public void test2() throws Exception {

        // 1.根据配置文件的路径，加载成字节输入流，存到内存中 注意：配置文件还未解析
        InputStream resourceAsStream = Resources.getResourceAsStream("mybatis-config.xml");

        // 2.解析了配置文件，封装了Configuration对象  2.创建sqlSessionFactory工厂对象
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);

        // 3.生产sqlSession 创建了执行器对象
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 4.调用sqlSession方法
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        /*User user1 = new User();
        user1.setId(1);
        user1.setUsername("赵六");
        User user3 = userDao.findByCondition(user1);
        System.out.println(user3);*/
        List<User> all = userDao.findAll();
        all.stream().forEach(System.out::println);

        // 5.释放资源
        sqlSession.close();
    }
}
