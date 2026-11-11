package cn.iocoder.yudao.module.fish.service.zkmedicinelogpond;

import java.util.*;

import cn.iocoder.yudao.module.fish.dal.dataobject.zkusemedicinelog.ZkUseMedicineLogDO;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkmedicinelogpond.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmedicinelogpond.ZkMedicineLogPondDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 用药存塘关联 Service 接口
 *
 * @author admin2
 */
public interface ZkMedicineLogPondService {

    /**
     * 创建用药存塘关联
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkMedicineLogPond(@Valid ZkMedicineLogPondSaveReqVO createReqVO);

    /**
     * 更新用药存塘关联
     *
     * @param updateReqVO 更新信息
     */
    void updateZkMedicineLogPond(@Valid ZkMedicineLogPondSaveReqVO updateReqVO);

    /**
     * 删除用药存塘关联
     *
     * @param id 编号
     */
    void deleteZkMedicineLogPond(Long id);

    /**
    * 批量删除用药存塘关联
    *
    * @param ids 编号
    */
    void deleteZkMedicineLogPondListByIds(List<Long> ids);

    /**
     * 获得用药存塘关联
     *
     * @param id 编号
     * @return 用药存塘关联
     */
    ZkMedicineLogPondDO getZkMedicineLogPond(Long id);

    /**
     * 获得用药存塘关联分页
     *
     * @param pageReqVO 分页查询
     * @return 用药存塘关联分页
     */
    PageResult<ZkMedicineLogPondDO> getZkMedicineLogPondPage(ZkMedicineLogPondPageReqVO pageReqVO);

    void customSaveBatch(Long useMedicineLogId, List<ZkMedicineLogPondDO> collect);

    void deleteByMedicineLogId(Long medicineLogId);

    List<ZkMedicineLogPondDO> selectByMedicineLogIds(Set<Long> medicineLogIds);
}