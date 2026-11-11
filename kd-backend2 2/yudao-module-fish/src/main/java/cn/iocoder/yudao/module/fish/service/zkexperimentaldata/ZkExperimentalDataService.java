package cn.iocoder.yudao.module.fish.service.zkexperimentaldata;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkexperimentaldata.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentaldata.ZkExperimentalDataDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 实验数据采集 Service 接口
 *
 * @author admin2
 */
public interface ZkExperimentalDataService {

    /**
     * 创建实验数据采集
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkExperimentalData(@Valid ZkExperimentalDataSaveReqVO createReqVO);

    /**
     * 更新实验数据采集
     *
     * @param updateReqVO 更新信息
     */
    void updateZkExperimentalData(@Valid ZkExperimentalDataSaveReqVO updateReqVO);

    /**
     * 删除实验数据采集
     *
     * @param id 编号
     */
    void deleteZkExperimentalData(Long id);

    /**
    * 批量删除实验数据采集
    *
    * @param ids 编号
    */
    void deleteZkExperimentalDataListByIds(List<Long> ids);

    /**
     * 获得实验数据采集
     *
     * @param id 编号
     * @return 实验数据采集
     */
    ZkExperimentalDataDO getZkExperimentalData(Long id);

    /**
     * 获得实验数据采集分页
     *
     * @param pageReqVO 分页查询
     * @return 实验数据采集分页
     */
    PageResult<ZkExperimentalDataDO> getZkExperimentalDataPage(ZkExperimentalDataPageReqVO pageReqVO);

    List<ZkExperimentalDataDO> getByIds(Set<Long> experimentalIdSet);

    Map<Long, Map<String, ZkExperimentalDataDO>> selectMapByIds(Set<Long> experimentalIdSet);
}