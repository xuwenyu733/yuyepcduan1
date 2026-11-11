package cn.iocoder.yudao.framework.common.util.easy;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EasyArrayDeque {


    /**
     * 将list转为set
     * @param list
     * @param <T>
     * @return
     */
    public static synchronized <T, R> ArrayDeque<T> toSet(ArrayDeque<T> list, EasyGetter<T, R> key) {
        if(CollectionUtils.isAnyEmpty(list)) return null;
        Set<R> set = new HashSet<>();
        ArrayDeque<T> res = new ArrayDeque<>();
        for(T t : list) {
            if(set.add(key.get(t))) res.add(t);
        }
        return res;
    }

}
