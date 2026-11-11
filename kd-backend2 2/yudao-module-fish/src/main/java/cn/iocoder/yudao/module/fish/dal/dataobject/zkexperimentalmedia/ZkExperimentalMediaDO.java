package cn.iocoder.yudao.module.fish.dal.dataobject.zkexperimentalmedia;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 实验多媒体 DO
 *
 * @author admin2
 */
@TableName("zk_experimental_media")
@KeySequence("zk_experimental_media_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZkExperimentalMediaDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 实验id
     */
    private Long experimentId;
    /**
     * 实验编号
     */
    private String experimentNumber;
    /**
     * 文件类型(1:图片;2:视频;)
     */
    private Integer fileType;
    /**
     * 体表类别
     */
    private String bodyType;
    /**
     * 文件id
     */
    private String fid;
    /**
     * 文件名列表
     */
    private String fileNames;
    /**
     * 文件地址列表
     */
    private String fileUrls;
    /**
     * 所属部门
     */
    private Long deptId;


}