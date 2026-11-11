package cn.iocoder.yudao.framework.redis.core;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.function.Supplier;

public interface PlusRedisService {

    /**
     * 保存字符串类型数据
     * @param str_name 保存字符串名字
     * @param str_value	保存字符串值
     * @param expire_minutes	保存字符串时间，是分钟
     * @return
     */
    boolean insertStr(String str_name, String str_value, Long expire_minutes);
    <T> T insertObj(String key, Long minutes, Class<T> clazz, Supplier<T> func);
    <T> List<T> insertArr(String key, Long minutes, Class<T> clazz, Supplier<List<T>> func);

    /**
     * 更新字符串类型数据
     * @param str_name	字符串名字
     * @param str_value	字符串值
     * @param expire_time	字符串时间，是分钟
     * @return
     */
    boolean updateStr(String str_name,String str_value,Long expire_time);

    /**
     * 删除字符串类型数据
     * @param str_name	字符串名字
     * @return
     */
    Boolean deleteStr(String str_name);

    Boolean exist(String key);

    /**
     * 查找字符串类型数据
     * @param str_name	字符串名字
     * @return
     */
    String selectStr(String str_name);

    List<String> getChildrenByParent(String parentKeyPattern);

    /**
     * 从redis中获取对象
     * @param str_name
     * @param clazz
     * @return
     * @param <T>
     */
    <T> T selectObj(String str_name, Class<T> clazz);

    /**
     * 从redis中获取对象数组
     * @param str_name
     * @param clazz
     * @return
     * @param <T>
     */
    <T> List<T> selectArr(String str_name, Class<T> clazz);

    boolean expire(String token, long minutes);

    String toStr(Object obj, Integer len);

    RedisTemplate<String, String> getRedisTemplate();
}
