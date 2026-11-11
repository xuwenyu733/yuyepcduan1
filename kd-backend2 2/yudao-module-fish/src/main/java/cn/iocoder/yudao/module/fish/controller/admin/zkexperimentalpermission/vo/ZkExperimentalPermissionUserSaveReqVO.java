package cn.iocoder.yudao.module.fish.controller.admin.zkexperimentalpermission.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 实验权限用户新增/修改 Request VO")
@Data
public class ZkExperimentalPermissionUserSaveReqVO {

    private Long id;

    private Long userId;

    @NotBlank(message = "用户姓名不能为空")
    private String userName;

    private Long roleId;

    private String roleName;

    private String phone;

    private String email;

    private Integer status;

    private String remark;

    private Long deptId;

}
