package cn.iocoder.yudao.framework.common.base_api;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.easy.*;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 通用用户接口
 */
public interface BaseUserService {

    /**
     * 获取用户信息 部门信息 角色信息
     * @param userIds 用户ids
     * @return 用户列表
     */
    Map<Long, Map<String, Object>> getFullUserInfo(List<Long> userIds);
    Map<Long, Map<String, Object>> getFullUserInfo();
    Map<String, Object> getFullUserInfo(Long userId);

    /**
     * 用 目标列表 从 用户列表中 获取值，拷贝属性对应关系 参考 oValKeys和tValKeys
     * @param tList 目标列表
     * @param tUserIdKey 用户id
     * @param oValKeys 源值拷贝属性
     * @param tValKeys 目标值拷贝熟悉
     * @param <T> 目标值泛型
     */
    default <T> void itemFullUserInfo(List<T> tList, String tUserIdKey, List<String> oValKeys, List<String> tValKeys) {
        // 1. 获取 用户 信息
        if(EasyListUtil.isEmpty(tList)) return;
        Set<Long> tUserIds = tList.stream().map(obj -> CommonUtil.getValByProperty(obj, tUserIdKey, Object.class)).filter(Objects::nonNull)
                .map(obj -> Long.parseLong(obj.toString())).collect(Collectors.toSet());
        if(CollectionUtils.isAnyEmpty(tUserIds)) return;
        Map<Long, Map<String, Object>> fullUserInfo = getFullUserInfo(new ArrayList<>(tUserIds));
        if(EasyMapUtil.isEmpty(fullUserInfo)) return;
        // 2. 开始赋值
        CommonUtil.originMap2Targets(fullUserInfo,"id", oValKeys, tList, tUserIdKey, tValKeys);
    }

    /**
     * username 、 nickname 、 deptId 、 avatar 、 deptName 、 roleName 、 email 、
     * @param userId 当前用户id
     * @return 获取用对应key的value
     */
    default Object byKey(String userId, String key) {
        if(StringUtil.isEmptyWith0(userId)) return null;
        Long uid = EasyBigDecimalUtil.toLong(userId);
        Map<String, Object> fullUserInfo = getFullUserInfo(uid);
        if(fullUserInfo == null) return "";
        return fullUserInfo.get(key);
    }
    default Object byKey(String userId, EasyOneParam<Map<String, Object>> func) {
        if(StringUtil.isEmptyWith0(userId)) return null;
        Long uid = EasyBigDecimalUtil.toLong(userId);
        Map<String, Object> fullUserInfo = getFullUserInfo(uid);
        if(fullUserInfo == null) return "";
        func.handle(fullUserInfo);
        return fullUserInfo;
    }

}
