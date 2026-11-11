package cn.iocoder.yudao.framework.common.util.easy;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @创建人 lr
 * @创建时间 2024-04-01
 * @描述
 */
public class EasyBeanUtils {

    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(bean);
                map.put(field.getName(), value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return map;
    }
    public static String getValByJsonStr(String jsonStr, String key_en) {
        try {
            if(StringUtils.isEmpty(jsonStr)) return "";
            // String[] split = jsonStr.split("\\{");
            // if(split.length>1) {
            //     jsonStr = "{" + split[1];
            // }
            String index = "json";
            if(jsonStr.startsWith("json")) jsonStr = jsonStr.substring(jsonStr.indexOf(index)+index.length());
            JSONObject value = JSON.parseObject(jsonStr);
            if(value == null)
                return "";
            else return value.getString(key_en);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static <T> Map<String, String> beanToMap3(T bean) {
        Map<String, String> map = new HashMap<>();
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(bean);
                map.put(field.getName(), value.toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return map;
    }

    /**
     * 从list中获取对应item，没有返回空
     * @param list   数组
     * @param key   数组.item.属性名  遍历该属性名的值  是否等于 value，如果等于则返回 数组.item
     * @param value
     * @return
     * @param <T>
     */
    public static <T> T getBeanByProperty(List<T> list, String key, Object value) {
        if(CollectionUtils.isAnyEmpty(list))
            return null;
        for(T item : list) {
            String localV = objV(item, key);
            System.out.println(String.format("当前item关键值:%s,目标值：%s", localV, value));
            if(Objects.equals(localV, value.toString())) {
                return item;
            }
        }
        return null;
    }

    /**
     * 传入bean、属性名，获取值
     * @param bean
     * @param property
     * @return
     * @param <T>
     */
    public static <T> String objV(T bean, String property) {
        try {
            BeanWrapperImpl src = new BeanWrapperImpl(bean);
            Object value = src.getPropertyValue(property);
            if(value == null)
                return "";
            else return value.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static <T> String objV3(T bean, String property) {
        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if(StringUtils.equals(field.getName(), property)) {
                    Object value = field.get(bean);
                    if(value == null)
                        return "";
                    else return value.toString();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 传入bean、属性名、属性值，设置值
     * @param bean
     * @param property
     * @return
     * @param <T>
     */
    public static <T> void objSet(T bean, String property, String value) {
        try {
            Class<?> clazz = bean.getClass();
            Field field = clazz.getDeclaredField(property);
            field.setAccessible(true);

            field.set(bean, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static <T> void objSet3(T bean, String property, String value) {
        try {
            BeanWrapperImpl src = new BeanWrapperImpl(bean);
            src.setPropertyValue(property, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 数组收集，从源list中
     * @param list
     * @param properties
     * @return
     * @param <T>
     */
    public static <T, L> HashSet<L> collectV(List<T> list, List<String> properties, Class<L> clazz) {
        HashSet<L> set = new HashSet<>();
        if(CollectionUtils.isAnyEmpty(list))
            return set;
        for(T item : list) {
            for(String property : properties) {
                String val = objV(item, property);
                if(StringUtil.isNotEmpty(val)) set.add(JSON.parseObject(val, clazz));
            }
        }
        return set;
    }

    /**
     * 遍历数组，  从原数组remotes复制值到目标数组locals中
     * @param remotes     源数组
     * @param remoteProperty   相等条件属性，
     * @param remoteVProperty  等值的value的属性
     * @param locals    目标数组
     * @param localPropertys   和源相等值属性数组
     * @param localVpropertys  需要设置值属性   和上面对应
     * @return
     * @param <T>
     * @param <B>
     */
    public static <T, B> List<T> setVal(List<B> remotes, String remoteProperty, String remoteVProperty, List<T> locals, List<String> localPropertys, List<String> localVpropertys) {
        if(CollectionUtils.isAnyEmpty(remotes))
            return locals;
        if(CollectionUtils.isAnyEmpty(locals))
            return new ArrayList<>();
        int count = 0;
        for(String localProperty : localPropertys) {
            for(T local : locals) {
                for(B remote : remotes) {
                    // 1. 如果目标集合的属性和源属性值相同，赋值
                    if(StringUtils.equals(objV(local, localProperty), objV(remote, remoteProperty))) {
                        String localKey = localVpropertys.get(count); // 目标集合的key属性名
                        String remoteV = objV(remote, remoteVProperty); // 目标集合中目标值

                        objSet(local, localKey, remoteV);
                    }
                }
            }
            count++;
        }
        return locals;
    }

    public static void main(String[] args) {
        String a = "33";
        Long aLong = JSON.parseObject(a, Long.class);
        System.out.println(aLong);
    }

}