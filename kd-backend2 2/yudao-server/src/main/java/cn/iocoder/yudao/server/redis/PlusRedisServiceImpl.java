package cn.iocoder.yudao.server.redis;

import cn.iocoder.yudao.framework.common.util.easy.EmailUtil;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import cn.iocoder.yudao.framework.redis.core.PlusRedisService;
import com.alibaba.fastjson.JSON;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
@Slf4j
public class PlusRedisServiceImpl implements PlusRedisService {

    // @Autowired
    // private RedisTemplate<String, String> redisTemplate;
    @Resource(name = "stringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 保存字符串类型数据
     * @param str_name 保存字符串名字
     * @param str_value	保存字符串值
     * @param expire_minutes	保存字符串时间，是分钟
     * @return
     */
    public boolean insertStr(String str_name, String str_value, Long expire_minutes){
        try {
            redisTemplate.opsForValue().set(str_name, str_value, expire_minutes, TimeUnit.MINUTES);
            return true;
        } catch (Exception e) {
            log.error("redisService保存系统异常:",e.getLocalizedMessage());
            e.printStackTrace();
            return false;
        }
    }
    public <T> T insertObj(String key, Long minutes, Class<T> clazz, Supplier<T> func) {
        // 1. 缓存获取
        T t = selectObj(key, clazz);
        if(t == null) {
            // 2. db 中获取
            t = func.get();
            if(t != null) insertStr(key, JSON.toJSONString(t), minutes);
            log.warn("【redisService-db中获取对象】middle,  t:{}", toStr(t, 10));
        } else
            log.warn("【redisService-缓存中获取对象】middle, t:{}", toStr(t, 10));
        return t;
    }
    public <T> List<T> insertArr(String key, Long minutes, Class<T> clazz, Supplier<List<T>> func) {
        // 1. 缓存获取
        List<T> t = selectArr(key, clazz);
        if(t == null) {
            // 2. db 中获取
            t = func.get();
            if(t != null) insertStr(key, JSON.toJSONString(t), minutes);
            log.warn("【redisService-db中获取数组】middle,  t:{}", toStr(t, 10));
        } else
            log.warn("【redisService-缓存中获取数组】middle, t:{}", toStr(t, 10));
        return t;
    }

    /**
     * 更新字符串类型数据
     * @param str_name	字符串名字
     * @param str_value	字符串值
     * @param expire_time	字符串时间，是分钟
     * @return
     */
    public boolean updateStr(String str_name,String str_value,Long expire_time){
        try {
            //如果存在直接删除
            Boolean hasKey = redisTemplate.hasKey(str_name);
            if(hasKey){
                redisTemplate.delete(str_name);
            }
            //重新保存
            redisTemplate.opsForValue().set(str_name, str_value, expire_time, TimeUnit.MINUTES);
            return true;
        } catch (Exception e) {
            log.error("redisService更新系统异常:",e.getLocalizedMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除字符串类型数据
     * @param str_name	字符串名字
     * @return
     */
    public Boolean deleteStr(String str_name){
        try {
            //如果存在就获取
            Boolean hasKey = redisTemplate.hasKey(str_name);
            if(hasKey){
                redisTemplate.delete(str_name);
            }
            return true;
        } catch (Exception e) {
            log.error("redisService删除系统异常:",e.getLocalizedMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Boolean exist(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 查找字符串类型数据
     * @param str_name	字符串名字
     * @return
     */
    public String selectStr(String str_name){
        try {
            //如果存在就获取
            Boolean hasKey = redisTemplate.hasKey(str_name);
            Object str = null;
            if(hasKey){
                str= redisTemplate.opsForValue().get(str_name);
            }
            return str != null ? str.toString(): "";
        } catch (Exception e) {
            log.error("redisService查询系统异常:",e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getChildrenByParent(String parentKeyPattern) {
        try {
            log.info("redisService根据redis字符串str_name:{}获取子值",parentKeyPattern);
            // 使用SCAN替代KEYS避免阻塞
            Set<String> keys = redisTemplate.execute(
                    (RedisCallback<Set<String>>) connection -> {
                        Set<String> result = new HashSet<>();
                        Cursor<byte[]> cursor = connection.scan(
                                ScanOptions.scanOptions()
                                        .match(parentKeyPattern + "*")
                                        .count(100) // 分批扫描数量
                                        .build()
                        );
                        while (cursor.hasNext()) {
                            result.add(new String(cursor.next()));
                        }
                        return result;
                    }
            );
            return redisTemplate.opsForValue().multiGet(new ArrayList<>(keys));
        } catch (Exception e) {
            log.error("redisService查询系统异常:",e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从redis中获取对象
     * @param str_name
     * @param clazz
     * @return
     * @param <T>
     */
    public <T> T selectObj(String str_name, Class<T> clazz) {
        try {
            String str = selectStr(str_name);
            if(StringUtils.isEmpty(str)) return null;
            T t = JSON.parseObject(str, clazz);
            return t;
        } catch (Exception e) {
            String sub = String.format("【PlusRedisServiceImpl selectObj异常】key:%s, error:%s", str_name, e.getMessage());
            String msg = String.format("key:%s, clazz:%s, error: %s", str_name, clazz, ExceptionUtil.getStackTrace(e));
            EmailUtil.send(sub, msg);
            log.error(sub, msg);
            return null;
        }
    }

    /**
     * 从redis中获取对象数组
     * @param str_name
     * @param clazz
     * @return
     * @param <T>
     */
    public <T> List<T> selectArr(String str_name, Class<T> clazz) {
        try {
            String str = selectStr(str_name);
            if(StringUtils.isEmpty(str)) return new ArrayList<>();
            List<T> t = JSON.parseArray(str, clazz);
            return t;
        } catch (Exception e) {
            String sub = String.format("【PlusRedisServiceImpl selectArr异常】key:%s, error:%s", str_name, e.getMessage());
            String msg = String.format("key:%s, clazz:%s, error: %s", str_name, clazz, ExceptionUtil.getStackTrace(e));
            EmailUtil.send(sub, msg);
            log.error(sub, msg);
            return null;
        }
    }

    public boolean expire(String token, long minutes) {
        Boolean expire = redisTemplate.expire(token, minutes, TimeUnit.MINUTES);
        return expire;
    }

    public String toStr(Object obj, Integer len) {
        if(obj == null) return "";
        String str = JSON.toJSONString(obj);
        String res = str;
        if(str.length() > (len+1)) res = str.substring(0, len);
        return res;
    }

    public RedisTemplate<String, String> getRedisTemplate() {
        return redisTemplate;
    }
}
