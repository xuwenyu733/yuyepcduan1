package cn.iocoder.yudao.module.fish.service.zkgrowbaitingredients;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowbaitingredients.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowbaitingredients.ZkGrowBaitIngredientsDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 饵料成分对比 Service 接口
 *
 * @author admin2
 */
public interface ZkGrowBaitIngredientsService {

    /**
     * 创建饵料成分对比
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkGrowBaitIngredients(@Valid ZkGrowBaitIngredientsSaveReqVO createReqVO);

    /**
     * 更新饵料成分对比
     *
     * @param updateReqVO 更新信息
     */
    void updateZkGrowBaitIngredients(@Valid ZkGrowBaitIngredientsSaveReqVO updateReqVO);

    /**
     * 删除饵料成分对比
     *
     * @param id 编号
     */
    void deleteZkGrowBaitIngredients(Long id);

    /**
    * 批量删除饵料成分对比
    *
    * @param ids 编号
    */
    void deleteZkGrowBaitIngredientsListByIds(List<Long> ids);

    /**
     * 获得饵料成分对比
     *
     * @param id 编号
     * @return 饵料成分对比
     */
    ZkGrowBaitIngredientsDO getZkGrowBaitIngredients(Long id);

    /**
     * 获得饵料成分对比分页
     *
     * @param pageReqVO 分页查询
     * @return 饵料成分对比分页
     */
    PageResult<ZkGrowBaitIngredientsDO> getZkGrowBaitIngredientsPage(ZkGrowBaitIngredientsPageReqVO pageReqVO);

}