package cn.iocoder.yudao.module.fish.dal.dataobject.zkschedulingtype;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 检测配置 DO
 *
 * @author 系统管理员
 */
@TableName("zk_scheduling_type")
@KeySequence("zk_scheduling_type_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkSchedulingTypeDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 检测名称
     */
    private String checkName;
    /**
     * 类型(1:养殖区;2:设备区;3:定期巡检_养殖;4:定期巡检_设备区;5:定期巡检_仓库区;6:;)
     */
    private String checkType;
    /**
     * 备注
     */
    private String checkContent;
    private String fid;
    private String fileUrl;

}
