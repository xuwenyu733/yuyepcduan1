package cn.iocoder.yudao.module.fish.service.zkbreedspecies;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkbreedspecies.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkbreedspecies.ZkBreedSpeciesDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 养殖品种 Service 接口
 *
 * @author admin2
 */
public interface ZkBreedSpeciesService {

    /**
     * 创建养殖品种
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkBreedSpecies(@Valid ZkBreedSpeciesSaveReqVO createReqVO);

    /**
     * 更新养殖品种
     *
     * @param updateReqVO 更新信息
     */
    void updateZkBreedSpecies(@Valid ZkBreedSpeciesSaveReqVO updateReqVO);

    /**
     * 删除养殖品种
     *
     * @param id 编号
     */
    void deleteZkBreedSpecies(Long id);

    /**
    * 批量删除养殖品种
    *
    * @param ids 编号
    */
    void deleteZkBreedSpeciesListByIds(List<Long> ids);

    /**
     * 获得养殖品种
     *
     * @param id 编号
     * @return 养殖品种
     */
    ZkBreedSpeciesDO getZkBreedSpecies(Long id);

    /**
     * 获得养殖品种分页
     *
     * @param pageReqVO 分页查询
     * @return 养殖品种分页
     */
    PageResult<ZkBreedSpeciesDO> getZkBreedSpeciesPage(ZkBreedSpeciesPageReqVO pageReqVO);

}