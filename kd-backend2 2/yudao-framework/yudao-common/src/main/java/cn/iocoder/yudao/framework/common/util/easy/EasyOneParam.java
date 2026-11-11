package cn.iocoder.yudao.framework.common.util.easy;

@FunctionalInterface
public interface EasyOneParam<T> {

    void handle(T t);

}
