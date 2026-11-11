package cn.iocoder.yudao.module.fish.controller.admin.zkscheduling.vo;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import lombok.*;
import java.util.*;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "管理后台 - 排班创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkSchedulingCreateReqVO extends ZkSchedulingBaseVO {

    List<AdminUserRespDTO> userList;
    List<Long> pondIdArr; // 巡检任务对应存塘id

    public ZkSchedulingCreateReqVO build_userIdStr() {
        if(EasyListUtil.isEmpty(this.getUserIds())) {
            String userIdStr = this.getUserIds().stream().map(obj -> obj.toString()).collect(Collectors.joining(","));
            this.setUserIdStr(userIdStr);
        }
        return this;
    }

}
