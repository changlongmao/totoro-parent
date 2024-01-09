package org.totoro.generator.factory;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 代码生成单例对象工厂类
 * @author ChangLF 2023/07/21
 */
@Slf4j
public final class GeneratorSingletonFactory {

    private static final Map<Class<?>, Object> OBJECT_MAP = new ConcurrentHashMap<>();

    private GeneratorSingletonFactory() {
        throw new UnsupportedOperationException("don't instance me");
    }

    /**
     * 通过Class获取单例对象，默认根据无参构造函数初始化
     * @param tClass class对象
     * @author ChangLF 2024/1/9 16:01
     * @return T
     **/
    public static <T> T getBean(Class<T> tClass) {
        Objects.requireNonNull(tClass, "class can't be null");
        Object obj = OBJECT_MAP.get(tClass);
        if (obj != null) {
            return (T) obj;
        }
        return doCreateBean(tClass);
    }

    /**
     * 创建单例对象
     * @param tClass class对象
     * @author ChangLF 2023/7/24 09:05
     * @return org.generator.strategy.GeneratorStrategy
     * @throws if {@link Class#getDeclaredConstructor} can't find, throw NoSuchMethodException
     **/
    private static <T> T doCreateBean(Class<T> tClass) {
        synchronized (tClass) {
            Object obj = OBJECT_MAP.get(tClass);
            if (obj != null) {
                return (T) obj;
            }
            try {
                Object newInstance = tClass.getDeclaredConstructor().newInstance();
                OBJECT_MAP.put(tClass, newInstance);
                return (T) newInstance;
            } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                log.warn("初始化实例{}失败", tClass, e);
                return null;
            }
        }
    }

}
