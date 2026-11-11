package cn.iocoder.yudao.framework.common.base_api;

import java.util.Map;

/**
 * 用户关联数据 通用接口
 */
public interface BaseUserRelationService {

    /**
     * 获取用户关联数据
     * @param userId 用户id
     * @param relationType 关联数据类型 cn.iocoder.yudao.framework.common.base_api.enums.UserRelationTypeEnum
     * @return 用户关联数据 userId:用户id; relationValue:关联数据; relationRemark:关联备注;
     */
    Map<String, Object> getUserRelation(Long userId, Integer relationType);

}
