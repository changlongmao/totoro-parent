package org.totoro.generator.factory;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 代码生成单例对象工厂类
 * @author ChangLF 2023/07/21
 */
@Slf4j
public class GeneratorSingletonFactory {

    private static final Map<Class<?>, Object> genMap = new ConcurrentHashMap<>();

    /**
     * 获取单例对象
     * @param genClass GeneratorStrategy class对象
     * @author ChangLF 2023/7/24 09:05
     * @return org.totoro.generator.strategy.GeneratorStrategy
     **/
    public static <T> T doCreateBean(Class<T> genClass) {
        Object obj = genMap.get(genClass);
        if (obj != null) {
            return (T) obj;
        }

        synchronized (GeneratorSingletonFactory.class) {
            try {
                Object newInstance = genClass.getDeclaredConstructor().newInstance();
                genMap.put(genClass, newInstance);
                return (T) newInstance;
            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                log.warn("初始化实例{}失败", genClass, e);
                return null;
            }
        }
    }
}
