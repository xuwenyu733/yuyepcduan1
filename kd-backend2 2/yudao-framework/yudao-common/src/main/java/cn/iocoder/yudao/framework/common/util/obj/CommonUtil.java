package cn.iocoder.yudao.framework.common.util.obj;

import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.Supplier2;
import cn.iocoder.yudao.framework.common.util.easy.*;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 对象操作工具类
 */
@Slf4j
public abstract class CommonUtil {
    
    public static String[] getNullPropertyNames (Object source) {
        Assert.notNull(source, "Source must not be null");
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
        
        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
    public static String[] getNotNullPropertyNames (Object source) {
        Assert.notNull(source, "Source must not be null");
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue != null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
    public static String[] getNotNullValues (Object source) {
        Assert.notNull(source, "Source must not be null");
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue != null) emptyNames.add(srcValue.toString());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * @param source 对象
     * @param property 对象属性
     * @param clazz 返回对象类型
     * @return 根据对象的property获取对象的property对象值
     * @param <T> 返回对象泛型
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValByProperty(Object source, String property, Class<T> clazz) {
        try {
            Assert.notNull(source, "Source must not be null");
            final BeanWrapper src = new BeanWrapperImpl(source);
            java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

            for(java.beans.PropertyDescriptor pd : pds) {
                if(!pd.getName().equals(property)) continue;
                Object srcValue = src.getPropertyValue(pd.getName());
                return (T) srcValue;
            }
            return null;
        } catch (BeansException e) {
            String sub = String.format("【CommonUtil通过property获取value异常】property：%s,clazz:%s", property, clazz);
            String msg = String.format("对应对象：%s, error: %s", JSON.toJSONString(source), ExceptionUtil.getStackTrace(e));
            EmailUtil.send(sub, msg);
            log.error(sub, msg);
            return null;
        }
    }

    /**
     * @param source   对象
     * @param property 需要设置值的属性名
     * @param val      设置的值
     */
    public static void setValByProperty(Object source, String property, Object val) {
        try {
            Assert.notNull(source, "Source must not be null");
            final BeanWrapper src = new BeanWrapperImpl(source);
            java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

            for(java.beans.PropertyDescriptor pd : pds) {
                if(!pd.getName().equals(property)) continue;
                src.setPropertyValue(property, val);
                return;
            }
        } catch (BeansException e) {
            String sub = String.format("【CommonUtil通过property给对象设置值异常】property：%s,val:%s", property, JSON.toJSONString(val));
            String msg = String.format("对应对象：%s, error: %s", JSON.toJSONString(source), ExceptionUtil.getStackTrace(e));
            EmailUtil.send(sub, msg);
            log.error(sub, msg);
        }
    }
    
    public static void copyPropertiesIgnoreNull (Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    /**
     * 给 目标值t 从 源值o中 复制对应属性的值
     * @param t 目标值 target
     * @param o 源值 origin
     * @param oValKeys 源值 value key
     * @param tValKeys 目标值 value key
     * @param <T> 目标值 泛型
     * @param <O> 源值 泛型
     */
    public static <T, O> void origin2Target(T t, O o, List<String> oValKeys, List<String> tValKeys) {
        if(t == null || o == null) return;
        for(int i=0;i<oValKeys.size();i++) {
            String oValKey = oValKeys.get(i);
            String tValKey = tValKeys.get(i);

            Object valByProperty = getValByProperty(o, oValKey, Object.class);
            setValByProperty(t, tValKey, valByProperty);
        }
    }
    public static <T, O> void origin2Target3(O o, List<String> oValKeys, T t, List<String> tValKeys) {
        origin2Target(t, o, oValKeys, tValKeys);
    }
    public static <T, TV, O> void origin2Target4(O o, List<Func1<O, TV>> oValGetters, T t, List<BiConsumer<T, TV>> tValSetters) {
        if(t == null || o == null) return;
        try {
            for(int i=0;i<oValGetters.size();i++) {
                Func1<O, TV> oEasySetter = oValGetters.get(i);
                TV valByProperty = oEasySetter.call(o);

                BiConsumer<T, TV> tEasySetter = tValSetters.get(i);
                tEasySetter.accept(t, valByProperty);
            }
        } catch (Exception e) {
            String sub = String.format("【CommonUtil通过源对象拷贝值到目标对象异常】o:%s, t:%s", o.getClass(), t.getClass());
            String msg = String.format("o:%s, oValGetters:%s, t:%s, tValSetters:%s, error: %s", JSON.toJSONString(o),
                    JSON.toJSONString(oValGetters), JSON.toJSONString(t), JSON.toJSONString(tValSetters), ExceptionUtil.getStackTrace(e));
            EmailUtil.send(sub, msg);
            log.error(sub, msg);
        }
    }
    public static <T> void originMap2Target(T t, Map<String, Object> oMap, List<String> oValKeys, List<String> tValKeys) {
        if(t == null || oMap == null) return;
        for(int i=0;i<oValKeys.size();i++) {
            String oValKey = oValKeys.get(i);
            Object oVal = oMap.get(oValKey);

            String tValKey = tValKeys.get(i);
            setValByProperty(t, tValKey, oVal);
        }
    }
    public static <T> void originMap2Target4(Map<String, Object> oMap, List<String> oValKeys, T t, List<String> tValKeys) {
        originMap2Target(t, oMap, oValKeys, tValKeys);
    }

    /**
     * 给 目标列表 从 原值列表 复制对应属性的值
     * @param tList 目标列表
     * @param oList 源列表
     * @param oIdKey 源值 id key
     * @param tIdKey 目标值 id key
     * @param oValKeys 找到后 原值 keys
     * @param tValKeys 找到后 目标值 keys
     * @param <T> 目标值 泛型
     * @param <O> 源值 泛型
     */
    public static <T, O> void origins2Targets(List<T> tList, List<O> oList, String oIdKey, String tIdKey, List<String> oValKeys, List<String> tValKeys) {
        if(CollectionUtils.isEmpty(oList) || CollectionUtils.isEmpty(tList)) return;
        for(T t : tList) {
            Object tValByProperty = getValByProperty(t, tIdKey, Object.class);
            for(O o : oList) {
                Object oValByProperty = getValByProperty(o, oIdKey, Object.class);

                if(Objects.equals(oValByProperty, tValByProperty)) {
                    origin2Target(t, o, oValKeys, tValKeys);
                }
            }
        }
    }
    public static <T, O> void origins2Targets3(List<O> oList, String oIdKey, List<String> oValKeys, List<T> tList, String tIdKey, List<String> tValKeys) {
        origins2Targets(tList, oList, oIdKey, tIdKey, oValKeys, tValKeys);
    }
    public static <T, TR, TV, O, OR> void origins2Targets4(List<O> oList, EasyGetter<O, OR> oIdGetter, List<Func1<O, TV>> oValGetters,
                                                       List<T> tList, EasyGetter<T, TR> tIdGetter, List<BiConsumer<T, TV>> tValSetters) {
        if(CollectionUtils.isEmpty(oList) || CollectionUtils.isEmpty(tList)) return;
        for(T t : tList) {
            TR tValByProperty = tIdGetter.get(t);
            for(O o : oList) {
                OR oValByProperty = oIdGetter.get(o);

                if(Objects.equals(oValByProperty, tValByProperty)) {
                    origin2Target4(o, oValGetters, t, tValSetters);
                }
            }
        }
    }
    public static <T, TR, TV, O, OR> void origins2Targets4(List<O> oList, EasyGetter<O, OR> oIdGetter,
                                                           List<T> tList, EasyGetter<T, TR> tIdGetter, EasyTwoParam<O, T> fun) {
        if(CollectionUtils.isEmpty(oList) || CollectionUtils.isEmpty(tList)) return;
        for(T t : tList) {
            TR tValByProperty = tIdGetter.get(t);
            for(O o : oList) {
                OR oValByProperty = oIdGetter.get(o);

                if(Objects.equals(oValByProperty, tValByProperty)) {
                    fun.handle(o, t);
                }
            }
        }
    }

    public static <T, O, K_> void originMap2Targets(Map<K_, Map<String, Object>> oMap, String oIdKey, List<String> oValKeys, List<T> tList, String tIdKey, List<String> tValKeys) {
        for(T t : tList) {
            Object tValByProperty = getValByProperty(t, tIdKey, Object.class);
            for(K_ k : oMap.keySet()) {
                Map<String, Object> currMap = oMap.get(k);
                Object oValByProperty = currMap.get(oIdKey);

                if(Objects.equals(oValByProperty, tValByProperty)) {
                    originMap2Target(t, currMap, oValKeys, tValKeys);
                }
            }
        }
    }

    public static <T> void originList2Targets(List<Map<String, Object>> oList, String oIdKey, List<String> oValKeys, List<T> tList, String tIdKey, List<String> tValKeys) {
        for(T t : tList) {
            Object tValByProperty = getValByProperty(t, tIdKey, Object.class);
            for(Map<String, Object> o : oList) {
                Object oValByProperty = o.get(oIdKey);

                if(Objects.equals(oValByProperty, tValByProperty)) {
                    originMap2Target(t, o, oValKeys, tValKeys);
                }
            }
        }
    }

    public static <T> void originList2Targets3(List<Map<String, Object>> oList, String oIdKey, List<T> tList, String tIdKey, EasyTwoParam<Map<String, Object>, T> fun) {
        for(T t : tList) {
            Object tValByProperty = getValByProperty(t, tIdKey, Object.class);
            for(Map<String, Object> o : oList) {
                Object oValByProperty = o.get(oIdKey);

                if(Objects.equals(oValByProperty, tValByProperty)) {
                    fun.handle(o, t);
                }
            }
        }
    }

    public static void copyProperties (Object source, Object target) {
        BeanUtils.copyProperties(source, target);
    }
    
    /**
     * 获取日志名字
     * @return
     * ai1315.2019-04-20.10.log
     */
    public static String getLogName() {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String date = sdf.format(new Date());
    	String logName = new StringBuffer().append("ai1315.").append(date)
    			.append(".0.log").toString();
    	return logName;
    }

    /**
     * 判断文件父路径是否存在
     * @param path 需要带文件名
     * @return
     */
    public static Boolean judgePath(String path) {
        File file = new File(path);
        File parentFile = file.getParentFile();
        if(!parentFile.exists()) {
            parentFile.mkdirs();
            return false;
        }
        return true;
    }
    
}
