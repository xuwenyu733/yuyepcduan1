package cn.iocoder.yudao.module.fish.dal.dataobject.zkpurifydictdata;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 业务字典明细 DO
 *
 * @author admin2
 */
@TableName("zk_purify_dict_data")
@KeySequence("zk_purify_dict_data_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurifyDictDataDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 字典类型(category:品类, source:来源, spec:规格)
     */
    private String dictType;
    /**
     * 字典标签
     */
    private String dictLabel;
    /**
     * 字典键值
     */
    private String dictValue;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 备注
     */
    private String remark;
    /**
     * 所属部门
     */
    private Long deptId;


}