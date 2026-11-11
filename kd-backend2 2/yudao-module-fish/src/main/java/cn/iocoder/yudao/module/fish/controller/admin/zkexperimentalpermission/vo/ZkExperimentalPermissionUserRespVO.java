package cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalpermission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 实验权限用户 Response VO")
@Data
public class ZkExperimentalPermissionUserRespVO {

    private Long id;
    private Long userId;
    private String userName;
    private Long roleId;
    private String roleName;
    private String phone;
    private String email;
    private Integer status;
    private String remark;
    private Long deptId;
    private LocalDateTime createTime;

}
