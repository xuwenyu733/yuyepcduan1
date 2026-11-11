package cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalpermission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 实验权限日志 Response VO")
@Data
public class ZkExperimentalPermissionLogRespVO {

    private Long id;
    private Long userId;
    private String userName;
    private String action;
    private String target;
    private String detail;
    private Long deptId;
    private LocalDateTime createTime;

}
