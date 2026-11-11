package cn.iocoder.yudao.module.fish.service.zkmeetinguser;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkmeetinguser.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmeeting.ZkMeetingDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmeetinguser.ZkMeetingUserDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 会议用户 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkMeetingUserService {

    /**
     * 创建会议用户
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkMeetingUser(@Valid ZkMeetingUserCreateReqVO createReqVO);

    /**
     * 更新会议用户
     *
     * @param updateReqVO 更新信息
     */
    void updateZkMeetingUser(@Valid ZkMeetingUserUpdateReqVO updateReqVO);

    /**
     * 删除会议用户
     *
     * @param id 编号
     */
    void deleteZkMeetingUser(Long id);

    /**
     * 获得会议用户
     *
     * @param id 编号
     * @return 会议用户
     */
    ZkMeetingUserDO getZkMeetingUser(Long id);

    /**
     * 获得会议用户列表
     *
     * @param ids 编号
     * @return 会议用户列表
     */
    List<ZkMeetingUserDO> getZkMeetingUserList(Collection<Long> ids);

    /**
     * 获得会议用户分页
     *
     * @param pageReqVO 分页查询
     * @return 会议用户分页
     */
    PageResult<ZkMeetingUserDO> getZkMeetingUserPage(ZkMeetingUserPageReqVO pageReqVO);

    /**
     * 获得会议用户列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 会议用户列表
     */
    List<ZkMeetingUserDO> getZkMeetingUserList(ZkMeetingUserExportReqVO exportReqVO);

    List<ZkMeetingUserDO> selectByUids(ZkMeetingDO zkMeeting, List<Long> userIds);

    void customSaveBatch(List<ZkMeetingUserDO> list);

    void customDelete(ZkMeetingUserDO zkMeetingUserDO);

    List<ZkMeetingUserDO> getByMeetingIds(Set<Long> meetingIds);
    List<ZkMeetingUserDO> getByMeetingIdsWithLast(Set<Long> meetingIds);

    ZkMeetingUserDO getByMeetingIdWithUserId(Long meetingId, Long userId);

    Integer updateMeetUserToDo(ZkMeetingDO meet);
}
