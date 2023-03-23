package com.aiz.mybatis;

import com.aiz.mybatis.annotations.Param;
import com.aiz.mybatis.annotations.Select;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @ClassName MapperProxyFactory
 * @Description MapperProxyFactory
 * @Author ZhangYao
 * @Date Create in 11:49 2023/3/23
 * @Version 1.0
 */
public class MapperProxyFactory {

    private static Map<Class, TypeHandler> typeHandlerMap = new HashMap<>();

    static {
        typeHandlerMap.put(String.class, new StringTypeHandler());
        typeHandlerMap.put(Integer.class, new IntegerTypeHandler());

        // 1、获取驱动
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getMapper(Class<T> mapper) {
        return (T) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{mapper}, (proxy, method, args) -> {
            // 结果支持List或单个User对象
            Object result;

            // 参数名：参数值 示例：arg0,参数值
            // 相当于 变量名：变量值，替换到sql中 示例：name,参数值
            Map<String, Object> paramValueMapping = new HashMap<>(16);
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                paramValueMapping.put(parameter.getName(), args[i]);
                paramValueMapping.put(parameter.getAnnotation(Param.class).value(), args[i]);
            }

            // 2、创建数据库连接
            Connection connection = getConnection();

            // 3、定义sql
            // select * from user where name = #{name}
            String sql = method.getAnnotation(Select.class).value();

            // #{}--->?
            ParameterMappingTokenHandler tokenHandler = new ParameterMappingTokenHandler();
            GenericTokenParser parser = new GenericTokenParser("#{", "}", tokenHandler);
            String parseSql = parser.parse(sql);

            // 4、获取statement对象
            PreparedStatement statement = connection.prepareStatement(parseSql);
            for (int i = 0; i < tokenHandler.getParameterMappings().size(); i++) {
                // sql中的#{}变量名
                String property = tokenHandler.getParameterMappings().get(i).getProperty();
                Object value = paramValueMapping.get(property); // 变量值
                TypeHandler typeHandler = typeHandlerMap.get(value.getClass()); // 根据值类型找TypeHandler
                typeHandler.setParameter(statement, i + 1, value);
            }

            // 5、执行sql
            statement.execute();

            // 6、处理结果
            List<Object> list = new ArrayList<>();
            ResultSet resultSet = statement.getResultSet();

            Class resultType = null;
            Type genericReturnType = method.getGenericReturnType();
            if (genericReturnType instanceof Class) {
                // 不是泛型
                resultType = (Class) genericReturnType;
            } else if (genericReturnType instanceof ParameterizedType) {
                // 是泛型
                Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
                resultType = (Class) actualTypeArguments[0];
            }

            // 根据setter方法记录 属性名：Method对象
            Map<String, Method> setterMethodMapping = new HashMap<>();
            for (Method declaredMethod : resultType.getDeclaredMethods()) {
                if (declaredMethod.getName().startsWith("set")) {
                    String propertyName = declaredMethod.getName().substring(3);
                    propertyName = propertyName.substring(0, 1).toLowerCase(Locale.ROOT) + propertyName.substring(1);
                    setterMethodMapping.put(propertyName, declaredMethod);
                }
            }

            // 记录sql返回的所有字段名
            ResultSetMetaData metaData = resultSet.getMetaData();
            List<String> columnList = new ArrayList<>();
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                columnList.add(metaData.getColumnName(i + 1));
            }

            while (resultSet.next()) {
                Object instance = resultType.newInstance();

                for (String column : columnList) {
                    Method setterMethod = setterMethodMapping.get(column);
                    // 根据setter方法参数类型找到TypeHandler
                    TypeHandler typeHandler = typeHandlerMap.get(setterMethod.getParameterTypes()[0]);
                    setterMethod.invoke(instance, typeHandler.getResult(resultSet, column));
                }
                list.add(instance);
            }
            // 7、释放资源
            connection.close();

            if (method.getReturnType().equals(List.class)) {
                result = list;
            } else {
                result = list.size() == 0 ? null : list.get(0);
            }

            return result;
        });
    }

    private static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mybatis?characterEncoding=utf-8&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=Asia/Shanghai",
                "root", "root");
        return connection;
    }

    @SuppressWarnings("all")
    public static <T> T getMapperSimple(Class<T> mapper) {
        return (T) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{mapper}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // User对象
                Object result = null;
                // 1、获取驱动
                Class.forName("com.mysql.cj.jdbc.Driver");
                // 2、创建数据库连接
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mybatis", "root", "root");
                // 3、定义sql
                String sql = "select * from t_user";
                // 4、获取statement对象
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                // 5、执行sql
                ResultSet resultSet = preparedStatement.executeQuery();
                // 6、处理结果
                while (resultSet.next()) {
                    System.out.println(resultSet.getString("username"));
                }
                // 7、释放资源
                connection.close();
                return result;
            }
        });
    }

}
