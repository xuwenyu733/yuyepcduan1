package cn.iocoder.yudao.module.fish.controller.admin.zkpond.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

/**
* 存塘 Base VO，提供给添加、修改、详细的子 VO 使用
* 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
*/
@Data
public class ZkPondBaseVO {

    @Schema(description = "存塘名称", example = "张三")
    private String pondName;
    @Schema(description = "系统编号", example = "车1_1")
     @NotNull(message = "系统编号不能为空")
    private String pondNum; // 系统编号

    @Schema(description = "所在车间", example = "芋艿")
    private String workshopName;

    @Schema(description = "所在车间id", required = true, example = "31435")
    // @NotNull(message = "所在车间id不能为空")
    private Long workshopId;

    @Schema(description = "经度")
    private String longitude;

    @Schema(description = "维度")
    private String dimension;

    @Schema(description = "负责人", example = "李四")
    private String userName;

    @Schema(description = "状态(0:待规划;1:规划中;2:养殖中;-1:已废弃;)", example = "1")
    private String status;

    @Schema(description = "负责人id", example = "5179")
    private Long userId;

    @Schema(description = "所属部门", example = "16747")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", required = true, example = "20661")
    @NotNull(message = "文件id不能为空")
    private String fid;

    private String pondScale; // 存塘规模
    private Long fishNum; // 存塘鱼数
    private PondFishLogVO pondFishLogVO;
    private Long pondSystemId;//存塘系统Id
    private String pondSystemName;//存塘系统名称
    private String pondDetailNum; // 存塘编号(区别于pond_num系统编号)
    private BigDecimal fishSpec; // 规格(g/尾)
    private BigDecimal totalWeight; // 总重量
    private String reviseLog; // 修改值的历史值
    private String batchNumber; // 批次编号
    private String tenantName; // 租户名称
    private String deptName; // 部门名称
}
