package cn.iocoder.yudao.module.fish.service.zkmeeting;

import java.util.*;
import jakarta.validation.*;

import cn.iocoder.yudao.module.fish.controller.admin.zkmeeting.to.MeetMsgItem;
import cn.iocoder.yudao.module.fish.controller.admin.zkmeeting.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmeeting.ZkMeetingDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 会议 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkMeetingService {

    /**
     * 创建会议
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkMeeting(@Valid ZkMeetingCreateReqVO createReqVO);

    /**
     * 更新会议
     *
     * @param updateReqVO 更新信息
     */
    void updateZkMeeting(@Valid ZkMeetingUpdateReqVO updateReqVO);

    /**
     * 删除会议
     *
     * @param id 编号
     */
    void deleteZkMeeting(Long id);

    /**
     * 获得会议
     *
     * @param id 编号
     * @return 会议
     */
    ZkMeetingDO getZkMeeting(Long id);
    ZkMeetingDO getByMeetingNum(String meetingNum);
    ZkMeetingDO getByMeetingNum(String meetingNum, String meetDay);

    /**
     * 获得会议列表
     *
     * @param ids 编号
     * @return 会议列表
     */
    List<ZkMeetingDO> getZkMeetingList(Collection<Long> ids);

    /**
     * 获得会议分页
     *
     * @param pageReqVO 分页查询
     * @return 会议分页
     */
    PageResult<ZkMeetingDO> getZkMeetingPage(ZkMeetingPageReqVO pageReqVO);

    /**
     * 获得会议列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 会议列表
     */
    List<ZkMeetingDO> getZkMeetingList(ZkMeetingExportReqVO exportReqVO);

    ZkMeetingDO custom_createZkMeeting(ZkMeetingCreateReqVO createReqVO);

    MeetMsgItem saveMsg(ZkMeetingCreateReqVO createReqVO);

    List<MeetMsgItem> getChatMsgList(Long meetingId, String meetingNum);
}
