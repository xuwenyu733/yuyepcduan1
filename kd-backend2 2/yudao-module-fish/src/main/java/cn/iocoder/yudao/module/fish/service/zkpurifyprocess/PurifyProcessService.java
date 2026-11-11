package cn.iocoder.yudao.module.fish.service.zkpurifyprocess;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkpurifyprocess.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifyprocess.PurifyProcessDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 优化流程记录 Service 接口
 *
 * @author admin2
 */
public interface PurifyProcessService {

    /**
     * 创建优化流程记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPurifyProcess(@Valid PurifyProcessSaveReqVO createReqVO);

    /**
     * 更新优化流程记录
     *
     * @param updateReqVO 更新信息
     */
    void updatePurifyProcess(@Valid PurifyProcessSaveReqVO updateReqVO);

    /**
     * 删除优化流程记录
     *
     * @param id 编号
     */
    void deletePurifyProcess(Long id);

    /**
    * 批量删除优化流程记录
    *
    * @param ids 编号
    */
    void deletePurifyProcessListByIds(List<Long> ids);

    /**
     * 获得优化流程记录
     *
     * @param id 编号
     * @return 优化流程记录
     */
    PurifyProcessDO getPurifyProcess(Long id);

    /**
     * 获得优化流程记录分页
     *
     * @param pageReqVO 分页查询
     * @return 优化流程记录分页
     */
    PageResult<PurifyProcessDO> getPurifyProcessPage(PurifyProcessPageReqVO pageReqVO);

}