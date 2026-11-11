package cn.iocoder.yudao.module.fish.dal.dataobject.zkscheduling;

import cn.iocoder.yudao.module.fish.dal.dataobject.zktask.ZkTaskDO;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import org.apache.commons.lang3.StringUtils;

/**
 * 排班 DO
 *
 * @author 系统管理员
 */
@TableName(value = "zk_scheduling", autoResultMap = true)
@KeySequence("zk_scheduling_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkSchedulingDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 排班名称
     */
    private String schedulingName;
    /**
     * 排班描述
     */
    private String schedulingContent;
    /**
     * 排班人id
     */
    private Long userId;
    /**
     * 排班人
     */
    private String userName;
    /**
     * 排班人所属部门
     */
    private Long deptId;
    private Long tenantId;
    /**
     * 状态(0:待排班;1:已排班;2:已撤销;-1:已删除;)
     */
    private String status;
    /**
     * 任务类型(1:打卡;2:巡查;3:其他;)
     */
    private String schedulingType;
    /**
     * 排班人id
     */
    private String userIdStr;
    /**
     * 文件id
     */
    private String fid;

    // 被安排人
    @TableField(exist = false)
    private List<Long> userIds;
    private String schedulingTime;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> selectDays;
    private Integer selectDayType;
    private String taskConfigIds; // 巡检配置ids

    private String pondIds; // 存塘ids(多个英文逗号隔开)
    @TableField(exist = false)
    private List<Long> pondIdArr; // 任务关联存塘id

    public void build_userIds(List<Long> userIds) {
        this.setUserIds(userIds);
    }
    public List<Long> select_userIds() {
        Set<Long> userIdSet = new HashSet<>();
        if(StringUtils.isEmpty(userIdStr)) return new ArrayList<>();
        String[] split = userIdStr.split(",");
        for(String str : split) {
            userIdSet.add(Long.parseLong(str));
        }
        this.userIds = new ArrayList<>(userIdSet);
        return userIds;
    }
    public ZkSchedulingDO build_pondIds() {
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
}
