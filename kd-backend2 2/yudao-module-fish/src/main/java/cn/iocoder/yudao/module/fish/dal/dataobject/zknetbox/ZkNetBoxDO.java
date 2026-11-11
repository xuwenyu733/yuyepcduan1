package cn.iocoder.yudao.module.fish.dal.dataobject.zknetbox;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 网盘资源 DO
 *
 * @author 系统管理员
 */
@TableName("zk_net_box")
@KeySequence("zk_net_box_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkNetBoxDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 资源类型(1:入职培训;2:运营手册;3:规章制度;4:工作任务及流程;)
     */
    private String fileType;
    /**
     * 资源类型名称
     */
    private String fileTypeName;
    /**
     * 资源前缀
     */
    private String prefixPath;
    /**
     * 备份
     */
    private String remark;
    /**
     * 记录人
     */
    private Long userId;
    /**
     * 所属部门
     */
    private Long deptId;
    private Long tenantId;
    /**
     * 文件id
     */
    private String fid;

}
