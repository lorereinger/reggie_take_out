package com.itheima.reggie.common;

/**
 * 基于ThreadLocal的封装工具类，用户存储当前线程的用户id
 */
public class BaseContext {

    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程的用户id
     * @param id
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 获取当前线程的用户id
     * @return
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
