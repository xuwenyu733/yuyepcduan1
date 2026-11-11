package cn.iocoder.yudao.module.fish.dal.dataobject.zktask;

import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zktask.vo.TaskTypeEnum;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkscheduling.ZkSchedulingDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail.TaskDetailDO;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import org.apache.commons.lang3.StringUtils;

/**
 * 任务 DO
 *
 * @author 系统管理员
 */
@TableName("zk_task")
@KeySequence("zk_task_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkTaskDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 业务id
     */
    private Long busId;
    private Long schedulingId;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 任务描述
     */
    private String taskContent;
    /**
     * 任务反馈
     */
    private String taskPoint;
    /**
     * 任务结果
     */
    private String taskSummary;
    /**
     * 记录人id
     */
    private Long userId;
    /**
     * 记录人
     */
    private String userName;
    /**
     * 记录人所属部门
     */
    private Long deptId;
    private Long tenantId;
    /**
     * 任务类型 参考字典配置：   zk_scheduling_type
     */
    private String taskType;
    /**
     * ==》已废弃：  状态(0:已下发;1:已通知;2:待完成;3:已完成;4:已评估;-1:已删除;)
     * 状态(1:待接手;2:已接收未完成;3:已完成;)
     */
    private String status;
    /**
     * 文件id
     */
    private String fid;
    private String fileUrl; // 详情url
    /**
     * 流程实例
     */
    private String processInstanceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime finishTime; // 任务完成时间
    private String taskMonth; // 任务月份(yyyy-MM)
    private String standTime; // 任务标准时间(yyyy-MM-dd HH:mm:ss)
    private String taskConfigIds; // 巡检配置ids
    private String checkTypes; // 巡检类型(多个英文,号分隔)

    @TableField(exist = false)
    private List<TaskDetailDO> taskDetailDOS;
    @TableField(exist = false)
    private List<Long> pondIdArr; // 任务关联存塘id
    @TableField(exist = false)
    private String pondIds; // 存塘ids(多个英文逗号隔开)

    public ZkTaskDO build_otherInfo(String taskMonth, String standTime) {
        this.setTaskMonth(taskMonth);
        this.setStandTime(standTime);
        return this;
    }
    public ZkTaskDO build_taskConfigIds(String taskConfigIds) {
        this.setTaskConfigIds(taskConfigIds);
        return this;
    }
    public ZkTaskDO build_pondIdArr(List<Long> pondIdArr) {
        this.pondIdArr = pondIdArr;
        if(EasyListUtil.isNotEmpty(pondIdArr)) {
            this.pondIds = pondIdArr.stream().filter(obj -> obj!=null).map(obj -> obj.toString()).collect(Collectors.joining(","));
        }
        return this;
    }
    public ZkTaskDO build_pondIds() {
        if(StringUtils.isEmpty(this.getPondIds())) return this;
        String[] split = pondIds.split(",");
        List<Long> pondIdArr = new ArrayList<>();
        for(String str : split) {
            if(StringUtils.isEmpty(str)) continue;
            pondIdArr.add(Long.parseLong(str));
        }
        this.pondIdArr = pondIdArr;
        return this;
    }

    public ZkTaskDO(Long schedulingId, String taskName, String taskContent, Long userId, String userName, Long deptId, String taskType, LocalDateTime startTime, LocalDateTime endTime) {
        this.schedulingId = schedulingId;
        this.taskName = taskName;
        this.taskContent = taskContent;
        this.userId = userId;
        this.userName = userName;
        this.deptId = deptId;
        this.taskType = taskType;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public ZkTaskDO(AdminUserRespDTO userDo, ZkSchedulingDO zkScheduling) {
        this.schedulingId = zkScheduling.getId();
        this.setUserId(userDo.getId());
        this.setUserName(userDo.getNickname());
        this.setDeptId(userDo.getDeptId());
    }

    public static ZkTaskDO buildShanBan(AdminUserRespDTO userDo, ZkSchedulingDO zkScheduling) {
        // Long schedulingId, String taskName, String taskContent, Long userId, String userName, Long deptId, String taskType, LocalDateTime startTime, LocalDateTime endTime
        String[] split = zkScheduling.getSchedulingTime().split(",");
        String standTime = split[0];
        LocalDateTime startTime = EasyDateUtil.parse(standTime);
        String taskMonth = EasyDateUtil.format(startTime, "yyyy-MM");
        LocalDateTime beforeTime = EasyDateUtil.plusMinutes1(startTime, -120);
        ZkTaskDO item = new ZkTaskDO(zkScheduling.getId(), "上班", zkScheduling.getSchedulingContent(), userDo.getId(), userDo.getNickname(), userDo.getDeptId(),
                TaskTypeEnum.SHANG_BAN.getValue(), beforeTime, startTime).build_otherInfo(taskMonth, standTime);

        return item;
    }

    public static ZkTaskDO buildXiaBan(AdminUserRespDTO userDo, ZkSchedulingDO zkScheduling) {
        String[] split = zkScheduling.getSchedulingTime().split(",");
        String standTime = split[1];
        LocalDateTime startTime = EasyDateUtil.parse(standTime);
        String taskMonth = EasyDateUtil.format(startTime, "yyyy-MM");
        LocalDateTime afterTime = EasyDateUtil.plusMinutes1(startTime, 300);
        ZkTaskDO item = new ZkTaskDO(zkScheduling.getId(), "下班", zkScheduling.getSchedulingContent(), userDo.getId(), userDo.getNickname(), userDo.getDeptId(),
                TaskTypeEnum.XIA_BAN.getValue(), startTime, afterTime).build_otherInfo(taskMonth, standTime);

        return item;
    }

    public static List<ZkTaskDO> buildDaKa(AdminUserRespDTO userDo, ZkSchedulingDO zkScheduling) {
        ZkTaskDO shanBan = buildShanBan(userDo, zkScheduling);
        ZkTaskDO xiaBan = buildXiaBan(userDo, zkScheduling);
        return Arrays.asList(shanBan, xiaBan);
    }

    public static List<ZkTaskDO> buildDaKa_all(AdminUserRespDTO userDo, ZkSchedulingDO zkScheduling) {
        List<ZkTaskDO> list = new ArrayList<>();
        String[] split = zkScheduling.getSchedulingTime().split(",");
        for(String item : zkScheduling.getSelectDays()) {
            // 1. 配置时间
            String tempTime = new StringBuilder(item).append(" ").append(split[0]).append(",")
                    .append(item).append(" ").append(split[1]).toString();
            ZkSchedulingDO tempSchedule = new ZkSchedulingDO();
            CommonUtil.copyPropertiesIgnoreNull(zkScheduling, tempSchedule);
            tempSchedule.setSchedulingTime(tempTime);
            // 2. 获取任务
            List<ZkTaskDO> resList = buildDaKa(userDo, tempSchedule);
            list.addAll(resList);
        }
        return list;
    }

    public static List<ZkTaskDO> buildXunCha_all(AdminUserRespDTO userDo, ZkSchedulingDO zkScheduling) {
        List<ZkTaskDO> taskDOS = buildXunCha_allByDays(userDo, zkScheduling, zkScheduling.getSelectDays());
        return taskDOS;
    }
    public static List<ZkTaskDO> buildXunCha_allByDays(AdminUserRespDTO userDo, ZkSchedulingDO zkScheduling, List<String> days) {
        List<ZkTaskDO> list = new ArrayList<>();
        String[] split = zkScheduling.getSchedulingTime().split(",");
        for(String item : days) {
            if(StringUtils.isEmpty(item)) continue;
            // 1. 配置时间
            String tempTime = new StringBuilder(item).append(" ").append(split[0]).append(",")
                    .append(item).append(" ").append(split[1]).toString();
            ZkSchedulingDO tempSchedule = new ZkSchedulingDO();
            CommonUtil.copyPropertiesIgnoreNull(zkScheduling, tempSchedule);
            tempSchedule.setSchedulingTime(tempTime);
            // 2. 获取任务
            List<ZkTaskDO> resList = buildXunCha(userDo, tempSchedule);
            list.addAll(resList);
        }
        return list;
    }


    public static List<ZkTaskDO> buildOther_all(AdminUserRespDTO userDo, ZkSchedulingDO zkScheduling) {
        List<ZkTaskDO> list = new ArrayList<>();
        String[] split = zkScheduling.getSchedulingTime().split(",");
        for(String item : zkScheduling.getSelectDays()) {
            // 1. 配置时间
            String tempTime = new StringBuilder(item).append(" ").append(split[0]).append(",")
                    .append(item).append(" ").append(split[1]).toString();
            ZkSchedulingDO tempSchedule = new ZkSchedulingDO();
            CommonUtil.copyPropertiesIgnoreNull(zkScheduling, tempSchedule);
            tempSchedule.setSchedulingTime(tempTime);
            // 2. 获取任务
            List<ZkTaskDO> resList = buildOther(userDo, tempSchedule);
            list.addAll(resList);
        }
        return list;
    }

    public static List<ZkTaskDO> buildXunCha(AdminUserRespDTO userDo, ZkSchedulingDO zkScheduling) {
        // Long schedulingId, String taskName, String taskContent, Long userId, String userName, Long deptId, String taskType, LocalDateTime startTime, LocalDateTime endTime
        String[] split = zkScheduling.getSchedulingTime().split(",");
        LocalDateTime startTime = EasyDateUtil.parse(split[0]);
        LocalDateTime endTime = EasyDateUtil.parse(split[1]);
        String standTime = split[0];
        String taskMonth = EasyDateUtil.format(startTime, "yyyy-MM");
        ZkTaskDO item = new ZkTaskDO(zkScheduling.getId(), zkScheduling.getSchedulingName(), zkScheduling.getSchedulingContent(), userDo.getId(), userDo.getNickname(), userDo.getDeptId(),
                zkScheduling.getSchedulingType(), startTime, endTime).build_otherInfo(taskMonth, standTime)
                .build_taskConfigIds(zkScheduling.getTaskConfigIds());

        return Arrays.asList(item);
    }

    public static List<ZkTaskDO> buildOther(AdminUserRespDTO userDo, ZkSchedulingDO zkScheduling) {
        // Long schedulingId, String taskName, String taskContent, Long userId, String userName, Long deptId, String taskType, LocalDateTime startTime, LocalDateTime endTime
        String[] split = zkScheduling.getSchedulingTime().split(",");
        LocalDateTime startTime = EasyDateUtil.parse(split[0]);
        LocalDateTime endTime = EasyDateUtil.parse(split[1]);
        String standTime = split[0];
        String taskMonth = EasyDateUtil.format(startTime, "yyyy-MM");
        ZkTaskDO item = new ZkTaskDO(zkScheduling.getId(), "其他", zkScheduling.getSchedulingContent(), userDo.getId(), userDo.getNickname(), userDo.getDeptId(),
                zkScheduling.getSchedulingType(), startTime, endTime).build_otherInfo(taskMonth, standTime);

        return Arrays.asList(item);
    }

    public ZkTaskDO build_schedulingId(Long schedulingId) {
        this.setSchedulingId(schedulingId);
        return this;
    }
}
