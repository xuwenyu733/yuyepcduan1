package cn.iocoder.yudao.framework.common.util.easy;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EasyDataMapper {

    /**
     * 通过 ids 获取 列表
     * @param mapper 对应mapper对象
     * @param getter 对应属性的lambda表达式
     * @param propertyValueList 属性值
     * @return 对应列表
     * @param <T> 返回泛型
     * @param <R> 属性泛型
     */
    public static <T, R> List<T> listByValueList(BaseMapper<T> mapper, SFunction<T, R> getter, Set<R> propertyValueList) {
        if(CollectionUtils.isAnyEmpty(propertyValueList)) return new ArrayList<>();
        LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(getter, propertyValueList);
        return mapper.selectList(queryWrapper);
    }

    public static <T, R> int deleteIn(BaseMapper<T> mapper, SFunction<T, R> getter, Set<R> propertyValueList) {
        if(CollectionUtils.isAnyEmpty(propertyValueList)) return 0;
        LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(getter, propertyValueList);
        return mapper.delete(queryWrapper);
    }



}
