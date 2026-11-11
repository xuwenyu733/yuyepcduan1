package cn.iocoder.yudao.module.fish.service.zkmeeting;

import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.framework.redis.core.PlusRedisService;
import cn.iocoder.yudao.framework.security.core.util.TokenUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkmeeting.to.MeetMsgItem;
import cn.iocoder.yudao.module.fish.controller.admin.zkmeeting.to.MeetingUserTypeEnum;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmeetingrecord.ZkMeetingRecordDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmeetinguser.ZkMeetingUserDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkmeetinguser.ZkMeetingUserMapper;
import cn.iocoder.yudao.module.fish.service.zkmeetingrecord.ZkMeetingRecordService;
import cn.iocoder.yudao.module.fish.service.zkmeetinguser.ZkMeetingUserService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import cn.iocoder.yudao.module.fish.controller.admin.zkmeeting.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmeeting.ZkMeetingDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkmeeting.ZkMeetingConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkmeeting.ZkMeetingMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 会议 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
@Slf4j
public class ZkMeetingServiceImpl implements ZkMeetingService {

    @Resource
    private ZkMeetingMapper zkMeetingMapper;

    @Resource
    private ZkMeetingUserMapper zkMeetingUserMapper;

    @Resource
    private ZkMeetingUserService zkMeetingUserService;

    @Resource
    private ZkMeetingRecordService zkMeetingRecordService;

    public static String MEET_MSG_KEY = "MEET_MSG_KEY:";

    @Resource
    private PlusRedisService plusRedisService;

    @Override
    public Long createZkMeeting(ZkMeetingCreateReqVO createReqVO) {
        // 1. 插入
        ZkMeetingDO zkMeeting = BeanUtils.toBean(createReqVO, ZkMeetingDO.class);
        zkMeetingMapper.insert(zkMeeting);
        // 2. 添加明细
        if(EasyListUtil.isNotEmpty(createReqVO.getUserIds())) {
            List<ZkMeetingUserDO> list = zkMeetingUserService.selectByUids(zkMeeting, createReqVO.getUserIds());
            zkMeetingUserService.customSaveBatch(list);
        }
        return zkMeeting.getId();
    }

    @Override
    public synchronized ZkMeetingDO custom_createZkMeeting(ZkMeetingCreateReqVO createReqVO) {
        String meetDay = EasyDateUtil.format(new Date(), "yyyy-MM-dd");
        // 1. 判断是否已经有了
        ZkMeetingDO history = getByMeetingNum(createReqVO.getMeetingNum(), meetDay);
        // 2. 没有会议创建会议添加主持人；有会议添加参会人；
        if(history == null) {
            // 2.1 执行添加逻辑
            history = BeanUtils.toBean(createReqVO, ZkMeetingDO.class).setMeetDay(meetDay);
            zkMeetingMapper.insert(history);
            // 2.2 第一个创建会议的为主持人
            // Long meetingId, String meetingNum, Long userId, String userName, Long deptId, String userType
            ZkMeetingUserDO meetHost = new ZkMeetingUserDO(history.getId(), history.getMeetingNum(), createReqVO.getUserId(), createReqVO.getUserName(), createReqVO.getDeptId(),
                    MeetingUserTypeEnum.HOST_USER.getValue());
            zkMeetingUserService.customSaveBatch(Arrays.asList(meetHost));
            log.warn("【加入会议-{}-{}】middle 主持人创建会议,会议信息:{}，成功入库", TokenUtil.userInfo(), history.getId(), JSON.toJSONString(history));
        } else {
            // 2.1 判断用户是否添加进入会议
            ZkMeetingUserDO meetingUserDO = zkMeetingUserService.getByMeetingIdWithUserId(history.getId(), createReqVO.getUserId());
            // 2.2 需要添加参会人
            if(meetingUserDO == null) {
                ZkMeetingUserDO meetHost = new ZkMeetingUserDO(history.getId(), history.getMeetingNum(), createReqVO.getUserId(), createReqVO.getUserName(), createReqVO.getDeptId(),
                        MeetingUserTypeEnum.JOIN_User.getValue());
                zkMeetingUserService.customSaveBatch(Arrays.asList(meetHost));
                log.warn("【加入会议-{}-{}】middle 参会人加入会议,会议信息:{}，成功入库", TokenUtil.userInfo(), history.getId(), JSON.toJSONString(history));
            }
        }
        // 3. 获取会议的用户
        List<ZkMeetingUserDO> meetingUserDOList = zkMeetingUserService.getByMeetingIdsWithLast(new HashSet<>(Arrays.asList(history.getId())));
        history.setZkMeetingUserDOS(meetingUserDOList);
        history.build_zkMeetingUserDOS(meetingUserDOList);

        return history;
    }

    @Override
    public MeetMsgItem saveMsg(ZkMeetingCreateReqVO createReqVO) {
        // 0. 创建当前 msgItem Long userId, String userName, String msg
        MeetMsgItem msgItem = new MeetMsgItem(createReqVO.getUserId(), createReqVO.getUserName(), createReqVO.getChatMsg(), createReqVO.getVersion(), createReqVO.getDeviceType());
        // 1. 创建key
        String redisKey = String.format("%s%s:%s", MEET_MSG_KEY, createReqVO.getMeetingId(), createReqVO.getMeetingNum());
        List<MeetMsgItem> meetMsgItems = plusRedisService.selectArr(redisKey, MeetMsgItem.class);
        if(EasyListUtil.isEmpty(meetMsgItems)) {
            // 第一次添加
            meetMsgItems = new ArrayList<>();
            msgItem.build_firstMsg(null);
        } else {
            Integer lastIndex = meetMsgItems.size()-1;
            msgItem.build_firstMsg(meetMsgItems.get(lastIndex));
        }
        meetMsgItems.add(msgItem);
        plusRedisService.insertStr(redisKey, JSON.toJSONString(meetMsgItems), 60*24*60L); // 缓存 2个月
        log.warn("【收到会议聊天信息-{}-{}】middle 消息入库,聊天信息:{}，成功入库Redis", TokenUtil.userInfo(), createReqVO.getMeetingId(), JSON.toJSONString(msgItem));
        //  2. 后续可以消息大于30条添加进入数据库
        // String chatMsg = meetMsgItems.stream().map(obj -> String.format("%s说:%s", obj.getUserName(), obj.getChatMsg())).collect(Collectors.joining("\n"));
        String chatMsg = meetMsgItems.stream().map(MeetMsgItem::getChatMsg).collect(Collectors.joining("\n"));
        ZkMeetingRecordDO meetingRecordDO = new ZkMeetingRecordDO(createReqVO.getMeetingId(), createReqVO.getMeetingNum(), createReqVO.getMeetDay(),
                chatMsg, createReqVO.getUserId(), createReqVO.getUserName(), createReqVO.getDeptId(), createReqVO.getTenantId());
        ZkMeetingRecordDO meetingRecordToDB = zkMeetingRecordService.updateOrInsert(meetingRecordDO);
        log.warn("【收到会议聊天信息-{}-{}】middle 消息入库,会议记录:{}，成功入库Mysql", TokenUtil.userInfo(), createReqVO.getMeetingId(), JSON.toJSONString(meetingRecordToDB));
        return msgItem;
    }

    @Override
    public List<MeetMsgItem> getChatMsgList(Long meetingId, String meetingNum) {
        // 1. 创建key
        String redisKey = String.format("%s%s:%s", MEET_MSG_KEY, meetingId, meetingNum);
        // 2. 获取记录
        List<MeetMsgItem> meetMsgItems = plusRedisService.selectArr(redisKey, MeetMsgItem.class);
        return meetMsgItems;
    }

    @Override
    public void updateZkMeeting(ZkMeetingUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkMeetingExists(updateReqVO.getId());
        // 更新
        ZkMeetingDO updateObj = BeanUtils.toBean(updateReqVO, ZkMeetingDO.class);
        if(StringUtil.isEmpty(updateObj.getMeetingNum())) {
            updateObj.setMeetingNum(StringUtil.genMeetingNum());
        }
        zkMeetingMapper.updateById(updateObj);
        // 2. 添加明细
        if(EasyListUtil.isNotEmpty(updateReqVO.getUserIds())) {
            // 2.1 删除历史
            zkMeetingUserService.customDelete(new ZkMeetingUserDO(updateObj.getId()));
            // 2.2 添加新的
            List<ZkMeetingUserDO> list = zkMeetingUserService.selectByUids(updateObj, updateReqVO.getUserIds());
            zkMeetingUserService.customSaveBatch(list);
        }
    }

    @Override
    public void deleteZkMeeting(Long id) {
        // 校验存在
        validateZkMeetingExists(id);
        // 删除
        zkMeetingMapper.deleteById(id);
    }

    private void validateZkMeetingExists(Long id) {
        if (zkMeetingMapper.selectById(id) == null) {
            throw exception(ZK_MEETING_NOT_EXISTS);
        }
    }

    @Override
    public ZkMeetingDO getZkMeeting(Long id) {
        return zkMeetingMapper.selectById(id);
    }

    @Override
    public ZkMeetingDO getByMeetingNum(String meetingNum) {
        QueryWrapper<ZkMeetingDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("meeting_num", meetingNum).orderByDesc("id").last("limit 1");
        return zkMeetingMapper.selectOne(queryWrapper);
    }

    @Override
    public ZkMeetingDO getByMeetingNum(String meetingNum, String meetDay) {
        QueryWrapper<ZkMeetingDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("meeting_num", meetingNum).eq("meet_day", meetDay).orderByDesc("id").last("limit 1");
        return zkMeetingMapper.selectOne(queryWrapper);
    }

    @Override
    public List<ZkMeetingDO> getZkMeetingList(Collection<Long> ids) {
        return zkMeetingMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkMeetingDO> getZkMeetingPage(ZkMeetingPageReqVO pageReqVO) {
        return zkMeetingMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkMeetingDO> getZkMeetingList(ZkMeetingExportReqVO exportReqVO) {
        return zkMeetingMapper.selectList(exportReqVO);
    }


}
