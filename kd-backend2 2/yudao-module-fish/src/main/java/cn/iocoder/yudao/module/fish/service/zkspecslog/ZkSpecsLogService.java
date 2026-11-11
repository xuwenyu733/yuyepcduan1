package cn.iocoder.yudao.module.fish.service.zkspecslog;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkspecslog.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfishdetail.ZkPondFishDetailDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkspecslog.ZkSpecsLogDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 规格记录 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkSpecsLogService {

    /**
     * 创建规格记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkSpecsLog(@Valid ZkSpecsLogCreateReqVO createReqVO);

    /**
     * 更新规格记录
     *
     * @param updateReqVO 更新信息
     */
    void updateZkSpecsLog(@Valid ZkSpecsLogUpdateReqVO updateReqVO);

    /**
     * 删除规格记录
     *
     * @param id 编号
     */
    void deleteZkSpecsLog(Long id);

    /**
     * 获得规格记录
     *
     * @param id 编号
     * @return 规格记录
     */
    ZkSpecsLogDO getZkSpecsLog(Long id);

    /**
     * 获得规格记录列表
     *
     * @param ids 编号
     * @return 规格记录列表
     */
    List<ZkSpecsLogDO> getZkSpecsLogList(Collection<Long> ids);

    /**
     * 获得规格记录分页
     *
     * @param pageReqVO 分页查询
     * @return 规格记录分页
     */
    PageResult<ZkSpecsLogDO> getZkSpecsLogPage(ZkSpecsLogPageReqVO pageReqVO);

    /**
     * 获得规格记录列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 规格记录列表
     */
    List<ZkSpecsLogDO> getZkSpecsLogList(ZkSpecsLogExportReqVO exportReqVO);

    List<ZkSpecsLogDO> getList();

    void customBatchInsert(List<ZkSpecsLogDO> needAdd);

    void customBatchUpdate(List<ZkSpecsLogDO> needUpdate);

    ZkSpecsLogDO getByPondId(Long pondId);

    List<ZkSpecsLogDO> updateLogBySpecs(ZkPondFishDetailDO detailDO);

    /**
     * 获得饵料系数模型列表
     * @param deptId 归属部门（可选，不传则取登录用户部门）
     * @return 饵料系数模型列表
     */
    List<ZkFcrModelRespVO> getFcrModelList(Long deptId);
}
