package cn.iocoder.yudao.framework.common.base_api;

import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.collection.MapUtils;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import com.alibaba.fastjson.JSON;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 通用部门接口
 */
public interface BaseDeptService {

    /**
     * 获取系统内所有部门
     * @return 返回部门map信息
     */
    Map<Long, Map<String, Object>> selectDeptList();

    /**
     * @param list 需要设置部门名称的列表
     * @param deptIdKeyOptional 该对象部门id的property
     * @param deptNameKeyOptional 该对象部门名称的property
     * @param <T> 对象泛型
     */
    @SuppressWarnings("unchecked")
    default <T> void listEnhancerDeptName(List<T> list, Optional<String> deptIdKeyOptional, Optional<String> deptNameKeyOptional){
        if(CollectionUtils.isAnyEmpty(list)) return;
        String deptIdKey = deptIdKeyOptional.orElse("deptId");
        String deptNameKey = deptNameKeyOptional.orElse("deptName");
        // 1. 获取 部门信息
        Map<Long, Map<String, Object>> longMapMap = selectDeptList();
        if(CollectionUtils.isAnyEmpty(longMapMap.keySet())) return;
        // 2. 赋值
        list.forEach(item -> {
            MapUtils.findAndThen(longMapMap, CommonUtil.getValByProperty(item, deptIdKey, Long.class), (deptObj) -> {
                // System.out.println("deptObj=================>"+ JSON.toJSONString(deptObj));
                CommonUtil.setValByProperty(item, deptNameKey, deptObj.get("name"));
            });
        });
    }

    /**
     * 获取子部门及传入进来的部门ids
     * @param originalDeptId 父部门
     * @return 当前部门及子部门
     */
    List<Long> getSubDeptIds(Long originalDeptId);
    default <T> void enhancerDeptIds(T t, Long originalDeptId, String deptIdsProperty) {
        if(t == null) return;
        if(originalDeptId == null || Objects.equals(originalDeptId, 0L)) return;
        List<Long> subDeptIds = getSubDeptIds(originalDeptId);
        CommonUtil.setValByProperty(t, deptIdsProperty, subDeptIds);
    }
}
