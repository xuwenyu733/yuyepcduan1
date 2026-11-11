package cn.iocoder.yudao.module.fish.service.zkgrowmodelconfig;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkgrowmodelconfig.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkgrowmodelconfig.ZkGrowModelConfigDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 生长模型配置 Service 接口
 *
 * @author admin2
 */
public interface ZkGrowModelConfigService {

    /**
     * 创建生长模型配置
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkGrowModelConfig(@Valid ZkGrowModelConfigSaveReqVO createReqVO);

    /**
     * 更新生长模型配置
     *
     * @param updateReqVO 更新信息
     */
    void updateZkGrowModelConfig(@Valid ZkGrowModelConfigSaveReqVO updateReqVO);

    /**
     * 删除生长模型配置
     *
     * @param id 编号
     */
    void deleteZkGrowModelConfig(Long id);

    /**
    * 批量删除生长模型配置
    *
    * @param ids 编号
    */
    void deleteZkGrowModelConfigListByIds(List<Long> ids);

    /**
     * 获得生长模型配置
     *
     * @param id 编号
     * @return 生长模型配置
     */
    ZkGrowModelConfigDO getZkGrowModelConfig(Long id);

    /**
     * 获得生长模型配置
     *
     * @param speciesId 品种ID
     * @return 生长模型配置
     */
    ZkGrowModelConfigDO getZkGrowModelConfigBySpeciesId(Long speciesId);

    /**
     * 获得生长模型配置分页
     *
     * @param pageReqVO 分页查询
     * @return 生长模型配置分页
     */
    PageResult<ZkGrowModelConfigDO> getZkGrowModelConfigPage(ZkGrowModelConfigPageReqVO pageReqVO);

}