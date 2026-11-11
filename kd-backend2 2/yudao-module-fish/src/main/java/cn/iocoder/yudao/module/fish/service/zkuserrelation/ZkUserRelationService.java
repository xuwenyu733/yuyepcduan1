package cn.iocoder.yudao.module.fish.service.zkuserrelation;

import java.util.*;

import cn.iocoder.yudao.framework.common.base_api.BaseUserRelationService;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkuserrelation.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkuserrelation.ZkUserRelationDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 用户信息关联 Service 接口
 *
 * @author admin2
 */
public interface ZkUserRelationService extends BaseUserRelationService {

    /**
     * 创建用户信息关联
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkUserRelation(@Valid ZkUserRelationSaveReqVO createReqVO);

    /**
     * 更新用户信息关联
     *
     * @param updateReqVO 更新信息
     */
    void updateZkUserRelation(@Valid ZkUserRelationSaveReqVO updateReqVO);

    /**
     * 删除用户信息关联
     *
     * @param id 编号
     */
    void deleteZkUserRelation(Long id);

    /**
    * 批量删除用户信息关联
    *
    * @param ids 编号
    */
    void deleteZkUserRelationListByIds(List<Long> ids);

    /**
     * 获得用户信息关联
     *
     * @param id 编号
     * @return 用户信息关联
     */
    ZkUserRelationDO getZkUserRelation(Long id);

    /**
     * 获得用户信息关联分页
     *
     * @param pageReqVO 分页查询
     * @return 用户信息关联分页
     */
    PageResult<ZkUserRelationDO> getZkUserRelationPage(ZkUserRelationPageReqVO pageReqVO);

}