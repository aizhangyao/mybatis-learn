package com.aiz.executor;

import com.aiz.pojo.Configuration;
import com.aiz.pojo.MappedStatement;

import java.util.List;

public interface Executor {

    <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object param) throws Exception;

    void close();
}
