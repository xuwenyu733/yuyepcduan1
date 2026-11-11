package cn.iocoder.yudao.framework.common.util.easy;

@FunctionalInterface
public interface EasyGetter<T, R> {
    R get(T obj);
}

