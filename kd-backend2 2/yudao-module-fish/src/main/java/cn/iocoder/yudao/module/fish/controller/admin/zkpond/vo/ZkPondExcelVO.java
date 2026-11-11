package cn.iocoder.yudao.module.fish.controller.admin.zkpond.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;

/**
 * 存塘 Excel VO
 *
 * @author 系统管理员
 */
@Data
public class ZkPondExcelVO {

    @ExcelProperty("编号")
    private Long id;

    @ExcelProperty("存塘名称")
    private String pondName;
    private String pondNum; // 存塘序号

    @ExcelProperty("所在车间")
    private String workshopName;

    @ExcelProperty("所在车间id")
    private Long workshopId;

    @ExcelProperty("经度")
    private String longitude;

    @ExcelProperty("维度")
    private String dimension;

    @ExcelProperty("负责人")
    private String userName;

    @ExcelProperty("状态(0:待规划;1:规划中;2:养殖中;-1:已废弃;)")
    private String status;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @ExcelProperty("负责人id")
    private Long userId;

    @ExcelProperty("所属部门")
    private Long deptId;
    private Long tenantId;

    @ExcelProperty("文件id")
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
