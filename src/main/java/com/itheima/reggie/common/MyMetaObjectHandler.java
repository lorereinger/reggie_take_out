package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元数据对象处理器
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]。。");
        log.info("metaObject:{}",metaObject.toString());
        //填充创建时间
        metaObject.setValue("createTime", LocalDateTime.now());
        //填充更新时间
        metaObject.setValue("updateTime", LocalDateTime.now());
        //填充创建人
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        //填充更新人
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    /**
     * 更新时填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]。。");
        log.info("metaObject:{}",metaObject.toString());

        //输出线程id
        log.info("线程id:{}",Thread.currentThread().getId());
        //填充更新时间
        metaObject.setValue("updateTime", LocalDateTime.now());
        //通过线程获取ID填充更新人
        metaObject.setValue("updateUser", Thread.currentThread().getId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
