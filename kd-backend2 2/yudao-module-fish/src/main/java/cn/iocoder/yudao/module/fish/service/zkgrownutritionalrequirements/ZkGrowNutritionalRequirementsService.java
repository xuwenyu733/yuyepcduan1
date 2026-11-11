package cn.iocoder.yudao.module.fish.service.zkgrownutritionalrequirements;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrownutritionalrequirements.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrownutritionalrequirements.ZkGrowNutritionalRequirementsDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 各阶段营养需求 Service 接口
 *
 * @author admin2
 */
public interface ZkGrowNutritionalRequirementsService {

    /**
     * 创建各阶段营养需求
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkGrowNutritionalRequirements(@Valid ZkGrowNutritionalRequirementsSaveReqVO createReqVO);

    /**
     * 更新各阶段营养需求
     *
     * @param updateReqVO 更新信息
     */
    void updateZkGrowNutritionalRequirements(@Valid ZkGrowNutritionalRequirementsSaveReqVO updateReqVO);

    /**
     * 删除各阶段营养需求
     *
     * @param id 编号
     */
    void deleteZkGrowNutritionalRequirements(Long id);

    /**
    * 批量删除各阶段营养需求
    *
    * @param ids 编号
    */
    void deleteZkGrowNutritionalRequirementsListByIds(List<Long> ids);

    /**
     * 获得各阶段营养需求
     *
     * @param id 编号
     * @return 各阶段营养需求
     */
    ZkGrowNutritionalRequirementsDO getZkGrowNutritionalRequirements(Long id);

    /**
     * 获得各阶段营养需求分页
     *
     * @param pageReqVO 分页查询
     * @return 各阶段营养需求分页
     */
    PageResult<ZkGrowNutritionalRequirementsDO> getZkGrowNutritionalRequirementsPage(ZkGrowNutritionalRequirementsPageReqVO pageReqVO);

}