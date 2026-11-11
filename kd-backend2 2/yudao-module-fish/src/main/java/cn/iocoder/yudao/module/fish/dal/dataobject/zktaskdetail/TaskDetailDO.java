package cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zktask.to.TaskDetailTypeValueEnum;
import cn.iocoder.yudao.module.fish.controller.admin.zktask.to.params.ProofingParam;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkschedulingtype.ZkSchedulingTypeDO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import org.apache.commons.lang3.StringUtils;

/**
 * 任务明细 DO
 *
 * @author 系统管理员
 */
@TableName("zk_task_detail")
@KeySequence("zk_task_detail_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDetailDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 任务id(zk_task)
     */
    private Long taskId;
    private Long schedulingId; // 排班表id
    /**
     * 检测配置表id(zk_scheduling_type.id)
     * 作用：解析巡检json，拿到检测配置表字典信息，就可以执行解析
     */
    private Long taskConfigId; // 巡检配置id
    /**
     * 检测名称
     */
    private String checkName;
    /**
     * 检测类型(1:养殖区;2:设备区;3:定期巡检_养殖;4:定期巡检_设备区;5:定期巡检_仓库区;6:;)
     */
    private String checkType;
    /**
     * 检测config
     */
    private String checkContent;
    /**
     * 检测res
     */
    private String checkRes;

    private Long workshopId; // 车间id
    private String workshopName; // 车间名称
    private String pondSystemName; // 系统名称
    private Long pondSystemId; // 系统id
    private String pondNum; // 系统编号
    private String pondName; // 存塘名称
    private Long pondId; // 存塘id
    private String pondDetailNum; // 存塘编号
    private String batchNumber; // 批次编号

    private Long userId; // 用户id
    private Long deptId; // 部门id
    private Long tenantId; // 租户id

    @TableField(exist = false)
    private Boolean checkIsOk = false; // 当前的 json选项是否 填写
    @TableField(exist = false)
    private List<ProofingParam> proofingParamList; // 打样 明细
    @TableField(exist = false)
    private LocalDateTime finishTime; // 任务完成时间
    /**
     * 检测配置表 内容
     * 作用：解析巡检json，拿到检测配置表字典信息，就可以执行解析
     */
    @TableField(exist = false)
    private ZkSchedulingTypeDO zkSchedulingTypeDO; //

    public TaskDetailDO build_schedulingId(Long schedulingId) {
        this.setSchedulingId(schedulingId);
        return this;
    }

    public TaskDetailDO(Long taskId, String checkName, String checkType, String checkContent) {
        this.taskId = taskId;
        this.checkName = checkName;
        this.checkType = checkType;
        this.checkContent = checkContent;
    }

    public TaskDetailDO build_pond(ZkPondDO pondDO) {
        this.workshopId = pondDO.getWorkshopId();
        this.workshopName = pondDO.getWorkshopName();
        this.pondSystemName = pondDO.getPondSystemName();
        this.pondSystemId = pondDO.getPondSystemId();
        this.pondNum = pondDO.getPondNum();
        this.pondName = pondDO.getPondName();
        this.pondId = pondDO.getId();
        this.pondDetailNum = pondDO.getPondDetailNum();
        this.batchNumber = pondDO.getBatchNumber();
        return this;
    }

    public TaskDetailDO build_taskConfigId(Long taskConfigId) {
        this.taskConfigId = taskConfigId;
        return this;
    }
    public TaskDetailDO build_authInfo(Long tenantId, Long deptId, Long userId) {
        this.tenantId = tenantId;
        this.deptId = deptId;
        this.userId = userId;
        return this;
    }

    /**
     * 检查巡检单是否填写，目前只要填写第一个就算填写了
     * @return
     */
    public TaskDetailDO build_checkIsOk() {
        if(StringUtils.isEmpty(this.getCheckContent())) return this;
        JSONArray objects = JSON.parseArray(this.getCheckContent());
        if(objects == null || objects.isEmpty()) return this;
        // 第一个巡检项
        JSONObject first = objects.getJSONObject(0);
        String typeValue = first.getString("typeValue");
        // 第一个巡检项的detailList
        JSONArray detailList = first.getJSONArray("detailList");
        if(detailList == null || detailList.isEmpty()) return this;
        JSONObject itemOne = detailList.getJSONObject(0);
        if(!StringUtils.equals(typeValue, TaskDetailTypeValueEnum.FORM.getValue())) {
            for(String key : itemOne.keySet()) {
                if(key.contains("_content")) {
                    Object o = itemOne.getOrDefault(key, null);
                    if(o != null && StringUtils.isNotBlank(o.toString())) {
                        this.checkIsOk = true;
                        break;
                    }
                }
            }
        } else if(StringUtils.equals(typeValue, TaskDetailTypeValueEnum.FORM.getValue())) { // 表单填写
            JSONArray tableValue = itemOne.getJSONArray("tableValue");
            JSONObject valueOne = tableValue.getJSONObject(0);
            if(valueOne!=null) {
                Integer t = 0;
                for(String key : valueOne.keySet()) {
                    if(t == 1) {
                        Object o = valueOne.getOrDefault(key, null);
                        if(o != null && StringUtils.isNotBlank(o.toString())) {
                            this.checkIsOk = true;
                            break;
                        }
                    }
                    t++;
                }
            }

        }
        return this;
    }
}
