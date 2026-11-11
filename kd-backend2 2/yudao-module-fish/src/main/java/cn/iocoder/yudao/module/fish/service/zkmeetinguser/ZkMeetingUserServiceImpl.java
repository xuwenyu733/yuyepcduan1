package cn.iocoder.yudao.module.fish.service.zkmeetinguser;

import cn.iocoder.yudao.framework.common.exception.CommonException;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.security.core.util.TokenUtil;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmeeting.ZkMeetingDO;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import cn.iocoder.yudao.module.fish.controller.admin.zkmeetinguser.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmeetinguser.ZkMeetingUserDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkmeetinguser.ZkMeetingUserConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkmeetinguser.ZkMeetingUserMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 会议用户 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
@Slf4j
public class ZkMeetingUserServiceImpl implements ZkMeetingUserService {

    @Resource
    private ZkMeetingUserMapper zkMeetingUserMapper;

    @Override
    public Long createZkMeetingUser(ZkMeetingUserCreateReqVO createReqVO) {
        // 插入
        ZkMeetingUserDO zkMeetingUser = BeanUtils.toBean(createReqVO, ZkMeetingUserDO.class);
        zkMeetingUserMapper.insert(zkMeetingUser);
        // 返回
        return zkMeetingUser.getId();
    }

    @Override
    public void updateZkMeetingUser(ZkMeetingUserUpdateReqVO updateReqVO) {
        // 校验存在
        ZkMeetingUserDO meetingUserDO = validateZkMeetingUserExists(updateReqVO.getId());
        if(meetingUserDO == null && updateReqVO.getMeetingId() != null) {
            Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
            meetingUserDO = getByMeetingIdWithUserId(updateReqVO.getMeetingId(), loginUserId);
            if(meetingUserDO == null)
                throw new CommonException(403, String.format("meetingId:%s,userId:%s 对应参会人信息遗失！", updateReqVO.getMeetingId(), loginUserId));
        }
        // 更新
        CommonUtil.copyPropertiesIgnoreNull(updateReqVO, meetingUserDO);
        zkMeetingUserMapper.updateById(meetingUserDO);
    }

    @Override
    public void deleteZkMeetingUser(Long id) {
        // 校验存在
        validateZkMeetingUserExists(id);
        // 删除
        zkMeetingUserMapper.deleteById(id);
    }

    private ZkMeetingUserDO validateZkMeetingUserExists(Long id) {
        ZkMeetingUserDO meetingUserDO = zkMeetingUserMapper.selectById(id);
        if (meetingUserDO == null) {
            throw exception(ZK_MEETING_USER_NOT_EXISTS);
        }
        return meetingUserDO;
    }

    @Override
    public ZkMeetingUserDO getZkMeetingUser(Long id) {
        return zkMeetingUserMapper.selectById(id);
    }

    @Override
    public List<ZkMeetingUserDO> getZkMeetingUserList(Collection<Long> ids) {
        return zkMeetingUserMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkMeetingUserDO> getZkMeetingUserPage(ZkMeetingUserPageReqVO pageReqVO) {
        return zkMeetingUserMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkMeetingUserDO> getZkMeetingUserList(ZkMeetingUserExportReqVO exportReqVO) {
        return zkMeetingUserMapper.selectList(exportReqVO);
    }

    @Override
    public List<ZkMeetingUserDO> selectByUids(ZkMeetingDO zkMeeting, List<Long> userIds) {
        // 1. 获取 会议相关人员
        QueryWrapper<ZkMeetingUserDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", new HashSet<>(userIds));
        List<ZkMeetingUserDO> list = zkMeetingUserMapper.selectByUids(queryWrapper);
        // 2. 如果查询人员为空则返回
        if(EasyListUtil.isEmpty(list)) {
            return list;
        }
        // 3. 人员不为空添加会议id
        list.forEach(item -> {
            item.setMeetingId(zkMeeting.getId());
        });
        return list;
    }

    @Override
    public void customSaveBatch(List<ZkMeetingUserDO> list) {
        if(EasyListUtil.isEmpty(list)) return;
        zkMeetingUserMapper.insertBatch(list);
    }

    @Override
    public void customDelete(ZkMeetingUserDO zkMeetingUserDO) {
        zkMeetingUserMapper.delete(new QueryWrapper<>(zkMeetingUserDO));
    }

    @Override
    public List<ZkMeetingUserDO> getByMeetingIds(Set<Long> meetingIds) {
        QueryWrapper<ZkMeetingUserDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("meeting_id", meetingIds).orderByDesc("id");

        return zkMeetingUserMapper.selectList(queryWrapper);
    }

    @Override
    public List<ZkMeetingUserDO> getByMeetingIdsWithLast(Set<Long> meetingIds) {
        // 1. 获取 参会人信息
        List<ZkMeetingUserDO> byMeetingIds = getByMeetingIds(meetingIds);
        if(EasyListUtil.isEmpty(byMeetingIds)) return new ArrayList<>();
        byMeetingIds = remove_repeatMeetUser(byMeetingIds);
        // 2. 执行 获取上次参会信息
        Set<Long> userIds = byMeetingIds.stream().map(obj -> obj.getUserId()).collect(Collectors.toSet());
        QueryWrapper<ZkMeetingUserDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds)
                .notIn("meeting_id", meetingIds)
                .isNotNull("meet_to_do").orderByDesc("user_id", "meeting_id");
        List<ZkMeetingUserDO> lastUserList = zkMeetingUserMapper.selectList(queryWrapper);
        if(EasyListUtil.isEmpty(lastUserList)) return byMeetingIds;
        // 3. 汇总分析
        Map<Long, List<ZkMeetingUserDO>> collect = lastUserList.stream().collect(Collectors.groupingBy(obj -> obj.getUserId()));
        for(Long userIdItem : collect.keySet()) {
            List<ZkMeetingUserDO> tempUserList = collect.get(userIdItem);
            if(EasyListUtil.isEmpty(tempUserList)) continue;
            ZkMeetingUserDO lastMeetUser = tempUserList.get(0);
            list_buildUser(lastMeetUser, byMeetingIds);
        }
        return byMeetingIds;
    }

    public List<ZkMeetingUserDO> remove_repeatMeetUser(List<ZkMeetingUserDO> byMeetingIds) {
        if(EasyListUtil.isEmpty(byMeetingIds)) return byMeetingIds;
        // 1. 分组去重
        Map<Long, List<ZkMeetingUserDO>> collect = byMeetingIds.stream().collect(Collectors.groupingBy(obj -> obj.getUserId()));
        List<ZkMeetingUserDO> newUserList = new ArrayList<>();
        List<ZkMeetingUserDO> needRemoveUserList = new ArrayList<>();
        for(Long userId : collect.keySet()) {
            List<ZkMeetingUserDO> tempList = collect.get(userId);
            for(int i=0;i<tempList.size();i++) {
                if(Objects.equals(0, i)) {
                    newUserList.add(tempList.get(i));
                } else {
                    needRemoveUserList.add(tempList.get(i));
                }
            }
        }
        // 2. 删除重复
        if(EasyListUtil.isNotEmpty(needRemoveUserList)) {
            Set<Long> needRemoveIds = needRemoveUserList.stream().map(obj -> obj.getId()).collect(Collectors.toSet());
            zkMeetingUserMapper.deleteBatchIds(needRemoveIds);
            log.warn("【加入会议或会议聊天分发-{}】middle,出现重复用户数据，重复数据:{}，成功删除重复，有时间可溯源解决", TokenUtil.userInfo(), JSON.toJSONString(needRemoveUserList));
        }
        return newUserList;
    }

    public void list_buildUser(ZkMeetingUserDO lastMeetUser, List<ZkMeetingUserDO> byMeetingIds) {
        for(ZkMeetingUserDO curr : byMeetingIds) {
            if(Objects.equals(lastMeetUser.getUserId(), curr.getUserId())) {
                curr.setLastMeetUser(lastMeetUser);
            }
        }
    }

    @Override
    public ZkMeetingUserDO getByMeetingIdWithUserId(Long meetingId, Long userId) {
        QueryWrapper<ZkMeetingUserDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("meeting_id", meetingId)
                .eq("user_id", userId).orderByDesc("id").last("limit 1");

        return zkMeetingUserMapper.selectOne(queryWrapper);
    }

    @Override
    public Integer updateMeetUserToDo(ZkMeetingDO meet) {
        if(meet == null || meet.getId() == null) {
            return 0;
        }
        QueryWrapper<ZkMeetingUserDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("meeting_id", meet.getId());
        List<ZkMeetingUserDO> meetingUserDOList = zkMeetingUserMapper.selectList(queryWrapper);
        if(EasyListUtil.isEmpty(meetingUserDOList)) return 0;
        meetingUserDOList.forEach(obj -> obj.setMeetToDo(meet.getMeetToDo()));
        zkMeetingUserMapper.updateBatch(meetingUserDOList, meetingUserDOList.size());
        String userInfo = meetingUserDOList.stream().map(obj -> String.format("%s_%s", obj.getUserId(), obj.getUserName())).collect(Collectors.joining("、"));
        log.warn("【补充会议纪要及待办-{}】middle,补充信息:{}，用户信息：{}成功入库", TokenUtil.userInfo(), JSON.toJSONString(meet), userInfo);
        return meetingUserDOList.size();
    }

}
