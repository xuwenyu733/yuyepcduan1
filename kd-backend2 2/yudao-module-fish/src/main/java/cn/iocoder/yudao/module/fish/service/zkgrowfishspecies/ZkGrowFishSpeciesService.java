package cn.iocoder.yudao.module.fish.service.zkgrowfishspecies;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowfishspecies.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowfishspecies.ZkGrowFishSpeciesDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 鱼类品种 Service 接口
 *
 * @author admin2
 */
public interface ZkGrowFishSpeciesService {

    /**
     * 创建鱼类品种
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkGrowFishSpecies(@Valid ZkGrowFishSpeciesSaveReqVO createReqVO);

    /**
     * 更新鱼类品种
     *
     * @param updateReqVO 更新信息
     */
    void updateZkGrowFishSpecies(@Valid ZkGrowFishSpeciesSaveReqVO updateReqVO);

    /**
     * 删除鱼类品种
     *
     * @param id 编号
     */
    void deleteZkGrowFishSpecies(Long id);

    /**
    * 批量删除鱼类品种
    *
    * @param ids 编号
    */
    void deleteZkGrowFishSpeciesListByIds(List<Long> ids);

    /**
     * 获得鱼类品种
     *
     * @param id 编号
     * @return 鱼类品种
     */
    ZkGrowFishSpeciesDO getZkGrowFishSpecies(Long id);

    /**
     * 获得鱼类品种分页
     *
     * @param pageReqVO 分页查询
     * @return 鱼类品种分页
     */
    PageResult<ZkGrowFishSpeciesDO> getZkGrowFishSpeciesPage(ZkGrowFishSpeciesPageReqVO pageReqVO);

    List<ZkGrowFishSpeciesDO> getByIds(Set<Long> speciesIds);
}