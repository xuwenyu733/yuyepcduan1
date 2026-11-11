package cn.iocoder.yudao.module.fish.service.zkpondwarn;

import java.util.*;
import jakarta.validation.*;

import cn.iocoder.yudao.module.fish.controller.admin.zkdevice.iot.warn.WarnMessage4IOT;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondwarn.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondwarn.ZkPondWarnDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 告警 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkPondWarnService {

    /**
     * 创建告警
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkPondWarn(@Valid ZkPondWarnCreateReqVO createReqVO);
    ZkPondWarnDO createZkPondWarn3(@Valid ZkPondWarnCreateReqVO createReqVO);

    /**
     * 更新告警
     *
     * @param updateReqVO 更新信息
     */
    void updateZkPondWarn(@Valid ZkPondWarnUpdateReqVO updateReqVO);

    /**
     * 删除告警
     *
     * @param id 编号
     */
    void deleteZkPondWarn(Long id);

    /**
     * 获得告警
     *
     * @param id 编号
     * @return 告警
     */
    ZkPondWarnDO getZkPondWarn(Long id);

    /**
     * 获得告警列表
     *
     * @param ids 编号
     * @return 告警列表
     */
    List<ZkPondWarnDO> getZkPondWarnList(Collection<Long> ids);

    /**
     * 获得告警分页
     *
     * @param pageReqVO 分页查询
     * @return 告警分页
     */
    PageResult<ZkPondWarnDO> getZkPondWarnPage(ZkPondWarnPageReqVO pageReqVO);

    /**
     * 获得告警列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 告警列表
     */
    List<ZkPondWarnDO> getZkPondWarnList(ZkPondWarnExportReqVO exportReqVO);

    Integer customInsert(WarnMessage4IOT warnMessage4IOT);

    List<ZkPondWarnDO> selectRecentInfo(Integer lastDays);
}
