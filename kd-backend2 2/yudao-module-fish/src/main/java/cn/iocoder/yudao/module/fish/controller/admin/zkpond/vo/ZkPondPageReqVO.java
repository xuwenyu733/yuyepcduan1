package cn.iocoder.yudao.module.fish.controller.admin.zkpond.vo;

import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 存塘分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ZkPondPageReqVO extends PageParam {

    private Long id;

    @Schema(description = "存塘名称", example = "张三")
    private String pondName;
    private String pondNum; // 存塘序号

    @Schema(description = "所在车间", example = "芋艿")
    private String workshopName;

    @Schema(description = "所在车间id", example = "31435")
    private Long workshopId;

    @Schema(description = "经度")
    private String longitude;

    @Schema(description = "维度")
    private String dimension;

    @Schema(description = "负责人", example = "李四")
    private String userName;

    @Schema(description = "状态(0:待规划;1:规划中;2:养殖中;-1:已废弃;)", example = "1")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "负责人id", example = "5179")
    private Long userId;

    @Schema(description = "所属部门", example = "16747")
    private Long deptId;
    private Long tenantId;

    @Schema(description = "文件id", example = "20661")
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
    private List<Long> deptIds; // 部门ids

    private String pondType; // 存塘类型 1:单一;2:混合;3:全部;
    private String fishNumType; // 1:养殖中;2:空闲中;3:全部;
}
