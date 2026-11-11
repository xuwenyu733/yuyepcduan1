package cn.iocoder.yudao.module.fish.service.zkgrowbaitnutrition;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowbaitnutrition.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowbaitnutrition.ZkGrowBaitNutritionDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 饵料营养成分 Service 接口
 *
 * @author admin2
 */
public interface ZkGrowBaitNutritionService {

    /**
     * 创建饵料营养成分
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkGrowBaitNutrition(@Valid ZkGrowBaitNutritionSaveReqVO createReqVO);

    /**
     * 更新饵料营养成分
     *
     * @param updateReqVO 更新信息
     */
    void updateZkGrowBaitNutrition(@Valid ZkGrowBaitNutritionSaveReqVO updateReqVO);

    /**
     * 删除饵料营养成分
     *
     * @param id 编号
     */
    void deleteZkGrowBaitNutrition(Long id);

    /**
    * 批量删除饵料营养成分
    *
    * @param ids 编号
    */
    void deleteZkGrowBaitNutritionListByIds(List<Long> ids);

    /**
     * 获得饵料营养成分
     *
     * @param id 编号
     * @return 饵料营养成分
     */
    ZkGrowBaitNutritionDO getZkGrowBaitNutrition(Long id);

    /**
     * 获得饵料营养成分分页
     *
     * @param pageReqVO 分页查询
     * @return 饵料营养成分分页
     */
    PageResult<ZkGrowBaitNutritionDO> getZkGrowBaitNutritionPage(ZkGrowBaitNutritionPageReqVO pageReqVO);

}