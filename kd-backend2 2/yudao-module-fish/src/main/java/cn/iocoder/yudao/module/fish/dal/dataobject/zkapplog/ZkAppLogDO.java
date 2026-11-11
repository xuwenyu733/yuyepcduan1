package cn.iocoder.yudao.module.fish.dal.dataobject.zkapplog;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * app记录 DO
 *
 * @author 系统管理员
 */
@TableName("zk_app_log")
@KeySequence("zk_app_log_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkAppLogDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * app版本
     */
    private Integer appVersion;
    private String appName;
    /**
     * app地址
     */
    private String fileUrl;
    private String fileId;
    /**
     * 备注
     */
    private String remark;
    private String baseWebUrl;
    private String deviceMac;


}
