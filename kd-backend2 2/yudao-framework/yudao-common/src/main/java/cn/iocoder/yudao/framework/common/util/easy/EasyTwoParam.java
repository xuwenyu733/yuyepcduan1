package cn.iocoder.yudao.framework.common.util.easy;

@FunctionalInterface
public interface EasyTwoParam<T, K> {

    void handle(T t, K k);

}
