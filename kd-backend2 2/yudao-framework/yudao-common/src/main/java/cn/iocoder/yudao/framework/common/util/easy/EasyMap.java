package cn.iocoder.yudao.framework.common.util.easy;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @创建人 lr
 * @创建时间 2024-04-29
 * @描述
 */
@Data
@NoArgsConstructor
public class EasyMap {

    public Map<String, Object> data = null;

    public EasyMap put(String key, Object obj) {
        if(data == null) {
            data = new HashMap<>();
        }
        data.put(key, obj);
        return this;
    }

    public Map<String, Object> value() {
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
