package cn.iocoder.yudao.module.fish.service.zkmeetingrecord;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkmeetingrecord.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmeetingrecord.ZkMeetingRecordDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 会议记录 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkMeetingRecordService {

    /**
     * 创建会议记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkMeetingRecord(@Valid ZkMeetingRecordCreateReqVO createReqVO);

    /**
     * 更新会议记录
     *
     * @param updateReqVO 更新信息
     */
    void updateZkMeetingRecord(@Valid ZkMeetingRecordUpdateReqVO updateReqVO);

    /**
     * 删除会议记录
     *
     * @param id 编号
     */
    void deleteZkMeetingRecord(Long id);

    /**
     * 获得会议记录
     *
     * @param id 编号
     * @return 会议记录
     */
    ZkMeetingRecordDO getZkMeetingRecord(Long id);

    /**
     * 获得会议记录列表
     *
     * @param ids 编号
     * @return 会议记录列表
     */
    List<ZkMeetingRecordDO> getZkMeetingRecordList(Collection<Long> ids);

    /**
     * 获得会议记录分页
     *
     * @param pageReqVO 分页查询
     * @return 会议记录分页
     */
    PageResult<ZkMeetingRecordDO> getZkMeetingRecordPage(ZkMeetingRecordPageReqVO pageReqVO);

    /**
     * 获得会议记录列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 会议记录列表
     */
    List<ZkMeetingRecordDO> getZkMeetingRecordList(ZkMeetingRecordExportReqVO exportReqVO);

    Long customSave(ZkMeetingRecordCreateReqVO createReqVO);

    ZkMeetingRecordDO getByMeetingId(Long meetingId);

    ZkMeetingRecordDO updateOrInsert(ZkMeetingRecordDO meetingRecordDO);

    void batchUpdateCustom(List<ZkMeetingRecordDO> needUpdate);
}
