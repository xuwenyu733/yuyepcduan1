package cn.iocoder.yudao.module.fish.controller.admin.zkmeeting;

import cn.iocoder.yudao.framework.common.exception.CommonException;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.framework.redis.core.PlusRedisService;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.security.core.util.TokenUtil;
import cn.iocoder.yudao.module.infra.websocket.WebSocketUsers;
import cn.iocoder.yudao.module.infra.websocket.vo.ReceiveOperateEnum;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.ChatRespVo;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.ChatVo;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.ReceiveOperateRespVo;
import cn.iocoder.yudao.module.fish.controller.admin.zkmeeting.to.MeetMsgItem;
import cn.iocoder.yudao.module.fish.controller.admin.zkmeeting.to.MeetSummaryVo;
import cn.iocoder.yudao.module.fish.controller.admin.zkmeeting.to.MeetingMsgReceiveTypeEnum;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmeetinguser.ZkMeetingUserDO;
import cn.iocoder.yudao.module.fish.service.aiagent.AiAgentService;
import cn.iocoder.yudao.module.fish.service.zkmeetinguser.ZkMeetingUserService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
// import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;
// import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;

import cn.iocoder.yudao.module.fish.controller.admin.zkmeeting.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmeeting.ZkMeetingDO;
// import cn.iocoder.yudao.module.fish.convert.zkmeeting.ZkMeetingConvert;
import cn.iocoder.yudao.module.fish.service.zkmeeting.ZkMeetingService;

@Tag(name = "管理后台 - 会议")
@RestController
@RequestMapping("/fish/zk-meeting")
@Validated
@Slf4j
public class ZkMeetingController {

    @Resource
    private ZkMeetingService zkMeetingService;
    @Resource
    private ZkMeetingUserService zkMeetingUserService;
    @Resource
    private AiAgentService aiAgentService;

    public static String MEET_USER_KEY = "MEET_USER_KEY:";
    @Resource
    private PlusRedisService plusRedisService;
    @Autowired
    private AdminUserApi adminUserApi;

    @Autowired
    private ThreadPoolExecutor executor;

    @PostMapping("/create")
    @Operation(summary = "创建会议")
    public CommonResult<Long> createZkMeeting(@Valid @RequestBody ZkMeetingCreateReqVO createReqVO) {
        if(StringUtil.isEmpty(createReqVO.getMeetingNum())) {
            createReqVO.setMeetingNum(StringUtil.genMeetingNum());
        }
        Long zkMeeting = zkMeetingService.createZkMeeting(createReqVO);
        return success(zkMeeting);
    }

    @PutMapping("/update")
    @Operation(summary = "更新会议")
    public CommonResult<Boolean> updateZkMeeting(@Valid @RequestBody ZkMeetingUpdateReqVO updateReqVO) {
        zkMeetingService.updateZkMeeting(updateReqVO);
        return success(true);
    }

    @PostMapping("/receiveMsg")
    @Operation(summary = "接受会议消息")
    public CommonResult<ZkMeetingDO> receiveMsg(@Valid @RequestBody ZkMeetingCreateReqVO createReqVO) {
        if(StringUtil.isEmpty(createReqVO.getMeetingNum())) {
            throw new CommonException(403, "会议号meetingNum不能为空!");
        }
        ZkMeetingDO zkMeeting = null;
        if(StringUtils.equals(createReqVO.getReceiveType(), MeetingMsgReceiveTypeEnum.JOIN_MEET.getValue())) {
            // 1. 加入会议消息
            zkMeeting = zkMeetingService.custom_createZkMeeting(createReqVO);
            // 2. 缓存 会议用户 信息
            String redisKey = String.format("%s%s:%s", MEET_USER_KEY, zkMeeting.getId(), zkMeeting.getMeetingNum());
            plusRedisService.insertStr(redisKey, JSON.toJSONString(zkMeeting), 60*24L); // 缓存一天
        } else if(StringUtils.equals(createReqVO.getReceiveType(), MeetingMsgReceiveTypeEnum.CHAT_MSG.getValue())) {
            // 2. 会议聊天消息
            // 2.1 添加消息
            zkMeeting = selectMeetFromRedis(createReqVO);
            MeetMsgItem msgItem = zkMeetingService.saveMsg(createReqVO.build_meetingId(zkMeeting.getId()).build_meetDay(zkMeeting.getMeetDay()));
            // 2.2 分发消息
            dishOutMeetChatMsg(zkMeeting, msgItem);
        }

        return success(zkMeeting.build_auth(createReqVO));
    }

    public ZkMeetingDO selectMeetFromRedis(ZkMeetingCreateReqVO createReqVO) {
        ZkMeetingDO zkMeeting = null;
        if(createReqVO.getMeetingId() == null) { // 1. 没有会议id，说明可能没有预进入会议或者信号波动遗失参数==》从db拿
            zkMeeting = zkMeetingService.custom_createZkMeeting(createReqVO);
            String redisKey = String.format("%s%s:%s", MEET_USER_KEY, zkMeeting.getId(), zkMeeting.getMeetingNum());
            plusRedisService.insertStr(redisKey, JSON.toJSONString(zkMeeting), 60*24L); // 缓存一天
            return zkMeeting.build_auth(createReqVO);
        }
        // 2. 有会议id==》从redis拿
        String redisKey = String.format("%s%s:%s", MEET_USER_KEY, createReqVO.getMeetingId(), createReqVO.getMeetingNum());
        zkMeeting = plusRedisService.selectObj(redisKey, ZkMeetingDO.class);
        if(zkMeeting == null) { // 3. 有会议id和meetingNum，redis没有==》从db拿
            zkMeeting = zkMeetingService.custom_createZkMeeting(createReqVO);
            plusRedisService.insertStr(redisKey, JSON.toJSONString(zkMeeting), 60*24L); // 缓存一天
        } else if(!zkMeeting.userHasInMeet(createReqVO.getUserId())) { // 4. 有会议id和meetingNum，redis有，但缺少当前参会人==》从db拿顺带添加参会人
            zkMeeting = zkMeetingService.custom_createZkMeeting(createReqVO);
            plusRedisService.insertStr(redisKey, JSON.toJSONString(zkMeeting), 60*24L); // 缓存一天
        }
        return zkMeeting.build_auth(createReqVO);
    }

    /**
     * 分发会议消息给每个参会人
     * @param zkMeeting
     * @param msgItem
     */
    public void dishOutMeetChatMsg(ZkMeetingDO zkMeeting, MeetMsgItem msgItem) {
        List<ZkMeetingUserDO> zkMeetingUserDOS = zkMeeting.getZkMeetingUserDOS();
        if(EasyListUtil.isEmpty(zkMeetingUserDOS)) {
            log.warn("【收到会议聊天信息-{}-{}】middle 分发会议聊天信息,聊天信息:{}，没有获取到参会人信息", TokenUtil.userInfo(), zkMeeting.getId(), JSON.toJSONString(msgItem));
            return;
        }
        for(ZkMeetingUserDO item : zkMeetingUserDOS) {
            // String operateKey, String content, String think, String version, String deviceType
            ReceiveOperateRespVo res = new ReceiveOperateRespVo(ReceiveOperateEnum.PC_MEET_CHAT_MSG.getOperateKey(), msgItem.getChatMsg(), "", msgItem.getVersion(), msgItem.getDeviceType());
            String resStr = JSON.toJSONString(res);
            WebSocketUsers.baseSendMsg(item.getUserId()+"", resStr, msgItem.getVersion(), msgItem.getDeviceType(), false);
            log.warn("【收到会议聊天信息-{}-{}】middle 分发会议聊天信息，向:{}分发 信息:{} 成功", TokenUtil.userInfo(), zkMeeting.getId(), item.getUserName(), JSON.toJSONString(res));
        }
    }

    @PostMapping("/summaryPoint")
    @Operation(summary = "获取会议纪要及待办")
    public CommonResult<ZkMeetingDO> summaryPoint(@Valid @RequestBody ZkMeetingCreateReqVO createReqVO) {
        if(StringUtil.isEmpty(createReqVO.getMeetingNum())) {
            throw new CommonException(403, "会议号meetingNum不能为空!");
        }
        if(createReqVO.getMeetingId() == null) {
            throw new CommonException(403, "会议meetingId不能为空!");
        }
        AdminUserRespDTO adminUserRespDTO = adminUserApi.getUser(SecurityFrameworkUtils.getLoginUserId());
        String uid = ChatVo.selectUid(adminUserRespDTO.getUsername(), adminUserRespDTO.getNickname(), adminUserRespDTO.getId());
        // 1. 获取 会议聊天记录
        List<MeetMsgItem> list = zkMeetingService.getChatMsgList(createReqVO.getMeetingId(), createReqVO.getMeetingNum());
        // 2. 请求大模型获取会议纪要和待办
        String strContent = MeetMsgItem.selectStrContent(list);
        // 3. 开始请求大模型
        String time = new Date().getTime()+"";
        ChatVo chatVo = new ChatVo(uid, createReqVO.getUserId()+"", strContent, createReqVO.getToken(),
                "pc", "deepseek-v3", "meet_summary", "false", time,
                String.format("%sAAA%s", createReqVO.getUserId(), time));
        ChatRespVo chatRespVo = aiAgentService.chat(chatVo);
        if(chatRespVo != null && StringUtils.isNotBlank(chatRespVo.getMsg())) {
            try {
                MeetSummaryVo meetSummaryVo = JSON.parseObject(chatRespVo.getMsg().replaceAll("<br>", ""), MeetSummaryVo.class);
                if (meetSummaryVo == null || (StringUtils.isBlank(meetSummaryVo.getMeetingMinutes())
                        && StringUtils.isBlank(meetSummaryVo.getMeetingBacklog()))) {
                    return CommonResult.error(500, "会议纪要服务暂不可用，请稍后重试或使用语音转写回退");
                }
                // Long id, String meetingNum, Long userId, String meetingPoint, String meetToDo, String userName, Long deptId
                ZkMeetingDO meet = new ZkMeetingDO(createReqVO.getMeetingId(), createReqVO.getMeetingNum(), createReqVO.getUserId(), meetSummaryVo.getMeetingMinutes(),
                        meetSummaryVo.getMeetingBacklog(), createReqVO.getUserName(), createReqVO.getDeptId());
                // 4. 异步存储个人任务
                CompletableFuture.runAsync(() -> zkMeetingUserService.updateMeetUserToDo(meet), executor);
                return success(meet);
            } catch (Exception parseEx) {
                log.error("【会议纪要】解析大模型结果失败: {}", chatRespVo.getMsg(), parseEx);
                return CommonResult.error(500, "会议纪要服务暂不可用（AI 未就绪），请稍后重试");
            }
        }
        return CommonResult.error(500, "会议纪要服务暂不可用，请稍后重试");
    }


    @DeleteMapping("/delete")
    @Operation(summary = "删除会议")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkMeeting(@RequestParam("id") Long id) {
        zkMeetingService.deleteZkMeeting(id);
        zkMeetingUserService.customDelete(new ZkMeetingUserDO(id));
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得会议")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkMeetingRespVO> getZkMeeting(@RequestParam("id") Long id) {
        ZkMeetingDO zkMeeting = zkMeetingService.getZkMeeting(id);
        listEnhancer(Arrays.asList(zkMeeting));
        return success(BeanUtils.toBean(zkMeeting, ZkMeetingRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得会议列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkMeetingRespVO>> getZkMeetingList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkMeetingDO> list = zkMeetingService.getZkMeetingList(ids);
        listEnhancer(list);
        return success(BeanUtils.toBean(list, ZkMeetingRespVO.class));
    }

    /**
     * 会议填充参会人
     * @param list
     */
    public void listEnhancer(List<ZkMeetingDO> list) {
        if(EasyListUtil.isEmpty(list)) return;
        // 1. 获取用户列表
        Set<Long> meetingIds = list.stream().map(obj -> obj.getId()).collect(Collectors.toSet());
        List<ZkMeetingUserDO> userList = zkMeetingUserService.getByMeetingIds(meetingIds);
        if(EasyListUtil.isEmpty(userList)) return;
        // 2. 执行赋值
        Map<Long, List<ZkMeetingUserDO>> map = userList.stream().collect(Collectors.groupingBy(obj -> obj.getMeetingId()));
        for(Long meetingId : map.keySet()) {
            for(ZkMeetingDO item : list) {
                if(Objects.equals(meetingId, item.getId())) {
                    List<ZkMeetingUserDO> tempArr = map.get(meetingId);
                    List<Long> tempUserIds = tempArr.stream().map(obj -> obj.getUserId()).collect(Collectors.toList());
                    String tempUserNameStr = tempArr.stream().map(obj -> obj.getUserName()).collect(Collectors.joining(","));
                    item.setUserIds(tempUserIds);
                    item.setUserNameStr(tempUserNameStr);
                }
            }
        }
    }

    @GetMapping("/page")
    @Operation(summary = "获得会议分页")
    public CommonResult<PageResult<ZkMeetingRespVO>> getZkMeetingPage(@Valid ZkMeetingPageReqVO pageVO) {
        PageResult<ZkMeetingDO> pageResult = zkMeetingService.getZkMeetingPage(pageVO);
        listEnhancer(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, ZkMeetingRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出会议 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkMeetingExcel(@Valid ZkMeetingExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkMeetingDO> list = zkMeetingService.getZkMeetingList(exportReqVO);
        listEnhancer(list);
        // 导出 Excel
        List<ZkMeetingExcelVO> datas = BeanUtils.toBean(list, ZkMeetingExcelVO.class);
        ExcelUtils.write(response, "会议.xls", "数据", ZkMeetingExcelVO.class, datas);
    }

}
