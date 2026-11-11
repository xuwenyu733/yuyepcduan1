package cn.iocoder.yudao.module.fish.service.zkdayrecord;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkdayrecord.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdayrecord.ZkDayRecordDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 时间字典 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkDayRecordService {

    /**
     * 创建时间字典
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkDayRecord(@Valid ZkDayRecordCreateReqVO createReqVO);

    /**
     * 更新时间字典
     *
     * @param updateReqVO 更新信息
     */
    void updateZkDayRecord(@Valid ZkDayRecordUpdateReqVO updateReqVO);

    /**
     * 删除时间字典
     *
     * @param id 编号
     */
    void deleteZkDayRecord(Long id);

    /**
     * 获得时间字典
     *
     * @param id 编号
     * @return 时间字典
     */
    ZkDayRecordDO getZkDayRecord(Long id);

    /**
     * 获得时间字典列表
     *
     * @param ids 编号
     * @return 时间字典列表
     */
    List<ZkDayRecordDO> getZkDayRecordList(Collection<Long> ids);

    /**
     * 获得时间字典分页
     *
     * @param pageReqVO 分页查询
     * @return 时间字典分页
     */
    PageResult<ZkDayRecordDO> getZkDayRecordPage(ZkDayRecordPageReqVO pageReqVO);

    /**
     * 获得时间字典列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 时间字典列表
     */
    List<ZkDayRecordDO> getZkDayRecordList(ZkDayRecordExportReqVO exportReqVO);

    void customSaveBatch(List<ZkDayRecordDO> days);

    PageResult<ZkDayRecordDO> myGetZkDayRecordPage(ZkDayRecordPageReqVO pageVO);
}
