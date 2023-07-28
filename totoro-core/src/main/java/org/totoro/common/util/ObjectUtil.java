package org.totoro.common.util;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.totoro.common.exception.CommonApiException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用object工具包
 *
 * @author changlf 2023-6-25
 */
@Slf4j
public class ObjectUtil {

    /**
     * 对象copy
     * @param source 源对象
	 * @param target 目标对象class
     * @author ChangLF 2023/6/25 11:46
     * @return T 拷贝后的目标对象
     **/
    public static <T> T copy(Object source, Class<T> target) {
        try {
            if (null != source) {
                T t = target.getConstructor().newInstance();
                BeanUtils.copyProperties(source, t);
                return t;
            }
        } catch (Exception e) {
            log.error("source={} target={}", source, target, e);
            throw new CommonApiException("101001011");
        }
        return null;
    }

    /**
     * 对象集合拷贝
     * @param list 源对象集合
	 * @param target 目标对象class
     * @author ChangLF 2023/6/25 11:46
     * @return java.util.List<T> 拷贝后的目标对象集合
     **/
    public static <S, T> List<T> copy(List<S> list, Class<T> target) {
        List<T> returnList = new ArrayList<>();
        try {
            if (CollectionUtils.isNotEmpty(list)) {
                for (Object source : list) {
                    if (null != source) {
                        T t = target.getDeclaredConstructor().newInstance();
                        BeanUtils.copyProperties(source, t);
                        returnList.add(t);
                    }
                }
            }
            return returnList;
        } catch (Exception e) {
            log.error("list={} target={}", list, target, e);
            throw new CommonApiException("101001011");
        }
    }

    /**
     * map转换成object
     */
    public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) {
        Preconditions.checkNotNull(map);
        try {
            Object obj = beanClass.getConstructor().newInstance();
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                field.setAccessible(true);
                field.set(obj, map.get(field.getName()));
            }
            return obj;
        } catch (Exception e) {
            log.error("map={} beanCalss={}", map, beanClass, e);
            throw new CommonApiException("101001011");
        }
    }

    /**
     * object转换成map
     */
    public static Map<String, Object> objectToMap(Object obj) {
        Preconditions.checkNotNull(obj);
        try {
            Map<String, Object> map = new HashMap<>();
            Field[] declaredFields = obj.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            }
            return map;
        } catch (Exception e) {
            log.error("obj={}", obj, e);
            throw new CommonApiException("101001011");
        }
    }

    /**
     * 校验字段是否为Object成员变量
     **/
    public static Boolean objectContainColumn(Object obj, String column) {
        Preconditions.checkNotNull(obj);
        if (StringUtil.isEmpty(column)) {
            return false;
        }
        try {
            Field[] declaredFields = obj.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                if (field.getName().equals(column)) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("obj={}", obj, e);
            throw new CommonApiException("101001011");
        }
        return false;
    }
}
