package com.sqx.testsupport;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;

/**
 * 纯 Mockito 单测中使用 LambdaQuery/UpdateWrapper 前，手动初始化实体表元数据。
 */
public final class MybatisPlusLambdaInit {

    private MybatisPlusLambdaInit() {
    }

    public static void init(Class<?>... entityClasses) {
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(new MybatisConfiguration(), "");
        for (Class<?> entityClass : entityClasses) {
            TableInfoHelper.initTableInfo(assistant, entityClass);
        }
    }
}
