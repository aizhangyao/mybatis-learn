package com.aiz.test;

import com.aiz.pojo.User;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @className JdbcDemo
 * @description JDBC操作数据库Demo
 * @author ZhangYao
 * @date Create in 18:48 2023/3/23
 * @version 1.0
 */
public class JdbcDemoTest {

    @SuppressWarnings("all")
    @Test
    public void testJDBC() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // 1、加载数据库驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 2、通过驱动管理类获取数据库连接
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mybatis", "root", "root");
            // 3、定义sql语句?表示占位符
            String sql = "select * from t_user where username = ?";
            // 4、获取预处理statement
            preparedStatement = connection.prepareStatement(sql);
            // 5、 设置参数，第一个参数为sql语句中参数的序号(从1开始)，第二个参数为设置的参数值
            preparedStatement.setString(1, "夜不收");
            // 6、向数据库发出sql执行查询，查询出结果集
            resultSet = preparedStatement.executeQuery();
            // 7、遍历查询结果集
            User user = new User();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                int age = resultSet.getInt("age");
                String sex = resultSet.getString("sex");
                String email = resultSet.getString("email");
                // 封装User
                user.setId(id);
                user.setUsername(username);
                user.setPassword(password);
                user.setAge(age);
                user.setSex(sex);
                user.setEmail(email);
            }
            System.out.println(user);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 8、释放资源
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
