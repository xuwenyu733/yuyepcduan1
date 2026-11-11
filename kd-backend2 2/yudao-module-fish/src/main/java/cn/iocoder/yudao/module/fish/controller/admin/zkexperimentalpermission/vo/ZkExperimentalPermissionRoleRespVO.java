package cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalpermission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 实验权限角色 Response VO")
@Data
public class ZkExperimentalPermissionRoleRespVO {

    private Long id;
    private String roleName;
    private String roleCode;
    private String permissions;
    private String description;
    private Integer status;
    private Long deptId;
    private LocalDateTime createTime;

}
