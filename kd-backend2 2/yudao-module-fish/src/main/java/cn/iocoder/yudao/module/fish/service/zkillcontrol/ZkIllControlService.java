package cn.iocoder.yudao.module.fish.service.zkillcontrol;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkillcontrol.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkillcontrol.ZkIllControlDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 疾病诊断与防治记录 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkIllControlService {

    /**
     * 创建疾病诊断与防治记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkIllControl(@Valid ZkIllControlCreateReqVO createReqVO);

    /**
     * 更新疾病诊断与防治记录
     *
     * @param updateReqVO 更新信息
     */
    void updateZkIllControl(@Valid ZkIllControlUpdateReqVO updateReqVO);

    /**
     * 删除疾病诊断与防治记录
     *
     * @param id 编号
     */
    ZkIllControlDO deleteZkIllControl(Long id);

    /**
     * 获得疾病诊断与防治记录
     *
     * @param id 编号
     * @return 疾病诊断与防治记录
     */
    ZkIllControlDO getZkIllControl(Long id);

    /**
     * 获得疾病诊断与防治记录列表
     *
     * @param ids 编号
     * @return 疾病诊断与防治记录列表
     */
    List<ZkIllControlDO> getZkIllControlList(Collection<Long> ids);

    /**
     * 获得疾病诊断与防治记录分页
     *
     * @param pageReqVO 分页查询
     * @return 疾病诊断与防治记录分页
     */
    PageResult<ZkIllControlDO> getZkIllControlPage(ZkIllControlPageReqVO pageReqVO);

    /**
     * 获得疾病诊断与防治记录列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 疾病诊断与防治记录列表
     */
    List<ZkIllControlDO> getZkIllControlList(ZkIllControlExportReqVO exportReqVO);

}
