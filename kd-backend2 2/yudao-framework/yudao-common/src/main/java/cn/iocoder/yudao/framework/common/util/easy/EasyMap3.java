package cn.iocoder.yudao.framework.common.util.easy;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @创建人 lr
 * @创建时间 2024-04-29
 * @描述
 */
@Data
@NoArgsConstructor
@SuppressWarnings("unchecked")
public class EasyMap3<T> {

    public Map<String, T> data = null;

    public EasyMap3<T> put(String key, T obj) {
        if(data == null) {
            data = new HashMap<>();
        }
        data.put(key, obj);
        return this;
    }

    public <V> V getDataValByKey(String key, Class<V> clazz) {
        if(data == null) return null;
        return (V) data.get(key);
    }

    public <V> List<V> getDataValListByKey(String key, Class<V> clazz) {
        if(data == null) return new ArrayList<>();
        return (List<V>) data.get(key);
    }

    public Map<String, T> value() {
        if(data == null) {
            data = new HashMap<>();
        }
        return this.data;
    }

    public String toJson() {
        if(data == null) return "";
        return JSON.toJSONString(data);
    }

}
