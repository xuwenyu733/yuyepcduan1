package cn.iocoder.yudao.module.fish.service.zkpurifydictdata;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifydictdata.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifydictdata.PurifyDictDataDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 业务字典明细 Service 接口
 *
 * @author admin2
 */
public interface PurifyDictDataService {

    /**
     * 创建业务字典明细
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPurifyDictData(@Valid PurifyDictDataSaveReqVO createReqVO);

    /**
     * 更新业务字典明细
     *
     * @param updateReqVO 更新信息
     */
    void updatePurifyDictData(@Valid PurifyDictDataSaveReqVO updateReqVO);

    /**
     * 删除业务字典明细
     *
     * @param id 编号
     */
    void deletePurifyDictData(Long id);

    /**
    * 批量删除业务字典明细
    *
    * @param ids 编号
    */
    void deletePurifyDictDataListByIds(List<Long> ids);

    /**
     * 获得业务字典明细
     *
     * @param id 编号
     * @return 业务字典明细
     */
    PurifyDictDataDO getPurifyDictData(Long id);

    /**
     * 获得业务字典明细分页
     *
     * @param pageReqVO 分页查询
     * @return 业务字典明细分页
     */
    PageResult<PurifyDictDataDO> getPurifyDictDataPage(PurifyDictDataPageReqVO pageReqVO);

}