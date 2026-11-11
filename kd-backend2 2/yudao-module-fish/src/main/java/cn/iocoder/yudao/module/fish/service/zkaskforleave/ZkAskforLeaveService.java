package cn.iocoder.yudao.module.fish.service.zkaskforleave;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.fish.controller.admin.zkaskforleave.vo.ZkAskforLeaveCreateReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkaskforleave.vo.ZkAskforLeaveExportReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkaskforleave.vo.ZkAskforLeavePageReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkaskforleave.vo.ZkAskforLeaveUpdateReqVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkaskforleave.ZkAskforLeaveDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;

/**
 * 请假 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkAskforLeaveService {

    /**
     * 创建请假
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkAskforLeave(@Valid ZkAskforLeaveCreateReqVO createReqVO);

    /**
     * 更新请假
     *
     * @param updateReqVO 更新信息
     */
    void updateZkAskforLeave(@Valid ZkAskforLeaveUpdateReqVO updateReqVO);

    /**
     * 删除请假
     *
     * @param id 编号
     */
    void deleteZkAskforLeave(Long id);

    /**
     * 获得请假
     *
     * @param id 编号
     * @return 请假
     */
    ZkAskforLeaveDO getZkAskforLeave(Long id);

    /**
     * 获得请假列表
     *
     * @param ids 编号
     * @return 请假列表
     */
    List<ZkAskforLeaveDO> getZkAskforLeaveList(Collection<Long> ids);

    /**
     * 获得请假分页
     *
     * @param pageReqVO 分页查询
     * @return 请假分页
     */
    PageResult<ZkAskforLeaveDO> getZkAskforLeavePage(ZkAskforLeavePageReqVO pageReqVO);

    /**
     * 获得请假列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 请假列表
     */
    List<ZkAskforLeaveDO> getZkAskforLeaveList(ZkAskforLeaveExportReqVO exportReqVO);
//    void handleProcess(PlWorkflowMessage message);

}
