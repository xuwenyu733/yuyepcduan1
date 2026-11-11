package cn.iocoder.yudao.module.fish.service.zkusemedicine;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.fish.controller.admin.zkusemedicine.vo.ZkUseMedicineCreateReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkusemedicine.vo.ZkUseMedicineExportReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkusemedicine.vo.ZkUseMedicinePageReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.zkusemedicine.vo.ZkUseMedicineUpdateReqVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkusemedicine.ZkUseMedicineDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;

/**
 * 用药记录 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkUseMedicineService {

    /**
     * 创建用药记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkUseMedicine(@Valid ZkUseMedicineCreateReqVO createReqVO);

    /**
     * 更新用药记录
     *
     * @param updateReqVO 更新信息
     */
    void updateZkUseMedicine(@Valid ZkUseMedicineUpdateReqVO updateReqVO);

    /**
     * 删除用药记录
     *
     * @param id 编号
     */
    void deleteZkUseMedicine(Long id);

    /**
     * 获得用药记录
     *
     * @param id 编号
     * @return 用药记录
     */
    ZkUseMedicineDO getZkUseMedicine(Long id);

    /**
     * 获得用药记录列表
     *
     * @param ids 编号
     * @return 用药记录列表
     */
    List<ZkUseMedicineDO> getZkUseMedicineList(Collection<Long> ids);

    /**
     * 获得用药记录分页
     *
     * @param pageReqVO 分页查询
     * @return 用药记录分页
     */
    PageResult<ZkUseMedicineDO> getZkUseMedicinePage(ZkUseMedicinePageReqVO pageReqVO);

    /**
     * 获得用药记录列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 用药记录列表
     */
    List<ZkUseMedicineDO> getZkUseMedicineList(ZkUseMedicineExportReqVO exportReqVO);
//    void handleProcess(PlWorkflowMessage message);

}
